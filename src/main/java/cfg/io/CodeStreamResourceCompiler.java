package cfg.io;

import java.io.*;
import java.util.*;
import cfg.util.*;
import java.nio.charset.Charset;

class CodeStreamResourceCompiler {
    CodeStream stream;
    public CodeStreamResourceCompiler(CodeStream _stream)  {    
	stream=_stream;
    }
    ArrayList<Resource> resources = new ArrayList<Resource>();

    CodeStringBuilder save = null;
    int reads = 0;

    int peek(int offset) throws IOException { return stream.peek(offset); }
    int peek() throws IOException { return stream.peek(0); }
    int read() throws IOException { 
	int code = stream.read();
	if (save != null && code != -1) { ++reads; save.append(code); }
	return code;
    }

    boolean match(int code) throws IOException {
	if (peek() == code) { read(); return true; }
	return false;
    }
    boolean match(String word) throws IOException  {
	for (int i=0; i<word.length(); ++i) {
	    if (peek(i) != word.charAt(i)) return false;
	}
	for (int i=0; i<word.length(); ++i) {
	    read();
	}
	return true;
    }

    boolean eof() throws IOException { return peek(0) == -1; }

    boolean comment() throws IOException {
	if (match("<!--")) {
	    while (!eof() && !match("-->")) { read(); }
	    return true;
	} else {
	    return false;
	}
    }
	
    boolean ws() throws IOException  {
	if (Character.isWhitespace(peek())) {
	    read();
	    while (Character.isWhitespace(peek())) { read(); }
	    return true;
	} else {
	    return false;
	}
    }

    boolean wsc() throws IOException {
	if (ws() || comment()) {
	    while (ws() || comment()) { }
	    return true;
	} else {
	    return false;
	}
    }

    boolean name() throws IOException {
	boolean ans = false;
	for (;;) {
	    int code=peek();
	    if (code != -1 && code != '=' && code != '/' && code != '>') {
		ans = true;
		read();
	    } else {
		break;
	    }
	}
	return ans;
    }

    boolean value() throws IOException {
	int code = peek();
	if (code == '\'' || code == '\"') {
	    read();
	    for (;;) {
		int code2 = read();
		if (code2 == code || code2 == '>' || code2 == -1) break;
	    }
	    return true;
	} else if (code != '>' && code != '/' && code != -1) {
	    read();
	    for (;;) {
		int code2 = read();
		if (code2 == '/' || code2 == '>' || code2 == -1) break;
	    }
	    return true;
	}
	return false;
    }

    boolean attribute() throws IOException {
	ws();
	if (name()) {
	    ws();
	    if (match('=')) {
		ws();
		value();
	    }
	    return true;
	} else {
	    return false;
	}
    }

    CodeStringBuilder tag() throws IOException {
	wsc();
	if (!match('<')) return null;
	ws();
	CodeStringBuilder csb = new CodeStringBuilder();
        if (match('/')) csb.append('/');
	for (;;) {
	    int code = peek();
	    if (code != -1 && !Character.isWhitespace(code) && code != '>' && code != '/') {
		csb.append(code);
		read();
	    } else {
		break;
	    }
	}
	while (attribute()) { }
        ws();
	if (match("/>")) { csb.append('/');  return csb; }
	match(">");
	return csb;
    }

    ResizableArrayList<String> nest = new ResizableArrayList<String>();

    void open(String tag) {
	if (nest.size() == 0) {
	    save = new CodeStringBuilder();
	}
	nest.add(tag);
    }

    void close(String tag) {
	for (int i=nest.size()-1; i>=0; --i) {
	    if (nest.get(i).equals(tag)) {
		nest.resize(i);
		if (i == 0) {
		    int length = save.length()-reads;
		    String value = save.toString(0,length);
		    final byte[] data = value.getBytes(Charset.forName("UTF-8"));
		    ByteArrayInputStreamFactory factory =
			new ByteArrayInputStreamFactory(data);
		    resources.add(new InputStreamFactoryResource(tag,factory));
		}
	    }
	}
    }
    
    public void compile() throws IOException {
	for (;;) {
	    int code;
	    for (;;) {
		code = peek();
		if (code == '<' || code == -1) break;
		read();
	    }
	    if (code == '<') {
		reads = 0;
		CodeStringBuilder csb = tag();
		if (csb.length() > 0 && csb.get(0) == '/') { 
		    String name=csb.toString(1,csb.length()-1);
		    close(name);
		} else if (csb.length() > 0 && csb.get(csb.length()-1) == '/') {
		    String name=csb.toString(0,csb.length()-1);
		    open(name);
		    close(name);
		} else if (csb.length() > 0) {
		    String name=csb.toString();
		    open(name);
		}
	    } else {
		if (nest.size() > 0) {
		    close(nest.get(0));
		}
		break;
	    }
	}
    }
}

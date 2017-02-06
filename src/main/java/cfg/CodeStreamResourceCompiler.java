package cfg;

class CodeStreamResourceCompiler {
    public CodeStreamResourceCompiler(CodeStream _stream)  {    
	stream=_stream;
    }
    ArrayList<Resource> resources = new ArrayList<Resource>();

    ArrayList<CodeStringBuilder> save = null;
    int reads = 0;

    int peek(int offset) { return stream.peek(offset); }
    int peek() { return stream.peek(0); }
    int read() { 
	int code = stream.read();
	if (save != null && code != -1) { ++reads; save.append(code); }
	return code;
    }

    boolean match(int code) {
	if (peek() == code) { read(); return true; }
	return false;
    }
    boolen match(String word) {
	for (int i=0; i<word.length(); ++i) {
	    if (!peek(i) == word.charAt(i)) return false;
	}
	for (int i=0; i<word.length(); ++i) {
	    read();
	}
	return true;
    }

    boolean eof() { return peek(0) == -1; }

    boolean comment() {
	if (match("<!--")) {
	    while (!eof() && !match("-->")) { read(); }
	    return true;
	} else {
	    return false;
	}
    }
	
    boolean ws() {
	if (Character.isWhitespace(peek())) {
	    read();
	    while (Character.isWhitespace(peek())) { read(); }
	    return true;
	} else {
	    return false;
	}
    }

    boolean wsc() {
	if (ws() || comment()) {
	    while (ws() || comment()) { }
	    return true;
	} else {
	    return false;
	}
    }

    boolean name() {
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

    boolean value() {
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

    boolean attribute() {
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

    CodeStringBuilder tag() {
	wsc();
	if (!match('<')) return null;
	ws();
	CodeStringBuilder csb = new CodeStringBuilder();
	for (;;) {
	    int code = peek();
	    if (code != -1 && !Character.isWhitespace(code)) {
		csb.append(code);
		read();
	    } else {
		break;
	    }
	}
	while (attribute()) { }
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
	for (int i=nest.length()-1; i>=0; --i) {
	    if (nest.get(i).equals(tag)) {
		nest.resize(i);
		if (i == 0) {
		    int length = save.length()-reads;
		    String value = new = save.toString(null,0,length);
		    final byte[] data = Charset.forName("UTF-8").encode(value);
		    ByteArrayInputStreamFactory factory =
			new ByteArrayInputStreamFactory(data);
		    resources.add(new InputStreamFactoryResource(tag,factory);
		}
	    }
	}
    }
    
    public void compile() {
	for (;;) {
	    int code;
	    for (;;) {
		int code = peek();
		if (code == '<' || code == -1) break;
		read();
	    }
	    if (code == '<') {
		reads = 0;
		CodeStringBuffer csb = tag();
		if (csb.length() > 0 && csb.get(0) == '/') { 
		    String name=csb.toStringBuilder(null,1,csb.length()-1)
			.toString();
		    close(name);
		} else if (csb.length() > 0 && csb.get(csb.length()-1) == '/') {
		    String name=csb.toStringBuilder(null,0,csb.length()-1)
			.toString();
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
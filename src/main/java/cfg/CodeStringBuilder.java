package cfg;

import java.io.IOException;
import java.util.*;

class CodeStringBuilder {
    private int[] data;
    private int _length;

    public CodeStringBuilder(int capacity) {
	data = new int[capacity];
	_length=0;
    }

    public CodeStringBuilder() { this(32); }

    int length() { return _length; }

    public void append(int value) {
	if (_length + 1 > data.length) {
	    data = Arrays.copyOf(data,(_length+1)*5/4);
	}
	data[_length]=value;
	++_length;
    }

    public void append(int[] source, int offset, int length) {
	if (_length + length > data.length) {
	    data = Arrays.copyOf(data,(_length+length)*5/4);
	}
	System.arraycopy(source,offset,data,_length,length);
	_length += length;
    }

    void append(CodeStream stream) throws IOException {
	int [] buf = new int[1024];
	for (;;) {
	    int size = stream.read(buf);
	    if (size == 0) break;
	    append(buf,0,size);
	}
    }

    public void append(CharSequence cs) {
	int[] cp = new int[cs.length()];
	int i = 0;
	int j = 0;
	while (i < cs.length()) {
	    cp[j] = java.lang.Character.codePointAt(cs,i);
	    i += Character.charCount(cp[j]);
	    ++j;
	}
	append(cp,0,j);
    }

    public CodeStream stream(int offset, int length) {
	return new IntArrayCodeStream(data,offset,length);
    }

    public CodeStream stream() {
	return stream(0,_length);
    }

    public int get(int index) {
	if (index >= 0 && index <= _length) {
	    return data[index];
	} else {
	    throw new IndexOutOfBoundsException("" + index + " not in [0,"+_length+")");
	}
    }

    /*
    public StringBuilder toStringBuilder(StringBuilder sb, 
					 int offset, int length) {
	if (sb == null) {
	    sb = new StringBuilder();
	}
	for (int i=0; i<length; ++i) {
	    sb.appendCodePoint(data[offset+i]);
	}
	return sb;
    }

    public StringBuilder toStringBuilder(StringBuilder sb) {
	return toStringBuilder(sb,0,_length);
    }
    */

    public String toString(int offset, int length) {
	return new String(data,offset,length);
    }

    @Override public String toString() {
	return new String(data,0,_length);
    }
}

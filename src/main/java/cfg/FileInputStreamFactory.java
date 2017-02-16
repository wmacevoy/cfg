package cfg;

import java.io.*;

public class FileInputStreamFactory implements InputStreamFactory {
    protected File file;
    public FileInputStreamFactory(File _file) {
	file=_file;
    }

    static class ExIterator implements ExceptionalIterator<InputStream,IOException> {
	File[] files;
	int k=0;
	ExIterator(File[] _files) { files=_files; }
	@Override public boolean hasNext() {
	    return k<3*files.length;
	}
	@Override public InputStream next() throws IOException {
	    int tmp=k;
	    ++k;
	    int i=tmp%3;
	    int j=tmp/3;
	    switch(i) {
	    case 0: return InputStreams.create("<" + FileResource.getName(files[j]) + ">");
	    case 1: return create(files[j]);
	    case 2: return InputStreams.create("</" + FileResource.getName(files[j]) + ">");
	    default: throw new IndexOutOfBoundsException();
	    }
	}
    }

    public static InputStream create(File _file) throws IOException {
	if (_file.isFile()) {
	    return new FileInputStream(_file);
	}
	if (_file.isDirectory()) {
	    return new CatInputStream(new ExIterator(_file.listFiles()));
	}
	return NullInputStream.NULL_INPUT_STREAM;
    }

    @Override public InputStream create() throws IOException {
	return create(file);
    }
}

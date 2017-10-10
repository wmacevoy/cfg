package cfg.io;

import java.io.*;
import java.util.*;
import cfg.util.*;

public class InputStreamFactories {
    public static final NullInputStreamFactory create() {
	return NullInputStreamFactory.NULL_INPUT_STREAM_FACTORY;
    }

    public static final FileInputStreamFactory create(File file) {
	return new FileInputStreamFactory(file);
    }

    public static final ByteArrayInputStreamFactory create(String data) {
        return create(data.getBytes(InputStreams.CHARSET_UTF8));
    }

    public static final ByteArrayInputStreamFactory create(byte[] bytes, int offset, int length) {
	return new ByteArrayInputStreamFactory(bytes,offset,length);
    }

    public static final ByteArrayInputStreamFactory create(byte[] bytes) {
	return create(bytes,0,bytes.length);
    }

    public static final ByteArrayInputStreamFactory create(int[] codepoints, int offset, int length) {
	return create(new String(codepoints,offset,length));
    }

    public static final ByteArrayInputStreamFactory create(int[] codepoints) {
	return create(codepoints,0,codepoints.length);
    }

    @SafeVarargs
    public static final CatInputStreamFactory create(ExceptionalFactory<InputStream,IOException>... factories) {
	return new CatInputStreamFactory(ExceptionalIterables.<InputStream,IOException>factories(factories));
    }

    public static byte[] bytes(InputStreamFactory factory) throws IOException {
	return InputStreams.bytes(factory.create());
    }

    public static String string(InputStreamFactory factory) throws IOException {
	return InputStreams.string(factory.create());
    }

    public InputStreamFactory caching(InputStreamFactory factory) {
	if (factory instanceof NullInputStreamFactory
	    ||factory instanceof ByteArrayInputStreamFactory
	    ||factory instanceof CachingInputStreamFactory
	    ||((factory instanceof DecoratedInputStreamFactory) &&
	       ((DecoratedInputStreamFactory) factory).decorates(CachingInputStreamFactory.class) != null)) {
	    return factory;
	}
	return new CachingInputStreamFactory(factory);
    }

    public InputStreamFactory caching(InputStreamFactory factory, long timeout) {
	if (factory instanceof NullInputStreamFactory
	    ||factory instanceof ByteArrayInputStreamFactory
	    ||factory instanceof CachingInputStreamFactory
	    ||((factory instanceof DecoratedInputStreamFactory) &&
	       ((DecoratedInputStreamFactory) factory).decorates(CachingInputStreamFactory.class) != null)) {
	    return factory;
	}
	if (timeout <= 0) {
	    return factory;
	} else if (timeout == Long.MAX_VALUE) {
	    return new CachingInputStreamFactory(factory);
	} else {
	    return new TimeoutCachingInputStreamFactory(factory,timeout);
	}
    }
}
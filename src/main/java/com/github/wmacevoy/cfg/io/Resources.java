package cfg.io;

import java.io.*;
import java.util.*;
import cfg.util.*;

public class Resources {
    public static Resource create(String name) {
	return new NullResource(name);
    }

    public static Resource create(String name,byte [] bytes, int offset, int length) {
        return new InputStreamFactoryResource(name,new ByteArrayInputStreamFactory(bytes,offset,length));
    }

    public static Resource create(String name,byte[] bytes) {
	return create(name,bytes,0,bytes.length);
    }

    public static Resource create(String name,String string) {
        return create(name,string.getBytes(InputStreams.CHARSET_UTF8));
    }

    public static Resource create(File file) {
	return new FileResource(file);
    }

    public static Resource cd(Resource resource, Collection<String> parts) throws IOException {
	return cd(resource,(String[])parts.toArray());
    }

    public static Resource cd(Resource resource, String... parts) throws IOException {
	Path path = new Path(parts);
	path.normalize();
	for (String part : path) {
	    ExceptionalIterator<Resource,IOException> i = resource.iterator();
	    boolean found = false;
	    while (i.hasNext()) {
		Resource candidate = i.next();
		if (candidate.getName().equals(part)) {
		    resource = candidate;
		    found = true;
		}
	    }
	    if (!found) {
		resource = create(part);
	    }
	}
	return resource;
    }
}

package cfg.io;

import java.io.*;
import cfg.util.*;

public class SubResource implements Resource {
    Path path;
    Resource resource;

    Resource sub() throws IOException {
	return Resources.cd(resource,path);	
    }

    public SubResource(Resource _resource, Path _path) {
	resource=_resource;
	path = new Path(_path);
	path.normalize();
    }


    @Override public String getName() {
	return path.size() == 0 ? resource.getName() : path.get(path.size()-1);
    }
    @Override public InputStream create() throws IOException {
	return sub().create();
    }
    @Override public ExceptionalIterator<Resource,IOException> iterator() throws IOException {
	return sub().iterator();
    }
}

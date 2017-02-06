package cfg;

class FileResource implements Resource {
    String name;
    File file;
    FileResource(String _name, File _file) {
	name=_name;
	file=_file;
    }

    InputStream stream() {
	if (file.isFile()) {
	    return new FileInputStream(file);
	}
	if (file.isDirectory()) {
	    return new DirectoryInputStream(file);
	}
	return null;
    }

    private List<Resource> resources = null;
    public List<Resource> contents() {
	if (resources == null) {
	    if (file.isFile()) {
		resources = new InputStreamFactoryResource(this).contents();
	    } else {
		resources = new ArrayList<Resource>();
		for (File sub : file.listFiles()) {
		    String name = sub.getName();
		    if (name.endsWith(".cfg")) {
			name = name.substring(0,name.length()-4);
		    }
		    resources.add(name,new FileResource(sub));
		}
	    }
	}
	return resources;
    }
}
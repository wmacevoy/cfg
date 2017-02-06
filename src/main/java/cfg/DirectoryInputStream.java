package cfg;

public class DirectoryInputStream extends CatInputStream {
    DirectoryInputStream(File directory) {
	streams.add(stream("<" + directory.getName() + ">\n"));
	for (File file : directory.getFiles()) {
	    if (file.isDirectory()) {
		streams.add(new DirectoryInputStream(file));
	    } else {
		String name = file.getName();
		if (name.endsWith(".cfg")) {
		    name=name.substring(0,name.length()-4);
		    streams.add(stream("<" + name + ">\n"));
		    streams.add(new FileInputStream(file));
		    streams.add(stream("</" + name + ">\n"));
		} else if (name.endsWith(".xml")) {
		    streams.add(new FileInputStream(file));
		}
	    }
	}
	streams.add(stream("</" + directory.getName() + ">\n"));
    }
}

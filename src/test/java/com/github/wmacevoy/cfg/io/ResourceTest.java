package cfg.io;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import java.nio.charset.Charset;
import cfg.util.*;

public class ResourceTest {
    static String string(InputStreamFactory factory) throws IOException {
	return InputStreamFactories.string(factory);
    }

    static String string(InputStream stream) throws IOException {
	return InputStreams.string(stream);
    }

    static String string(byte[] bytes) {
	return new String(bytes,Charset.forName("UTF-8"));
    }
    
    static byte[] bytes(String string) {
        return string.getBytes(Charset.forName("UTF-8"));
    }
    static Resource resource(String name, String string) throws IOException {
	return resource(name,bytes(string));
    }
    
    static Resource resource(String name,byte[] bytes) {
	return resource(name,new ByteArrayInputStreamFactory(bytes));
    }

    static Resource resource(String name,InputStreamFactory factory) {
	return new InputStreamFactoryResource(name,factory);
    }

    static Resource resource(File file) {
	return new FileResource(file);
    }

    @Test public void testNullResource() throws IOException {
	Resource resource = new NullResource("null");
	assertEquals(string(resource),"");
	assertFalse(resource.iterator().hasNext());
    }

    @Test public void testInputStreamFactoryResource() throws IOException {
	String value = "<hi>there</hi><bye>now</bye>";
	Resource resource = resource("test",value);
	assertEquals(string(resource), value);
	List<Resource> contents = list(resource);
	Resource a = contents.get(0);
	Resource b = contents.get(1);
	assertEquals(a.getName(),"hi");
	assertEquals(b.getName(),"bye");
	assertEquals(string(a),"there");
	assertEquals(string(b),"now");
    }
    List<Resource> list(ExceptionalIterator<Resource,IOException> iterator) throws IOException {
	List<Resource> ans = new ArrayList<Resource>();
	while (iterator.hasNext()) {
	    ans.add(iterator.next());
	}
	return ans;
    }
    List<Resource> list(Resource resource) throws IOException { return list(resource.iterator()); }

    @Test public void testFileResource() throws IOException {
	String prefix = "testFileResource";
	String suffix = ".cfg";
	File file = File.createTempFile(prefix, suffix);
	file.deleteOnExit();
	String value = "<hi>there</hi><bye>now</bye>";
	write(file,value);

        Resource resource=resource(file);

	assertEquals(string(resource),value);
        List<Resource> contents = list(resource.iterator());
	assertEquals(contents.get(0).getName(),"hi");
	assertEquals(contents.get(1).getName(),"bye");
	assertEquals(string(contents.get(0)),"there");
	assertEquals(string(contents.get(1)),"now");

	assertEquals(string(cd(resource,"hi")),"there");
	assertEquals(string(cd(resource,"bye")),"now");	
    }

    void write(File file, String contents) throws IOException {
	try (OutputStream out = new FileOutputStream(file);
	     PrintStream ps = new PrintStream(out)) {
		ps.print(contents);
	}
    }

    void mkdir(File file) throws IOException {
	file.mkdirs();
    }

    void mkdir(String file) throws IOException {
	mkdir(new File(file));
    }

    void write(String file, String contents) throws IOException {
	write(new File(file),contents);
    }

    cfg.functions.RandomGenerators generators = new cfg.functions.RandomGenerators();
    
    String random(String pattern) {
	return generators.get(pattern).generate();
    }

    void system(String command) throws Exception {
	Runtime runtime = Runtime.getRuntime();
	Process process = runtime.exec(command);
	try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
	    String line;
	    while ((line = reader.readLine()) != null) {
		System.out.println(line);
	    }
	}
	process.waitFor();
    }

    Resource cd(Resource resource, String... path) throws IOException {
	return Resources.cd(resource,path);
    }

    @Test public void testDirectoryResource() throws Exception {
	File baseDir = new File(System.getProperty("java.io.tmpdir"));
	String subDir = "testDirectoryResource-" + random("[-+A-Za-z0-9]{32}");
	String dir = baseDir.getAbsolutePath() + "/" + subDir;
	try {
	    mkdir(dir);
	    mkdir(dir + "/db");
	    write(dir + "/db/info.cfg","true");
	    write(dir + "/db/connect.cfg","<server>db.local</server><type>postgresql</type>");
	    write(dir + "/db/features.cfg","<sql>yes</sql>");
	    write(dir + "/users.cfg","<user>admin</user><pass>secret</pass>");
	    Resource resource = new FileResource(new File(dir));
	    assertEquals(string(cd(resource,"db","connect","server")),"db.local");
	    assertEquals(string(cd(resource,"db","connect","type")),"postgresql");
	    assertEquals(string(cd(resource,"db","features","sql")),"yes");
	    assertEquals(string(cd(resource,"users","user")),"admin");
	    assertEquals(string(cd(resource,"users","pass")),"secret");
	    assertEquals(string(cd(resource,"missing","in","action")),"");	    	    
	} finally {
	    system("/bin/rm -rf " + dir);
	}
    }
}

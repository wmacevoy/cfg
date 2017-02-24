package cfg.io;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

import cfg.*;
import cfg.util.*;

public class ClassResourceTest {
    public static void lr(StringBuilder sb, Path path, Resource resource) throws IOException {
	for (ExceptionalIterator<Resource,IOException> i=resource.iterator(); i.hasNext(); ) {
	    Resource ri = i.next();
	    path.add(ri.getName());
	    sb.append(path.toString());
	    sb.append("\n");
	    lr(sb,path,ri);
	    path.resize(path.size()-1);
	}
    }

    @Test public void testList0() throws Exception {
	ClassResource resource = new ClassResource("jar",Cfg.class,new Path());
	StringBuilder sb = new StringBuilder();
	lr(sb,new Path(), resource);
	assertEquals(sb.toString(),
		     "cfg\n"+
		     "cfg/self-test\n"+
		     "cfg/self-test/messages\n"+
		     "cfg/self-test/messages/name\n"+
		     "cfg/self-test/messages/hi\n"+
		     "cfg/self-test/misc\n"+
		     "cfg/self-test/misc/key\n"+
		     "cfg/self-test/misc/encpass\n"+
		     "cfg/self-test/misc/version\n"+
		     "cfg/self-test/misc/db\n"+
		     "cfg/self-test/misc/db/user\n"+
		     "cfg/self-test/misc/db/pass\n"+
		     "cfg/self-test/misc/plain\n"+
		     "cfg/self-test/misc/secret\n"+
		     "cfg/self-test/misc/message\n"+
		     "cfg/self-test/misc/home\n");
    }

    @Test public void testString0() throws Exception {
	ClassResource resource = new ClassResource("jar",Cfg.class,new Path("cfg","self-test"));
	String value =
	    "<messages><name>dave</name>\n"+
	    "<hi>good morning, ${name}. this is version ${../misc/version}</hi>\n"+
	    "</messages><misc><key>$env{KEY}</key>\n"+
	    "<encpass>f2bcbd7f372ffad5f849b8c2a73bb57c7c63543d6fac93dfdb04949a5cf293309a3389651f9aa56d272ad6d6</encpass>\n"+
	    "<version>3.14</version>\n"+
	    "<db>\n"+
	    "  <user>dbadmin</user>\n"+
	    "  <pass>$decrypt{${key},${encpass}}</pass>\n"+
	    "</db>\n"+
	    "<plain>Hello, World!</plain>\n"+
	    "<secret>$encrypt{${key},${plain}}</secret>\n"+
	    "<message>$decrypt{${key},${secret}}</message>\n"+
	    "<home>$env{HOME}</home>\n"+
	    "</misc>";
	String stream = InputStreams.string(resource.create());
	assertEquals(stream,value);
    }
                  
    @Test public void testList1() throws Exception {
	ClassResource resource = new ClassResource("jar",Cfg.class,new Path("cfg","self-test","messages"));
	StringBuilder sb = new StringBuilder();
	lr(sb,new Path(), resource);
	assertEquals(sb.toString(),
		     "name\n"+
		     "hi\n");
    }
}



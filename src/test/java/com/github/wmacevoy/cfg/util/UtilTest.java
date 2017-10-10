package cfg.util;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.lang.reflect.*;
import cfg.functions.*;

public class UtilTest
{
    static class Problem extends Exception {};

    static class ExIterable implements ExceptionalIterable<String,Problem> {
	String[] strings;
	class ExIterator implements ExceptionalIterator<String,Problem> {
	    int k;
	    @Override public boolean hasNext() { return k<strings.length; }
	    @Override public String next() throws Problem {
		int tmp=k;
		++k;
		if (strings[tmp] == null) throw new Problem();
		return strings[tmp];
	    }
	}

	
	ExIterable(String... _strings) {
	    strings=_strings;
	}
	@Override public ExIterator iterator() throws Problem {
	    if (strings == null) throw new Problem();
	    return new ExIterator();
	}
    }

    String string(ExceptionalIterable<String,Problem> it) {
	try {
	    return string(it.iterator());
	} catch (Problem ex) {
	    return "PROBLEM";
	}
    }

    String string(ExceptionalIterator<String,Problem> it) {
	StringBuilder sb = new StringBuilder();
	boolean first = true;
	try {
	    while (it.hasNext()) {
		if (first) first=false;
		else sb.append(",");
		try {
		    sb.append(it.next());
		} catch (Problem p) {
		    sb.append("problem");
		}
	    }
	} catch (Problem p) {
	    sb.append("?");
	}
	return sb.toString();
    }

    @Test public void testSelf() throws Problem {
	assertEquals(string(new ExIterable()),"");
	assertEquals(string(new ExIterable((String[])null)),"PROBLEM");
	assertEquals(string(new ExIterable("a")),"a");
	assertEquals(string(new ExIterable((String)null)),"problem");
	assertEquals(string(new ExIterable("a",null)),"a,problem");
	assertEquals(string(new ExIterable(null,"b")),"problem,b");	
    }

    @Test public void testMetaNull() {
	assertEquals(string(new MetaExceptionalIterable<String,Problem>()),"");
    }

    @Test public void testMetaElements() {
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().elements()),"");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().elements("a")),"a");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().elements("a","b")),"a,b");	
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().elements("a").elements()),"a");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().elements().elements("a")),"a");
    }

    ExIterable iterable(String... strings) {
	return new ExIterable(strings);
    }

    @Test public void testMetaIterable() {
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(iterable())),"");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(iterable("a"))),"a");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(iterable("a","b"))),"a,b");	
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(iterable("a")).iterable(iterable())),"a");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(iterable()).iterable(iterable("a"))),"a");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(iterable("a")).iterable(iterable("b"))),"a,b");
    }

    @Test public void testMetaFactories() {
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(new ExIterable())),"");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(new ExIterable("a"))),"a");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(new ExIterable("a","b"))),"a,b");	
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(new ExIterable("a")).iterable(new ExIterable())),"a");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(new ExIterable()).iterable(new ExIterable("a"))),"a");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(new ExIterable("a")).iterable(new ExIterable())),"a");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(new ExIterable()).iterable(new ExIterable("a"))),"a");
	assertEquals(string(new MetaExceptionalIterable<String,Problem>().iterable(new ExIterable("a")).iterable(new ExIterable("b"))),"a,b");
    }
    
    String string(Iterable<String> strings) {
	return string(ExceptionalIterables.<String,Problem>iterable(strings));
    }
    ResizableArrayList<String> list(String... strings) {
	ResizableArrayList<String> ans = new ResizableArrayList<String>(strings.length);
	for (int i=0; i<strings.length; ++i) {
	    ans.add(strings[i]);
	}
	return ans;
    }

    @Test public void testResize() {
	assertEquals(string(new ResizableArrayList<String>()),"");
	assertEquals(string(new ResizableArrayList<String>(1)),"");
	assertEquals(string(new ResizableArrayList<String>(list())),"");
	assertEquals(string(new ResizableArrayList<String>(list("a"))),"a");		
	assertEquals(string(new ResizableArrayList<String>(list("a","b"))),"a,b");
	assertEquals(string(list()),"");
	assertEquals(string(list("a")),"a");
	assertEquals(string(list("a","b")),"a,b");
	assertEquals(string(list("a","b").resize(0,"x")),"");	
	assertEquals(string(list("a","b").resize(1,"x")),"a");
	assertEquals(string(list("a","b").resize(2,"x")),"a,b");
	assertEquals(string(list("a","b").resize(3,"x")),"a,b,x");
	assertEquals(string(list("a","b").resize(4,"x")),"a,b,x,x");
    }

    ExceptionalFactory<String,Problem> factory(final String string) {
	return new ExceptionalFactory<String,Problem> () {
	    @Override public String create() throws Problem {
		if (string == null) throw new Problem();
		return string;
	    }
	};
    }

    @Test public void testIterables() {
	assertEquals(string(ExceptionalIterables.<String,Problem>empty()),"");
	assertEquals(string(ExceptionalIterables.<String,Problem>iterable(list())),"");
	assertEquals(string(ExceptionalIterables.<String,Problem>iterable(list("a"))),"a");
	assertEquals(string(ExceptionalIterables.<String,Problem>iterable(list("a","b","c"))),"a,b,c");		
	assertEquals(string(ExceptionalIterables.<String,Problem>elements()),"");
	assertEquals(string(ExceptionalIterables.<String,Problem>elements("a")),"a");
	assertEquals(string(ExceptionalIterables.<String,Problem>elements("a","b","c")),"a,b,c");		
	assertEquals(string(ExceptionalIterables.<String,Problem>factories()),"");
	assertEquals(string(ExceptionalIterables.<String,Problem>factories(factory("a"))),"a");
	assertEquals(string(ExceptionalIterables.<String,Problem>factories(factory("a"),factory(null),factory("c"))),"a,problem,c");
    }


    @Test public void testIterators() {
	assertEquals(string(ExceptionalIterators.<String,Problem>empty()),"");
	assertEquals(string(ExceptionalIterators.<String,Problem>iterator(list().iterator())),"");
	assertEquals(string(ExceptionalIterators.<String,Problem>iterator(list("a").iterator())),"a");
	assertEquals(string(ExceptionalIterators.<String,Problem>iterator(list("a","b","c").iterator())),"a,b,c");		
	assertEquals(string(ExceptionalIterators.<String,Problem>elements()),"");
	assertEquals(string(ExceptionalIterators.<String,Problem>elements("a")),"a");
	assertEquals(string(ExceptionalIterators.<String,Problem>elements("a","b","c")),"a,b,c");		
	assertEquals(string(ExceptionalIterators.<String,Problem>factories()),"");
	assertEquals(string(ExceptionalIterators.<String,Problem>factories(factory("a"))),"a");
	assertEquals(string(ExceptionalIterators.<String,Problem>factories(factory("a"),factory(null),factory("c"))),"a,problem,c");
    }
    
}
package cfg.functions;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.lang.reflect.*;
import cfg.functions.*;

public class GeneratorTest
{
    @Test public void testStringGenerator() {
	StringGenerator g = new StringGenerator("alice");
	assertEquals(g.toString(),"'alice'");
	assertEquals(g.generate(),"alice");
    }

    @Test public void testStringsGenerator() {
	ArrayList<String> strings = new ArrayList<String>();
	strings.add("alice");
	strings.add("bob");
	StringsGenerator g = new StringsGenerator(strings);
	assertEquals(g.toString(),"('alice','bob')");
	for (int i=0; i<10; ++i) {
	    String ans = g.generate();
	    boolean alice = ans.equals("alice");
	    boolean bob = ans.equals("bob");
	    assertEquals(alice || bob,true);
	}
    }

    @Test public void testOrGenerator() {
	ArrayList<Generator> generators = new ArrayList<Generator>();
	generators.add(new StringGenerator("alice"));
	generators.add(new StringGenerator("bob"));
	OrGenerator g = new OrGenerator(generators);
	assertEquals(g.toString(),"('alice'|'bob')");
	for (int i=0; i<10; ++i) {
	    String ans = g.generate();
	    boolean alice = ans.equals("alice");
	    boolean bob = ans.equals("bob");
	    assertEquals(alice || bob,true);
	}
    }

    @Test public void testAndGenerator() {
	ArrayList<Generator> generators = new ArrayList<Generator>();
	generators.add(new StringGenerator("alice"));
	generators.add(new StringGenerator("bob"));
	AndGenerator g = new AndGenerator(generators);
	assertEquals(g.toString(),"'alice'&'bob'");
	assertEquals(g.generate(),"alicebob");
    }

    @Test public void testRepeatGenerator() {
	RepeatGenerator g = new RepeatGenerator(new StringGenerator("x"),3,5);
	assertEquals(g.toString(),"'x'{3,5}");
	for (int i=0; i<10; ++i) {
	    String ans = g.generate();
	    assertEquals(ans.length() >= 3 && ans.length() <= 5, true);
	}
    }
    
    @Test public void testRandomStringGenerator() {
	RandomGenerator g = new RandomGenerator("alice");
	assertEquals(g.toString(),"alice->'alice'");
	assertEquals(g.generate(),"alice");
    }

    @Test public void testRandomSetGenerator() {
	RandomGenerator g = new RandomGenerator("[a-c]");
	assertEquals(g.toString(),"[a-c]->('a','b','c')");
	for (int i=0; i<10; ++i) {
	    String ans = g.generate();
	    assertEquals(ans.length(),1);
	    boolean a = ans.equals("a");
	    boolean b = ans.equals("b");
	    boolean c = ans.equals("c");
	    assertEquals(a || b || c, true);
	}
    }

    @Test public void testRandomRepeatGenerator() {
	Generator g = new RandomGenerator("x{3,5}");
	System.out.println("repeat: " + g.toString());
	assertEquals(g.toString(),"x{3,5}->'x'{3,5}");
	for (int i=0; i<10; ++i) {
	    String ans = g.generate();
	    assertEquals(ans.length() >= 3 && ans.length() <= 5, true);
	}
    }
}

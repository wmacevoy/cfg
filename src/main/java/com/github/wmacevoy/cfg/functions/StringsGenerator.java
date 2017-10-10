package com.github.wmacevoy.cfg.functions;

import java.util.ArrayList;

public class StringsGenerator implements Generator { 
    ArrayList<String> strings;
    void add(String string) {
	strings.add(string);
    }
    public StringsGenerator(ArrayList<String> _strings) {
	strings=_strings;
    }
    @Override public String generate() {
	return strings.get(RandomGenerator.random(0,strings.size()-1));
    }	

    @Override public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("(");
	for (String string : strings) {
	    if (sb.length() > 1) { sb.append(","); }
	    sb.append("'" + string + "'");
	}
	sb.append(")");
	return sb.toString();
    }
}

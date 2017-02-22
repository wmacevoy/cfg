package cfg.io;

import java.util.*;
import cfg.util.*;

public class Path extends ResizableArrayList<String> implements Comparable<Path> {
    public Path(int initialCapacity) {
	super(initialCapacity);
    }
    public Path(Collection<String> parts) {
	super(parts);
    }
    public Path(String... parts) {
	super(parts.length);
	for (int i=0; i<parts.length; ++i) {
	    add(parts[i]);
	}
    }

    public Path(Path root, String... parts) {
	super(root.size()+parts.length);
	addAll(root);
	for (int i=0; i<parts.length; ++i) {
	    add(parts[i]);
	}
    }

    @Override public int compareTo(Path to) {
	int n = Math.min(size(),to.size());
	for (int i=0; i<n; ++i) {
	    String a = get(i);
	    String b = to.get(i);
	    if (a != null && b != null) {
		int diff = a.compareTo(b);
		if (diff != 0) return diff;
	    } else if (a == null && b != null) {
		return to.size() == i+1 ? 1 :  -1;
	    } else if (a != null && b == null) {
		return size() == i+1 ?  -1 : 1;
	    }
	}
	int delta = size()-to.size();
	if (delta < 0) return -1;
	if (delta > 0) return  1;
	return 0;
    }

    @Override public boolean equals(Object to) {
	return compareTo((Path)to) == 0;
    }

    public boolean normalize() {
	boolean changed = false;
	int i=0;
        while (i<size()) {
	    String original = get(i);
	    String part = original.trim();
	    if (part.equals(".") || part.equals("") 
		|| (part.equals("..") && i==0)) {
		remove(i);
		changed = true;
            } else if (part.equals("..")) {
		removeRange(i-1,i+1);
		--i;
		changed = true;
            } else {
		if (part != original) {
		    set(i,part);
		    changed = true;
		}
                ++i;
            }
        }
	return changed;
    }

    public String toString(int offset, int size) {
	StringBuilder sb = new StringBuilder();
	for (int i=0; i<size; ++i) {
	    if (i > 0) sb.append('/');
	    sb.append(get(offset+i));
	}
	return sb.toString();
    }
    @Override public String toString() {
	return toString(0,size());
    }

    @Override public Path clone() {
	return new Path(this);
    }

    public boolean endsWith(String... endings) {
	if (size() == 0) return false;
	String tail = get(size()-1);
	for (String ending : endings) {
	    if (tail.endsWith(ending)) { return true; }
	}
	return false;
    }

    public String tail() {
	return (size() > 0) ? get(size()-1) : null;
    }
}

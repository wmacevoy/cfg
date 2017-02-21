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
	    int diff = get(i).compareTo(to.get(i));
	    if (diff != 0) return diff;
	}
	return size()-to.size();
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
}

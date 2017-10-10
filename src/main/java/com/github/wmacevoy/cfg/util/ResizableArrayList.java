package cfg.util;

import java.util.*;

public class ResizableArrayList<E> extends ArrayList<E> {
    public ResizableArrayList() { super(); }
    public ResizableArrayList(Collection<? extends E> c) { super(c); }
    public ResizableArrayList(int initialCapacity) { super(initialCapacity); }
    public ResizableArrayList resize(int _size, E fill) {
	if (_size < size()) {
	    removeRange(_size,size());
	} else if (_size > size()) {
	    ensureCapacity(_size);
	    while (size() < _size) {
		add(fill);
	    }
	}
	return this;
    }
    public ResizableArrayList resize(int _size) { return resize(_size,null); }
}

package cfg.util;

import java.util.*;

public class ResizableArrayList<E> extends ArrayList<E> {
    public ResizableArrayList() { super(); }
    public ResizableArrayList(Collection<? extends E> c) { super(c); }
    public ResizableArrayList(int initialCapacity) { super(initialCapacity); }
    public void resize(int _size, E fill) {
	if (_size < size()) {
	    removeRange(_size,size());
	} else if (_size > size()) {
	    ensureCapacity(_size);
	    while (_size > 0) {
		add(fill);
		--_size;
	    }
	}
    }
    public void resize(int _size) { resize(_size,null); }
}

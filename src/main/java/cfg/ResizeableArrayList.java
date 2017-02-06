package cfg;

public class ResizableArrayList<E> extends ArrayList<E> {
    public ResizableArrayList() { super(); }
    public ResizableArrayList(Collection<? extends E> c) { super(c); }
    public ResizableArrayList(int initialCapacity) { super(initialCapacity); }
    public void resize(int _length, E fill) {
	if (_length < length()) {
	    removeRange(_length,length());
	} else if (_length > length()) {
	    ensureCapacity(_length);
	    while (_length > 0) {
		add(fill);
		--_length;
	    }
	}
    }
    public void resize(int _length) { resize(_length,null); }
}
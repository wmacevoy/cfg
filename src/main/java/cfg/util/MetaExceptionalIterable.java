package cfg.util;

public class MetaExceptionalIterable<T,E extends Throwable> implements ExceptionalIterable<T,E> {
    java.util.Collection<ExceptionalIterable<T,E>> iterables;

    class MetaIterator implements ExceptionalIterator<T,E> {
	java.util.Iterator<ExceptionalIterable<T,E>> i;
	ExceptionalIterator<T,E> j;
	
	MetaIterator() {
	    i = iterables.iterator();
	    j = ExceptionalIterators.<T,E>empty();
	}

	@Override public boolean hasNext() throws E {
	    while (!j.hasNext() && i.hasNext()) {
		j=i.next().iterator();
	    }
	    return j.hasNext();
	}
	@Override public T next() throws E {
	    while (!j.hasNext() && i.hasNext()) {
		j=i.next().iterator();
	    }
	    return j.next();
	}
    }

    @Override public ExceptionalIterator<T,E> iterator() {
	return new MetaIterator();
    }

    public MetaExceptionalIterable() {
	this(new java.util.ArrayList<ExceptionalIterable<T,E>>());
    }

    public MetaExceptionalIterable(java.util.Collection<ExceptionalIterable<T,E>> _iterables) {
	iterables = _iterables;
    }

    public void elements(T... elements) {
	iterables.add(ExceptionalIterables.elements(elements));
    }

    public void iterable(Iterable<T> iterable) {
	iterables.add(ExceptionalIterables.iterable(iterable));
    }

    public void factories(ExceptionalFactory<T,E>... factories) {
	iterables.add(ExceptionalIterables.factories(factories));
    }

    public void factories(Iterable<ExceptionalFactory<T,E>> factories) {
	iterables.add(ExceptionalIterables.factories(factories));
    }

    public void iterable(ExceptionalIterable<T,E> iterable) {
	iterables.add(iterable);
    }
}

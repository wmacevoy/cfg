package cfg.util;

public class MetaExceptionalIterable<T,E extends Throwable> implements ExceptionalIterable<T,E> {
    java.util.Collection<ExceptionalIterable<T,E>> iterables;

    class MetaIterator implements ExceptionalIterator<T,E> {
	java.util.Iterator<ExceptionalIterable<T,E>> i;
	ExceptionalIterator<T,E> j;
	
	MetaIterator() throws E {
	    i = iterables.iterator();
	    j = null;
	    while (j == null && i.hasNext()) {
	    	j=i.next().iterator();
		if (!j.hasNext()) j=null;
	    }
	}

	@Override public boolean hasNext() throws E {
	    return j != null;
	}

	@Override public T next() throws E {
	    T ans = j.next();
	    while (!j.hasNext() && i.hasNext()) {
		j=i.next().iterator();
	    }
	    if (!j.hasNext()) j=null;
	    return ans;
	}
    }

    @Override public ExceptionalIterator<T,E> iterator() throws E {
	return new MetaIterator();
    }

    public MetaExceptionalIterable() {
	this(new java.util.ArrayList<ExceptionalIterable<T,E>>());
    }

    public MetaExceptionalIterable(java.util.Collection<ExceptionalIterable<T,E>> _iterables) {
	iterables = _iterables;
    }

    public MetaExceptionalIterable elements(T... elements) {
	iterables.add(ExceptionalIterables.elements(elements));
	return this;
    }

    public MetaExceptionalIterable iterable(Iterable<T> iterable) {
	iterables.add(ExceptionalIterables.iterable(iterable));
	return this;
    }

    public MetaExceptionalIterable  iterable(ExceptionalIterable<T,E> iterable) {
	iterables.add(iterable);
	return this;
    }

    public MetaExceptionalIterable factories(ExceptionalFactory<T,E>... factories) {
	iterables.add(ExceptionalIterables.factories(factories));
	return this;
    }

    public MetaExceptionalIterable factories(Iterable<ExceptionalFactory<T,E>> factories) {
	iterables.add(ExceptionalIterables.factories(factories));
	return this;
    }

}

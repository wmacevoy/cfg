package cfg.util;

import java.util.*;

public class ExceptionalIterators {
    public static <T,E extends Throwable> ExceptionalIterator<T,E> empty() {
	return new ExceptionalIterator<T,E>() {
	    @Override public boolean hasNext() { return false; }
	    @Override public T next() { return null; }
	};
    }

    public static <T,E extends Throwable> ExceptionalIterator<T,E> iterator(final Iterator<T> iterator) {
	return new ExceptionalIterator<T,E>() {
	    @Override public boolean hasNext() {
		return iterator.hasNext();
	    }
	    @Override public T next() {
		return iterator.next();
	    }
	};
    }

    @SafeVarargs
    public static <T,E extends Throwable> ExceptionalIterator<T,E> elements(final T... elements) {
	return new ExceptionalIterator<T,E>() {
	    int k=0;
	    @Override public boolean hasNext() {
		return k<elements.length;
	    }
	    @Override public T next() {
		int tmp=k;
		++k;
		return elements[tmp];
	    }
	};
    }

    public static <T,E extends Throwable> ExceptionalIterator<T,E> factories(final Iterator<ExceptionalFactory<T,E>> iterator) {
	return new ExceptionalIterator<T,E>() {
	    @Override public boolean hasNext() {
		return iterator.hasNext();
	    }
	    @Override public T next() throws E {
		return iterator.next().create();
	    }
	};
    }


    @SafeVarargs
    public static <T,E extends Throwable> ExceptionalIterator<T,E> factories(final ExceptionalFactory<T,E>... factories) {
	return new ExceptionalIterator<T,E>() {
	    int k = 0;
	    @Override public boolean hasNext() {
		return k < factories.length;
	    }
	    @Override public T next() throws E {
		int tmp=k;
		++k;
		return factories[tmp].create();
	    }
	};
    }
}

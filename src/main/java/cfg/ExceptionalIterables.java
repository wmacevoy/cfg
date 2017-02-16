package cfg;

import java.util.*;

public class ExceptionalIterables {

    public static <T,E extends Throwable> ExceptionalIterable<T,E> empty() {
	return new ExceptionalIterable<T,E> () {
	    @Override public ExceptionalIterator<T,E> iterator() {
		return ExceptionalIterators.<T,E>empty();
	    }
	};
    }

    public static <T,E extends Throwable> ExceptionalIterable<T,E> iterable(final Iterable<T> iterable) {
	return new ExceptionalIterable<T,E> () {
	    @Override public ExceptionalIterator<T,E> iterator() {
		return ExceptionalIterators.<T,E>iterator(iterable.iterator());
	    }
	};
    }

    public static <T,E extends Throwable> ExceptionalIterable<T,E> elements(final T... elements) {
	return new ExceptionalIterable<T,E> () {
	    @Override public ExceptionalIterator<T,E> iterator() {
		return ExceptionalIterators.<T,E>elements(elements);
	    }
	};
    }

    public static <T,E extends Throwable> ExceptionalIterable<T,E> factories(final Iterable<ExceptionalFactory<T,E>> iterable) {
	return new ExceptionalIterable<T,E> () {
	    @Override public ExceptionalIterator<T,E> iterator() {
		return ExceptionalIterators.<T,E>factories(iterable.iterator());
	    }
	};
    }

    public static <T,E extends Throwable> ExceptionalIterable<T,E> factories(final ExceptionalFactory<T,E>... factories) {
	return new ExceptionalIterable<T,E> () {
	    @Override public ExceptionalIterator<T,E> iterator() {
		return ExceptionalIterators.factories(factories);
	    }
	};
    }

}

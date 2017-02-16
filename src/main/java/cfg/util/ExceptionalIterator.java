package cfg.util;

public interface ExceptionalIterator<T,E extends Throwable> {
    boolean hasNext() throws E;
    T next() throws E;
}

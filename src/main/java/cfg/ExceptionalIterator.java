package cfg;

public interface ExceptionalIterator<T,E extends Throwable> {
    boolean hasNext() throws E;
    T next() throws E;
}

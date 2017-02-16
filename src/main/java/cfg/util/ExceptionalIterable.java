package cfg.util;

public interface ExceptionalIterable<T,E extends Throwable> {
    ExceptionalIterator<T,E> iterator() throws E;
}

package cfg;

public interface ExceptionalFactory<T,E extends Throwable> {
    T create() throws E;
}

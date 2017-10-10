package cfg.io;

import java.io.*;

public class ResourceIOException extends IOException {
    final Throwable exception;
    public ResourceIOException(Throwable _exception) {
	exception=_exception;
    }
}
    

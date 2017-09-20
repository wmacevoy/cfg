package cfg.io;

import java.io.*;
import cfg.util.*;

public interface InputStreamFactory extends ExceptionalFactory<InputStream,IOException>, Closeable { }

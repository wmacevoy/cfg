package cfg;

import java.io.*;
import java.util.*;

public interface Resource extends ExceptionalFactory<InputStream,IOException>, ExceptionalIterable<Resource,IOException> {
    String getName();
}


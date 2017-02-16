package cfg.io;

import java.io.*;
import java.util.*;
import cfg.util.*;

public interface Resource extends InputStreamFactory,IterableResource {
    String getName();
}


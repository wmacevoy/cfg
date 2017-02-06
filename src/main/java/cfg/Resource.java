package cfg;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public interface Resource extends InputStreamFactory {
    static final InputStream NULL = new NullInputStream();

    static final List<Resource> EMPTY 
	= Collections.unmodifiableList(new ArrayList<Resource>());

    String getName();
    InputStream stream();
    List<Resource> contents();
}

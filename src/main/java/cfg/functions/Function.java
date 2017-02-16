package cfg.functions;

import java.util.List;
import cfg.Cfg;

public interface Function {
    int args(); // -1 for arbitrary
    String eval(Cfg cfg, String base, String id, List<String> args);
}

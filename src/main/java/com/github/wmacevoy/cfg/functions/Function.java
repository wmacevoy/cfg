package com.github.wmacevoy.cfg.functions;

import java.util.List;
import com.github.wmacevoy.cfg.Cfg;

public interface Function {
    int args(); // -1 for arbitrary
    String eval(Cfg cfg, String base, String id, List<String> args);
}

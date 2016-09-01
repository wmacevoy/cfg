package cfg;

import java.util.List;

public class DecryptFunction implements Function {
    @Override public int args() { return 2; }

    @Override
        public String eval(Cfg cfg, String base, String id, List<String> args) {
        String key = cfg.cook(base,args.get(0));
        String secret = cfg.cook(base,args.get(1));
        return kiss.API.decrypt(key,secret);
    }
}

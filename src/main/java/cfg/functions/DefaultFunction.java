package cfg.functions;

import java.util.List;
import cfg.Cfg;

public class DefaultFunction implements Function {
    @Override public int args() { return -1; }
    
    @Override
        public String eval(Cfg cfg, String base, String id, List<String> args) {
        StringBuilder sb = new StringBuilder();
        sb.append("$");
        sb.append(id);
        if (args.size() > 0) {
            sb.append("{");
            for (int i=0; i<args.size(); ++i) {
                if (i > 0) sb.append(",");
                sb.append(args.get(i));
            }
            sb.append("}");
        }
        return sb.toString();
    }
}

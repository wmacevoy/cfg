package cfg.functions;

import java.util.List;
import cfg.Cfg;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Function implements Function {
    @Override public int args() { return 1; }

    @Override
        public String eval(Cfg cfg, String base, String id, List<String> args) {
        String message = cfg.cook(base,args.get(0));
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 must be provided by JCE");
        }
        try {
            md.update(message.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("UTF-8 encoding is provided by Java");
        }
        byte[] digest = md.digest();
        String hexDigest = Cipher.hex(digest);
        return hexDigest;
    }
}

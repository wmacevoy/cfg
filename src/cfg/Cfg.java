package cfg;

import static kiss.API.*;

import java.io.File;
import java.io.ByteArrayInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 
import javax.xml.xpath.XPathConstants; 
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.TreeMap;
import java.util.ArrayList;
import java.nio.charset.Charset;

public class Cfg 
{
    String root;

    public Cfg() { root="/"; }
    public Cfg(String _root) { 
        if (!_root.startsWith("/")) {
            _root = "/" + _root;
        }

        if (!_root.endsWith("/")) {
            _root = _root + "/";
        }

        root = _root;
    }

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xPath = xPathfactory.newXPath();

    TreeMap < String , Document > documents
        = new TreeMap < String , Document > ();

    TreeMap < String , XPathExpression > xPathExpressions
        = new TreeMap < String , XPathExpression > ();

    public String get(String raw) {
        return cook(root,raw);
    }
    
    static boolean isId0(char ch) {
        return Character.isLetter(ch);
    }

    static boolean isId1(char ch) {
        return Character.isLetter(ch) || Character.isDigit(ch) || ch == '-' || ch == '_';
    }

    int parseId(String raw, int pos, StringBuilder sbid) {
        if (pos < raw.length() && isId0(raw.charAt(pos))) {
            sbid.append(raw.charAt(pos));
            ++pos;
            while (pos < raw.length() && isId1(raw.charAt(pos))) {
                sbid.append(raw.charAt(pos));
                ++pos;
            }
        }
        return pos;
    }

    int parseArgs(String raw, int pos, ArrayList<String> args) {
        if (pos < raw.length() && raw.charAt(pos) == '{') {
            ++pos;
            StringBuilder arg = new StringBuilder();
            int depth = 0;
            while (pos < raw.length()) {
                char c=raw.charAt(pos);
                if (c == '{') { ++depth; }
                if (c == '}') {
                    if (depth <= 0) { ++pos; break; }
                    --depth;
                }
                if (c == ',' && depth == 0) {
                    args.add(arg.toString());
                    arg.setLength(0);
                } else {
                    arg.append(c);
                }
                ++pos;
            }
            args.add(arg.toString());
        }
        return pos;
    }


    public String cook(String base, String raw) {
        StringBuilder cooked = new StringBuilder();
        int pos = 0;
        while (pos < raw.length()) {
            char c = raw.charAt(pos);
            if (c != '$') {
                cooked.append(c);
                ++pos;
                continue;
            }
            if (pos+1 < raw.length() && raw.charAt(pos+1) == '$') {
                cooked.append('$');
                pos += 2;
                continue;
            }

            ++pos;
            StringBuilder sbid = new StringBuilder();
            pos = parseId(raw,pos,sbid);
            String id = sbid.toString();
                
            ArrayList<String> args = new ArrayList<String>();
            pos = parseArgs(raw,pos,args);

            if (id.equals("") && args.size() == 1) {
                String arg = cook(base,args.get(0)).trim();
                if (!arg.startsWith("/")) {
                    arg = base + arg;
                }
                arg = getString(arg);
                cooked.append(arg);
                continue;
            }

            if (id.equals("raw") && args.size() == 1) {
                String arg = cook(base,args.get(0)).trim();
                if (!arg.startsWith("/")) {
                    arg = base + arg;
                }
                arg = getRawString(arg);
                cooked.append(arg);
                continue;
            }

            if (id.equals("env") && args.size() == 1) {
                String arg = cook(base,args.get(0)).trim();
                cooked.append(System.getenv(arg));
                continue;
            }

            if (id.equals("trim") && args.size() == 1) {
                String arg = cook(base,args.get(0)).trim();
                cooked.append(arg);
                continue;
            }

            if (id.equals("decrypt") && args.size() == 2) {
                String key = cook(base,args.get(0));
                String secret = cook(base,args.get(1));
                String plain = decrypt(key,secret);
                cooked.append(plain);
                continue;
            } 

            if (id.equals("encrypt") && args.size() == 2) {
                String key = cook(base,args.get(0));
                String plain = cook(base,args.get(1));
                cooked.append(encrypt(key,plain));
                continue;
            }

            cooked.append("$");
            cooked.append(id);
            if (args.size() > 0) {
                cooked.append("{");
                for (int i=0; i<args.size(); ++i) {
                    if (i > 0) cooked.append(",");
                    cooked.append(args.get(i));
                }
                cooked.append("}");
            }
            continue;
        }
        return cooked.toString();
    }

    public String realPath(String _resource) {
        String resource = _resource.trim();
        if (!resource.startsWith("/")) {
            resource = root + resource;
        }
        _resource = resource;

        String[] parts = resource.split("/");
        for (int i=0; i<parts.length; ++i) parts[i]=parts[i].trim();
        int i=0,n=parts.length;
        while (i<n) {
            if (parts[i].equals(".") || parts[i].equals("") || (parts[i].equals("..") && i==0)) {
                for (int j=i+1; j<n; ++j) {
                    parts[j-1]=parts[j];
                }
                --n;
            } else if (parts[i].equals("..")) {
                for (int j=i+1; j<n; ++j) {
                    parts[j-2]=parts[j];
                }
                n -= 2;
                --i;
            } else {
                ++i;
            }
        }

        parts = java.util.Arrays.copyOf(parts,n);
        StringBuilder sb = new StringBuilder();
        for (i=0; i<n; ++i) {
            sb.append("/");
            sb.append(parts[i]);
        }
        resource=sb.toString();

        return resource;
    }

    private String[] getFilePath(String resource) {
        resource=realPath(resource);

        int base=1,slash=-1;
        while ((slash = resource.indexOf('/',base)) != -1) {
            String dir = resource.substring(0,slash);
            if (ClassLoader.class.getResourceAsStream(dir) == null) {
                break;
            }
            base=slash+1;
        }
        slash = resource.indexOf('/',base);
        if (slash == -1) slash = resource.length();
        String file = resource.substring(0,slash) + ".cfg";
        String path = resource.substring(base);
        String[] ans = new String[] { file , path, resource };
        return ans;
    }

    public String getString(String resource) {
        String[] filePath = getFilePath(resource);
        String file = filePath[0];
        String path = filePath[1];
        String base = file.substring(0,file.length()-4) + "/";
        String raw=getRawString(file,path,resource);
        return cook(base,raw);
    }

    public String getRawString(String resource) {
        String[] filePath = getFilePath(resource);
        return getRawString(filePath[0],filePath[1],resource);
    }

    static InputStream stream(String data) {
        byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        return new ByteArrayInputStream(bytes);
    }

    private String getRawString(String file, String path, String resource) {
        Document document = documents.get(file);
        String base = file.substring(file.lastIndexOf('/')+1,file.length()-4);
        if (documents.get(file) == null) {
            try {
                InputStream body = 
                    ClassLoader.class.getResourceAsStream(file);
                InputStream head = stream("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<" + base + ">\n");
                InputStream tail = stream("</" + base + ">\n");

                CatInputStream in = new CatInputStream(head,body,tail);

                DocumentBuilderFactory documentBuilderFactory 
                    = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder 
                    = documentBuilderFactory.newDocumentBuilder();
                    
                document = documentBuilder.parse(in);
                documents.put(file,document);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("could not load resource: " + resource);
            }
        }

        try {
            XPathExpression xPathExpression = 
                xPathExpressions.get(resource);
            if (xPathExpression == null) {
                xPathExpression = xPath.compile(path);
                xPathExpressions.put(resource,xPathExpression);
            }
            return (String) 
                xPathExpression.evaluate(document, 
                                         XPathConstants.STRING);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("could not load resource: " + resource);
        }
    }
}

package cfg;

import static kiss.API.*;

import java.io.File;
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
    
    public String cook(String base, String raw) {
        StringBuilder cooked = new StringBuilder();
        int pos = 0;
        while (pos < raw.length()) {
            if (raw.charAt(pos) == '$' && pos+1 < raw.length()) {
                if (raw.charAt(pos+1) == '{') {
                    int start = pos+1;
                    int end = raw.indexOf('}',start);
                    if (end != -1) {
                        String part = raw.substring(start+1,end).trim();
                        if (!part.startsWith("/")) {
                            part = base + part;
                        }
                        String as = getString(part);
                        cooked.append(as);
                        pos = end + 1;
                        continue;
                    }
                } else if (raw.substring(pos,pos+5).equals("$env{")) {
                    int start = pos+4;
                    int end = raw.indexOf('}',start);
                    if (end != -1) {
                        String part = raw.substring(start+1,end).trim();
                        String as = System.getenv(part);
                        cooked.append(as);
                        pos = end + 1;
                        continue;
                    }
                } else if (raw.substring(pos,pos+9).equals("$decrypt{")) {
                    int start = pos+8;
                    int end = raw.indexOf('}',start);
                    if (end != -1) {
                        String[] parts = raw.substring(start+1,end).split(",");
                        String key = parts[0].trim();
                        if (!key.startsWith("/")) { key = base + key; }
                        key = getString(key).trim();
                        String enc = parts[1].trim();
                        if (!enc.startsWith("/")) { enc = base + enc; }
                        enc = getString(enc).trim();
                        String as = decrypt(key,enc);
                        if (as != null) {
                            cooked.append(as);
                        }
                        pos = end + 1;
                        continue;
                    }
                } else if (raw.substring(pos,pos+9).equals("$encrypt{")) {
                    int start = pos+8;
                    int end = raw.indexOf('}',start);
                    if (end != -1) {
                        String[] parts = raw.substring(start+1,end).split(",");
                        String key = parts[0].trim();
                        if (!key.startsWith("/")) { key = base + key; }
                        key = getString(key).trim();
                        String plain = parts[1].trim();
                        if (!plain.startsWith("/")) { plain = base + plain; }
                        plain = getString(plain);
                        String as = encrypt(key,plain);
                        if (as != null) {
                            cooked.append(as);
                        }
                        pos = end + 1;
                        continue;
                    }
                } else if (raw.charAt(pos+1) == '$') {
                    cooked.append("$");
                    pos += 2;
                    continue;
                }
            }
            cooked.append(raw.charAt(pos));
            ++pos;
        }
        return cooked.toString();
    }

    private String[] getFilePath(String resource) {
        if (!resource.startsWith("/")) {
            resource = root + resource;
        }
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
        return new String[] { file , path, resource };
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

    private String getRawString(String file, String path, String resource) {
        Document document = documents.get(file);
        if (documents.get(file) == null) {
            try {
                InputStream in = 
                    ClassLoader.class.getResourceAsStream(file);
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

    public <T> T getFactory(Class<T> type,String path) {
        try {
            return (T) 
                Class.forName(getString(path)+"$Factory")
                .newInstance();
        } catch (Exception e) {
            throw new Error("no factory: " + e);
        }
    }
}



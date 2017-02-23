package cfg;

import cfg.functions.Function;
import cfg.io.InputStreams;
import cfg.io.InputStreamFactory;
import cfg.io.InputStreamFactories;

import java.util.Arrays;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import java.util.HashMap;
import java.nio.charset.Charset;

import java.util.*;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Cfg 
{
    private cfg.functions.Env env = new cfg.functions.Env();
    public String env(String name) { 
	return env.get(name); 
    }
    public void env(String name, String value) { 
	env.set(name,value); 
    }
    public static String ENV(String name) { 
	return cfg.functions.Env.getenv(name); 
    }
    public static void ENV(String name, String value) { 
	cfg.functions.Env.setenv(name,value); 
    }

    public HashMap<String,Function> functions = new HashMap<String,Function>() {{
            put("",new cfg.functions.StringFunction());
            put("raw",new cfg.functions.RawStringFunction());
            put("env",new cfg.functions.EnvFunction());
            put("encrypt",new cfg.functions.EncryptFunction());
            put("decrypt",new cfg.functions.DecryptFunction());
            put("random",new cfg.functions.RandomFunction());
            put("key",new cfg.functions.KeyFunction());	    
        }};

    Function defaultFunction = new cfg.functions.DefaultFunction();

    String root;
    
    public Cfg() { root="/"; }
    public Cfg(String _root) { 
	if (_root == null || _root.equals("")) {
	    _root = "/";
	}
	if (_root.endsWith(".cfg")) {
	    _root = _root.substring(0,_root.length()-4);
	}
	if (!_root.endsWith("/")) {
	    _root = _root + "/";
	}
	if (!isURL(_root) && !_root.startsWith("/")) {
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

            Function f = functions.get(id);

            if (f == null || (f.args() != -1 && f.args() != args.size())) {
                f = defaultFunction;
            }
            cooked.append(f.eval(this,base,id,args));
        }
        return cooked.toString();
    }

    public String realPath(String resource) {
        resource = resource.trim();
        if (!isURL(resource) && !resource.startsWith("/")) {
            resource = root + resource;
        }

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

        StringBuilder sb = new StringBuilder();
	if (!isURL(parts[0])) sb.append("/");
        for (i=0; i<n; ++i) {
            if (i > 0) sb.append("/");
            sb.append(parts[i]);
        }
	String realResource = sb.toString();

        return realResource;
    }

    static Pattern urlPattern 
	= Pattern.compile("[A-Za-z0-9]+:.*");
    static boolean isURL(String resource) {
	return urlPattern.matcher(resource).matches();
    }
    static InputStream load(String resource) {
    	if (isURL(resource)) {
            URL url = null;
            try {
		url = new URL(resource);
	    } catch (MalformedURLException ex) {
		System.out.println("malformed " + resource);
	    }
            if (url != null) {
		try {
		    System.out.println("load url: " + resource);
		    return url.openStream();
		} catch (IOException ex) {
		    return null;
		}
            }
            return null;
        } else {
            return ClassLoader.class.getResourceAsStream(resource);
        }
    }
    
    private String[] getFilePath(String resource) {
        resource=realPath(resource);

        int base=1,slash=-1;
        while ((slash = resource.indexOf('/',base)) != -1) {
            String dir = resource.substring(0,slash);
            if (load(dir) == null) {
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

    private String getRawString(final String file, String path, String resource) {
        Document document = documents.get(file);
        String base = file.substring(file.lastIndexOf('/')+1,file.length()-4);
        if (documents.get(file) == null) {
            try {
                InputStreamFactory head = InputStreamFactories.create("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
								      "<" + base + ">\n");
		InputStreamFactory body = new InputStreamFactory() {
			@Override public InputStream create() throws IOException {
			    return load(file);
			}
		    };
		    
		InputStreamFactory tail = InputStreamFactories.create("</" + base + ">\n");

		InputStream in = InputStreams.create(head,body,tail);
								
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

    public static void main(String [] args) throws Exception {
	Cfg cfg = null;
	if (args[0].equals("--root")) {
	    cfg = new Cfg(args[1]);
	    args=Arrays.copyOfRange(args,1,args.length);
	} else {
	    cfg = new Cfg();
	}

	for (String arg : args) {
	    System.out.println(cfg.get(arg));
	}
    }

}

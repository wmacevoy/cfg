package cfg;

import java.security.SecureRandom;
import java.util.ArrayList;

class RandomGenerator  implements Generator {
    Generator generator;
    CharSequence source;
    int position;
    char at() {
	return (position < source.length() ? source.charAt(position) : (char) 0);
    }
    boolean next() {
	if (position < source.length()) {
	    ++position;
	    return true;
	} else {
	    return false;
	}
    }
    
    boolean end() {
	return position >= source.length();
    }

    static SecureRandom rng = new SecureRandom();
    static int random(int min, int max) {
	return rng.nextInt(max-min+1)+min;
    }

    static final Generator NULL = new StringGenerator("");

    RandomGenerator(CharSequence _source) {
	source=_source;
	position = 0;
	generator = or();
	if (!end()) { generator = null; }
	if (generator == null) {
	    throw new UnsupportedOperationException("syntax error: "  + source + " near " + position);
	}
    }

    @Override
    public String generate() {
	return generator.generate();
    }

    char peek(String acceptable) {
	if (!end()) {
	    int index = acceptable.indexOf(at());
	    if (index >= 0) {
		return acceptable.charAt(index);
	    }
	}
	return (char) 0;
    }

    char next(String acceptable) {
	char ans = peek(acceptable);
	if (ans != 0) next();
	return ans;
    }

    Integer number() {
	StringBuilder sb = new StringBuilder();
	char digit;
	while ((digit = next("0123456789")) != 0) {
	    sb.append(digit);
	}
	if (sb.length() == 0) return null;
	return Integer.parseInt(sb.toString());
    }

    Generator or() {
	ArrayList<Generator> parts = new ArrayList<Generator>();
	for (;;) {
	    Generator part = and();
	    if (part != null) {
		parts.add(part);
		if (next("|") != 0) continue;
	    }
	    break;
	}
	if (parts.size() == 0) return NULL;
	if (parts.size() == 1) return parts.get(0);
	return new OrGenerator(parts);
    }

    Generator set() {
	if (next("[") == 0) return null;

	ArrayList<String> parts = new ArrayList<String>();
	boolean range = false;
	if (next("-") != 0) { parts.add("-"); }

        while (!end() && next("]") == 0) {
	    if ((next("-") != 0) && parts.size() > 0 && !end()) {
		char a = parts.get(parts.size()-1).charAt(0);
		char b = at();
		if (b > a) {
		    while (++a <= b) {
			parts.add(Character.toString(a));
		    }
		}
		next();
	    } else if (!end()) {
		parts.add(Character.toString(at()));
		next();
	    }
	}
	if (parts.size() == 0) return NULL;
	if (parts.size() == 1) return new StringGenerator(parts.get(0));
	return new StringsGenerator(parts);
    }

    Generator and() {
	ArrayList<Generator> parts = new ArrayList<Generator>();
	Generator part = null;

	while (!end() && peek("|)") == 0) {
	    int cursor = position;
	    if (peek("(") != 0) {
		boolean ok = true;
		ok = ok && next("(") != 0;
		ok = ok && (part = or()) != null;
		ok = ok && next(")") != 0;
		if (ok) {
		    parts.add(part);
		} else {
		    position = cursor;
		    break;
		}
	    } else if (peek("{") != 0) {
		boolean ok = true;
		Integer min = null;
		Integer max = null;
		    
		ok = ok && (next("{") != 0);
		ok = ok && (min = number()) != null;
		if (next("}") != 0) {
		    max = min;
		} else {
		    ok = ok && (next(",") != 0);
		    ok = ok && (max = number()) != null;
		    ok = ok && (next("}") != 0);
		}
		if (ok) {
		    if (parts.size() > 0) {
			Generator single = parts.get(parts.size()-1);
			Generator repeat = 
			    new RepeatGenerator(single,min,max);
			parts.set(parts.size()-1,repeat);
		    }
		} else {
		    position = cursor;
		    break;
		}
	    } else if (peek("[") != 0) {
		boolean ok = true;
		ok = ok && ((part = set()) != null);
		if (ok) {
		    parts.add(part);
		} else {
		    position = cursor;
		    break;
		}
	    } else {
		StringBuilder sb = new StringBuilder();
		while (!end() && peek("{([|])}") == 0) {
		    sb.append(at());
		    next();
		}
		if (sb.length() > 0) {
		    part = new StringGenerator(sb.toString());
		    parts.add(part);
		} else {
		    position = cursor;
		    break;
		}
	    }
	}
	if (parts.size() == 0) return NULL;
	if (parts.size() == 1) return parts.get(0);
	return new AndGenerator(parts);
    }

    @Override public String toString() {
	return source+"->" + generator;
    }
}
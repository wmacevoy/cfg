package cfg;

public class StringGenerator implements Generator { 
    String string; 
    StringGenerator(String _string) { 
	string=_string;
    } 
    @Override public String generate() { return string; }

    @Override public String toString() {
	return "'" + string + "'";
    }
}

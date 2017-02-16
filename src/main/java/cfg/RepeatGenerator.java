package cfg;

public class RepeatGenerator implements Generator {
    Generator generator;
    int min;
    int max;

    RepeatGenerator(Generator _generator, int _min, int _max) {
	generator=_generator;
	min=_min;
	max=_max;
    }

    @Override public String generate() {
	int n=RandomGenerator.random(min,max);
	StringBuilder sb = new StringBuilder();
	for (int i=0; i<n; ++i) {
	    sb.append(generator.generate());
	}
	return sb.toString();
    }

    @Override public String toString() {
	return generator.toString() + "{" + min + "," + max + "}";
    }
}

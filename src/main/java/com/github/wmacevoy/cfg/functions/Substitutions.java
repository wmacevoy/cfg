package com.github.wmacevoy.cfg.functions;

import com.github.wmacevoy.cfg.jregex.PerlSubstitution;
import com.github.wmacevoy.cfg.jregex.Replacer;
import java.util.HashMap;
import com.github.wmacevoy.cfg.jregex.Pattern;

public class Substitutions {
    
    private HashMap<String,Pattern> patterns = null;
    private HashMap<String,PerlSubstitution> substitutions = null;

    public Replacer get(String pattern, String substitution) {
        if (patterns == null) {
            synchronized(this) {
                if (patterns == null) {
                    patterns = new HashMap<String,Pattern>();
                }
            }
        }
        Pattern rePattern = patterns.get(pattern);
        if (rePattern == null) {
          rePattern = new Pattern(pattern);
          patterns.put(pattern,rePattern);
        }
         
        if (substitutions == null) {
            synchronized(this) {
                if (substitutions == null) {
                    substitutions = new HashMap < String, PerlSubstitution > ();
                }
            }
        }
        PerlSubstitution reSubstitution = substitutions.get(substitution);
        if (reSubstitution == null) {
            reSubstitution = new PerlSubstitution(substitution);
            substitutions.put(substitution, reSubstitution);
        }
        
        return new Replacer(rePattern, reSubstitution);
    }

}

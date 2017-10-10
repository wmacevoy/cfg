/*
 * Copyright 2017 wmacevoy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.wmacevoy.cfg.jregex;

import com.github.wmacevoy.cfg.functions.Cipher;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author wmacevoy
 */

public class JRegexTest
{
    @Test public void testRegex1() {
        Pattern pattern = new Pattern("\\b(\\d+)\\b");
        PerlSubstitution subst = new PerlSubstitution("'$1'");
        Replacer replacer = new Replacer(pattern, subst);
        String src = "abc 123 def";
        String result = replacer.replace(src);
        String expect="abc '123' def";
        assertEquals(expect,result);
    }
}
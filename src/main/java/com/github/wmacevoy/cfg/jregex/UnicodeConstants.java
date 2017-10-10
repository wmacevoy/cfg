/**
 * Copyright (c) 2001, Sergey A. Samokhodkin
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. 
 * - Redistributions in binary form 
 * must reproduce the above copyright notice, this list of conditions and the following 
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of jregex nor the names of its contributors may be used 
 * to endorse or promote products derived from this software without specific prior 
 * written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY 
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * @version 1.2_01
 */

package com.github.wmacevoy.cfg.jregex;

interface UnicodeConstants{
   public static final int CATEGORY_COUNT=32;
   public static final int Cc=Character.CONTROL;
   public static final int Cf=Character.FORMAT;
   public static final int Co=Character.PRIVATE_USE;
   public static final int Cn=Character.UNASSIGNED;
   public static final int Lu=Character.UPPERCASE_LETTER;
   public static final int Ll=Character.LOWERCASE_LETTER;
   public static final int Lt=Character.TITLECASE_LETTER;
   public static final int Lm=Character.MODIFIER_LETTER;
   public static final int Lo=Character.OTHER_LETTER;
   public static final int Mn=Character.NON_SPACING_MARK;
   public static final int Me=Character.ENCLOSING_MARK;
   public static final int Mc=Character.COMBINING_SPACING_MARK;
   public static final int Nd=Character.DECIMAL_DIGIT_NUMBER;
   public static final int Nl=Character.LETTER_NUMBER;
   public static final int No=Character.OTHER_NUMBER;
   public static final int Zs=Character.SPACE_SEPARATOR;
   public static final int Zl=Character.LINE_SEPARATOR;
   public static final int Zp=Character.PARAGRAPH_SEPARATOR;
   public static final int Cs=Character.SURROGATE;
   public static final int Pd=Character.DASH_PUNCTUATION;
   public static final int Ps=Character.START_PUNCTUATION;
   public static final int Pi=Character.START_PUNCTUATION;
   public static final int Pe=Character.END_PUNCTUATION;
   public static final int Pf=Character.END_PUNCTUATION;
   public static final int Pc=Character.CONNECTOR_PUNCTUATION;
   public static final int Po=Character.OTHER_PUNCTUATION;
   public static final int Sm=Character.MATH_SYMBOL;
   public static final int Sc=Character.CURRENCY_SYMBOL;
   public static final int Sk=Character.MODIFIER_SYMBOL;
   public static final int So=Character.OTHER_SYMBOL;
   
   public static final int BLOCK_COUNT=256;
   public static final int BLOCK_SIZE=256;
   
   public static final int MAX_WEIGHT=Character.MAX_VALUE+1;
   public static final int[] CATEGORY_WEIGHTS=new int[CATEGORY_COUNT];
}

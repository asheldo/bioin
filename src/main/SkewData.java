package main;

import java.util.*;

public class SkewData {

    String text;
    Integer [] skewRun;
    int [] skewIncr;
    int part;
    List<Integer> min = new LinkedList<>();
    int skew, minSkew, maxSkew;        

    public SkewData(String text, char base1, char base2) {
        this.text = text;
        part = Math.min(1000, text.length());
        skewRun = new Integer [part];
        skewIncr = mapSkew(text.toUpperCase(),
                           base1, base2);
    }

    void add(int ix) {
        int incr = skewIncr[ix];
        skew += incr;
        if (incr <= 0) {
            if (skew < minSkew) {
                minSkew = skew;
                min.clear();
                min.add(ix + 1);
            } else if (skew == minSkew) {
                min.add(ix + 1);
            }
        } else {
            maxSkew = Math.max(maxSkew, skew);
        }                      
        if (ix < skewRun.length) {
            skewRun[ix] = skew;
        }           
    }

    public static SkewData findMinimalSkew(String text, 
                                           char base1, char base2) 
    {
        SkewData data = new SkewData(text, base1, base2);
        int len = data.skewIncr.length;
        for (int ix = 0; ix < len; ++ix) {
            data.add(ix);
        }
        return data;
    }

    public static int [] mapSkew(String text, char base1, char base2) {
        int [] skew = new int [text.length()];
        for (int i = 0; i < skew.length; ++i) {
            char c = text.charAt(i);
            if (c == 'C') {
                skew[i] = -1;
            } else if (c == 'G') {
                skew[i] = 1;
            } else {
                skew[i] = 0;
            }
        }
        return skew;
    }
}

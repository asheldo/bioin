package main;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class SkewData extends Processor {

    String text;
    Integer [] skewRun;
    List<String> skewMinRun;
    int [] skewIncr;
    int part;
    List<Integer> min = new LinkedList<>();
    int skew, minSkew, maxSkew;        

    public SkewData(String text, char base1, char base2,
                    int part) {
        this.text = text;
        this.part = Math.min(part, text.length());
        skewRun = new Integer [part];
    }

    void add(int ix) {
        int incr = skewIncr[ix];
        skew += incr;
        if (incr <= 0) {
            if (skew < minSkew) {
                minSkew = skew;
                min.clear();
                min.add(ix + 1);
            } else if (skew == minSkew && min.size() < 100) {
                min.add(ix + 1);
            }
        } else {
            maxSkew = Math.max(maxSkew, skew);
        }                      
        if (ix < skewRun.length) {
            skewRun[ix] = skew;
        }           
    }

    void runTheMin() {
        if (false && min.size() == 0) {
            return;
        }
        int half = part/2;
        int startMinRun = Math.max(0, min.get(0) - half);
        int [] copy = Arrays.copyOfRange(skewIncr,
            startMinRun, startMinRun + part);
        IntFunction<String> f = new IntFunction<String>() {
            public String apply(int i) {
                return Integer.toString(i);
            }
        };
        skewMinRun = Arrays.stream(copy).mapToObj(f).collect(Collectors.toList());
        skewMinRun.set(half, " ** " + skewMinRun.get(half));
        print("\nskewMinRun %s\n", skewMinRun);
    }
    
    public static SkewData findMinimalSkew(String text, 
                                           char base1, char base2,
                                           int runPart) 
    {
        SkewData data = new SkewData(text, base1, base2, runPart);
        data.skewIncr = mapSkew(text.toUpperCase(),
                           base1, base2);
        int len = data.skewIncr.length;
        for (int ix = 0; ix < len; ++ix) {
            data.add(ix);
        }
        data.runTheMin();
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

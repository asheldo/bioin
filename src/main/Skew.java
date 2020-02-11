package main;

import java.util.*;

public class Skew extends Processor {
    
    public static void main(String [] args) throws Exception {
        System.out.println("String:");
        FileInputs ins = FileInputs.scanFileSingleInput();
        SkewData data = findMinimalSkew(ins, 'G', 'C');
        System.out.println(data.min);
        TextFileUtil.writeKmersListPlus(ins.outputFile, 
            data.min,
            data.minSkew,
            data.maxSkew,
            data.skewRun.length,
            Arrays.asList(data.skewRun)
            );
    }
    
    static class SkewData {
        String text;
        Integer [] skewRun;
        int [] skewIncr;
        int part;
        List<Integer> min = new LinkedList<>();
        int skew, minSkew, maxSkew;        
        
        public SkewData(FileInputs ins, char base1, char base2) {
            text = ins.sourceText0;
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
    }
    
    static SkewData findMinimalSkew(FileInputs ins, 
                                    char base1, char base2) 
    {
        SkewData data = new SkewData(ins, base1, base2);
        int len = data.skewIncr.length;
        for (int ix = 0; ix < len; ++ix) {
            data.add(ix);
        }
        return data;
    }
    
    static int [] mapSkew(String text, char base1, char base2) {
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

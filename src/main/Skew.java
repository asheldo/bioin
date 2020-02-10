package main;

import java.util.*;

public class Skew extends Processor
{
    
    public static void main(String [] args) throws Exception {
        System.out.println("String:");
        FileInputs ins = FileInputs.scanFileSingleInput();

        String str = ins.sourceText0;
        Integer [] skewRun = new Integer [str.length()];
        
        List<Integer> min = findMinimalSkew(str, 'G', 'C', skewRun);
        System.out.println(min);
        TextFileUtil.writeKmersListPlus(ins.outputFile, 
            min,
            skewRun.length,
            Arrays.asList(skewRun)
            );

    }
    
    static List<Integer> findMinimalSkew(String text, 
             char base1, char base2, Integer [] skewRun) {
        List<Integer> min = new LinkedList<>();
        int skew = 0;
        int [] skewIncr = mapSkew(text.toUpperCase(), 
            base1, base2);
        int minSkew = 0;
        int ix = 0;
        for (int i : skewIncr) {
            skew += i;
            skewRun[ix] = skew;
            if (i <= 0) {
                if (skew < minSkew) {
                    minSkew = skew;
                    min.clear();
                    min.add(ix +1);
                } else if (skew == minSkew) {
                    min.add(ix +1);
                }
            }
            ++ix;
        }
        return min;
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

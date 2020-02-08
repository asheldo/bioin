package main;

import java.util.*;

public class FastKmerSearchData
 {
    final int len;
    final int k;
    final String text;
    final Integer [] counts;
    final int [] base4kmers;
    int maxCount;

    final Map<String,Integer> countKmers 
    = new LinkedHashMap<>();

    FastKmerSearchData(final String text, 
                       final String kParam) {
        this.text = text;
        this.len = text.length();
        this.k = Integer.parseInt(kParam);
        this.counts = new Integer [len - k + 1];
        this.base4kmers = Base4er.calc(this);
    }
    

    static class Base4er {
        // i.e. 2^32 is 4^16
        static final int pow4s [ ] = pow(4, 15);

        static int [] pow(int base, int n)  {
            int [] pows = new int [n];
            pows[0] = 1;
            for (int i = 1; i < n; i++) {
                pows[i] = pows[i-1] * base;
            }
            return pows;
        }

        static int mapChar(char c) {
            switch (c) {
                case 'A':
                    return 0;
                case 'C':
                    return 1;
                case 'G':
                    return 2;
                case 'T':
                    return 3;
                default:
                    throw new RuntimeException("problem");
            }
        }

        static int [] calc(FastKmerSearchData d)  {
            int n = d.counts.length;
            int [] base4s = new int [n];

            for (int i = 0; i < n; i++) {
                base4s[i] = mapChar(d.text.charAt(i));
            }
            for (int i=0; i < n; i++) {
                int base4kmer = 0;
                for (int j=0; j < d.k; ++j) {
                    base4kmer += pow4s[d.k-j-1] * base4s[i];
                }
                base4s[i] = base4kmer;
            }
            return base4s;
        }
    }
}

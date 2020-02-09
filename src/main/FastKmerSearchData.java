package main;

import java.util.*;

public class FastKmerSearchData
 {
    final int len; // text lengrh
    final int k;
    final int L; // window length
    final String text;
    final Integer [] counts; // whole genome
    final int [] base4kmers; // base4 k-mer N
    int maxCount;
    
    final int clumpThreshold;
    final byte [] clumped;
    final int [] clumpCount;

    final Map<String,Integer> countKmers 
        = new LinkedHashMap<>();

    FastKmerSearchData(final String text, 
                       final int k,
                       final int L,
                       final int clumpThreshold) {
        this.text = text;
        this.len = text.length();
        this.k = k;
        this.L = L; // window, may be entire genome
        this.clumpThreshold = clumpThreshold;
         // costly init
        this.base4kmers = Base4er.calc(this);
        //
        this.counts = new Integer [len - k + 1];
        //
        int kmax = (int) Math.pow(4, k);
        this.clumped = new byte [kmax];
        this.clumpCount = new int [kmax];
        
    }
}

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
    

}

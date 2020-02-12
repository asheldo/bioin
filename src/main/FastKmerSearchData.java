package main;

import java.util.*;

public class FastKmerSearchData extends Processor
 {
    final int len; // text lengrh
    final int k; // kmer length
    final int L; // window length
    final int hamming; // hamming 
    final String text;
    final Integer [] counts; // whole genome
    final int [] base4kmers; // base4 k-mer N
    int maxCount;
    
    final int clumpThreshold;
    final byte [] clumped;
    final int [] clumpCount;

    final Map<String,Integer> countKmers 
        = new LinkedHashMap<>();

    public FastKmerSearchData(final String text, 
                       final int k,
                       final int L,
                       final int clumpThreshold) {
        this(text, k, L, clumpThreshold, 0);
    }
                       
                       
    public FastKmerSearchData(final String text, 
                       final int k,
                       final int L,
                       final int clumpThreshold,
                       final int hammingDistance) {
        this.text = text;
        this.len = text.length();
        this.k = k;
        this.L = L; // window, may be entire genome
        this.clumpThreshold = clumpThreshold;
        this.hamming = hammingDistance;
        
        checkpoint();
         // costly init
        this.base4kmers = Base4er.calc(this);
        println("t calc: " + checkpoint());
        
        this.counts = new Integer [len - k + 1];
        int kmax = (int) Math.pow(4, k);
        this.clumped = new byte [kmax];
        this.clumpCount = new int [kmax];
        
        println("t(clump)=" + checkpoint());
    }
}

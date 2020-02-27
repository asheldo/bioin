package main;

import java.util.*;

public class FastKmerSearchData extends Processor
{
    public static final int OBJECTS = 4;
    
    final int len; // text lengrh
    final int k; // kmer length
    final int L; // window length
    final int hamming; // hamming 
    
    final int [] hammingBase2High; // high base2 bit of base4 place
    final int [] hammingBase2Low; // low bit
    
    final String text;
    final Integer [] counts; // whole genome
    final int [] base4kmers; // base4 k-mer N
    int maxCount;
    
    
    final int clumpThreshold;
    byte [] clumped;
    int [] clumpCount;

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
                              final boolean initClumps) {
        this(text, k, L, clumpThreshold, 0, initClumps);
    }
                       
    public FastKmerSearchData(final String text, 
                              final int k,
                              final int L,
                              final int clumpThreshold,
                              final int hammingDistance) {
        this(text, k, L, clumpThreshold, hammingDistance, true);
    }
    
    public FastKmerSearchData(final String text, 
                       final int k,
                       final int L,
                       final int clumpThreshold,
                       final int hammingDistance,
                       final boolean initClumps) {
        this.text = text;
        this.len = text.length();
        this.k = k;
        this.L = L; // window, may be entire genome
        this.clumpThreshold = clumpThreshold;
        this.hamming = hammingDistance;
        this.hammingBase2High = Base4er.base4HighBits(k);
        this.hammingBase2Low = Base4er.base4LowBits(k);
        
        checkpoint();
         // costly init
        this.base4kmers = Base4er.calc(this);
        println("t calc: " + checkpoint());
        
        this.counts = new Integer [len - k + 1];
        
        int kmax = (int) Math.pow(4, k);
        if (initClumps && k < 12) {
        // try  {
            this.clumped = new byte [kmax];
            this.clumpCount = new int [kmax];
        /*
        } catch (Throwable e) {

        System.err.println(e.getMessage());
        throw e;
        }
        */
 
        }
        println("t(clump)=" + checkpoint());
    }
}

package main.assembly;

public class KmerStringSearchData {
    
    final int len; // text lengrh
    final int k; // kmer length
    final int L; // window length
    final int hamming; // hamming 

    
    final String text;
    final Integer [] counts; 
    
    // whole genome
    
    // array of char arrays
    final char [][] kmers; // k-mer N
    
    int maxCount;
    
    public KmerStringSearchData(String text,
                                int k, 
                                int L, int hamming) {
        this.k = k;
        this.L = L;
        this.text = text;
        this.hamming = hamming;
        len = text.length();
        this.counts = new Integer [len - k + 1];
        kmers = new char[L][k];
        for (int i = 0;  i < L; ++i) {
            kmers [i] = text.substring(i, i + k)
                .toCharArray();
        }
        // if (k < 12) {
        // this.clumped = new byte [kmax];
        // this.clumpCount = new int [kmax];
    }
}

package main;

import java.util.*;

public class MotifMedian extends Processor
{
    public static void main(String[] args) throws Exception {
        FileInputs m = FileInputs.scanFileParamsAndSources(
            "mot157.txt");
        print("inputs: %s \n", m);
        int k = 9;
        int hammingD = 0; //2;
        if (m.params != null) {
            try {
                k = Integer.parseInt(m.params.get(0));
                if (m.params.size() > 1) {
                    hammingD = Integer.parseInt(m.params.get(1));
                } else {
                    hammingD = k - 1;
                }
            } catch (Exception e) {
                // e.printStackTrace(System.err);
            }
        
        
            // START HERE
            println("Motif.med");
            
            
            processMotifs(m, k, hammingD);
        }
    }
    

    // e.g. 16 kmers in i and j, 2 out of 9 pos
    static void processMotifs(FileInputs m,
                              final int k,
                              final int hammingD) throws Exception {
        
        final int ct = m.sourceText.length;
        MotifMedian mmed = new MotifMedian(); // d, reverseCompMis);
       
        for (int i = 0; i < ct; ++i) {
            String text = m.sourceText[i];
            if (text == null)
                break;
            mmed.mmeds.add(
                readSourceN(text, k, hammingD));
            checkpoint();
            
            /*
            nbrs.getKmerNeighbors(m.outputFile, 
                d, Integer.MAX_VALUE,         
                false);
            nbrs.reportFrequent(d, 
                extension(m.outputFile, ".out"));
             */
        }
        mmed.searchAll();
        
        print("med: %s %s\n",
            mmed.best, // score
            Base4er.decode(mmed.bestKmer, mmed.k)
            );
    }
    
    
    //
    
    int n;
    int max;
    int k;

    int [] hammingBase2Low;
    int [] hammingBase2High;

    List<FastKmerSearchData> mmeds =
        new LinkedList<>();

    double [] kmers;
    
    double best = Double.MAX_VALUE;
    
    int bestKmer;
    
    //
    MotifMedian() {
        super();
        
    }
    
    void searchAll() { 
        n = mmeds.size();
        FastKmerSearchData d1 = mmeds.get(0);
        hammingBase2Low = d1.hammingBase2Low;
        hammingBase2High = d1.hammingBase2High;
        k = d1.k;
        max = (int) Math.pow(4, d1.k);
        
        // min score ??
        kmers = new double [max];
        int start = 0;
        int used = 0;
        int step = max / 8;
        while (true) {
         for (int i = start; i < max; i += step, ++used) {
            kmers[i] = scoreOne(i, used);
            if (kmers[i] < best) {
                best = kmers[i];
                bestKmer = i;
                print("best %s %s\n", bestKmer, best);
                if (best <= 1) {
                    break;
                }
            }
         }
         ++start;
         if (start >= max || used >= max) {
            break;
         }
        }
        println(listD(kmers, 1).toString());
    }
        
    
    double scoreOne(int kmer, int used) {
        double score = 0;
        for (int s = 0; s < n; ++s) {
            FastKmerSearchData d =
                mmeds.get(s);
            int min = Integer.MAX_VALUE;
            int best = 0;
            for (int ix = 0; ix < d.L - k + 1; ++ix) {
                int h = calcHamming(kmer, d.base4kmers[ix]);
                if (h < min) {
                    best = ix;
                    min = h;
                }
                if (h == 0) {
                    break; // shortcircuit
                }
            }
            score += min;
            if (min == 0 && s > n/2 && score < best) {
                print("score %d %d %d %s\n",
                    used, kmer, k, score);
            }
        }
        return score;
    }
    
    int count = 0;
    boolean debug = false;
    
    int calcHamming(int kmer, int kmerNeighbor) {
        int diff = kmer ^ kmerNeighbor;
        
        // different base4's?
        int diffBase4 = 0;
        for (int i = 0; i < k; ++i) {
            diffBase4 +=
                (hammingBase2High[i] & diff) > 0 
                ||
                (hammingBase2Low[i] & diff) > 0 
                ? 1 : 0;
        }
        if (debug && diff != 9 && ++count < 1000) {
            print("exact %s %s %s %d %d %d\n", 
                  diffBase4,
                  Base4er.decode(kmer, k), 
                  Base4er.decode(kmerNeighbor, k),
                  kmer, kmerNeighbor, diff);
        }
        return diffBase4;
    }
    /*
    void report() {
        
        TextFileUtil.writeKmersListPlus(
            extension(m.outputFile, ".out"),
            kmer);
    }
    */
    
    static FastKmerSearchData readSourceN(final String text, 
                                          final int k,
                                          final int hammingD)
    {
        print("\nText\n: %s %d\n",
              text, k);
        FastKmerSearchData d = new FastKmerSearchData(
            text, k, 
            text.length(), 0, hammingD);
        return d;
    }
    
    
    //
    
}

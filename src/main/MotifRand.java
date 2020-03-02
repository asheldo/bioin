package main;

import java.util.*;
import main.*;

public class MotifRand extends Processor
{
    private static int simulations = 100000;
    
    private static int stuck = 2999;
    
    private static int stuckMin = 999;
    
    private static int seed = 123456;
    
    Random rand = new Random(seed);
    
    public static void main(String[] args) throws Exception {
        FileInputs m = FileInputs.scanFileParamsAndSources(
            "3/motr1.txt");
        print("inputs: %s \n", m.params);
        if (m.params != null) {
            int k = Integer.parseInt(m.params.get(0));
            int t = Integer.parseInt(m.params.get(1));

            // START HERE
            println("Motif.greed");
            processMotifs(m, k);
        }
    }

    // e.g. 16 kmers in i and j, 2 out of 9 pos
    static void processMotifs(final FileInputs m,
                              final int k) throws Exception {

        MotifRand mg = new MotifRand((byte) 1); // d, reverseCompMis);   
        mg.read(m.sourceText, k);
        mg.initMatrix();

        checkpoint();
        Integer [] best = mg.searchAll(simulations);

        print("med: %s %s\n",
              mg.best, // score
              Base4er.decode(mg.bestKmer, mg.k)
              );

        TextFileUtil.writeKmersListPlus(
            m.outputFile,
            Base4er.decode(Arrays.asList(best), 
                           mg.k));
    }

    //
    int L;
    int t;
    int max;
    int k;

    int [] base2Low;
    int [] base2High;

    List<FastKmerSearchData> texts =
    new LinkedList<>();

    // double [] kmers;

    double best = Double.MAX_VALUE;
    int bestKmer;
    int [] bestMotifs;
    double score;

    byte laplace;
  

    //
    MotifRand(byte laplace) {
        super();
        this.laplace = laplace;
    }

    /** and convert kmer to int */
    void read(String [] sourceText, int k) {
        print("texts: %s",
              Arrays.asList(sourceText));
        for (int i = 0; i < sourceText.length; ++i) {
            String text = sourceText[i];
            if (text == null)
                break;

            FastKmerSearchData n = readSourceN(text, k);
            texts.add(n);
        }
        t = texts.size();
        print("texts: %s", texts);
    }

    void initMatrix() {
        FastKmerSearchData d1 = texts.get(0);
        L = d1.L;
        base2Low = d1.hammingBase2Low;
        base2High = d1.hammingBase2High;
        k = d1.k;
        max = (int) Math.pow(4, d1.k);
    }

    int randK() {
        return rand.nextInt(L - k);
    }
    
    Integer [] randMatrix() {
        Integer [] mat = new Integer [t];
        for (int i = 0; i < t; ++i) {
            mat[i] = this.texts.get(i)
                .base4kmers[randK()];
        }
        return mat;
    }

    /**
     BestMotifs ← motif matrix formed by first k-mers in each string from Dna
     for each k-mer Motif in the first string from Dna
     Motif1 ← Motif
     for i = 2 to t
     form Profile from motifs Motif1, …, Motifi - 1
     Motifi ← Profile-most probable k-mer in the i-th string in Dna
     Motifs ← (Motif1, …, Motift)
     if Score(Motifs) < Score(BestMotifs)
     BestMotifs ← Motifs
     */
    Integer [] searchAll(int sims) { 
        boolean debug = false;
        checkpoint();
        Integer [] motifData = randMatrix();
        MotifMatrix mm = MotifMatrix.create(
            motifData, t, k, k, laplace);
        MotifMatrix bestMm = mm;
        bestMm.scores();
        print("init %d: %s", simulations, bestMm);
              
        int trend = 0;
        int memory = 0;
        while (true) {
            mm = searchOne(motifData);
            mm.scores();
            --simulations;
            int diff = mm.scoreSum - bestMm.scoreSum;
            if (diff < 0) {
                memory = trend = 0; //reset
                bestMm = mm;
                print("next %d sim: %s", simulations, bestMm);
            } else if (diff == 0) {
                ++memory;
                print("indiff %d sim: %s == %s",
                        simulations , bestMm.scoreSum , mm);
            } else {
                ++trend;
                printif(debug, "noop: %s < %s",
                    bestMm.scoreSum , mm);
            }
            if (simulations == 0 
                || trend > stuck
                || memory > stuckMin) break;
            motifData = randMatrix();
            // mm = MotifMatrix.create( motifData, t, k, k, laplace);
            // mm.scores();
        }
        
        print("final %d @ %s: %s", simulations, 
            checkpoint(), bestMm);
        return bestMm.motifData;
    }
    
    int depth = 1;
    
    MotifMatrix searchOne(Integer [] motifData) {
        boolean debug = false;
        MotifMatrix mm = MotifMatrix.create(
            motifData,
            t, k, k, laplace);
        mm.scores();
        
        int i = 0;
        Integer [] d = new Integer [0];
        // FastKmerSearchData line1 = texts.get(0);
        // for (int i = 0; i < L - k + 1; ++i) 
        while (true) {
            ++i;
            List<Integer> motifs = new ArrayList<>();
            for (int j = 0; j < t; ++j) {
                // form Profile from motifs Motif1, …, Motifi - 1
                // Motifi ← Profile-most probable k-mer in the i-th string in Dna
                // score last, only profile others:
                motifs.add(mm.mostProbable(texts.get(j)));
            }
            
            MotifMatrix nextMm = MotifMatrix.create(
                motifs.toArray(d),
                t, k, k,
                laplace);
            nextMm.scores();
            if (nextMm.scoreSum < mm.scoreSum) {
                mm = nextMm;
                if (i > depth) {
                    depth = i;
                    print( "search %d next: %s", i, nextMm);
                }
            } else {
                printif(debug, "not: %s");
                break;
            }
        }
        return mm;
    }


    /*
     void report() {

     TextFileUtil.writeKmersListPlus(
     extension(m.outputFile, ".out"),
     kmer);
     }
     */

    static FastKmerSearchData readSourceN(final String text, 
                                          final int k)
    {
        print("\nText\n: %s %d\n",
              text, k);
        FastKmerSearchData d = new FastKmerSearchData(
            text, k, 
            text.length(), 0, 0);
        return d;
    }

}

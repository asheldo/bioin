package main;

import java.util.*;

public class MotifGibbs extends Processor
{
    private static int simulations = 1700,
            reps = 20;

    private static int stuck = 9999;

    private static int stuckMin = 9999;

    private static int seed = 999;

    Random rand = new Random(seed);

    public static void main(String[] args) throws Exception {
        FileInputs m = FileInputs.scanFileParamsAndSources(
            "3/motr1.txt");
        print("inputs: %s \n", m.params);
        if (m.params != null) {
            int k = Integer.parseInt(m.params.get(0));
            // int t = Integer.parseInt(m.params.get(1));

            // START HERE

            processMotifs(m, k);
        }
    }

    // e.g. 16 kmers in i and j, 2 out of 9 pos
    static void processMotifs(final FileInputs m,
                              final int k) throws Exception {

        MotifGibbs mg = new MotifGibbs((byte) 1); // d, reverseCompMis);   
        mg.read(m.sourceText, k);
        mg.initMatrix();

        checkpoint();
        Integer [] best;
        // if (m.options.contains("gibbs")) {
        print("Motif.gibbs %s %s\n",
                reps, simulations);
        // simulations = 1000
        MotifMatrix mm = mg.searchAll(reps);
        best = mm.motifData;
        int bestKmer = mm.consensusMotif;
       
        print("med %s of: %s\n",
              Base4er.decode(bestKmer, mg.k),
              Base4er.decode(Arrays.asList(best), 
                  mg.k));
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

    int bestKmer;
    int [] bestMotifs;
    double score;

    byte laplace;

    //
    MotifGibbs(byte laplace) {
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
        return rand.nextInt(L - k + 1);
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
    MotifMatrix searchAll(int sims) { 
        boolean debug = false;
        
        int trend = 0;
        int memory = 0;
        MotifMatrix bestMm = null;
        while (true) {
            checkpoint();
            Integer [] motifData = randMatrix();
            /*
            MotifMatrix mm = MotifMatrix.create(
                motifData, t, k, k, laplace);
            mm.scores();
            if (bestMm == null 
                || mm.scoreSum < bestMm.scoreSum) {
                    bestMm = mm;
            }
            printif(debug, "next init %d: %s", sims, bestMm);
            */
            MotifMatrix mm = searchOne(motifData);
            mm.scores();
            --sims;
            
            int diff = bestMm == null ? -1 : mm.scoreSum - bestMm.scoreSum;
            if (diff < 0) {
                memory = trend = 0; //reset
                bestMm = mm;
                print("next %d sim: %s\n", sims, bestMm);
            } else if (diff == 0) {
                ++memory;
                print("indiff %d sim: %s == %s\n",
                      simulations , bestMm.scoreSum , mm);
            } else {
                ++trend;
                printif(debug, "noop: %s < %s\n",
                        bestMm.scoreSum , mm);
            }
            if (sims == 0 
                    || trend > stuck
                    || memory > stuckMin) {
                printif(debug, "end %s %s\n", trend, memory);
                break;
            }
            // motifData = randMatrix();
            
        }

        print("final %d @ %s: %s\n", simulations, 
              checkpoint(), bestMm,
              Arrays.asList(bestMm.motifData));
        return bestMm;
    }

    int depth = 1;

    MotifMatrix searchOne(Integer [] motifData) {
        boolean debug = false;
        MotifMatrix mm = MotifMatrix.create(
            motifData,
            t, k, k, laplace);
        mm.scores();
        MotifMatrix bestMm = mm;
        int i = 0;
        Integer [] d = new Integer [0];
        // FastKmerSearchData line1 = texts.get(0);
        // for (int i = 0; i < L - k + 1; ++i) 
        while (bestMm.scoreSum > 0 && i < simulations) 
        {
            ++i;
            // (int j = 0; j < t; ++j) {
            // form Profile from motifs Motif1, …, Motifi - 1
            // Motifi ← Profile-most probable k-mer in the i-th string in Dna
            // score last, only profile others:

            double pr = rand.nextDouble();
            int kmersRowIndex = rand.nextInt(t);
            
            int [] kmersRow = texts.get(kmersRowIndex)
                .base4kmers;
            mm = mm.probDist(pr, kmersRowIndex, kmersRow);
            mm.scores();
            
            if (mm.scoreSum < bestMm.scoreSum) {
                bestMm = mm;
                if (i > depth) {
                    depth = i;
                    print("search %d next: %s", i, bestMm);
                }
            } else {
                printif(debug, "not: %s", bestMm.scoreSum);
                // if (--simulations == 0) break;
            } 
        } 
        return bestMm;
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

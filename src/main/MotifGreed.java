package main;

import java.util.*;

/**
 * https://stepik.org/lesson/159/step/5?unit=8217
 */
public class MotifGreed extends Processor
{
    public static void main(String[] args) throws Exception {
        FileInputs m = FileInputs.scanFileParamsAndSources(
            "3/motg1.txt");
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
        
        MotifGreed mg = new MotifGreed((byte) 1); // d, reverseCompMis);   
        mg.read(m.sourceText, k);
        mg.initMatrix();
        
        checkpoint();
        Integer [] best = mg.searchAll();
        
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
    MotifGreed(byte laplace) {
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
    
    Integer [] firstsMatrix() {
        Integer [] mat = new Integer [t];
        for (int i = 0; i < t; ++i) {
            mat[i] = this.texts.get(i).base4kmers[0];
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
    Integer [] searchAll() { 
        boolean debug = false;
        Integer [] bestKmers = firstsMatrix();
        MotifMatrix bestMm = MotifMatrix.create(
            bestKmers,
            t, k, k, laplace);
        int bestScore = sum(bestMm.scores());
        
        print("init mm:\n %s %s %s %s \n", 
              Base4er.decode(bestMm.consensusMotif, k),
              bestScore,
              listD(bestMm.scores(), 1),
              Arrays.asList(Base4er.decode(
                  Arrays.asList(bestKmers), k)));
        
        Integer [] d = new Integer [0];
        FastKmerSearchData line1 = texts.get(0);
        for (int i = 0; i < L - k + 1; ++i) {
            List<Integer> motifs = new ArrayList<>();
            motifs.add(line1.base4kmers[i]);
            
            for (int j = 1; j < t; ++j) {
                // form Profile from motifs Motif1, …, Motifi - 1
                // Motifi ← Profile-most probable k-mer in the i-th string in Dna
                // score last, only profile others:
                Integer[] kmers = motifs.toArray(d);
                MotifMatrix mm = MotifMatrix.create(
                    kmers,
                    j, k, k, laplace);
                motifs.add(mm.mostProbable(texts.get(j)));
            }
            Integer[] kmers = motifs.toArray(d);
            MotifMatrix mm = MotifMatrix.create(
                kmers,
                t, k, k,
                laplace);
            int score = sum(mm.scores());
            if (score < bestScore) {
                bestScore = score;
                bestMm = mm;
                bestKmers = kmers;
            }
            
            printif(debug,"next mm:\n %s %s %s %s \n", 
                  Base4er.decode(mm.consensusMotif, k),
                  score,
                  listD(mm.scores(), 1),
                  Arrays.asList(Base4er.decode(
                      Arrays.asList(kmers), k)));
            
        }
        print("final mm:\n %s %s %s %s \n", 
              Base4er.decode(bestMm.consensusMotif, k),
              bestScore,
              listD(bestMm.scores(), 1),
              Arrays.asList(Base4er.decode(
                  Arrays.asList(bestKmers), k)));
        return bestKmers;
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

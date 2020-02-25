package main;
import java.util.stream.*;
import java.util.*;
import java.util.function.*;

// long version
public class MotifMatrix extends Processor
{
    
    /*
     When:

       b^y = x

     Then the base b logarithm of a number x:

      logb (x) = y
     
     Entropy is a measure of the uncertainty 
     of a probability distribution (p1, …, pN), 
     and is defined as follows:

     H(p_1, \ldots, p_N) = -\sum_{i=1}^{N}{p_i · \log_{2}{p_i}}

     For example, the entropy of the probability distribution (0.2, 0.6, 0.0, 0.2) corresponding to the 2nd column of the NF-κB profile matrix is

     -(0.2 \log_{2}{0.2} + 0.6\log_{2}{0.6} + 0.0\log_{2}{0.0} + 0.2\log_{2}{0.2}) \approx 1.371
≈1.371
     whereas the entropy of the more conserved final column (0.0, 0.6, 0.0, 0.4) is

     -(0.0 \log_2{0.0} + 0.6\log_2{0.6} + 0.0\log_2{0.0} + 0.4\log_2{0.4}) \approx 0.971
   )≈0.971
     and the entropy of the very conserved 5th column (0.0, 0.0, 0.9, 0.1) is

     -(0.0 \log_2{0.0} + 0.0\log_2{0.0} + 0.9\log_2{0.9} + 0.1\log_2{0.1}) \approx 0.467
     )≈0.467
     Note: Technically, log2(0) is undefined, but in the computation of entropy, we assume that 0 · log2(0) is equal to 0.
     
     The information entropy, often just entropy,
     is a basic quantity in information theory
     associated to any random variable, which can
     be interpreted as the average level of 
     "information", "surprise", or "uncertainty"
     inherent in the variable's possible outcomes. 
     The concept of information entropy was 
     introduced by Claude Shannon in his 1948 paper
     "A Mathematical Theory of Communication".[1]

     The entropy is the expected value of the 
     self-information, a related quantity also
     introduced by Shannon. The self-information
     quantifies the level of information or 
     surprise associated with one particular
     outcome or event of a random variable,
     whereas the entropy quantifies how 
     "informative" or "surprising" the entire
     random variable is, averaged on all its 
     possible outcomes.
    */
    // ( - logb( P(xi) ) ) == I(xi)
    // E == E(I(xi))
    /*
    Shannon's definition is basically unique 
    in that it is the only such one that has 
    certain properties: it is determined 
    entirely by the probability distribution
    of the data source, it is additive for 
    independent sources, it is maximized at 
    the uniform distribution, it is minimized 
    (and equal to zero) when there is 100% 
    probability of only one event occurring, 
    and it obeys a certain derived version of 
    the chain rule of probability. Axiomatic 
    derivations of entropy are explained 
    further below on the page.
    */

    public static void main(String [] args) {
        String [] motifs = new String [] {
        "TCGGGGGTTTTT",
        "CCGGTGACTTAC",
        "ACGGGGATTTTC",
        "TTGGGGACTTTT",
        "AAGGGGACTTCC",
        "TTGGGGACTTCC",
        "TCGGGGATTCAT",
        "TCGGGGATTCCT",
        "TAGGGGAACTAC",
        "TCGGGTATAACC"
        };
        
        // m.sourceText = motifs;
        MotifMatrix mm = create(motifs);
        println("e, m, p, s");
        print("%s\n s\n s\n s\n%s\n",
            listD(mm.entropies, 10000),
            /*
            Base4er.decode(mm.consensusMotif, mm.k),
            Arrays.asList(mm.positions),
            Arrays.asList(mm.positionScores),
            */
            ""
            );

        
    }
    
    static MotifMatrix create(String [] motifs) {
        MotifMatrix mm = new MotifMatrix(motifs);
        mm.init();
        
        return mm;
    }
    
    final int n;
    final int L;
    final int k;
    // t = # of moTifs
    final FastKmerSearchData [] motifData;

    // Kmer Data, n x k
    long [][] motifsMatrix;

    // byte only gets you to 256, short 256^2
    int [] positionScores; 
    int [] positions;
    
    int [][] tCounts; // t x k = [4][]

    // could be int if we multiply by t again
    double [][] profiles; // note ceiling(t/4)

    double [] entropies;
    
    int consensusMotif;
    
    
    MotifMatrix(String [] motifs) {
        n = motifs.length;
        L = k = motifs[0].length();
        motifData = new FastKmerSearchData [n];
        for (int i = 0; i < motifs.length; ++i) {
            String kmer = motifs[i];
            FastKmerSearchData d = new FastKmerSearchData(
                kmer, k, k, 0, false
            );
            motifData[i] = d;
        }
    }
    
    void init() {
        this.tCounts = calcCounts();
        this.profiles = calcProfiles();
        this.positionScores = calcScores();
        this.entropies = calcEntropies();
        
    }


    /*
    // e.g. 16 kmers in i and j, 2 out of 9 pos
    void processMotifs(FileInputs m,
                              final int k,
                              final int hammingD) throws Exception {
        final String kmer = null;
        final int ct = m.sourceText.length;
        for (int i = 0; i < ct; ++i) {
            String text = m.sourceText[i];


            FastKmerSearchData d = readSourceN(text,
                                               k, hammingD);
            checkpoint();
            Motif nbrs = getKmerNeighbors(m.outputFile, 
                                          d, Integer.MAX_VALUE,         
                                          false, true);

            nbrs.reportFrequent(d, 
                                extension(m.outputFile, ".out"));
        }
        TextFileUtil.writeKmersListPlus(
            extension(m.outputFile, ".out"),
            kmer);
    }
    */
    

    /**/

    /*
     Finally, we form a consensus string, 
     denoted Consensus(Motifs), from the 
     most popular letters in each column 
     of the motif matrix. If we select 
     Motifs correctly from the collection 
     of upstream regions, then
     Consensus(Motifs) provides an ideal 
     candidate regulatory motif for these 
     regions. For example, the consensus 
     string for the NF-κB binding sites 
     in the figure below is TCGGGGATTTCC.
     */
   

    double [] calcEntropies() {
        // - p log p
        double [] entropies = new double[k];
        for (int i = 0; i < k; ++i) {
            for (int b = 0; b < Base4er.BASE; ++b) {
                double p = profiles[i][b];
                entropies[i] -= p > 0
                    ? p * Math.log(p) : 0;
            }
        }
        return entropies;
    }
    
     
    int [] calcScores() {
        // count position mismatches
        int [] scores = new int[k];
        for (int i = 0; i < k; ++i) {
            Map<Double,Integer> max = new TreeMap<>();
            for (int b = 0; b < Base4er.BASE; ++b) {
                scores[i] += b == positions[i] ? tCounts[i][b] : 0;
            }
        }
        return scores;
    }
    
    double [][] calcProfiles() {
        positions = new int[k];
        double [][] probs = new double [k][Base4er.BASE];
        Integer [][] pp = Base4er.pow4Permutations;
        int off = pp.length - k;
        int consensus = 0;
        for (int i = 0; i < k; ++i) {
            Map<Double,Integer> max = new TreeMap<>();
            for (int b = 0; b < Base4er.BASE; ++b) {
                probs[i][b] = (double) tCounts[i][b] / n;
                max.put(probs[i][b], b);
            }
            
            Integer [] ppk = pp[16 - off - 1];
            int mx = new LinkedList<Integer>(max.values())
                .get(max.size() - 1);
            positions[i] = mx;
            consensus += ppk[mx];
        }
        return probs;
    }
    
    int [][] calcCounts() {
        int [][] counts = new int [k][Base4er.BASE];
        Integer [][] pp = Base4er.pow4Permutations;
        int off = pp.length - k;
        for (FastKmerSearchData d : this.motifData) {
            int kmer = d.base4kmers[0];
            for (int i = 0; i < k; ++i) {
                Integer [] ppk = pp[16 - off - 1 - i];
                for (int b = 0; b < Base4er.BASE; ++b) {
                    if ((kmer & ppk[b]) == ppk[b]) {
                        ++counts[i][b]; // a
                    }
                }
            }
        }
        return counts;
    }
    
    /*
    boolean exactHamming(FastKmerSearchData d, 
                         int kmer, int kmerNeighbor) {
        int diff = kmer ^ kmerNeighbor;
        if (debug && diff != 9 && ++count < 1000) {
            print("exact %s %s %d %d %d\n", 
                  Base4er.decode(kmer, d), 
                  Base4er.decode(kmerNeighbor, d),
                  kmer, kmerNeighbor, diff);
        }
        // different base4's?
        int diffBase4 = 0;
        for (int i = 0; i < d.k; ++i) {
            diffBase4 +=
                (d.hammingBase2High[i] & diff) > 0 
                ||
                (d.hammingBase2Low[i] & diff) > 0 
                ? 1 : 0;
        }
        return diffBase4 == d.hamming;
    } */
    
    /*
     For the general case of tt motifs of 
     length n the maximum score is given 
     by:
     (t−ceiling(t/4))∗n 
     */

    //
    
}

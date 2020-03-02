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
        MotifMatrix mm = create(motifs);
        List<Double> entropiesD =
            listD(mm.entropies, 10000);
        double eSum = sum(mm.entropies);
        
        println("eSum, len, e, cM, pos, sco -> matrices");
        print("%s \n%s \n %s \n%s \n%s \n%s\n",
            eSum,
            mm.entropies.length,
            entropiesD,
            Base4er.decode(mm.consensusMotif, mm.k),
            listD(mm.positions, 1),
            listD(mm.positionScores, 1)
            );
        printMatrices(mm);
    }
    
    static void printMatrices(MotifMatrix mm) {
        /*for (String s : mm.motifs) {
            String r = s.replaceAll("(\\w)", "$1 ");
            System.out.println(r);
        }*/
        println("cts");
        for (int [] c : mm.tCounts) {
            print("%s\n", listD(c, 1));
        }
        println("profiles");
        for (double [] p : mm.profiles) {
            print("%s\n", listD(p, 10000));
        }
        double e =  -(8*0.1*log2(0.1) + 4*0.2*log2(0.2) + 2*0.3*log2(0.3) + 3*0.4*log2(0.4) + 1*0.5*log2(0.5) + 2*0.6*log2(0.6) + 2*0.7*log2(0.7) + 1*0.8*log2(0.8) + 3*0.9*log2(0.9));
        print("%s\n", e);
    }
    
    static double log2(double p) {
        return Base4er.log2(p);
    }
    
    static double sum(double [] d) {
        double dSum = 0;
        for (double x : d) {
            dSum += x;
        }
        return dSum;
    }
    
    // and calc profile probs
    static MotifMatrix create(String [] motifs) {
        MotifMatrix mm = new MotifMatrix(motifs);
        mm.laplace = 0;
        mm.init();
        return mm;
    }
    
    // and calc profile probs with laplace 1
    static MotifMatrix create(Integer [] motifData,
                              int t, int L, int k,
                              byte laplace) {
        MotifMatrix mm = new MotifMatrix(t, L, k, motifData);
        mm.laplace = laplace;
        mm.init();
        return mm;
    }
    
    final int n;
    final int L;
    final int k;
    
    // t = # of moTifs
    final Integer [] motifData;

    // Kmer Data, n x k
    // long [][] motifsMatrix;

    // byte only gets you to 256, short 256^2
    private int [] positionScores; 
    int scoreSum;
    
    int [] positions;
    
    int [][] tCounts; // t x k = [4][]

    // could be int if we multiply by t again
    double [][] profiles; // note ceiling(t/4)

    private double [] entropies;
    
    int consensusMotif;
    
    byte laplace;
    
    // final String [] motifs;
    
    MotifMatrix(String [] motifs) {
        // this.motifs = motifs;
        n = motifs.length;
        L = k = motifs[0].length();
        motifData = new Integer [n];
        for (int i = 0; i < motifs.length; ++i) {
            String kmer = motifs[i];
            FastKmerSearchData d = new FastKmerSearchData(
                kmer, k, k, 0, false
            );
            motifData[i] = d.base4kmers[0];
        }
    }
    
    MotifMatrix(final int n, final int k, final int L, 
                final Integer [] motifData) {
        
        this.n = n; // motifs.length;
        this.L = L; // = k = motifs[0].length();
        this.k = k;
        this.motifData = motifData; // new FastKmerSearchData [n];
    }
    
    void init() {
        this.tCounts = calcCounts();
        this.profiles = calcProfiles();
    }
    
    public int [] scores() {
        if (positionScores == null) {
            positionScores = calcScores();
            scoreSum = sum(positionScores);
        }
        return positionScores;
    }
    
    public double [] entropies() {
        if (entropies == null) {
            entropies = calcEntropies();
        }
        return entropies;
    }
    /*
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
                // if (b != positions[i]) 
                double p = profiles[i][b];
                entropies[i] -= (p * Base4er.log2(p));
                print("%s %s \n", p, listD(entropies, 100));
            }
        }
        return entropies;
    }
    
    /** 
     * effectively: 
     * hamming distances for aggregated cokumn data
     */
    int [] calcScores() {
        // count position mismatches, i.e. minimize
        int [] scores = new int[k];
        for (int i = 0; i < k; ++i) {
            for (int b = 0; b < Base4er.BASE; ++b) {
                scores[i] += b != positions[i] 
                    ? tCounts[i][b] : 0;
            }
        }
        return scores;
    }
    
    double [][] calcProfiles() {
        positions = new int[k];
        double [][] probs = new double [k][Base4er.BASE];
        Integer [][] pp = Base4er.pow4Permutations;
        consensusMotif = 0;
        for (int i = k - 1; i >= 0; --i) {
            int ki = k - i - 1;
            Map<Double,Integer> max = new TreeMap<>();
            for (int b = 0; b < Base4er.BASE; ++b) {
                if (laplace > 0) {
                    probs[ki][b] = 
                        (double) (tCounts[ki][b] + 1) 
                        / (n + Base4er.BASE);
                } else {
                    probs[ki][b] = 
                        (double) tCounts[ki][b] / n;
                }
                max.put(probs[ki][b], b);
            }
            Integer [] ppk = pp[i];
            int mx = new LinkedList<Integer>(max.values())
                .get(max.size() - 1);
            positions[ki] = mx;
            consensusMotif += ppk[mx];
        
        }
        return probs;
    }
    
    int [][] calcCounts() {
        int [][] counts = new int [k][Base4er.BASE];
        Integer [][] pp = Base4er.pow4Permutations;     
        int n = 0;
        for (int kmer : this.motifData) {
            // int kmer = d.base4kmers[0];
            // printif(n == 0, "ppk for %s", kmer); 
            for (int i = k - 1; i >= 0; --i) {
                Integer [] ppk = pp[i];//   pp[16 - off - 1 - i];
                // printif(n == 0, "%s \n", Arrays.asList(ppk));
                int ki = k - i - 1;
                for (int b = Base4er.BASE - 1; b >= 0; --b) {
                    if ((kmer & ppk[b]) == ppk[b]) {
                        ++counts[ki][b]; // a
                        // printif(n == 0, "%s \n", b);
                        break;
                    }
                }
            }
            ++n;
        }
        return counts;
    }
    
    // must init first
    int mostProbable(FastKmerSearchData d) {
        double maxProb = -1.0;
        int most = 0;
        for (int i = 0; i < d.L - d.k + 1; ++i) {
            double p = 1.0;
            int kmer = d.base4kmers[i];
            for (int ix = d.k - 1; ix >= 0; --ix) {
                Integer [] pix = Base4er.pow4Permutations[ix];
                for (int b = 3; b >= 0; --b) {
                    int ppk = pix[b];
                    if ((ppk & kmer) == ppk) {
                        p *= profiles[d.k - ix - 1][b];
                        break;
                    }
                }
                if (p == 0) {
                    break;
                }
            }
            if (p > maxProb) {
                maxProb = p;
                most = kmer;
            }
        }
        return most;
    }

    @Override
    public String toString() {
        return String.format("mm:\n %s %s %s %s \n", 
              Base4er.decode(consensusMotif, k),
              scoreSum,
              listD(positionScores, 1),
              Arrays.asList(Base4er.decode(
                  Arrays.asList(motifData), k)));
    }
   
    
/*
    double scoreOne(int kmer, int used) {
        double score = 0;
        for (int s = 0; s < t; ++s) {
            FastKmerSearchData d =
                texts.get(s);
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
            if (min == 0 && s > t/2 && score < best) {
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
                (base2High[i] & diff) > 0 
                ||
                (base2Low[i] & diff) > 0 
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
    */
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

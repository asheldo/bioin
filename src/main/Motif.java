package main;

import java.util.*;
import main.*;

public class Motif extends Neighbors // Processor
{
    static boolean debug = false;

    public static void main(String[] args) throws Exception {
        FileInputs m = FileInputs.scanFileParamsAndSources(
            "Salmonella_enterica.txt");
        print("inputs: %s \n", m);
        int k = 9;
        int hammingD = 2;
        if (m.params != null) {
            try {
                k = Integer.parseInt(m.params.get(0));
                if (m.params.size() > 1) {
                    hammingD = Integer.parseInt(m.params.get(1));
                }
            } catch (Exception e) {
                // e.printStackTrace(System.err);
            }
        }
        {
            // START HERE
            println("Motif.processKmerMotif w/o rev compl");
            processMotifs(m, k, hammingD);
        }
    }

    // Variants of factory method
    static FastKmerSearchData readSourceN(final String text, 
                                         final int k,
                                         final int hammingD)
    {
        print("\nText/n: %s/%d\n",
            text, k);
        FastKmerSearchData d = new FastKmerSearchData(
            text, k, 
            text.length(), 0, hammingD);
        return d;
    }

    // e.g. 16 kmers in i and j, 2 out of 9 pos
    static void processMotifs(FileInputs m,
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
    

    // 1
    static Motif getKmerNeighbors(final String outputFile, 
                                  final FastKmerSearchData d,
                                  final int limit, // KmerSearch searchTODO,
                                  final boolean exactDistance,
                                  final boolean reverseCompMis) throws Exception {
        Motif nbrs = new Motif(d, reverseCompMis);
        for (int ix = 0; ix < limit
             && ix <= d.len - d.k; ++ix) {
            Integer kmer = d.base4kmers[ix];
            if (debug) {
                print("test: %s %s\n", 
                      Base4er.decode(kmer, d),
                      nbrs.test(kmer));
            }
            final Collection<Integer> neighbors
                = new HashSet<>();
            // 
            nbrs.nextKmerRelatives(kmer, 
                                   0, 0, neighbors, exactDistance);
            for (int kmerI : neighbors) {
                ++d.clumpCount[kmerI];
            }
            if (reverseCompMis) {
                neighbors.clear();
                int reverseComplement = Base4er.reverseComplementOf(kmer, d);
                nbrs.nextKmerRelatives(reverseComplement,
                                       0, 0, neighbors, 
                                       // true); 
                                       exactDistance);
                for (int kmerI : neighbors) {
                    ++d.clumpCount[kmerI];
                }                       
            }

            if (debug) {
                String [] decoded = Base4er.decode(neighbors, d);
                print("process nbrs: %s \n%s \n%s \n", 
                      kmer, 
                      Arrays.asList(neighbors),
                      Arrays.asList(decoded));
            }
            // report(d, outputFile, decoded); // kmers.toArray(s));
        }
        return nbrs;
    }

    //
   
    Motif(FastKmerSearchData d, boolean reverseCompMis) {
        super(d, reverseCompMis);
    }
    
    void reportFrequent(FastKmerSearchData d, 
                        String outputFile) throws Exception {
        print("t(process): %d\n", 
              checkpoint());
        int max = 1;
        Map<Integer,List<String>> counts 
            = new LinkedHashMap<>();
        for (int ix = 0; ix < d.clumpCount.length; ++ix) {
            int ct = d.clumpCount[ix];
            if (ct > max) {
                max = ct;
            }
        }
        boolean debug = true && max < 300;

        List<String> kmers = new LinkedList<>();
        for (int ix = 0; ix < d.clumpCount.length; ++ix) {
            int ct = d.clumpCount[ix];
            if (ct > Math.max(0, max - 3)) {
                String kmer = Base4er.decode(ix, d);
                if (ct == max) {
                    kmers.add(kmer);
                }
                //
                if (debug) {
                    if (null == counts.get(ct)) {
                        counts.put(ct, new LinkedList<String>());
                    }
                    counts.get(ct).add(kmer);
                }
            }
        }
        String list = kmers.toString()
            .replaceAll("[,\\[\\]]", "");
        print("kmers max: %s", list);
        
    }

    
}

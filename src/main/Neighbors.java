package main;

import java.util.*;

public class Neighbors extends Processor
{
    public static void main(String[] args) throws Exception {
        FileInputs m = FileInputs.scanFileInputs();
        print("inputs: %s \n", m);
        int k = 9;
        int hammingD = 2;
        if (m.param1 != null) {
            String [] params = 
                m.param1.trim().split(" ");
            k = Integer.parseInt(params[0]);
            if (params.length > 1) {
                hammingD = Integer.parseInt(params[1]);
            }
        }
        FastKmerSearchData d = new FastKmerSearchData(
            m.sourceText0,
            k, 
            m.sourceText0.length(),
            0,
            hammingD);
        KmerSearch search = KmerSearch.KmerSearchFactory
            .create(m.options);
        Neighbors nb = process(d, search);
        nb.analyzeAndReport(d, search, m.outputFile);
	}
    
    static Neighbors process(final FastKmerSearchData d,
                             final KmerSearch searchTODO) {
        Neighbors nbrs = new Neighbors(d);
        for (int ix = 0; ix <= d.len - d.k; ++ix) {
            Integer kmer = d.base4kmers[ix];
            // print("test: %s \n", nbrs.test(kmer));
    
            // todo: keep these in window!
            Integer [] neighbors 
                = nbrs.nextKmerInclRelatives(kmer);
            print("nbrs: %s \n%s \n%s \n", kmer, 
                Arrays.asList(neighbors),
                Arrays.asList(Base4er.decode(neighbors, d)));
            for (int kmerI : neighbors) {
                ++d.clumpCount[kmerI];
            }
        }
        return nbrs;
    }
    
 
    final FastKmerSearchData d;
    
    final int permuts;
    
    final int permsSize;
    
    final Integer [][] placePermuts
        = Base4er.pow4Permutations; // excludes implicit places' zeroes
    
 
    Neighbors(FastKmerSearchData d) {
        this.d = d;
        int o = FastKmerSearchData.OBJECTS; // 4 bases
        // 2^4=16
        permuts = (int) Math.pow(o, d.hamming); // 4^2 or 4^1
        permsSize = calcSize();
        print("permSize pp: %s %s %s %s \n", permsSize,
              Arrays.asList(placePermuts[0]),
              Arrays.asList(placePermuts[1]),
              Arrays.asList(placePermuts[2]));
        //  int [][] places = new int [d.k][4];
    }
    
    // move?
    private int calcSize() {
        int s = 0; 
        // 9 .. 1
        for (int i = 1; i < d.k; ++i) {
            for (int j = i; j <= d.k; ++j) {
                s += permuts; // 8*16..1*16
                
            }    
            if (d.hamming == 1) {
                break;
            }
        }
        return s;
    }
    
    Object test(int kmer) {
        final Integer [] iPlace = placePermuts[0];
        final Integer [] jPlace = placePermuts[0];
        int kmerZero = zeroPlaces(
            zeroPlaces(kmer, iPlace), jPlace);
            
        
        return list(new Integer []
            { kmer, kmerZero, zeroPlaces(kmer, iPlace)});
    }
    
    // todo ... bit math
    Integer [] nextKmerInclRelatives(int kmer) {
        final Integer [] kPerms = new Integer [permsSize];
        
        int kIx = 0;
        final int base = Base4er.BASE;
        // r to l
        for (int i = 0; i < d.k; ++i) {
            final Integer [] iPlace = placePermuts[i]; // e.g. @16:12,8,4,0 
            int start = d.hamming == 1 ? i : i + 1;
            for (int j = start; j < d.k; ++j) {
                // time for 2 bit math !?!
                final Integer [] jPlace = placePermuts[j];
                int kmerZero = zeroPlaces(
                    zeroPlaces(kmer, iPlace), jPlace);
                    
                print("zero: %s %s\n", kmerZero, 
                    Base4er.decode(kmerZero, d));
                for (int b = 0; b < base ; ++b) {
                    for (int bb = 0; bb < base ; ++bb) {
                        kPerms[kIx++] 
                            = permute(kmerZero, iPlace[b], jPlace[bb] );
                    }
                    if (d.hamming == 1) {
                        break;
                    }
                }
            }
            if (d.hamming == 1) {
                break;
            }
        }        
        return kPerms;
    }
    
    int permute(int kmerZero, int kmerIPlace, int kmerJPlace) {
        return (kmerZero | kmerIPlace) | kmerJPlace;
    }
    
    // before permute
    int zeroPlaces(int kmer, Integer [] place) {
        for (int i : place) {
            if (i > 0 && (kmer & i) == i) {
                return kmer - i;
            } 
        }
        return kmer; // Zero in place
    }
    
    // e.g. 16 kmers in i and j, 2 out of 9 pos
    
    static void analyzeAndReport(final FastKmerSearchData d,
                          final KmerSearch searchTODO,
                          final String outputFile) throws Exception {
        // todo Use Clump Count !
        List<String> kmers = new LinkedList<>();
        int n = 0;
        for (int c : d.clumpCount) {
            if (c >= d.clumpThreshold) {
                kmers.add(Base4er.decode(n, d));
            }
            if (++n >= d.len || kmers.size() > 200) {
                break;
            }
        }
        report(d, outputFile, kmers);
    }

    static void report(final FastKmerSearchData d,
                       final String outputFile,
                       final List<String> kmers) throws Exception {
        print("top %d k-mers: \n%s \n", kmers.size(), kmers);
        print("first %d any-mers: \n%s \n", d.countKmers.size(), d.countKmers);
        Integer [] arrCounts = Arrays.copyOfRange(d.counts,
                                                  0, Math.max(100, d.counts.length));
        print("first %d position counts: %s\n", arrCounts.length,
              Arrays.asList(arrCounts));

        TextFileUtil.writeKmersListPlus(
            outputFile, // *.out in assets
            kmers, // frequent-est k-length patterns
            // d.countKmers.get(kmers.get(0)), // frequency
            kmers.size(), // 
            d.countKmers.size(), // total # k-mers
            d.countKmers);

	}

}
    /*
    public static class Base2xKHamming extends Processor {
        // 0 out of k is best
        static int hammingDist(final int k, 
                               final int kmerI, 
                               final int kmerJ, 
                               final int mismatches) {
            
            int kBase2 = k * 2;
            // int x = k * mismatches;

            int kmerAnd = kmerI & kmerJ;
            int diffBase2 = kBase2
                - Integer.bitCount(kmerAnd);
            switch (diffBase2) {
                case 0:
                    return 0; // == case
                case 1:
                    return 1; // 
                default:
                    return maskAndRotate(k, kmerAnd, diffBase2);
            }
        }
        
        static int maskAndRotate(int k, int kmerAnd, int diffBase2) {
            
        }
    } 
    
        // old str way
        static int hammingDist(int k,
                                      CharSequence kmerI, 
                                      CharSequence kmerJ,
                                      int mismatches) {
            int d = 0;
            for (int i = 0; d < k && d <= mismatches 
                 && i < kmerJ.length(); ++i) {
                if (kmerI.charAt(i) != kmerJ.charAt(i)) {
                    ++d;
                }
            }
            return d;
        }
    */

/*
Neighbors(Pattern, d)
        if d = 0
            return {Pattern}
        if |Pattern| = 1 
            return {A, C, G, T}
        Neighborhood ← an empty set
        SuffixNeighbors ← Neighbors(Suffix(Pattern), d)
        for each string Text from SuffixNeighbors
            if HammingDistance(Suffix(Pattern), Text) < d
                for each nucleotide x
                    add x • Text to Neighborhood
            else
                add FirstSymbol(Pattern) • Text to Neighborhood
        return Neighborhood
*/

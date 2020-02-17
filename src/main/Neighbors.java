package main;

import java.util.*;

public class Neighbors extends Processor
{
    static boolean debug = true;
    
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
        if (m.options.contains("exact")) {
            println("processFirstKmerNeighborDetail exact");
            Neighbors nb = processKmerNeighbors(
                extension(m.outputFile, "_exact.out"), 
                d, 1, true);
        } else if (m.options.contains("neighbors")) {
            println("processFirstKmerNeighborDetail first");
            Neighbors nb = processKmerNeighbors(m.outputFile, 
                d, 1, false);
        } else if (m.options.contains("all")) {
            println("processFirstKmerNeighborDetail all");
            checkpoint();
            Neighbors nb = processKmerNeighbors(m.outputFile, 
                d, Integer.MAX_VALUE, false);
            nb.reportFrequentWordsWithMismatches(d, 
                m.outputFile);
        } else {
            Neighbors nb = process(d, search);
            nb.analyzeAndReport(d, search, m.outputFile);
        }
	}
    
    static void reportFrequentWordsWithMismatches(FastKmerSearchData d, 
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

        List<String> kmers = new LinkedList<>();
        for (int ix = 0; ix < d.clumpCount.length; ++ix) {
            int ct = d.clumpCount[ix];
            if (ct > Math.min(1, max - 3)) {
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

        TextFileUtil.writeKmersListPlus(
            outputFile, // *.out in assets
            max,
            kmers, // frequent-est k-length patterns
            // d.countKmers.get(kmers.get(0)), // frequency
            kmers.size(),
            counts);
    }
    
  
    // Variants of Neighbors factory method
    
    // 1
    static Neighbors processKmerNeighbors(final String outputFile, 
                                          final FastKmerSearchData d,
                                          final int limit, // KmerSearch searchTODO,
                                          final boolean exactDistance) throws Exception {
        Neighbors nbrs = new Neighbors(d);
        for (int ix = 0; ix < limit
                && ix <= d.len - d.k; ++ix) {
            Integer kmer = d.base4kmers[ix];
            if (debug) {
                print("test: %s %s\n", 
                    Base4er.decode(kmer, d),
                    nbrs.test(kmer));
            }
            // Integer [] neighbors = nbrs.nextKmerInclRelatives(kmer);
            // fix:
            final Collection<Integer> neighbors
                = new LinkedHashSet<>(); // Integer [nbrs.permsSize];
            
            nbrs.nextKmerRelatives(kmer, 
                0, 0, neighbors, exactDistance);
            for (int kmerI : neighbors) {
                ++d.clumpCount[kmerI];
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
    
    // many - broken?
    static Neighbors process(final FastKmerSearchData d,
                             final KmerSearch searchTODO) {
        Neighbors nbrs = new Neighbors(d);
        for (int ix = 0; ix <= d.len - d.k; ++ix) {
            Integer kmer = d.base4kmers[ix];
            print("test: %s \n", nbrs.test(kmer));
            // neighbors = nbrs.nextKmerInclRelatives(kmer);
            final List<Integer> neighbors
                = new LinkedList<>(); // Integer [nbrs.permsSize];
            nbrs.nextKmerRelatives(kmer, 
                0, 0, neighbors, false);
            print("nbrs: %s \n%s \n%s \n", kmer, 
                neighbors,
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
    
    // recursive
    void nextKmerRelatives(final int kmer, 
                           final int depth,
                           final int permute,
                           final Collection<Integer> kPerms,
                           final boolean exactDistance) {
        final int base = Base4er.BASE;
        // r to l is fine
        for (int i = depth; i < d.k; ++i) {
            int checkCt = 0;
            Set<Integer> check = new HashSet<>();
            final Integer [] iPlace = placePermuts[i]; // e.g. @16:12,8,4,0   
            int permuteI = (depth == 0)
                ? zeroPlaces(kmer, iPlace)
                : zeroPlaces(permute, iPlace);
            if (false && debug && depth == 0) {
                print("zero: %d %d %s %s %s\n", 
                    depth, 
                    i,
                    Arrays.asList(iPlace),
                    permute, 
                    Base4er.decode(permuteI, d));
            }
            for (int b = 0; b < base ; ++b) {
                // todo check an int array first?
                int permuteIB = permuteBase4(permuteI, 
                    iPlace[b]);
                // add at ANY depth? not just hamming?
                if (!exactDistance || 
                       (depth + 1 == d.hamming
                        && exactHamming(d, kmer, permuteIB))) {
                    kPerms.add(permuteIB);
                    check.add(permuteIB);
                    checkCt++;
                }
                if (depth + 1 < d.hamming) {
                    // recur
                    nextKmerRelatives(kmer, 
                        i + 1,             
                        permuteIB, 
                        kPerms,
                        exactDistance);
                }
            }
            if (false && debug && kPerms.size() < 500)
                print("ct: %d %d\n", check.size(), checkCt);
        }
    }
    
    int count = 0;
    
    // 
    
    int permuteBase4(int kmerZero, int ... kmerPlace) {
        int perm = kmerZero;
        for (int p : kmerPlace) {
            perm |= p;
        }
        return kmerZero + kmerPlace[0];
    }

    // before permute
    int zeroPlaces(int kmer, Integer [] place) {
        int perm = kmer;
        for (int p : place) {
            if ((perm & p) > 0) {
                perm -= p;
            }
        }
        return perm;
    }
    
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

    static void report(final FastKmerSearchData d,
                       final String outputFile,
                       final String [] kmers) throws Exception {
        Integer [] arrCounts = Arrays.copyOfRange(d.counts,
                                                  0, Math.max(100, d.counts.length));
        print("first %d position counts: %s\n", arrCounts.length,
              Arrays.asList(arrCounts));

        TextFileUtil.writeKmersListPlus(
            outputFile, // *.out in assets
            kmers // frequent-est k-length patterns
            // d.countKmers.get(kmers.get(0)), // frequency
            );

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

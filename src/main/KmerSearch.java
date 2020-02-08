package main;
import java.util.*;

public abstract class KmerSearch
{
    static class KmerSearchFactory {
        KmerSearch pick(Set<String> options) {
            if (options.contains("textual")) {
                return new KmerTextualSearch();
            }
            return new KmerBase4Search();
        }
    }
    
    static void countTopKmers(KmerSearchData d) {
        for (int ix = 0; d.len >= ix + d.k; ++ix) {
            int count = countNextKmer(d, ix);
            // extra info
            d.maxCount = Math.max(d.maxCount, count);
        }
    }

    // primary algo:
    abstract int countNextKmer(final KmerSearchData d, final int ix);
    

    /** find all k-mers' positions */
    abstract List<Integer> findKmerPositions(KmerSearchData d);
    
    static class KmerTextualSearch {
        
        static void countTopKmers(KmerSearchData d) {
            for (int ix = 0; d.len >= ix + d.k; ++ix) {
                int count = countNextKmer(d, ix);
                // extra info
                d.maxCount = Math.max(d.maxCount, count);
            }
        }

        // primary algo:
        static int countNextKmer(final KmerSearchData d, final int ix) {
            if (d.counts[ix] != null) {
                return d.counts[ix];
            }
            int count = 0;
            d.counts[ix] = ++count;
            final String kmer = d.text.substring(ix, d.k + ix);
            for (int i = ix + 1; i + d.k <= d.len; ++i) {
                if (d.counts[i] == null) {
                    String imer = d.text.substring(i, d.k + i);
                    if (kmer.equals(imer)) {
                        d.counts[i] = ++count;
                    }
                }
            }
            d.countKmers.put(kmer, count);
            return count;
        } 

        /** find all k-mers' positions */
        static List<Integer> findKmerPositions(KmerSearchData d) {
            int max = 0;
            List<Integer> tops = new LinkedList<>();
            int pos = 0;
            for (pos = 0; pos < d.counts.length; ++pos) {
                if (max < d.counts[pos]) {
                    max = d.counts[pos];
                    tops.clear();
                    tops.add(pos);
                } else if (max == d.counts[pos]) {
                    tops.add(pos);
                }
            }
            return tops;
        }
    }
    
    
}

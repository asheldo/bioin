package main;

import java.util.*;

public abstract class KmerSearch extends Processor {
    
    public static class KmerSearchFactory {
        public static KmerSearch create(Set<String> options) {
            if (options.contains("textual")) {
                return new KmerTextualSearch();
            }
            return new KmerBase4Search();
        }
    }
    
    abstract public void countTopKmers(FastKmerSearchData d);
    // primary algo
    
    /** find all k-mers' positions */
    abstract public List<Integer> findKmerPositions(
        FastKmerSearchData d);
    
    // strategies:
    
    static class KmerTextualSearch extends KmerSearch {
        
        @Override
        public void countTopKmers(FastKmerSearchData d) {
            for (int ix = 0; d.L >= ix + d.k; ++ix) {
                int count = countNextKmer(d, ix);
                // extra info
                d.maxCount = Math.max(d.maxCount, count);
            }
        }

        // primary algo:
        static int countNextKmer(final FastKmerSearchData d, final int ix) {
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
        public List<Integer> findKmerPositions(final FastKmerSearchData d) {
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
    
    // Fast?:
    static class KmerBase4Search extends KmerSearch {

        @Override
        public void countTopKmers(final FastKmerSearchData d) {
            Map<Integer,Object> test = new HashMap<>();
            for (int ix = 0; d.L >= ix + d.k; ++ix) {
                final int n = d.base4kmers[ix];
                int count = countNextKmer(d, ix, n);
                d.clumpCount[n] = count;
                if (count >= d.clumpThreshold) {
                    d.clumped[n] = 1;
                }
                test.put(count, 
                    Base4er.reverse(d.base4kmers[ix], d));
                // extra info
                d.maxCount = Math.max(d.maxCount, count);
            }
        }
        
        /**
         * ComputingFrequencies:
         */
        static int countNextKmer(final FastKmerSearchData d, 
                                 final int ix,
                                 final int n) {
            if (d.counts[ix] != null) {
                return d.counts[ix];
            }
            int count = 0;
            d.counts[ix] = ++count;
            for (int i = ix + 1; i + d.k <= d.L; ++i) {
                if (d.counts[i] == null) {
                   if (n == d.base4kmers[i]) { // (kmer.equals(imer)) {
                        d.counts[i] = ++count;
                    }
                }
            }
            if (count > 1) {
                String kmer = Base4er.reverse(n, d);
                d.countKmers.put(kmer, count);
            }
            return count;
        } 
        
        /** find all k-mers' positions */
        @Override
        public List<Integer> findKmerPositions(final FastKmerSearchData d) {
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
    
    // CLUMP modified FAST
    public static class KmerClumpBase4Search extends KmerBase4Search {

        @Override
        public void countTopKmers(final FastKmerSearchData d) {
            super.countTopKmers(d);
        }

        public void shiftRightKmersCount(final FastKmerSearchData d,
                                         final int start) {                       
            final int decKmerBase4 = d.base4kmers[start - 1];
            // leftmost removed
            d.clumpCount[decKmerBase4] 
                = d.clumpCount[decKmerBase4] - 1;
            // add next k-mer, completely w/i window
            final int incKmerBase4 
                = d.base4kmers[start + d.L - d.k];
            d.clumpCount[incKmerBase4] =
                d.clumpCount[incKmerBase4] + 1;
            if (d.clumpCount[incKmerBase4]
                    >= d.clumpThreshold) {
                d.clumped[incKmerBase4] = 1;
            }
        }
    }
}

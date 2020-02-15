package main;

import java.util.*;
import main.*;

public class KmerBase4Search extends KmerSearch {

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
                     Base4er.decode(d.base4kmers[ix], d));
            // extra info
            d.maxCount = Math.max(d.maxCount, count);
        }
    }

    /**
     * ComputingFrequencies:
     */
    protected int countNextKmer(final FastKmerSearchData d, 
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
            String kmer = Base4er.decode(n, d);
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

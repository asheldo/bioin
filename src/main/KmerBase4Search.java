package main;

import java.util.*;
import main.*;

public class KmerBase4Search extends KmerSearch {

    @Override
    public void countTopKmers(final FastKmerSearchData d) {
        Map<Integer,Object> test = new HashMap<>();
        for (int ix = 0; d.L >= ix + d.k; ++ix) {
            final int n = d.base4kmers[ix];
            // do search
            int count = countNextKmer(d, ix, n);
            d.clumpCount[n] = count;
            if (count >= d.clumpThreshold) {
                d.clumped[n] = 1;
            }
            String kmer = Base4er.decode(d.base4kmers[ix], d);
            test.put(count, kmer);
            // extra info
            d.maxCount = Math.max(d.maxCount, count);
        }
        // print test
    }

    /**
     * ComputingFrequencies:
     */
    static int countNextKmer(final FastKmerSearchData d, 
                             final int ix,
                             final int n) {
        int countEq = 0;
        if (d.counts[ix] != null) {
            if (d.hamming == 0) {
                return d.counts[ix];
            }
        } else {
            d.counts[ix] = 0;
        }
        ++countEq; // init
        for (int i = ix + 1; i + d.k <= d.L; ++i) {
            if (d.hamming > 0 || d.counts[i] == null) {
                // zero diatance
                if (n == d.base4kmers[i]) {
                    ++countEq;
                    if (d.counts[i] == null) {
                        d.counts[i] = countEq;
                    } else {
                        d.counts[i] += countEq;
                    }
                } else if (d.hamming > 0) { 
                    int dist = Hamming.hammingDist(d.k,
                        d.text.subSequence(ix, ix + d.k),
                        d.text.subSequence(i, i + d.k),
                        d.hamming);
                    if (dist <= d.hamming) {
                        d.counts[i] = ++count; 
                    }
                }
            }
        }
        d.counts[ix] = count;
        if (count > 1) {
            String kmer = Base4er.decode(n, d);
            d.countKmers.put(kmer, count);
        }
        return count;
    } 

    /** for audit, find all k-mers' positions */
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

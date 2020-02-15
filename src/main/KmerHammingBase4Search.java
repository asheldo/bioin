package main;

import java.util.*;

public class KmerHammingBase4Search extends KmerBase4Search
{

    @Override
    public void countTopKmers(final FastKmerSearchData d) {
        if (d.hamming == 0) {
            super.countTopKmers(d);
        }
        if (d.hamming > 0) {
            // todo
            throw new UnsupportedOperationException("hamming todo:" + d);
        }
        //Map<Integer,Object> test = new HashMap<>();
        for (int ix = 0; d.L >= ix + d.k; ++ix) {
            final int n = d.base4kmers[ix];
            int count = countNextKmer(d, ix, n);
            d.clumpCount[n] = count;
            if (count >= d.clumpThreshold) {
                d.clumped[n] = 1;
            }
            // test.put(count,  Base4er.decode(d.base4kmers[ix], d));
            // extra info
            d.maxCount = Math.max(d.maxCount, count);
        }
    }

    /**
     * ComputingFrequencies:
     */
    @Override
    protected int countNextKmer(final FastKmerSearchData d, 
                                final int ix,
                                final int n) {
        if (d.hamming == 0) {
            return super.countNextKmer(d, ix, n);
        }
        // todo
        throw new UnsupportedOperationException("hamming todo:" + d);
        /*
        int count = 0;
        if (d.counts[ix] != null) {
            if (d.hamming == 0) {
                return d.counts[ix];
            }
        } else {
            d.counts[ix] = 0;
        }
        ++count; // init
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
        */
    } 

    /** for audit, find all k-mers' positions */
    @Override
    public List<Integer> findKmerPositions(final FastKmerSearchData d) {
        return super.findKmerPositions(d);
    }
}

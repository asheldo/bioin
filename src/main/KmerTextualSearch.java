package main;
 
import java.util.*;

public class KmerTextualSearch extends KmerSearch {

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


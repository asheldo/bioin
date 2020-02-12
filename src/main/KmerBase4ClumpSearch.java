package main;


// CLUMP modified FAST
public class KmerBase4ClumpSearch extends KmerBase4Search {

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

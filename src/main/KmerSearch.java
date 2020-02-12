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
    
    // strategies extracted
}

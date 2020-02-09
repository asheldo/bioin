package main;

import java.util.*;

public class ClumpCount extends Processor
{
    
    public static void main(String[] args) throws Exception {
        FileInputs m = 
            FileInputs.scanFileInputs("thermophilia.txt");
        String cArgs = m.param1 == null 
            ? "9 500 3" : m.params.get(1);
        String [] clumpArgs = cArgs.split(" ");
        String k = clumpArgs[0];
        String L = clumpArgs[1]; //window
        String t = clumpArgs[2]; // times
        print("inputs: %s \n", clumpArgs);
        String text = m.params.get(0);
        FastKmerSearchData d = new FastKmerSearchData(
            text, 
            Integer.parseInt(k),
            Integer.parseInt(L),
            Integer.parseInt(t));
        // 1st window freq
        KmerSearch.KmerClumpBase4Search search = 
            new KmerSearch.KmerClumpBase4Search();

        process(d, search);
        analyzeAndReport(d, search, m.outputFile);
    }

    static void process(final FastKmerSearchData d, 
                        final KmerSearch.KmerClumpBase4Search search) { 
        checkpoint();
        search.countTopKmers(d);
        print("t=%d \n", checkpoint());
        print("\n %d \n ", d.maxCount);
        for (int ix = 1; ix <= d.len - d.L; ++ix) {
            search.shiftRightKmersCount(d, ix);
            if (ix % 10000 == 0) {
                print("%d ", d.maxCount);
            }
        }
        print("t=%d \n", checkpoint());
    }

    static void analyzeAndReport(final FastKmerSearchData d,
                                 final KmerSearch search,
                                 final String outputFile) 
    throws Exception { 
        List<String> kmers = new LinkedList<>();
        for (int ix = 0; ix < d.clumped.length; ++ix) {
            if (d.clumped[ix] != 0) {
                String kmer =
                    Base4er.reverse(ix, d);
                // d.base4kmers[ix], d);
                kmers.add(kmer);
                // print("@ %d of %d ", top, d.len);
                // print("k-mer: %s \n", kmer);
            }
        }
        report(d, outputFile, kmers);
    }

    static void report(final FastKmerSearchData d,
                       final String outputFile,
                       final List<String> kmers) throws Exception {
        print("top %d k-mers: ... \n", 
            kmers.size()); 
            // kmers.subList(0, kmers.size() - 1));
        // print("first %d any-mers: \n%s \n", d.countKmers.size(), d.countKmers);
        // Integer [] arrCounts = Arrays.copyOfRange(d.counts,
        //                            0, Math.max(100, d.counts.length));
        // print("first %d position counts: %s\n", arrCounts.length,
        //      Arrays.asList(arrCounts));

        TextFileUtil.writeKmersListPlus(
            outputFile, // *.out in assets
            kmers, // frequent-est k-length patterns
            // d.countKmers.get(kmers.get(0)), // frequency
            kmers.size(), // 
            d.countKmers.size()); // total # k-mers
            // d.countKmers
	}
}

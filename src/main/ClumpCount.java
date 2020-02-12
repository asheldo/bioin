package main;

import java.util.*;

public class ClumpCount extends Processor
{
    
    public static void main(String[] args) throws Exception {
        FileInputs m = 
            FileInputs.scanFileInputs("thermophilia.txt");
        String cArgs = m.param1 == null 
            ? m.options.iterator().next()
            : m.params.get(1);
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
        KmerBase4ClumpSearch search = 
            new KmerBase4ClumpSearch();

        process(d, search);
        analyzeAndReport(d, search, m.outputFile);
    }

    static void process(final FastKmerSearchData d, 
                        final KmerBase4ClumpSearch search) { 
        checkpoint();
        search.countTopKmers(d);
        print("t=%d \n", checkpoint());
        print("maxCount was %d\n", d.maxCount);
        for (int ix = 1; ix <= d.len - d.L; ++ix) {
            search.shiftRightKmersCount(d, ix);
        }
        print("proc t=%d \n", checkpoint());
    }

    static void analyzeAndReport(final FastKmerSearchData d,
                                 final KmerSearch search,
                                 final String outputFile) 
    throws Exception { 
        List<String> kmers = new LinkedList<>();
        for (int ix = 0; ix < d.clumped.length; ++ix) {
            if (d.clumped[ix] != 0) {
                String kmer = Base4er.decode(ix, d);
                kmers.add(kmer);
            }
        }
        report(d, outputFile, kmers);
    }

    static void report(final FastKmerSearchData d,
                       final String outputFile,
                       final List<String> kmers) throws Exception {
        print("top %d k-mers: ... \n", 
            kmers.size()); 
        TextFileUtil.writeKmersListPlus(
            outputFile, // *.out in assets
            kmers, // frequent-est k-length patterns
            // d.countKmers.get(kmers.get(0)), // frequency
            kmers.size(), // 
            d.countKmers.size()); // total # k-mers
            // d.countKmers
	}
}

package main;
import java.util.*;

public class KmerFreqCount extends Processor
{
    public static void main(String[] args) throws Exception {
        FileInputs m = 
            FileInputs.scanFileInputs("freq.txt");
        String k = m.param1 == null 
            ? m.options.iterator().next()
            : m.params.get(1);
       
        print("inputs: %s \n", k);
        String text = m.params.get(0);
        FastKmerSearchData d = new FastKmerSearchData(
            text, 
            Integer.parseInt(k),
            text.length(),
            1);
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
    }

    static void analyzeAndReport(final FastKmerSearchData d,
                                 final KmerSearch search,
                                 final String outputFile) 
    throws Exception { 
        report(d, outputFile);
    }

    static void report(final FastKmerSearchData d,
                       final String outputFile) throws Exception {
        print("top %d k-mers: ... \n", 
              d.clumpCount.length); 
        List<Integer> list = new LinkedList<>();
        for (int i : d.clumpCount) {
            list.add(i);
        }
        TextFileUtil.writeKmersListPlus(
            outputFile, // *.out in assets
            list, // frequent-est k-length patterns
            // d.countKmers.get(kmers.get(0)), // frequency
            list.size(), // 
            d.countKmers.size()); // total # k-mers
        // d.countKmers
    }
}

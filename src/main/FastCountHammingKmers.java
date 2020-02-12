package main;

import java.nio.file.*;
import java.util.*;
import org.apache.commons.codec.binary.*;
import java.io.*;

public class FastCountHammingKmers extends Processor {
	
	public static void main(String[] args) throws Exception {
		FileInputs m = FileInputs.scanFileInputs();
		print("inputs: %s \n", m);
        int k = 9;
        int hammingD = 2;
        if (m.param1 != null) {
            String [] params = 
                m.param1.trim().split(" ");
            k = Integer.parseInt(params[0]);
            if (params.length > 1) {
                hammingD = Integer.parseInt(params[1]);
            }
        }
		FastKmerSearchData d = new FastKmerSearchData(
            m.sourceText0,
            k, 
            m.sourceText0.length(),
            0,
            hammingD);
        KmerSearch search = KmerSearch.KmerSearchFactory
            .create(m.options);
		process(d, search);
		analyzeAndReport(d, search, m.outputFile);
	}

	static void process(final FastKmerSearchData d, 
                        final KmerSearch search) { 
		long start = System.currentTimeMillis();
        // triggering hamming version
		search.countTopKmers(d);
		long end = System.currentTimeMillis();
		print("t=%d \n", (end-start));
	}
	

	
	static void analyzeAndReport(final FastKmerSearchData d,
	             				 final KmerSearch search,
                                 final String outputFile) 
	throws Exception { 
		List<Integer> kmerPos = search.findKmerPositions(d);
		List<String> kmers = new LinkedList<>();
		for (int top : kmerPos) {
			String kmer = d.text.substring(top, d.k + top);
			kmers.add(kmer);
			print("@ %d of %d ", top, d.len);
	    	print("k-mer: %s \n", kmer);
		}
		report(d, outputFile, kmers);
	}
	
	static void report(final FastKmerSearchData d,
					   final String outputFile,
	                   final List<String> kmers) throws Exception {
		print("top %d k-mers: \n%s \n", kmers.size(), kmers);
		print("first %d any-mers: \n%s \n", d.countKmers.size(), d.countKmers);
		Integer [] arrCounts = Arrays.copyOfRange(d.counts,
								0, Math.max(100, d.counts.length));
		print("first %d position counts: %s\n", arrCounts.length,
		     Arrays.asList(arrCounts));
		
		TextFileUtil.writeKmersListPlus(
		    outputFile, // *.out in assets
			kmers, // frequent-est k-length patterns
			d.countKmers.get(kmers.get(0)), // frequency
			kmers.size(), // 
			d.countKmers.size(), // total # k-mers
			d.countKmers);
		
	}
	
}


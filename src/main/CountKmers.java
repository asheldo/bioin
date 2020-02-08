package main;

import java.nio.file.*;
import java.util.*;
import org.apache.commons.codec.binary.*;
import java.io.*;

public class CountKmers extends Processor
{
	
	public static void main(String[] args) throws Exception {
		FileInputs m = scanFileInputs();
		print("inputs: %s \n", m);
		KmerSearchData d = new KmerSearchData(m.sourceText0, m.param1);
		process(d);
		analyzeAndReport(d, m.outputFile);
	}

	static void process(KmerSearchData d) { 
		long start = System.currentTimeMillis();
		countTopKmers(d);
		long end = System.currentTimeMillis();
		print("t=%d \n", (end-start));
	}
	
	static void countTopKmers(KmerSearchData d) {
		for (int ix = 0; d.len >= ix + d.k; ++ix) {
			int count = countNextKmer(d, ix);
			// extra info
			d.maxCount = Math.max(d.maxCount, count);
		}
	}

	static int countNextKmer(final KmerSearchData d, final int ix) {
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
	static List<Integer> findKmerPositions(KmerSearchData d) {
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
	
	static void analyzeAndReport(final KmerSearchData d,
	             				 final String outputFile) 
	throws Exception { 
		List<Integer> kmerPos = findKmerPositions(d);
		List<String> kmers = new LinkedList<>();
		for (int top : kmerPos) {
			String kmer = d.text.substring(top, d.k + top);
			kmers.add(kmer);
			print("@ %d of %d ", top, d.len);
	    	print("k-mer: %s \n", kmer);
		}
		report(d, outputFile, kmers);
	}
	
	static void report(final KmerSearchData d,
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
	
	static class KmerSearchData {
		final int len;
		final int k;
		final String text;
		final Integer [] counts;
		int maxCount;
		
		final Map<String,Integer> countKmers 
		    = new LinkedHashMap<>();
		
		KmerSearchData(final String text, 
		               final String kParam) {
			this.text = text;
			this.len = text.length();
			this.k = Integer.parseInt(kParam);
			this.counts = new Integer [len - k + 1];
		}
		
	}


}


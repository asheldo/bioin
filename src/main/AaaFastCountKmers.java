package main;

import java.nio.file.*;
import java.util.*;
import org.apache.commons.codec.binary.*;
import java.io.*;

public class FastCountKmers extends Processor
{
	
	public static void main(String[] args) throws Exception {
		FileInputs m = scanFileInputs();
		print("inputs: %s \n", m);
		KmerSearchData d = new KmerSearchData(m.sourceText0, m.param1);
        KmerSearch search = KmerSearchFactory.pick(m.options);
		process(d, search);
		analyzeAndReport(d, m.outputFile);
	}

	static void process(KmerSearchData d, KmerSearch search) { 
		long start = System.currentTimeMillis();
		search.countTopKmers(d);
		long end = System.currentTimeMillis();
		print("t=%d \n", (end-start));
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
		final int [] base4kmers;
		int maxCount;
		
		final Map<String,Integer> countKmers 
		    = new LinkedHashMap<>();
		
		KmerSearchData(final String text, 
		               final String kParam) {
			this.text = text;
			this.len = text.length();
			this.k = Integer.parseInt(kParam);
			this.counts = new Integer [len - k + 1];
			this.base4kmers = Base4er.calc(this);
		}
	}

	static class Base4er {
		// i.e. 2^32 is 4^16
		static final int pow4s [ ] = pow(4, 15);

		static int [] pow(int base, int n)  {
			int [] pows = new int [n];
			pows[0] = 1;
			for (int i = 1; i < n; i++) {
				pows[i] = pows[i-1] * base;
			}
			return pows;
		}

		static int mapChar(char c) {
			switch (c) {
				case 'A':
					return 0;
				case 'C':
					return 1;
				case 'G':
					return 2;
				case 'T':
					return 3;
				default:
					throw new RuntimeException("problem");
			}
		}

		static int [] calc(KmerSearchData d)  {
			int n = d.counts.length;
			int [] base4s = new int [n];

			for (int i = 0; i < n; i++) {
				base4s[i] = mapChar(d.text.charAt(i));
			}
			for (int i=0; i < n; i++) {
				int base4kmer = 0;
				for (int j=0; j < d.k; ++j) {
					base4kmer += pow4s[d.k-j-1] * base4s[i];
				}
				base4s[i] = base4kmer;
			}
			return base4s;
		}
	}
}


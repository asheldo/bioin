package main;

import java.nio.file.*;
import java.util.*;
import org.apache.commons.codec.binary.*;
import java.io.*;

/**
  * see readme, array driven
  * memory efficient in critical findTopKmers method
  * map is just used for a diagnostic after
  */
public class CountWords01 {

	static String Sf(String s, Object ... args) {
		return String.format(s, args);
	}
	
	public static void main(String[] args) throws Exception {
		CountParams m = scanThem();
		System.out.println(m);
		Dstring d = new Dstring(m.sourceText, m.k);
		long start = System.currentTimeMillis();
		List<Integer> tops = findTopKmers(d);
		long end = System.currentTimeMillis();
		System.out.println("t=" + (end-start));
		List<String> kmers = new LinkedList<>();
		for (int top : tops) {
			String kmer = d.s.substring(top, d.k + top);
			kmers.add(kmer);
			System.out.println(Sf("@ %d of %d", top, d.len));
			System.out.println("n = "+d.counts[top]);
	    	System.out.println("k-mer: "+kmer);
		}
		System.out.println("top kmers: " + kmers.size());
		System.out.println(kmers);
		System.out.println("all kmers: " + d.countKmers.size());
		TextFileUtil.writeKmersListPlus(
		    m.outputFile.getAbsolutePath(),
			d.countKmers.get(kmers.get(0)),
			kmers.size(), kmers, 
			d.countKmers.size(), d.countKmers);
		Integer [] arr = Arrays.copyOfRange(d.counts,
		    0, Math.max(500, d.counts.length));
		List<Integer> list = Arrays.asList(arr);
		System.out.println(list);
	}

	static List<Integer> findTopKmers(Dstring d) {
		for (int ix = 0; d.len >= ix + d.k; ++ix) {
			int count = countNextKmer(d, ix);
			// ignore, let top() analyze
		}
		return d.tops();
	}
	
	static int countNextKmer(final Dstring d, final int ix) {
		if (d.counts[ix] != null) {
			return d.counts[ix];
		}
		int count = 0;
		d.counts[ix] = ++count;
		final String kmer = d.s.substring(ix, d.k + ix);
		for (int i = ix + 1; i + d.k <= d.len; ++i) {
			if (d.counts[i] == null) {
				String imer = d.s.substring(i, d.k + i);
				if (kmer.equals(imer)) {
					d.counts[i] = ++count;
				}
			}
		}
		d.countKmers.put(kmer, count);
		return count;
	} 

	static class Dstring {
		final String s;
		final Integer [] counts;
		Map<String,Integer> countKmers 
		    = new LinkedHashMap<>();
		final int len;
		final int k;
		
		Dstring(final String s, 
		              final int k) {
			this.s = s;
			this.len = s.length();
			this.k = k;
			this.counts = new Integer [len - k + 1];
		}
		
		List<Integer> tops() {
			int max = 0;
			List<Integer> tops = new LinkedList<>();
			int pos = 0;
			for (pos = 0; pos < counts.length; ++pos) {
				if (max < counts[pos]) {
					max = counts[pos];
					tops.clear();
					tops.add(pos);
				} else if (max == counts[pos]) {
					tops.add(pos);
				}
			}
			return tops;
		}
	}

    /**
      *
      */
	static class CountParams {
		int k;
		String sourceText;
		File outputFile;

		@Override
		public String toString() {
			return "k:" + k;
        }
	}

	static CountParams scanThem() throws Exception {
		
		CountParams m = new CountParams();
		
		BufferedReader input = new BufferedReader(
		    new InputStreamReader(System.in));
		// Scanner input = new Scanner(System.in);
		String filePath =
		    new StringBuilder("/storage/emulated/0/AppProjects/")
			.append("%s").append("/src/main/assets/")
			.append("%s").toString();
		final String p = "bioin";
		System.out.print("Project with asset:");
		System.out.println(Sf("(default: %s)", p));
		String proj = input.readLine().trim();
		final String f = "Vibrio_cholerae.txt";
		System.out.print("Filename of text and k asset:");
		System.out.println(Sf("(default: %s)", f));
		final String file = input.readLine().trim();
		filePath = Sf(filePath, 
		    proj.isEmpty() ? p : proj, 
		    file.isEmpty() ? f : file);
		List<String> data
		    = TextFileUtil.readTextAndK(filePath);
		m.sourceText = data.get(0);
		m.k = Integer.parseInt(data.get(1));
		m.outputFile = new File(filePath + ".out");
		input.close();
		return m;
	}

}


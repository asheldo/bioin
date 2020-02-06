package main;

import java.nio.file.*;
import java.util.*;

public class CountWords
{
	public static void main(String[] args) throws Exception {
		CountParams m = scanThem();
		System.out.println(m);
		Dstring d = new Dstring(m.sourceText, m.k, 0);
		int lastposition = findTopKmer(d);
		String kmer = d.s.substring(lastposition, d.k + lastposition);
		System.out.println("@"+lastposition);
		System.out.println(d.counts[lastposition]);
	    System.out.println(kmer);
		List<Integer> list = Arrays.asList(d.counts);
		System.out.println(list);
	}

	static int findTopKmer(Dstring d) {
		d.next();
		while (countNextKmer(d)) {
			d.next();
		}
		return d.top();
	}
	
	static boolean countNextKmer(final Dstring d) {
		int count = 0;
		for (int i = d.ix; i + d.k <= d.len; ++i) {
			if (d.counts[i] != null) {
				continue;
			}
			String imer = d.s.substring(i, d.k + i);
			if (d.kmer.equals(imer)) {
				d.counts[i] = ++count;
			}
		}
		return d.more;
	} 

	static class Dstring {
		final String s;
		final Integer [] counts;
		int ix;
		final int len;
		final int k;
		String kmer;
		boolean more;
		
		Dstring(final String s, 
		              final int k,
				      final int ix) {
			this.s = s;
			this.ix = ix;
			this.len = s.length();
			this.k = k;
			this.counts = new Integer [len - k + 1];
		}
		
		public void next() {
			this.more = len > ix + k;
			this.kmer = s.substring(ix, k + ix);
		    ++ix;
		}
		
		int top() {
			int max = 0;
			int top = 1;
			int pos = 0;
			for (pos = 0; pos < counts.length; ++pos) {
				if (counts[pos] != null && max < counts[pos]) {
					max = counts[pos];
					top = pos;
				}
			}
			return top;
		}
	}

    /**
      *
      */
	static class CountParams {
		int k;
		String sourceText;

		@Override
		public String toString() {
			return "k:" + k;
        }
	}

	static CountParams scanThem() throws Exception {
		
		CountParams m = new CountParams();
		Scanner input = new Scanner(System.in);
		System.out.print("Enter filename of text and k source: ");
		String filePath = "/storage/emulated/0/AppProjects/bioin/src/main/assets/"
		    + input.next();
		
		List<String> data = ReadText.bufferedLines(filePath);
		m.sourceText = data.get(0);
		m.k = Integer.parseInt(data.get(1));
		return m;
	}

}


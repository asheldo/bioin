import java.nio.file.*;
import java.util.*;
import main.*;
// CCATGAGCC
// GTAAAACGT
// CAGGCGCCA
// GGCTCCGGG

public class CountPattern extends Processor
{
	public static void main(String[] args) throws Exception {
        FileInputs m = FileInputs.scanFileInputs();
		print("inputs %s %s %s \n",
            m.sourceText0.substring(0, 3), 
            m.param1.substring(0,3), "");
        checkpoint();
		List<Integer> positions = countAll(
            new CountDstring(m.param1, 
            m.sourceText0, 0));
        long t = checkpoint();
        String spaced = positions.toString()
            .replaceAll(",", "");
		System.out.println(positions.size() + " positions: " 
            + positions);
        TextFileUtil.writeKmersListPlus(
		    m.outputFile, // *.out in assets
            "t="+t,
            positions.size(), // positions,
            spaced
        );
	}

	static int feedback = 10;

	static List<Integer> countAll(CountDstring d) {
		List<Integer> positions =
		    new LinkedList<>();
		d.next();
		while (countOne(d, positions)) {
			d.next();
		}
		return positions;
	}
	
	static boolean countOne(final CountDstring d, 
							final List<Integer> count) {
		if (d.match) {
			count.add(d.ix - 1);
			if (count.size() % feedback == 0) {
			    System.out.println(count.size() 
								   + " positions: ... " 
								   + count.get(count.size() - 1));
			}
		}
		return d.more;
	} 

	static class CountDstring {
		final String s;
		int ix;
		final int len;
		final String patt;
		
		boolean match;
		boolean more;
		
		CountDstring(final String s, 
		        final String patt,
				final int ix) {
			this.s = s;
			this.ix = ix;
			this.len = s.length();
			this.patt = patt;
		}
		
		public void next() {
			this.more = len > ix + patt.length();
			this.match = s.startsWith(patt, ix);
		    ++ix;
		}
	}
}


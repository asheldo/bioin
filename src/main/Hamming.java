package main;

import java.util.*;

public class Hamming extends Processor
 {

    public static void main(String [] args) throws Exception {
        System.out.println("String:");
        FileInputs ins = FileInputs.scanFileInputs();
        // FileInputs ins = FileInputs.scanFileSingleInput(2);
        // String [] text = ins.sourceText0.toUpperCase().split(" ");
        String kmerI = ins.params.get(0); //[0];
        String text = ins.params.get(1);
        String matchesParam = ins.params.get(2); // .param1;
        
        int k = kmerI.length();
        int matches = matchesParam == null ? 9 : 
            Integer.parseInt(matchesParam);
        List<Integer> positions = 
            hammingMatches(text, k, kmerI, matches);
        print("ct=%s\n", positions.size());
        TextFileUtil
            .writeKmersListPlus(ins.outputFile,
                                positions.size(),
                                positions,
                                kmerI, 
                                matches,
                                text.length());
    }

    public static List<Integer> hammingMatches(String text, 
                                               int k, 
                                               String kmerI, 
                                               int mismatches)
    {
        List<Integer> positions = new LinkedList<>();
        int start = 0;
        int lastStart = text.length() - k + 1;
        for (int i = start; i < lastStart; ++i) {
            String kmerJ = text.substring(i, i + k);
            int distance = hammingDist(k, kmerI, kmerJ,
                mismatches);
            if (distance <= mismatches) {
                positions.add(i);
            }
        }
        return positions;
    }

    // Distance
    public static int hammingDist(int k,
                                  CharSequence kmerI, 
                                  CharSequence kmerJ,
                                  int mismatches) {
        int d = 0;
        for (int i = 0; d < k && d <= mismatches 
                && i < kmerJ.length(); ++i) {
            if (kmerI.charAt(i) != kmerJ.charAt(i)) {
                ++d;
            }
        }
        return d;
    }

    /*  int len = text.length();
     int ct = len - k + 1;
     int [] hammings =
     new int[(ct * (ct + 1)) / 2] ;
     for (int i = 0, ix = 0; i < ct ; ++i) {
     String kmerI = text.substring(i, k);
     for (int j = i + 1; j < ct ; ++j, ++ix) {
     String kmerJ = text.substring(j, k);

     hammings[ix] = hammimgs(kmerI, kmerJ);;
     }
     }
     */
}

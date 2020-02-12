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
        String kmerJ = ins.params.get(1);
        String dParam = ins.params.get(2); // .param1;
        List<Integer> list = new LinkedList<>();
        int k = kmerI.length();
        int d = dParam == null ? 9 : 
            Integer.parseInt(dParam);
        hammingMatches(kmerJ, k, kmerI, d, list);
        
        System.out.println(list.size());
        TextFileUtil
            .writeKmersListPlus(ins.outputFile,
                                list.size(),
                                list,
                                kmerI, d
                                // kmerJ            
                                );
    }

    private static void hammingMatches(String text, 
                                       int k, 
                                       String kmerI, 
                                       int mismatches, 
                                       List<Integer> list)
    {
        int start = 0;
        int lastStart = text.length() - k + 1;
        for (int i = start; i < lastStart; ++i) {
            String kmerJ = text.substring(i, i + k);
            int distance = hamming(k, kmerI, kmerJ);
            if (distance <= mismatches) {
                list.add(i);
            }
        }
    }

    // Distance
    static int hamming(int max, 
                       String kmerI, 
                       String kmerJ) {
        int d = 0;
        for (int i = 0; d < max && i < kmerJ.length(); ++i) {
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

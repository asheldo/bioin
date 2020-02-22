package main;

import java.util.*;

public class Skew extends Processor {
    
    public static void main(String [] args) throws Exception {
        print("Skew: %s\n", Skew.class);
        
        FileInputs ins = FileInputs.scanFileSingleInput(-1,
            "Salmonella_enterica.txt");
        String text = ins.sourceText0;
        SkewData data =
            SkewData.findMinimalSkew(text, 'G', 'C', 200);
        print("\ndata min %s\n", data.min);
        TextFileUtil.writeKmersListPlus(ins.outputFile, 
            data.min,
            data.minSkew,
            data.maxSkew,
            data.skewRun.length,
            Arrays.asList(data.skewRun),
            Arrays.asList(data.skewMinRun)
            );
    }
    
    
}

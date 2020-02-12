package main;

import java.util.*;

public class Skew extends Processor {
    
    public static void main(String [] args) throws Exception {
        System.out.println("String:");
        FileInputs ins = FileInputs.scanFileSingleInput();
        String text = ins.sourceText0;
        SkewData data =
            SkewData.findMinimalSkew(text, 'G', 'C');
        System.out.println(data.min);
        TextFileUtil.writeKmersListPlus(ins.outputFile, 
            data.min,
            data.minSkew,
            data.maxSkew,
            data.skewRun.length,
            Arrays.asList(data.skewRun)
            );
    }
    
    
}

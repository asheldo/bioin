package main.assembly;
import main.*;
import java.util.*;

public class StringReconstruct extends Processor {
    /*
     The de Bruijn Graph Construction Problem;
     The Eulerian Path Problem;
     The String Spelled by a Genome Path Problem.
     StringReconstruction(Patterns)
     dB ← DeBruijn(Patterns)
     path ← EulerianPath(dB)
     Text﻿ ← PathToGenome(path)
     return Text
    */
    public static void main(String [] args) throws Exception {
        String f = "2_2/dataset-re.txt";
        FileInputs fis = FileInputs.scanFileInputs(f);
        // glue k-1-mers into adjacency
        List<String> params = debruijnKmers(fis.params);
        // path (not cycle) ... xyklwrks
        List<String> euler = StringComp.eulerCycle(params, true);
        String out = StringComp.eulerPathOut(euler);
        List<String> path = 
            Arrays.asList(out.split("->"));
        String genome = StringComp.compose(path);
        println(fis.outputFile);
        println(genome);
        TextFileUtil.writeKmersListPlus("",
            fis,
            genome);
    }
    
    static List<String> debruijnKmers(List<String> params) {
        StringComp composer =
            new StringComp(StringComp.convert(params.subList(1, params.size())));
        List<StringComp.Node> debruijn = composer.graphGlue(); // sorted
        String out = StringComp.adjacencyOut(debruijn);
        return Arrays.asList(out.split("\n"));
    }
}

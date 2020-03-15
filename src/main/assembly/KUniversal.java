package main.assembly;
import main.*;

public class KUniversal extends Processor
{
    public static void main(String [] args) throws Exception {
        String f = "2_2/dataset-re.txt";
        FileInputs fis = FileInputs.scanFileInputs(f);
      /*  // glue k-1-mers into adjacency
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
        */
    }
}

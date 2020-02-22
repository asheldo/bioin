package main;

public class Probs extends Processor {

    public static void main(String [] args) throws Exception {
        print("Skew: %s\n", Skew.class);
        FileInputs ins = FileInputs.scanFileInputs();
        //           "Salmonella_enterica.txt");
        // String text = ins.sourceText0;
        String [] opt = ins.options.iterator().next()
            .split("\\w");
        int L = 1000, n = 500, k = 9;
        FastKmerSearchData d = 
            new FastKmerSearchData("AAAAAAAAAA", k, L, 0 );
        double p = 0, pN = 0;
        p = new Probs().probability(d);
        pN = 1 - (Math.pow(1.0 - p, (double) n));
        double ct = n * (L - k + 1) /
            Math.pow((double)Base4er.BASE,
                     (double) d.k);
        TextFileUtil.writeKmersListPlus(ins.outputFile, 
                                        ct,
                                        pN,
                                        p,
                                        n, 
                                        d.k,
                                        d.L
                                        );
    }

    public double probability(FastKmerSearchData d) {
        double n = d.L - d.k + 1;
        double p1 = 1 / 
            Math.pow((double)Base4er.BASE,
            (double) d.k);
        double pK = 1 - Math.pow(1 - p1, n);
        return pK;
    }

}

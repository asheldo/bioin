package main;

public class ProfileMost extends Processor
{
    public static void main(String[] args) throws Exception {
        FileInputs m = FileInputs.scanFileSingleInput(-1, ", ", "3/prof5.txt", false);
        print("inputs: %s \n", m.params);
        
        if (m.params != null) {
            
            int hammingD = 0; //2;
            String text = m.params.get(0);
            int k = Integer.parseInt(m.params.get(1));
            
                // flip?
            String [][] profile = new String [Base4er.BASE][k];
                
            for (int i = 0; i < Base4er.BASE; i++) {
                String p = m.params.get(2+i);
                if (p == null) {
                    break;
                }
                profile[i] = p.split(" ");
                // } catch (Exception e) {
                // e.printStackTrace(System.err);
            }

            // START HERE
            println("Motif.profile");

            processMotifs(m, text, profile, k);
        }
    }


    // e.g. 16 kmers in i and j, 2 out of 9 pos
    static ProfileMost processMotifs(FileInputs m,
                              final String text,
                              final String [][] profile,
                              final int k) throws Exception {

        FastKmerSearchData d = new FastKmerSearchData(text, k, text.length(), 0);
        ProfileMost pm = new ProfileMost(); // d, reverseCompMis);
        pm.d = d;
       
        // flip
        pm.profile = new double [k][Base4er.BASE];
        for (int i = 0; i < k; ++i) {
            for (int b = 0; b < Base4er.BASE; ++b) {
                pm.profile[i][b] = 
                    Double.parseDouble(profile[b][i]);
            }
        }
        pm.calc();
        return pm;
    }
    
    void calc() {
        checkpoint();
        for (int i = 0; i < d.L - d.k + 1 ; ++i) {
           
            double p = 1.0;
            int kmer = d.base4kmers[i];
            for (int ix = d.k - 1; ix >= 0; --ix) {
                for (int b = 3; b >= 0; --b) {
                    int ppk = Base4er.pow4Permutations[ix][b];
                    if ((ppk & kmer) == ppk) {
                        p *= profile[d.k - ix - 1][b];
                        break;
                    }
                }
            }
            if (maxProb < p) {
                maxProb = p;
                this.kmer = kmer;
            }
            /*
             nbrs.getKmerNeighbors(m.outputFile, 
             d, Integer.MAX_VALUE,         
             false);
             nbrs.reportFrequent(d, 
             extension(m.outputFile, ".out"));
             */
        }
        

        print("prof: %s \n%s\n %s\n",
              maxProb, // score
              Base4er.decode(this.kmer, d.k),
              "t=" + checkpoint()
              );
    }

    //
    double [][] profile;
    FastKmerSearchData d;
    double maxProb;
    int kmer;
    
}

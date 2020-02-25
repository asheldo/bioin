package main;
import java.util.*;

public class Base4er extends Processor {
    
    public static final int BASE = 4;
    
    // i.e. 2^32 is 4^16
    public static final Integer pow4s [] = pow(16);
    
    public static final Integer pow4Permutations [][] 
        = powPerms();
    
    static Integer [] pow(int n)  {
        Integer [] pows = new Integer [n];
        pows[0] = 1;
        for (int i = 1; i < n; i++) {
            pows[i] = pows[i-1] * BASE;
        }
        return pows;
    }

    static double LOG10_2 = Math.log10(2);
    
    static double log2(double p) {
        return p == 0 ? 0 : Math.log10(p) / LOG10_2;
    }
    
    // 0 in a place is not wasted space!
    static Integer [][] powPerms() {
        Integer powPerms[][] = new Integer [pow4s.length][BASE];
        for (int n = 0; n < pow4s.length; n++) {
            // implicit 
            powPerms[n][0] = 0;
            for (int b = 1, pow = 0; b < BASE; b++) {
                pow += pow4s[n];
                powPerms[n][b] = pow;
            }
        }
        return powPerms;
    }
    
    // 1 4 16 64 256 1024 4096
    // 2 8 32 128 512 2048 8192
    static int [] base2bits(boolean low, int k) {
        int [] bits = new int [k];
        bits[0] = low ? 1 : 2;
        for (int i = 1; i < k; ++i) {
           bits[i] = bits[i-1] * 4;
        }
        return bits;
    }
    
    static int [] base4HighBits(int k) {
        
        return base2bits(false, k);
    }
    
    static int [] base4LowBits(int k) {
        return base2bits(true, k);
    }
    
    public static int [] map(String text) {
        int n = text.length();
        int [] base4s = new int [n];
        for (int i = 0; i < n; i++) {
            base4s[i] = mapChar(text.charAt(i));
        }
        return base4s;
    }
    
    static int mapChar(char c) {
        switch (c) {
            case 'A':
                case 'a':
                return 0;
            case 'C':
                case 'c':
                return 1;
            case 'G':
                case 'g':
                return 2;
            case 'T':
                case 't':
                return 3;
            default:
                throw new RuntimeException("problem: " + c);
        }
    }

    static int [] calc(FastKmerSearchData d)  {
        int n = d.text.length();
        int [] base4s = new int [n];

        for (int i = 0; i < n; i++) {
            base4s[i] = mapChar(d.text.charAt(i));
        }
        
        for (int i=0; i < n - d.k + 1; i++) {
            int base4kmer = 0;
            for (int j=0; j < d.k; ++j) {
                base4kmer += pow4s[d.k - j - 1] * base4s[i + j];
            }
            base4s[i] = base4kmer;
        }
        //
        List<Integer> test = new LinkedList<>();
        List<String> tests = new LinkedList<>();
        // TODO
        for (int i : base4s) { 
            if (test.size() >= 10 || test.size() == n - d.k + 1) {
                break;
            }
            test.add(i); tests.add(decode(i, d));
        }
        print("first 10 base4:\n%s\n%s\n",
            test, tests);
        
        return base4s;
    }
    
    static char mapInt(int d) {
        switch (d) {
            case 0:
                return 'A';
            case 1:
                return 'C';
            case 2:
                return 'G';
            case 3:
                return 'T';
            default:
                throw new RuntimeException("problem: " + d);
        }
    }

  static char complement(char d) {
        switch (d) {
            case 'T':
            case 't':
                return 'A';
            case 'G':
                case 'g':
                return 'C';
            case 'C':
                case 'c':
                return 'G';
            case 'A':
                case 'a':
                return 'T';
            default:
                throw new RuntimeException("problem: " + d);
        }
    }
      
    public static String [] decode(final Collection<Integer> nn, 
                                   final FastKmerSearchData d)
    {
        return decode(nn, d.k);
    }
    
    public static String [] decode(final int [] nn, 
                                   final int k)
    {
        List<Integer> n = new LinkedList<>();
        return decode(n, k);
    }
    
    public static String [] decode(final Collection<Integer> nn, 
                                   final int k)
    {
        String [] decoded = new String [nn.size()];
        int i = 0;
        for (int n : nn) {
            decoded[i++] = decode(n, k);
        }
        return decoded;
    }
    
    public static String decode(final int n, 
                                final FastKmerSearchData d)
    {
        return decode(n, d.k);
    }
    
    public static String decode(final int n, 
                                final int k)
    {
        int remain = n;
        String kmer = "";
        for (int i = k - 1; i >= 0; i--) {
            int place = remain / pow4s[i];
            remain -= place * pow4s[i];
            kmer += mapInt(place);
        }
        return kmer;
    }
    
    public static void main(String [] args) throws Exception {
        System.out.println("String:");
        FileInputs ins = FileInputs.scanFileSingleInput();
        
        String str = ins.sourceText0;
        
        String reverse = reverseComplement(str);
        System.out.println(reverse);
        TextFileUtil.writeKmersListPlus(ins.outputFile, Collections.singletonList(reverse));
    
    }
    
    public static String reverseComplement(String s)
    {
        String kmer = "";
        for (int i = s.length() -1; i >= 0; i--) {
            char place = s.charAt(i);
            kmer += complement(place);
        }
        return kmer;
    }
    
    public static int reverseComplementOf(final int kmerRC, 
                                             final FastKmerSearchData d) {
        int kmer = 0;
        boolean debugLog = false;
        for (int i = 0; i < d.k; ++i) {
            // final boolean debug = debugLog && i == 0 && kmerRC < 10000;
            final Integer [] placeRC = Base4er.pow4Permutations[i];
            final Integer [] place = Base4er.pow4Permutations[d.k - i - 1];
            
            if ((placeRC[3] & kmerRC) == placeRC[3]) {
                // noop T A
                
            }
            else if ((placeRC[2] & kmerRC) == placeRC[2]) {
                kmer += place[1]; // G C
                
            }
            else if ((placeRC[1] & kmerRC) == placeRC[1]) {
                kmer += place[2]; // C G
                
            }
            else { // if ((placeRC[] & kmerRC) == 0) {
                kmer += place[3]; // A T
                
            }
            if (debugLog && i + 1 == d.k) {
                print("\n%s:%s\n", decode(kmerRC, d), decode(kmer, d));
            }
        }
        return kmer; 
    }
    
}

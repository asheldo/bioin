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
            if (test.size() >= 10) {
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
        String [] decoded = new String [nn.size()];
        int i = 0;
        for (int n : nn) {
            decoded[i++] = decode(n, d);
        }
        return decoded;
    }
    
    public static String decode(final int n, final FastKmerSearchData d)
    {
        int remain = n;
        String kmer = "";
        for (int i = d.k - 1; i >= 0; i--) {
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
}

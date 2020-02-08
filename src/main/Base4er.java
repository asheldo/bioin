package main;
import java.util.*;

public class Base4er {
    // i.e. 2^32 is 4^16
    public static final int pow4s [ ] = pow(4, 15);

    static int [] pow(int base, int n)  {
        int [] pows = new int [n];
        pows[0] = 1;
        for (int i = 1; i < n; i++) {
            pows[i] = pows[i-1] * base;
        }
        return pows;
    }

    static int mapChar(char c) {
        switch (c) {
            case 'A':
                return 0;
            case 'C':
                return 1;
            case 'G':
                return 2;
            case 'T':
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
        for (int i : base4s) { 
            test.add(i); tests.add(reverse(i, d));
        }
        System.out.println(test);
        System.out.println(tests);
        //
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
                return 'A';
            case 'G':
                return 'C';
            case 'C':
                return 'G';
            case 'A':
                return 'T';
            default:
                throw new RuntimeException("problem: " + d);
        }
    }
      
    public static String reverse(final int n, final FastKmerSearchData d)
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

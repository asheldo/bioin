package main;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Processor {
	
    static long t = System.currentTimeMillis();
    
    public static long checkpoint() {
        long t0 = t;
        t = System.currentTimeMillis();
        return t - t0;
    }
    
    public static long t() {
        return System.currentTimeMillis();
    }
    
    
    public static List<Integer> toInt(String ... ss) {
        List<Integer> is = new LinkedList<>();
        for (String s : ss) {
            is.add(Integer.parseInt(s));
        }
        return is;
    }
    
    public static Integer toInt(String s) {
        return Integer.parseInt(s);
    }
    
    public static int sum(int [] i) {
        int sum = 0;
        for (int n : i) {
            sum  += n;
        }
        return sum;
    }
    
    public static String extension(String file, String outExt) {
        return file.replaceAll("\\.[^.]*$", outExt);
    }
	/** shorthand for String.format */
	public static String Sf(String s, Object ... args) {
		return String.format(s, args);
	}
	
    static List list(Object [] o) {
        return Arrays.asList(o);
    }
    
    static List listD(int [] o, int fac) {
        List<Double> dd = new ArrayList<>();
        for (double d : o) {
            dd.add((double)((int) (d * fac))/fac);
        }
        return dd;
    }
    
    static List listD(double [] o, int fac) {
        List<Double> dd = new ArrayList<>();
        for (double d : o) {
            dd.add((double)((int) (d * fac))/fac);
        }
        return dd;
    }
    
    public static void printlnBases(int [] bases, int k) {
        String [] decodes = Base4er.decode(bases, k);
        // List<String> list = Arrays.asList(decodes);
        for (String s : decodes) {
            String r = s.replaceAll("(\\w)", "$1 ");
            System.out.println(r);
        }
        
    }
	
	public static void println(String m) {
		System.out.println(m);
	}
	
    public static void printif(boolean b, String m, Object ... args) {
        if (b) print(m, args);
    }
    
	public static void print(String m, Object ... args) {
        try {
          
		    System.out.print(Sf(m, args));
        } catch (Exception e) {
            print("error %s", m);
            e.printStackTrace(System.err);
        }
	}

}

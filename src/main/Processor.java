package main;
import java.io.*;
import java.util.*;

public class Processor {
	
    static long t = System.currentTimeMillis();
    
    public static long checkpoint() {
        long t0 = t;
        t = System.currentTimeMillis();
        return t - t0;
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
    
	public static void println(String m) {
		System.out.println(m);
	}
	
	public static void print(String m, Object ... args) {
        try {
          
		    System.out.print(Sf(m, args));
        } catch (Exception e) {
            print("error %s", e);
        }
	}

}

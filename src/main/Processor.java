package main;
import java.io.*;
import java.util.*;

public class Processor {
	
	/** shorthand for String.format */
	static String Sf(String s, Object ... args) {
		return String.format(s, args);
	}
	
	static void println(String m) {
		System.out.println(m);
	}
	
	static void print(String m, Object ... args) {
		System.out.print(Sf(m, args));
	}

}

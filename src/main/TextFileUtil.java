package main;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import org.apache.commons.codec.binary.*;

public class TextFileUtil {
	
	/** 
	 * first two lines only
	 */
    static List<String> readTextAndK(String filePath) throws Exception
	{
		List<String> contentBuilder = new LinkedList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) 
			{
                
                if (sCurrentLine.trim().length() > 0) {
                    contentBuilder.add(sCurrentLine);
                }
			}
			br.close();
		} 
		catch (IOException e) 
		{
			throw e;
		}
		return contentBuilder;
	}
	
    static void writeLists(
        List ... outs) throws Exception
	{

    }

    public static void writeKmersListPlus(
                                        final String filePath,
                                        final Object ... outs) throws Exception{
        writeKmersListPlus("", new FileInputs(filePath),
            outs);
    }
    
	public static void writeKmersListPlus(
        final String delim,
        final FileInputs fileInputs,
	    Object ... outs) throws Exception
	
	{
        try {
            int n = 0;
			FileWriter fw = new FileWriter(fileInputs.outputFile);
			for (Object out : outs) {
                if (n++ > 0) {
                    fw.write("\n");
                }
                if (out instanceof Iterable) {
                    Iterator i = ((Iterable) out).iterator();
                    while (true) {
                        fw.write(i.next());
                        if (i.hasNext()) {
                            fw.write("\n");
                        } else {
                            break;
                        }
                    }
                } else {
			        fw.write(out == null 
                        ? "null" : delim == "" 
                        ? out.toString()
                        : out.toString().replaceAll("[, ]", "\n"));
				}
			}
			fw.flush();
			fw.close();
		} 
		catch (IOException e) {
			throw e;
		}
	}
}

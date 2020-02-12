package main;

import java.io.*;
import java.util.*;

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
    
	public static void writeKmersListPlus(final String filePath,
	    Object ... outs) throws Exception
	{
		try {
			FileWriter fw = new FileWriter(filePath);
			for (Object out : outs) {
			    fw.write(out.toString());
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		} 
		catch (IOException e) {
			throw e;
		}
	}
}

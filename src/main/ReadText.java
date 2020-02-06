package main;

import java.io.*;
import java.util.*;

public class ReadText {
	
    static List<String> bufferedLines(String filePath) throws Exception
	{
		List<String> contentBuilder = new LinkedList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) 
			{
				contentBuilder.add(sCurrentLine);
			}
			br.close();
		} 
		catch (IOException e) 
		{
			throw e;
		}
		return contentBuilder;
	}
}

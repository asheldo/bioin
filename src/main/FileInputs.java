package main;

import java.io.*;
import java.util.*;

public class FileInputs extends Processor {

    public String sourceText0;

    public String param1;
    
    public List<String> params;

    public Set<String> options = new HashSet<>();

    public String outputFile;

    @Override
    public String toString() {
        return "param1:" + param1.substring(0, 2);
     }

    public static FileInputs scanFileInputs() throws Exception {
        return scanFileInputs("Vibrio_cholerae.txt");
    }
    
    public static FileInputs scanFileInputs(final String f) throws Exception {

        FileInputs m = new FileInputs();

        BufferedReader input = new BufferedReader(
            new InputStreamReader(System.in));
        String filePath =
            new StringBuilder("/storage/emulated/0/AppProjects/")
            .append("%s").append("/src/main/assets/")
            .append("%s").toString();
        final String p = "bioin";
        print("Project with asset:");
        println(Sf("(default: %s)", p));
        String proj = input.readLine().trim();
        
        print("Filename of text and k asset:");
        println(Sf("(default: %s)", f));
        final String file = input.readLine().trim();
        LinkedList<String> options = new LinkedList<>();
        print("Comma-separated options:");
        println(Sf("(default: %s)", options));
        final String opts = input.readLine().trim();
        for (String opt : opts.split(",")) { 
            m.options.add(opt);
            println(opt);
        }

        filePath = Sf(filePath, 
                      proj.isEmpty() ? p : proj, 
                      file.isEmpty() ? f : file);
                      
        List<String> data
            = TextFileUtil.readTextAndK(filePath);
        m.params = data;
        print("read: %s\n", data.size());
        m.sourceText0 = data.get(0);
        if (data.size() > 1) {
            m.param1 = data.get(1);
        }

        m.outputFile = filePath + ".out";
        input.close();
        return m;
    }
    
    public static FileInputs scanFileSingleInput() throws Exception {

        FileInputs m = new FileInputs();

        BufferedReader input = new BufferedReader(
            new InputStreamReader(System.in));
        String filePath =
            new StringBuilder("/storage/emulated/0/AppProjects/")
            .append("%s").append("/src/main/assets/")
            .append("%s").toString();
        final String p = "bioin";
        print("Project with asset:");
        println(Sf("(default: %s)", p));
        String proj = input.readLine().trim();
        final String f = "String.txt";
        print("Filename of single text asset:");
        println(Sf("(default: %s)", f));
        final String file = input.readLine().trim();
        LinkedList<String> options = new LinkedList<>();
        print("Comma-separated options:");
        println(Sf("(default: %s)", options));
        final String opts = input.readLine().trim();
        for (String opt : opts.split(",")) { 
            m.options.add(opt);
            println(opt);
        }

        filePath = Sf(filePath, 
                      proj.isEmpty() ? p : proj, 
                      file.isEmpty() ? f : file);
        List<String> data
            = TextFileUtil.readTextAndK(filePath);
        m.sourceText0 = data.get(0);
        // m.param1 = data.get(1);

        m.outputFile = filePath + ".out";
        input.close();
        return m;
    }
}
	

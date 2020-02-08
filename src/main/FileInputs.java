package main;

import java.io.*;
import java.util.*;

public class FileInputs extends Processor {

    String sourceText0;

    String param1;

    Set<String> options = new HashSet<>();

    String outputFile;

    @Override
    public String toString() {
        return "param1:" + param1;
     }

    public static FileInputs scanFileInputs() throws Exception {

        FileInputs m = new FileInputs();

        BufferedReader input = new BufferedReader(
            new InputStreamReader(System.in));
        String filePath =
            new StringBuilder("/storage/emulated/0/AppProjects/")
            .append("%s").append("/src/main/assets/")
            .append("%s").toString();
        final String p = "bioin";
        System.out.print("Project with asset:");
        System.out.println(Sf("(default: %s)", p));
        String proj = input.readLine().trim();
        final String f = "Vibrio_cholerae.txt";
        System.out.print("Filename of text and k asset:");
        System.out.println(Sf("(default: %s)", f));
        final String file = input.readLine().trim();
        LinkedList<String> options = new LinkedList<>();
        System.out.print("Comma-separated options:");
        System.out.println(Sf("(default: %s)", options));
        final String opts = input.readLine().trim();
        for (String opt : opts.split(",")) { 
            m.options.add(opt);
            System.out.println(opt);
        }

        filePath = Sf(filePath, 
                      proj.isEmpty() ? p : proj, 
                      file.isEmpty() ? f : file);
        List<String> data
            = TextFileUtil.readTextAndK(filePath);
        m.sourceText0 = data.get(0);
        m.param1 = data.get(1);

        m.outputFile = filePath + ".out";
        input.close();
        return m;
    }
}
	

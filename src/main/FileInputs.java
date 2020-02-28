package main;

import java.io.*;
import java.util.*;

public class FileInputs extends Processor {

    public enum InputOption {
        INPUT_FILE("E_coli.txt", 
                   "Vibrio_cholerae.txt", "datasetx.txt"),
        SEARCH_METHOD("textual", "hamming");

        public final List<String> suggest;

        InputOption(String ... suggest) {
            this.suggest = Arrays.asList(suggest);
        }
    }
   

    public static Map<InputOption,String> mapIn(String inputFile) {
        Map<InputOption,String> map = new HashMap<>();
        map.put(InputOption.INPUT_FILE, inputFile);                                 
        return map;
    }
    
    public static FileInputs scanFileInputs() throws Exception {
        return scanFileInputs(mapIn(
            InputOption.INPUT_FILE.suggest.get(1)));
    }
    
    public static FileInputs scanFileInputs(final String suggest) throws Exception {
        return scanFileInputs(mapIn(suggest));
    }
    
    public static FileInputs scanFileInputs(final Map<InputOption,String> inputOptions) throws Exception {
        String suggest = inputOptions.get(InputOption.INPUT_FILE);   
        FileInputs m = new FileInputs();
        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in)))
        {
            m.scan(input, suggest);
        }
        return m;
    }
    

    /** source just base lines */
    public static FileInputs scanFileSingleInput() throws Exception {
        return scanFileSingleInput(1, "dataset.txt");
    }
    
    public static FileInputs scanFileSingleInput(int take,
                                                 String f) throws Exception {
         return scanFileSingleInput(take, " ", f, false);
    }
    
    public static FileInputs scanFileSingleInput(final int take,
                                                 final String f,
                                                 final boolean paramPrefix) throws Exception {
        return scanFileSingleInput(take, " ", f, paramPrefix);
    }
    
    
    public static FileInputs scanFileSingleInput(
            final int take, 
            final String joinDelimiter,                                               
            final String f,
            final boolean paramPrefix) throws Exception {

        FileInputs m = new FileInputs(paramPrefix);
        BufferedReader input = new BufferedReader(
            new InputStreamReader(System.in));
        m.scanLines(input, f, take, joinDelimiter);
        input.close();
        return m;
    }


    public static FileInputs scanFileParamsAndSources(                                         
        final String f) throws Exception {
        boolean paramPrefix = true;
        FileInputs m = new FileInputs(paramPrefix);
        BufferedReader input = new BufferedReader(
            new InputStreamReader(System.in));
        m.scanLinesArray(input, f);
        input.close();
        return m;
    }
    
    public Optional<String> preface = Optional.empty();
    public Optional<String> paramPrefix = Optional.empty();
    
    public String sourceText0 = "";

    public String [] sourceText = new String [1];

    public String epilogue = "";

    public String param1;

    public List<String> params = new LinkedList<>();;

    public Set<String> options = new HashSet<>();

    public String filePath;

    public String outputFile;
    
    private final boolean hasParamPrefix;

    FileInputs() {
        this(false);
    }
    
    FileInputs(boolean hasParamPrefix) {
        this.hasParamPrefix = hasParamPrefix;
    }
    
    @Override
    public String toString() {
        return String.format(
            "param1: %s, content len ~ %d ",
            param1.substring(0, 2), 
            this.sourceText0.length());
    }
    
    void scan(final BufferedReader input,
              final String suggest) throws Exception
    {
        filePath =
            new StringBuilder("/storage/emulated/0/AppProjects/")
            .append("%s").append("/src/main/assets/")
            .append("%s").toString();
        final String p = "bioin";
        print("Project with asset:");
        println(Sf("(default: %s)", p));
        String proj = input.readLine().trim();

        print("Filename of text and k asset:");
        println(Sf("(default: %s)", suggest));
        final String file = input.readLine().trim();
        // options = new LinkedList<>();
        print("Comma-separated options:");
        println(Sf("(default: %s)", options));
        final String opts = input.readLine().trim();
        for (String opt : opts.split(",")) { 
            options.add(opt);
            println(opt);
        }

        filePath = Sf(filePath, 
                      proj.isEmpty() ? p : proj, 
                      file.isEmpty() ? suggest : file);

        List<String> data
            = TextFileUtil.readTextAndK(filePath);
        params = data;
        print("read: %s\n", data.size());
        sourceText0 = data.get(0);
        if (data.size() > 1) {
            param1 = data.get(1);
        }
        outputFile = filePath + ".out";
    }
    
    void scanLinesArray(final BufferedReader input, 
                        final String f) throws Exception
    {
        scanLines(input, f, -1, null);
    }
    
    void scanLines(final BufferedReader input, 
                   final String f,
                   final int take,
                   final String joinDelimiter) throws Exception
    {
        filePath =
            new StringBuilder("/storage/emulated/0/AppProjects/")
            .append("%s").append("/src/main/assets/")
            .append("%s").toString();
        final String p = "bioin";
        print("Project with asset:");
        println(Sf("(default: %s)", p));
        String proj = input.readLine().trim();

        print("Filename of single text asset:");
        println(Sf("(default: %s)", f));
        final String file = input.readLine().trim();
        // LinkedList<String> options = new LinkedList<>();
        print("Comma-separated options:");
        println(Sf("(default: %s)", options));
        final String opts = input.readLine().trim();
        for (String opt : opts.split(","))
        { 
            options.add(opt);
            println(opt);
        }
        filePath = Sf(filePath, 
                      proj.isEmpty() ? p : proj, 
                      file.isEmpty() ? f : file);
        outputFile = filePath + ".out";
        if (joinDelimiter != null) {
            readSourceLines(take, joinDelimiter);
        } else {
            readSourceLinesArray();
        }
    }

    // sourceText
    
    public void readSourceLines(final int take) throws Exception {
       readSourceLines(take, " ");
    }
        
    public void readSourceLines(final int take,
                                final String joinDelimiter) throws Exception
    {
        final List<String> data =
            TextFileUtil.readTextAndK(filePath);
        int lim = take < 0 ? data.size() : take;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lim; ++i) {
            String get = data.get(i);
            if (get == null) {
                continue;
            }
            if (i > 0 ||
                    (takeParamPrefix(get)
                    && !takePreface(get))) 
                {
                sb.append(data.get(i));
                if (i < take) { // splitable
                    sb.append(joinDelimiter);
                }
                if (i % 1000 == 0) 
                    print("" + (char) ('\\' + i % 8));
            }
            if (!paramPrefix.isPresent() &&
                    !preface.isPresent()) {
                        params.add(get);
                        if (i == 0) {

                            param1 = get;
                        }
                    }
        }
        sourceText[0] = sourceText0 = sb.toString();
    }


    public void readSourceLinesArray() throws Exception
    {
        final List<String> data =
            TextFileUtil.readTextAndK(filePath);
        int lim = data.size();
        sourceText = new String [lim];
        for (int i = 0; i < lim; ++i) {
            String get = data.get(i);
            if (get != null && (i > 0 ||
                (takeParamPrefix(get)
                && !takePreface(get)))) 
            {
                sourceText[i-1] = data.get(i);
                if (i % 10 == 0) {
                    print("" + (char) ('\\' + i % 8));
                }
            }
        }
        sourceText0 = sourceText[0];
    }
    
    private boolean takePreface(final String get) {
        if (!preface.isPresent()) {
            String pre = get.substring(0, 
                                       Math.min(4, get.length()));
            if (!pre.matches("\\s*[A-Da-d]+"))
            {
                preface = Optional.of(get);
                println(get);
                return true; // continue;
            }
            else
            {
                preface = Optional.of("");
            }
        }
        return false;
    }
    
    private boolean takeParamPrefix(final String get) {
        if (hasParamPrefix && !paramPrefix.isPresent())
        {
            String pre = get.substring(0, 
                Math.min(4, get.length()));
            if (pre.matches("\\s*[\\d ]+"))
            {
                paramPrefix = Optional.of(get);
                params = Arrays.asList(get.split(" "));
                print("params from: ", get, params);
                return true; // continue;
            }
            else
            {
                paramPrefix = Optional.of("");
            }
        }
        return false;
    }
}
	

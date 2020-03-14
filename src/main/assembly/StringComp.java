package main.assembly;

import main.Processor;
import main.*;
import java.util.*;
import java.util.concurrent.*;
 

// https://stepik.org/lesson/196/?unit=8239
public class StringComp extends Processor
{
    public static void main(String [] args) throws Exception {
        String f = "2_1/dataset.txt";
        FileInputs fis = 
            FileInputs.scanFileInputs(f);
        if (fis.options.contains("euler")) {
            euler(fis);
        } else if (fis.options.contains("debk")) {
            debruijnKmers(fis);
        } else if (fis.options.contains("debr")) {
            debruijn(fis);
        } else if (fis.options.contains("adj")) {
            adjacency(fis);
        }
        else if (fis.options.contains("decomp")) {
            decomp(fis);
        } 
        else {
            compose(fis);
        }
    }
        
    static void debruijn(FileInputs fis) throws Exception {
        int k = Integer.parseInt(fis.params.get(0));
        println("" + k);
        String text = fis.params.get(1);
        int n = text.length() - k + 1;
        checkpoint();
        KmerStringSearchData d =
            new KmerStringSearchData(text,
                k, n, 0);
        //
        StringComp composer =
            new StringComp(d.kmers);
        List<Node> debruijn = composer.graphGlue(); // sorted
        
        String out = adjacencyOut(debruijn);
        TextFileUtil.writeKmersListPlus("",
            fis, 
            // graph,
            out);
    }
    
    static void debruijnKmers(FileInputs fis) throws Exception {
        println("" + fis.params.size());
        checkpoint();
        StringComp composer =
            new StringComp(convert(fis.params));
        List<Node> debruijn = composer.graphGlue(); // sorted

        String out = adjacencyOut(debruijn);
        TextFileUtil.writeKmersListPlus("",
                                        fis, 
                                        // graph,
                                        out);
    }
    
    static class IntNode {
        Integer l; //value
        
        List<Integer> rs = new LinkedList<>();
        
        IntNode(String l, String ... rs) {
            this.l = toInt(l);
            for (String r : rs) {
                this.rs.add(toInt(r));
            }
        }
    }
    
    /* yeah right
     EulerianCycle(Graph)
     form a cycle Cycle by randomly walking in Graph 
     (don't visit the same edge twice!)
     while there are unexplored edges in Graph
       select a node newStart in Cycle with still unexplored edges
       form Cycle’ by traversing Cycle (starting at newStart)
       and then randomly walking 
       Cycle ← Cycle’
     return Cycle
     */
    static void euler(final FileInputs fis) throws Exception {
        int n = fis.params.size();
        Random rnd = new Random();
        println("" + n + " @" + checkpoint());
        List<Integer> euler = new LinkedList<Integer>();
        IntNodeMap cloner = readAdj(fis);
        int pick = rnd.nextInt(cloner.size());
        Map.Entry<Integer,List<Integer>> start
            = cloner.getEntry(pick);
        List<Integer> outs = start.getValue();
        Integer in = start.getKey();
        long t = t();
        int i = 0;
        try {
            euler = new LinkedList<Integer>();
            LinkedList<Integer> stack = 
                new LinkedList<Integer>();
            while (true) {
                if (outs.isEmpty()) {
                    // adj.remove(in);
                    euler.add(in);
                    in = stack.pop();
                    outs = cloner.get(in);
                    // --sz;
                    if (stack.isEmpty()) {
                        euler.add(start.getKey());
                        break;
                    }
                    continue;
                }
                // random step
                stack.push(in);
                in = outs.remove(rnd.nextInt(outs.size()));
                outs = cloner.get(in);
                if (t() - t > 120000L) {             
                    throw new TimeoutException("60s");
                } else if ((++i) % 500 == 0) {
                    println(in + " = " + outs.toString() + " .. " + euler + " ... " + stack);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        Collections.reverse(euler);
        String out = eulerPathOut(euler);
        TextFileUtil.writeKmersListPlus("",
            fis, 
            out);
    }

    static class IntNodeMap extends LinkedHashMap<Integer,List<Integer>> {
        Map.Entry<Integer,List<Integer>> getEntry(int n) {
            Iterator<Map.Entry<Integer,List<Integer>>> e = 
                entrySet().iterator();
            while (e.hasNext()) {
                Map.Entry<Integer,List<Integer>> x =
                    e.next();
                if (0 == n--) {
                    return x;
                }
            }
            return null;
        }

        @Override
        public IntNodeMap clone()
        {
            IntNodeMap copy = (IntNodeMap) super.clone();
            for (Map.Entry e : copy.entrySet()) {
                List<Integer> list = (List<Integer>) e.getValue();
                e.setValue(new LinkedList<>(list));
            }
            return copy;
        }
       
    }
    
    private static IntNodeMap readAdj(FileInputs fis)
    {
        IntNodeMap map = new IntNodeMap<>();
        for (String adj : fis.params) {
            String [] l = adj.split(" -> ");
            String [] rs = l[1].split(",");
            map.put(toInt(l[0]), toInt(rs));
        }
        return map;
    }
    
    static void decomp(FileInputs fis) throws Exception {
        int k = Integer.parseInt(fis.params.get(0));
        println("" + k);
        String text = fis.params.get(1);
        int n = text.length() - k + 1;
        KmerStringSearchData d =
            new KmerStringSearchData(text,
                                     k, n, 0);
        for (char [] kmer : d.kmers) {
            println(new String(kmer));
        }
        TextFileUtil.writeKmersListPlus(
            fis.outputFile, 
            d.kmers);
    }
    
    static void compose(FileInputs fis) throws Exception {
        // int k = Integer.parseInt(fis.params.get(0));
        Integer L = fis.params.size();
        println(L.toString());
        List<String> kmers = fis.params; //.subList(1, L);
        StringComp composer =
            new StringComp(convert(kmers));
        char [] text = composer.join(composer.reads);
        println(new String(text));
        TextFileUtil.writeKmersListPlus(
            fis.outputFile, 
            text);
    }

    static void adjacency(FileInputs fis) throws Exception {
        Integer L = fis.params.size();
        println(L.toString());
        List<String> kmers = fis.params; //.subList(1, L);
        StringComp composer =
            new StringComp(convert(kmers));
        List<Node> graph = composer.graph(false);
        String out = adjacencyOut(graph);
        TextFileUtil.writeKmersListPlus("",
            fis,
            // graph,
            out);
    }

    static String eulerPathOut(List<Integer> graph) {
        print("t=%d\n", checkpoint());
        StringBuilder out = new StringBuilder();
        for (Integer n : graph) {
            if (out.length() > 0) {
                out.append("->");
            }
            out.append(n);
        }
        print("o-t=%d\n", checkpoint());
        if (out.length() < 500) {
            println(out.toString());
        }
        return out.toString();
    }
    
    static String adjacencyOut(List<Node> graph) {
        print("t=%d\n", checkpoint());
        String out = "";
        for (Node n : graph) {
            if (n.rights != null) {
                String es = n.rights.toString();
                out += String.format("%s -> %s\n", n.kmer,
                                     es.substring(1, es.length() - 1).replaceAll(" ", ""));
            }
        }
        out = out.substring(0, out.length() - 1);
        print("o-t=%d\n", checkpoint());
        if (out.length() < 500)
        {
            println(out);
        }
        return out;
    }
    
    public static char [] prefix(char [] kmer) {
        return Arrays.copyOfRange(kmer, 0, kmer.length - 1);
    }
    
    public static char [] suffix(char [] kmer) {
        return Arrays.copyOfRange(kmer, 1, kmer.length);
    }
    
    public static char [][] convert(List<String> readsList) {
        int s = readsList.size();
        char [][] reads = new char [s][];
        for (int i = 0; i < s; ++i) {
            reads[i] = readsList.get(i)
                .toCharArray();
        }
        // println(reads);
        return reads;
    }
    
    public static List<String> convert(char [][] reads) {
        List<String> s = new LinkedList<>();
        for (char [] r : reads) {
            s.add(new String(r));
        }
        // println(reads);
        return s;
    }
    
    
    
    /**
    has to: 
    sort lex-ly
    
     */
    static class Node implements Comparable<Node> { 
        int readIndex;
        String kmer;
        int k;
        List<Node> rights = new LinkedList<>();
        Node edgeRight;
        Node edgeLeft;
        boolean visited;
        
        public Node(int readIndex,
                    String kmer,
                    int k) 
        {
            this.readIndex = readIndex;
            this.kmer = kmer;
            this.k = k;
        }

        @Override
        public String toString() {
            return kmer;
        }
        
        @Override
        public int compareTo(StringComp.Node n) {
            return kmer.compareTo(n.kmer);
        }
        
        public String prefix() {
            return kmer.substring(0, k - 1);
        }

        public String suffix() {
            return kmer.substring(1, k);
        }
    }
    
    //
    
    char [][] reads;
    int k;
    int L;

    public StringComp(char [][] reads) {
        this.reads = reads;
        k = reads[0].length;
        L = reads.length + k - 1;
    }
    
    public char[] join(char [][] kmers) {
        char [] joined = new char [L];
        int ks = kmers.length;
        
        int n = 0;
        while (n < k) {
            println("" + new String(kmers[0]) + " " + n + " " + k);
            joined[n] = kmers[0][n];
            ++n;
        }
        for (int i = 1; i < ks; ++i) {
            println("" + new String(kmers[i]) + " " + i + " " + ks);
            joined[n++] = kmers[i][k-1];
        }
        return joined;
    }
    
    void dfs(Node node) {
        println(node.kmer);
        
        node.visited = true;
        
        
        for (Node n : node.rights) {
            if (!n.visited) {
                dfs(n);
            }
        }
    }
    
    public List<Node> graphGlue() {
        
        LinkedList<Node> graph =
            new LinkedList<>();
        LinkedList<Node> nodes =
            new LinkedList<>();
        // 1
        // Node x = null;
        for (int i = 0; i < reads.length; ++i) {
            Node n = new Node(i,
                new String(reads[i]).substring(0, k-1), 
                k-1);
            nodes.add(n);
            
            Node last = new Node(i + 1,
                                 new String(reads[i]).substring(1, k), 
                                 k-1);
            n.rights.add(last);
        }
        
        // 2 - sort and glue lefts, sort rights
        Node x = null;
        Collections.sort(nodes);
        for (Node n : nodes) {
            if (x == null || n.compareTo(x) != 0) {
                graph.add(n);
                x = n;
            } else {
                x.rights.addAll(n.rights);
            }
        }
        for (Node n : graph) {
            Collections.sort(n.rights);
        }
        
        return graph;
    }
    
    /**
     sort 
     then link prefixes/suffixes
     find linkless prefix (start)
     ?
     */
    public List<Node> graph(boolean link) {
        LinkedList<Node> graph =
            new LinkedList<>();
        LinkedList<Node> ordered =
            new LinkedList<>();
        
        // 1
        for (int i = 0; i < reads.length; ++i) {
            Node n = new Node(i,
                new String(reads[i]), k);
            ordered.add(n);
        }
        // 2
        LinkedHashMap<String, List<Node>> prefixes
            = new LinkedHashMap<>();
        for (Node n : ordered) {
            String pre = n.prefix();
            prefixes.putIfAbsent(pre, new LinkedList<Node>());
            prefixes.get(pre).add(n); // in order b/c of sort
        }
        // List<Node> lefts = ordered.subList(0, reads.length-1);
        // 3
        for (Node o : ordered) {
            o.rights = prefixes.get(o.suffix());
        }
        // 4 a/b
        if (!link) {
            {
                Collections.sort(ordered);
                return ordered; // with rightNodes
            }
            
        }
        
        return graph;
        // 4
       /*
        LinkedList<Node> checkpoint = graph;
        
        
        dfs(start);
        
        
        // xxxxx
        while (!todo.isEmpty()) {
            Iterator<Node> iterP = graph.iterator();
            while (iterP.hasNext()) {
                left = iterP.next();
            }
            if (left.edgesRight.size() == 0) {
                graph = checkpoint; // reset
                todo = new LinkedList<>(sorted);
                continue;
            } else if (left.edgesRight.size() == 1) {
                left = left.edgesRight.get(0);
                graph.add(left);
                checkpoint = graph;
                todo.remove(todo);
                continue;
            } else {
                for (Node test : left.edgesRight) {
                    proposed.add(test);
                    
                }
            }
            
        }
         graph = sorted;

         return graph;
        */
        
        
    }
    
    /* todo */
    public List<char[][]> arrangements(char [][] kmers) {
        List<char[][]> list = new LinkedList<>();
        list.add(kmers);
        return list;
    }
}

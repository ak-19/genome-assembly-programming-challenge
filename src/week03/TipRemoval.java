package week03;

import java.io.FileNotFoundException;
import java.util.*;

public class TipRemoval {
    public static void main(String[] args) throws FileNotFoundException {
        // Scanner reader = new Scanner(new FileInputStream("files/TipRemoval.txt"));
        Scanner reader = new Scanner(System.in);

        int k = 15;
        List<String> reads = new ArrayList<String>();

        while (reader.hasNext()) {
            reads.add(reader.next());
        }

        new Thread(null, new Runnable() {
            public void run() {
                try {
                    TipRemoval tipRemoval = new TipRemoval(reads, k);
                    System.out.println(tipRemoval.tipsNumber);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }, "1", 1 << 26).start();
    }

    public TipRemoval(List<String> reads, int k) {
        constructDeBruijnGraph(reads.get(0).length(), k, reads);
        boolean foundTip = true;

        while (foundTip) {
            foundTip = false;
            for (String key : graph.keySet()) {
                BidirectionalVertex vertex = graph.get(key);
                if (vertex.to.size() == 0) {
                    for (String from : vertex.from.keySet()) {
                        graph.get(from).to.remove(key);
                    }
                    graph.remove(key);
                    foundTip = true;
                    tipsNumber++;
                    break;
                } else if (vertex.from.size() == 0) {
                    for (String to : vertex.to.keySet()) {
                        graph.get(to).from.remove(key);
                    }
                    graph.remove(key);
                    foundTip = true;
                    tipsNumber++;
                    break;
                }

            }
        }

    }


    private int tipsNumber = 0;
    private HashMap<String, BidirectionalVertex> graph;


    private void constructDeBruijnGraph(int N, int k, List<String> reads) {
        graph = new HashMap<String, BidirectionalVertex>();
        for (String read : reads) {
            for (int startPosition = 0; startPosition <= N - k; startPosition++) {
                String prefix = read.substring(startPosition, startPosition + k - 1);
                String suffix = read.substring(startPosition + 1, startPosition + k);
                //exclude self edged
                if (!prefix.equals(suffix)) {
                    BidirectionalVertex prefixVertex;
                    if (!graph.containsKey(prefix)) {
                        prefixVertex = new BidirectionalVertex();
                    } else {
                        prefixVertex = graph.get(prefix);
                    }
                    prefixVertex.to.put(suffix, 1);
                    graph.put(prefix, prefixVertex);

                    BidirectionalVertex suffixVertex;
                    if (!graph.containsKey(suffix)) {
                        suffixVertex = new BidirectionalVertex();
                    } else {
                        suffixVertex = graph.get(suffix);
                    }
                    suffixVertex.from.put(prefix, 1);
                    graph.put(suffix, suffixVertex);
                }
            }
        }

    }

    private static class BidirectionalVertex {
        private HashMap<String, Integer> from;
        private HashMap<String, Integer> to;

        public BidirectionalVertex() {
            this.from = new HashMap<String, Integer>();
            this.to = new HashMap<String, Integer>();
        }
    }
}

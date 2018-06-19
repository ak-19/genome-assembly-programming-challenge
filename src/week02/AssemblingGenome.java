package week02;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class AssemblingGenome {
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        int linesToRead = 5396;
        int k = 10;
        new Thread(null, new Runnable() {
            public void run() {
                try {
                    List<String> k_mers = new ArrayList<String>();
                    for (int i = 1; i <= linesToRead; i++) {
                        k_mers.add(reader.next());
                    }
                    UniversalString us = new UniversalString(k, k_mers);
                    System.out.print(us);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }, "1", 1 << 26).start();
    }

    private static class UniversalString {
        private static class Edge {
            String from, to;
            boolean used = false;

            public Edge(String from, String to) {
                this.from = from;
                this.to = to;
            }
        }

        private int k;
        private List<String> k_mers;
        HashMap<String, List<Edge>> k_1_mer = new HashMap<String, List<Edge>>();
        private StringBuilder universalString = null;

        public UniversalString(int k, List<String> k_mers) {
            this.k = k;
            this.k_mers = k_mers;
            generateGraphFromKmers();
        }

        private void generateGraphFromKmers() {
            for (String km : k_mers) {
                String fromK_1 = km.substring(0, k - 1);
                String toK_1 = km.substring(1);
                List<Edge> fromAdj = k_1_mer.get(fromK_1);
                if (fromAdj == null) {
                    fromAdj = new ArrayList<Edge>();
                }
                fromAdj.add(new Edge(fromK_1, toK_1));
                k_1_mer.put(fromK_1, fromAdj);
            }

            Stack<String> path = findEulerPath(k_mers.get(0).substring(0, k - 1));
            universalString = new StringBuilder(path.pop());
            while (path.size() > k - 2 + 1) {
                String kmer = path.pop();
                universalString.append(kmer.substring(kmer.length() - 1));
            }
        }

        @Override
        public String toString() {
            return universalString.toString();
        }

        private Stack<String> findEulerPath(String start) {
            Stack<String> path = new Stack<String>();
            for (Edge e : k_1_mer.get(start)) {
                if (!e.used) {
                    e.used = true;
                    findPath(e.to, path);
                }
            }
            path.push(start);
            return path;
        }

        private void findPath(String kmer, Stack<String> path) {
            for (Edge e : k_1_mer.get(kmer)) {
                if (!e.used) {
                    e.used = true;
                    findPath(e.to, path);
                }
            }
            path.push(kmer);
        }
    }
}

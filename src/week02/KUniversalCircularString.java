package week02;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class KUniversalCircularString {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner reader = new Scanner(System.in);
        //Scanner reader = new Scanner(new FileInputStream("files/KUniversalCircularString.txt"));
        int k = reader.nextInt();

        new Thread(null, new Runnable() {
            public void run() {
                try {
                    UniversalKString us = new UniversalKString(k);
                    System.out.println(us);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }, "1", 1 << 26).start();
    }

    private static class UniversalKString {
        private static class Edge {
            int from, to;
            boolean used = false;

            public Edge(int from, int to) {
                this.from = from;
                this.to = to;
            }
        }

        private int k;
        private int V;
        private List<int[]> k_mers;
        HashMap<String, Integer> k_1_merIndex = new HashMap<String, Integer>();
        HashMap<Integer, String> index_k_1 = new HashMap<Integer, String>();
        private StringBuilder universalString = null;

        public UniversalKString(int k) {
            this.k = k;
            k_mers = getFullKMerList(k);
            V = k_mers.size() / 2;
            generateGraphFromKmers();

        }

        private void generateGraphFromKmers() {
            int index = 0;
            List[] adj;
            adj = new List[V];
            for (int i = 0; i < V; i++) {
                adj[i] = new ArrayList<Edge>();
            }

            for (int[] km : k_mers) {
                String from = getSubustringFromBinArray(0, k - 2, km);
                String to = getSubustringFromBinArray(1, k - 1, km);
                Integer fromIndex = k_1_merIndex.get(from);
                if (fromIndex == null){
                    fromIndex = index++;
                    k_1_merIndex.put(from, fromIndex);
                    index_k_1.put(fromIndex, from);

                }

                Integer toIndex = k_1_merIndex.get(to);

                if (toIndex == null){
                    toIndex = index++;
                    k_1_merIndex.put(to, toIndex);
                    index_k_1.put(toIndex, to);
                }

                adj[fromIndex].add(new Edge(fromIndex, toIndex));
            }

            List<Integer> eulerPath = findEulerPath(adj);
            universalString = new StringBuilder(index_k_1.get(eulerPath.get(eulerPath.size() - 1)));
            for (int i = eulerPath.size() - 2; i > 0; i--) {
                String curr = index_k_1.get(eulerPath.get(i));
                universalString.append(curr.charAt(k - 2));
            }

        }

        @Override
        public String toString() {
            return universalString.toString();
        }

        private List<Integer> findEulerPath(List<Edge>[] adj) {
            List<Integer> path = new ArrayList<Integer>();
            findPath(0, path, adj);
            return path;
        }

        private void findPath(int v, List<Integer> path, List<Edge>[] adj) {
            for (Edge e: adj[v]) {
                if (!e.used){
                    e.used = true;
                    findPath(e.to, path, adj);
                }
            }
            path.add(v);
        }

        private String getSubustringFromBinArray(int start, int end, int a[]) {
            StringBuilder s = new StringBuilder();
            for (int i = start; i <= end; i++) {
                s.append(a[i]);
            }
            return s.toString();
        }

        private List<int[]> getFullKMerList(int k) {
            List<int[]> kMers = new ArrayList<int[]>();
            if (k > 0) {
                constructFullKMerList(kMers, 0, k, 0, new int[k]);
                constructFullKMerList(kMers, 0, k, 1, new int[k]);
            }
            return kMers;
        }

        private void constructFullKMerList(List<int[]> kMers, int pos, int k, int vbit, int[] bits) {
            bits[pos++] = vbit;
            if (pos >= k) {
                kMers.add(bits);
            } else {
                constructFullKMerList(kMers, pos, k, 0, bits.clone());
                constructFullKMerList(kMers, pos, k, 1, bits.clone());
            }
        }
    }
}

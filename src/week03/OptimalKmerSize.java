package week03;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class OptimalKmerSize {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner reader = new Scanner(System.in);

        int numberOfReads = 400;
        List<String> reads = new ArrayList<String>();
        for (int i = 0; i < numberOfReads; i++) {
            reads.add(reader.next());
        }

        new Thread(null, new Runnable() {
            public void run() {
                try {
                    OptimalKmerSize optimalK = new OptimalKmerSize(reads.get(0).length(), reads);
                    System.out.println(optimalK.val());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }, "1", 1 << 26).start();


    }

    public OptimalKmerSize(int k, List<String> reads) {
        HashSet<String> prefixes = new HashSet<String>();
        HashSet<String> suffixes = new HashSet<String>();
        int lo = 2, hi = k;

        while (lo < hi) {
            int currOptimum = lo + (hi - lo) / 2;
            if (isOptimal(currOptimum, reads, prefixes, suffixes)) {
                optimalK = currOptimum;
                lo = currOptimum + 1;
            } else {
                hi = currOptimum - 1;
            }
        }
    }

    private boolean isOptimal(int k, List<String> reads, HashSet<String> prefixes, HashSet<String> suffixes) {
        prefixes.clear();
        suffixes.clear();
        int k_1 = k - 1;

        for (String read : reads) {
            int N = read.length();
            for (int kmerStart = 0; kmerStart <= N - k; kmerStart++) {
                prefixes.add(read.substring(kmerStart, kmerStart + k_1));
                suffixes.add(read.substring(kmerStart + 1, kmerStart + 1 + k_1));
            }
        }


        for (String prefix : prefixes) {
            if (!suffixes.contains(prefix)) {
                return false;
            }
        }

        return prefixes.size() == suffixes.size();
    }

    private int optimalK = 0;

    private int val() {
        return optimalK;
    }
}

package week01;

import java.io.*;
import java.util.*;

public class GenomeFromErrorFreeReads {

    public static void main(String[] args) throws IOException {
        //BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("files/GenomeFromErrorFreeReads.txt")), 32768);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in), 32768);
        List<Read> reads = new ArrayList<Read>();
        String line;
        int index = 0;
        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty()) {
                reads.add(new Read(index++, line));
            } else {
                break;
            }
        }
        new Thread(null, new Runnable() {
            public void run() {
                try {
                    OutputWriter writer = new OutputWriter(System.out);
                    GenomeFromErrorFreeReads assembly = new GenomeFromErrorFreeReads(reads, writer);
                    writer.printf("%s", assembly.getGenome());
                    writer.writer.flush();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }, "1", 1 << 26).start();
    }

    private StringBuilder genome;
    private List<Read> reads;
    private int readsNum;
    private int lastProcessRead = -1;

    public GenomeFromErrorFreeReads(List<Read> reads, OutputWriter writer) {
        this.writer = writer;
        this.readsNum = reads.size();
        this.reads = reads;
        buildOverlapMap();
        genome = new StringBuilder();
        genome.append(reads.get(0).fullRead);
        processReads(0);
        cutLastRead();
    }

    private void cutLastRead() {
        Read lastRead = reads.get(lastProcessRead);
        if (lastRead.overlapRelations.containsKey(0)){
            int tailOverlapLength = lastRead.overlapRelations.get(0);
            genome =  new StringBuilder(genome.toString().substring(tailOverlapLength));
        }
    }

    private void processReads(int readIndex) {
        Read currentRead = reads.get(readIndex);
        currentRead.processed = true;
        int maxOverlap = 0;
        int maxOverlapIndex = -1;
        for (Map.Entry<Integer,Integer> read: currentRead.overlapRelations.entrySet()) {
            if (read.getValue() > maxOverlap && !reads.get(read.getKey()).processed){
                maxOverlap = read.getValue() ;
                maxOverlapIndex = read.getKey();
            }
        }
        if (maxOverlapIndex == -1){
            return;
        }
        genome.append(reads.get(maxOverlapIndex).fullRead.substring(maxOverlap));
        lastProcessRead = maxOverlapIndex;
        processReads(maxOverlapIndex);
    }

    private void buildOverlapMap() {
        for (int from = 0; from < readsNum - 1; from++) {
            for (int to = from + 1; to < readsNum; to++) {
                constructOverlapRelation(reads.get(from), reads.get(to));
                constructOverlapRelation(reads.get(to), reads.get(from));
            }
        }
    }

    private void constructOverlapRelation(Read from, Read to) {
        char[] fromRead = from.fullRead.toCharArray(), toRead = to.fullRead.toCharArray();
        for (int overlapSize = 0; overlapSize < toRead.length; overlapSize++) {
            boolean overlapBroken = false;
            int toPos = 0;
            for (int pos = overlapSize; pos < fromRead.length; pos++) {
                if (fromRead[pos] != toRead[toPos++]) {
                    overlapBroken = true;
                    break;
                }
            }
            if (!overlapBroken){
                from.overlapRelations.put(to.index, toPos);
                return;
            }
        }
    }

    public String getGenome() {
        return this.genome.toString();
    }

    private static class Read {
        private boolean processed;
        private int index;
        private String fullRead;
        private HashMap<Integer, Integer> overlapRelations = new HashMap<Integer, Integer>();

        public Read(int index, String read) {
            this.processed = false;
            this.fullRead = read;
            this.index = index;
        }
    }

    private final OutputWriter writer;

    private static class OutputWriter {
        public PrintWriter writer;

        OutputWriter(OutputStream stream) {
            writer = new PrintWriter(stream);
        }

        public void printf(String format, Object... args) {
            writer.print(String.format(Locale.ENGLISH, format, args));
        }
    }
}

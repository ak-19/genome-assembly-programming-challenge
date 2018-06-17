package week02;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PuzzleAssembly {
    public static void main(String[] args) throws IOException {
        FastReader reader = new FastReader();
        List<PuzzlePiece> puzzlePieces = new ArrayList<PuzzlePiece>();
        for (int i = 1; i <= 25; i++) {
            String line = reader.nextLine();
            puzzlePieces.add(new PuzzlePiece(line.trim().replace("(", "").replace(")", "").split(",")));
        }

        PuzzlePiece[][] solution = solve(puzzlePieces);

        try {
            for (int row = 0; row < 5; row++) {
                StringBuilder line = new StringBuilder();
                for (int col = 0; col < 5; col++) {
                    line.append(generatePieceString(solution[row][col]) + (col == 4 ? "" : ";"));
                }
                System.out.println(line.toString().trim());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String generatePieceString(PuzzlePiece puzzlePiece) {
        StringBuilder pieceString = new StringBuilder();
        pieceString.append("(" + puzzlePiece.sides[0] + ",");
        pieceString.append(puzzlePiece.sides[1] + ",");
        pieceString.append(puzzlePiece.sides[2] + ",");
        pieceString.append(puzzlePiece.sides[3] + ")");
        return pieceString.toString().trim();
    }

    private static PuzzlePiece[][] solve(List<PuzzlePiece> pieces) {
        PuzzlePiece[][] solution = new PuzzlePiece[5][5];
        findCorners(pieces, solution);
        findSides(pieces, solution);
        findRemainingPieces(solution, pieces);
        return solution;
    }

    private static void findRemainingPieces(PuzzlePiece[][] solution, List<PuzzlePiece> pieces) {
        for (int row = 1; row < 4; row++) {
            for (int col = 1; col < 4; col++) {
                String left = solution[row][col - 1].sides[3];
                String top = solution[row - 1][col].sides[2];
                PuzzlePiece match = findMatchingPiece(left, top, pieces);
                solution[row][col] = match;
            }
        }
    }

    private static PuzzlePiece findMatchingPiece(String left, String top, List<PuzzlePiece> pieces) {
        for (int i = pieces.size() - 1; i >= 0; i--) {
            PuzzlePiece curr = pieces.get(i);
            String[] sides = curr.sides;
            if (sides[1].equals(left) && sides[0].equals(top)) {
                pieces.remove(curr);
                return curr;
            }
        }
        return null;
    }

    private static void findSides(List<PuzzlePiece> pieces, PuzzlePiece[][] solution) {
        List<PuzzlePiece> top = new ArrayList<PuzzlePiece>();
        List<PuzzlePiece> left = new ArrayList<PuzzlePiece>();
        List<PuzzlePiece> bottom = new ArrayList<PuzzlePiece>();
        List<PuzzlePiece> right = new ArrayList<PuzzlePiece>();

        int n = pieces.size();
        for (int i = n - 1; i >= 0; i--) {
            String[] sides = pieces.get(i).sides;
            if (sides[0].equals("black")) {
                top.add(pieces.get(i));
                pieces.remove(i);
            } else if (sides[1].equals("black")) {
                left.add(pieces.get(i));
                pieces.remove(i);
            } else if (sides[2].equals("black")) {
                bottom.add(pieces.get(i));
                pieces.remove(i);
            } else if (sides[3].equals("black")) {
                right.add(pieces.get(i));
                pieces.remove(i);
            }
        }


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (k != i && k != j && i != j) {
                        if (solution[0][0].sides[3].equals(top.get(i).sides[1])) {
                            if (top.get(i).sides[3].equals(top.get(j).sides[1])) {
                                if (top.get(j).sides[3].equals(top.get(k).sides[1])) {
                                    solution[0][1] = top.get(i);
                                    solution[0][2] = top.get(j);
                                    solution[0][3] = top.get(k);
                                }
                            }
                        }
                        if (solution[0][0].sides[2].equals(left.get(i).sides[0])) {
                            if (left.get(i).sides[2].equals(left.get(j).sides[0])) {
                                if (left.get(j).sides[2].equals(left.get(k).sides[0])) {
                                    solution[1][0] = left.get(i);
                                    solution[2][0] = left.get(j);
                                    solution[3][0] = left.get(k);
                                }
                            }
                        }
                        if (solution[4][0].sides[3].equals(bottom.get(i).sides[1])) {
                            if (bottom.get(i).sides[3].equals(bottom.get(j).sides[1])) {
                                if (bottom.get(j).sides[3].equals(bottom.get(k).sides[1])) {
                                    solution[4][1] = bottom.get(i);
                                    solution[4][2] = bottom.get(j);
                                    solution[4][3] = bottom.get(k);
                                }
                            }
                        }
                        if (solution[0][4].sides[2].equals(right.get(i).sides[0])) {
                            if (right.get(i).sides[2].equals(right.get(j).sides[0])) {
                                if (right.get(j).sides[2].equals(right.get(k).sides[0])) {
                                    solution[1][4] = right.get(i);
                                    solution[2][4] = right.get(j);
                                    solution[3][4] = right.get(k);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void findCorners(List<PuzzlePiece> pieces, PuzzlePiece[][] solution) {
        int n = pieces.size();
        for (int i = n - 1; i >= 0; i--) {
            String[] sides = pieces.get(i).sides;
            if (sides[0].equals("black") && sides[1].equals("black")) {
                solution[0][0] = pieces.get(i);
                pieces.remove(i);
            } else if (sides[2].equals("black") && sides[3].equals("black")) {
                solution[4][4] = pieces.get(i);
                pieces.remove(i);
            } else if (sides[1].equals("black") && sides[2].equals("black")) {
                solution[4][0] = pieces.get(i);
                pieces.remove(i);
            } else if (sides[0].equals("black") && sides[3].equals("black")) {
                solution[0][4] = pieces.get(i);
                pieces.remove(i);
            }
        }
    }

    private static class PuzzlePiece {
        private String[] sides;

        public PuzzlePiece(String[] sides) {
            this.sides = sides;
        }
    }

    private static class FastReader {
        BufferedReader br;
        StringTokenizer st;

        public FastReader() throws FileNotFoundException {
            //br = new BufferedReader(new InputStreamReader(System.in));
           br = new BufferedReader(new InputStreamReader(new FileInputStream("files/PuzzleAssembly.txt")), 32768);
        }

        String nextLine() {
            String temp = "";
            try {
                temp = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return temp;
        }
    }
}

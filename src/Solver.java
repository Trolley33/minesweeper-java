import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Solver
{
    static int[][] generateWeightedBoard(Cell[][] board, int mineNumber) {
        int[][] weighted_board = new int[board.length][board[0].length];

        ArrayList<Cell> unseenCells = new ArrayList<> ();
        for (Cell[] column : board) {
            for (Cell cell : column) {
                if (cell.getState () == 0 || cell.getState () == 2) unseenCells.add (cell);
            }
        }

        if (unseenCells.size () > 40) return weighted_board;

        StringBuilder inputString = new StringBuilder ();

        for (int i = 0; i < unseenCells.size (); i++) {
            if (i < mineNumber) inputString.append ('1');
            else inputString.append ('0');
        }

        ArrayList<String> permutations = new ArrayList<> ();
        generateDistinctPermutn (inputString.toString (), "", permutations);


        ArrayList<int[][]> possible_boards = new ArrayList<> ();
        for (int x = 0; x < permutations.size (); x++)
        {
            int[][] b = convertBinToBoard (permutations.get (x), unseenCells.toArray (new Cell[0]), board.length, board[0].length);
            normaliseBoard (b);
            possible_boards.add (b);
        }



        for (int[][] b : possible_boards)
        {
            boolean isValid = compareToOriginal(convertCellBoardToInt (board), b);
            if (isValid) {
                addWeightsFromBoard(b, weighted_board);
            }
        }

        return weighted_board;
    }

    static void addWeightsFromBoard(int[][] board, int[][] weight_board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++)
            {
                if (board[i][j] == -1) {
                    weight_board[i][j] += 1;
                }
            }
        }
    }

    static int[][] convertCellBoardToInt(Cell[][] board) {
        int[][] new_board = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++)
            {
                if (board[i][j].getState () == 0 || board[i][j].getState () == 2 || board[i][j].isMine ()) {
                    new_board[i][j] = -2;
                } else if (board[i][j].getState () == 1) {
                    new_board[i][j] = board[i][j].getNearbyMines ();
                }
            }
        }

        return new_board;
    }

    static boolean compareToOriginal(int[][] original, int[][] other) {
        boolean isValid = true;

        for (int i = 0; i < original.length; i++) {
            if (!isValid) break;
            for (int j = 0; j < original[i].length; j++)
            {
                if (original[i][j] == -2) continue;

                if (original[i][j] != other[i][j]) {
                    isValid = false;
                    break;
                }
            }
        }

        return isValid;
    }

    static void printBoard(int[][] b) {
        for (int j = 0; j < b[0].length; j++)
        {
            for (int i = 0; i < b.length; i++)
            {
                System.out.print (b[i][j] + "|");
            }
            System.out.println ();
        }
        System.out.println ("====");
    }
    static void normaliseBoard(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++)
            {
                if (board[i][j] >= 0)
                {
                    int val = 0;
                    val += getCellFromBoard (i - 1, j - 1, board);
                    val += getCellFromBoard (i, j - 1, board);
                    val += getCellFromBoard (i + 1, j - 1, board);

                    val += getCellFromBoard (i - 1, j, board);

                    val += getCellFromBoard (i + 1, j, board);

                    val += getCellFromBoard (i - 1, j + 1, board);
                    val += getCellFromBoard (i, j + 1, board);
                    val += getCellFromBoard (i + 1, j + 1, board);

                    board[i][j] = val;
                }
            }
        }
    }

    static int getCellFromBoard(int i, int j, int[][] board) {
        if (i < 0 || j < 0 || i >= board.length || j >= board[0].length) return 0;

        if (board[i][j] == -1) return 1;

        return 0;
    }

    static int[][] convertBinToBoard(String input, Cell[] unseenCells, int width, int height) {
        int[][] board = new int[width][height];

        for (int x = 0; x < unseenCells.length; x++) {
            Cell c = unseenCells[x];
            if (input.charAt (x) == '1') {
                board[c.getI ()][c.getJ ()] = -1;
            }
        }

        return board;
    }

    // Function to print all the distinct
    // permutations of str
    static void generateDistinctPermutn(String str, String ans, ArrayList<String> perms)
    {

        // If string is empty
        if (str.length() == 0) {
            perms.add(ans);
            return;
        }

        // Make a boolean array of size '26' which
        // stores false by default and make true
        // at the position which alphabet is being
        // used
        boolean binary[] = new boolean[2];

        for (int i = 0; i < str.length(); i++) {

            // ith character of str
            char ch = str.charAt(i);

            // Rest of the string after excluding
            // the ith character
            String ros = str.substring(0, i) +
                    str.substring(i + 1);

            // If the character has not been used
            // then recursive call will take place.
            // Otherwise, there will be no recursive
            // call
            if (!binary[ch - '0'])
                generateDistinctPermutn (ros, ans + ch, perms);
            binary[ch - '0'] = true;
        }
    }

    static void generatePermutn(String str, String ans, ArrayList<String> perms)
    {

        // If string is empty
        if (str.length() == 0) {
            perms.add (ans);
            return;
        }

        for (int i = 0; i < str.length(); i++) {

            // ith character of str
            char ch = str.charAt(i);

            // Rest of the string after excluding
            // the ith character
            String ros = str.substring(0, i) +
                    str.substring(i + 1);

            // Recurvise call
            generatePermutn(ros, ans + ch, perms);
        }
    }
}

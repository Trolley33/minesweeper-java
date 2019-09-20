import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Random;

public class GameLogic {

    private static double MINE_DENSITY = 0.15;
    private int mineNumber;

    private GameWindow gameWindow;
    private Cell[][] cells;

    private Cell currentInside;
    private double[] mousePos;

    private boolean minesPlaced;

    GameLogic(GameWindow gw) {
        this.gameWindow = gw;
        mousePos = new double[2];
        startup ();
    }

    void startup() {
        mineNumber = (int) Math.ceil (MINE_DENSITY * GameWindow.BOARD_SIZE[0] * GameWindow.BOARD_SIZE[1]);

        cells = new Cell[GameWindow.BOARD_SIZE[0]][GameWindow.BOARD_SIZE[1]];

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++)
            {
                cells[i][j] = new Cell (i, j);
            }
        }
        minesPlaced = false;
    }

    /**
     * Game lost, mine selected.
     */
    void bang() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Bang!\nPlay Again?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            startup ();
        } else {
            gameWindow.stage.close ();
        }
    }

    void win() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "You Won!\nPlay Again?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            startup ();
        } else {
            gameWindow.stage.close ();
        }
    }

    void placeMines(int i, int j) {
        Random random = new Random ();
        int placed = 0;
        while (placed < mineNumber) {
            int r1 = random.nextInt (cells.length);
            int r2 = random.nextInt (cells[0].length);
            // Avoid placing mines on top of each other, or on the mouse cursor.
            if (!cells[r1][r2].isMine () && r1 != i && r2 != j) {
                cells[r1][r2].setMine ();
                placed++;
            }
        }
        minesPlaced = true;
        calculateNearby ();
    }

    void calculateNearby() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++)
            {
                Cell[] nearbyCells = getAdjacentCells (i, j);
                int nearbyMines = 0;
                for (Cell c : nearbyCells) {
                    if (c.isMine ()) nearbyMines++;
                }
                cells[i][j].setNearby (nearbyMines);
            }
        }
    }

    Cell getCell(int i, int j) {
        if (i < 0 || i >= cells.length || j < 0 || j >= cells[0].length)
            return null;
        return cells[i][j];
    }

    int getI(double x) {
        float w = GameWindow.WINDOW_SIZE[0] / GameWindow.BOARD_SIZE[0];
        return (int) Math.floor (x / w);
    }

    int getJ(double y) {
        float h = GameWindow.WINDOW_SIZE[1] / GameWindow.BOARD_SIZE[1];
        return (int) Math.floor (y / h);
    }

    void mouseMoved(MouseEvent event) {
        mousePos = new double[] {event.getX (), event.getY ()};
        int i = getI (event.getX ());
        int j = getJ (event.getY ());

        currentInside = cells[i][j];

    }

    void mousePressed(MouseEvent event) {
        int i = getI (event.getX ());
        int j = getJ (event.getY ());
        Cell cell = cells[i][j];
        if (event.getButton () == MouseButton.PRIMARY)
        {
            if (!minesPlaced) {
                placeMines (i, j);
            }
            if (cell.getState () == 0)
            {
                if (!cell.isMine ())
                {
                    cell.setState (1);
                    floodFill (i, j);
                } else {
                    cell.setState (1);
                    bang();
                }
            }
            calculateProbability();
        }
        else if (event.getButton () == MouseButton.SECONDARY)
        {
            if (cell.getState () == 0)
                cell.setState (2);
            else if (cell.getState () == 2)
                cell.setState (0);

            checkIfWon();
        }
    }

    void calculateProbability() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++)
            {
                cells[i][j].setProbability(0);
            }
        }

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++)
            {
                if (cells[i][j].getState () == 1 && cells[i][j].getNearbyMines () > 0)
                {
                    Cell[] nearbyCells = getAdjacentCells (i, j);
                    for (Cell c : nearbyCells)
                    {
                        c.addProbability ((float)cells[i][j].getNearbyMines () / (float)getAdjacentCells (c.getI (), c.getJ ()).length);
                    }
                }
            }
        }
    }

    void checkIfWon() {
        int minesMarked = 0;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++)
            {
                if (cells[i][j].getState () == 2 && cells[i][j].isMine ()) {
                    minesMarked++;
                }
            }
        }

        if (minesMarked == mineNumber) {
            win();
        }
    }

    void floodFill(int i, int j) {
        if (getCell (i, j).getNearbyMines() != 0)
            return;

        Cell[] adjacentCells = getAdjacentCells(i, j);
        for (Cell cell : adjacentCells) {
            if (cell.getState () == 0 && !cell.isMine ()) {
                cell.setState (1);
                floodFill (cell.getI (), cell.getJ ());
            }
        }
    }

    Cell[] getAdjacentCells(int i, int j) {
        ArrayList<Cell> adjacent = new ArrayList<> ();
        if (getCell (i-1, j-1) != null) adjacent.add (cells[i-1][j-1]);
        if (getCell (i, j-1) != null) adjacent.add (cells[i][j-1]);
        if (getCell (i+1, j-1) != null) adjacent.add (cells[i+1][j-1]);

        if (getCell (i-1, j) != null) adjacent.add (cells[i-1][j]);

        if (getCell (i+1, j) != null) adjacent.add (cells[i+1][j]);

        if (getCell (i-1, j+1) != null) adjacent.add (cells[i-1][j+1]);
        if (getCell (i, j+1) != null) adjacent.add (cells[i][j+1]);
        if (getCell (i+1, j+1) != null) adjacent.add (cells[i+1][j+1]);

        return adjacent.toArray (new Cell[0]);
    }

    double[] getMousePosition() {
        return mousePos;
    }

    Cell getCurrentInside() {
        return currentInside;
    }

}

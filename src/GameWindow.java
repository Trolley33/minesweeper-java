import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;

public class GameWindow {

    public static int[] WINDOW_SIZE = {700, 700};
    public static int[] BOARD_SIZE = {7, 7};

    Stage stage;
    GameLogic gameLogic;

    Canvas canvas;
    GraphicsContext gc;

    Timeline gameLoop;

    GameWindow(Stage stage) {
        this.stage = stage;

        gameLogic = new GameLogic(this);

        // Setup screen refresh.
        gameLoop = new Timeline(new KeyFrame(Duration.millis(33.3), e -> gameUpdate()));
        gameLoop.setCycleCount(Animation.INDEFINITE);
    }

    void loadImages() {
        try
        {
            Cell.images = new Image[] {
                    new Image (new FileInputStream ("./images/default-cell.png")),
                    new Image (new FileInputStream ("./images/down-cell.png")),
                    new Image (new FileInputStream ("./images/marked-cell.png")),
                    new Image (new FileInputStream ("./images/flag.png")),
                    new Image (new FileInputStream ("./images/mine.png")),
            };
        } catch (Exception e) {
            System.out.println ("Images not found...");
        }
    }

    void bindMouseEvents() {
        canvas.setOnMouseMoved (e -> gameLogic.mouseMoved (e));
        canvas.setOnMousePressed (e -> gameLogic.mousePressed (e));
    }

    void createStage() {
        Group root = new Group();
        Scene scene = new Scene(root, WINDOW_SIZE[0], WINDOW_SIZE[1]);

        // Create canvas to draw on
        canvas = new Canvas(WINDOW_SIZE[0], WINDOW_SIZE[1]);
        gc = canvas.getGraphicsContext2D();

        loadImages ();
        bindMouseEvents();

        // Start refreshing screen.
        gameLoop.play();

        // Launch window.
        root.getChildren().add(canvas);
        stage.setTitle("Minesweeper");
        stage.setScene(scene);
        stage.show();

    }

    void draw () {
        for (int i = 0; i < BOARD_SIZE[0]; i++) {
            for (int j = 0; j < BOARD_SIZE[1]; j++) {
                gameLogic.getCell(i, j).draw(gc);
            }
        }
        Cell currentlyHovered = gameLogic.getCurrentInside();
        if (currentlyHovered != null)
            currentlyHovered.drawBorder(gc);

    }

    void gameUpdate() {
        gc.clearRect(0,0,250,250);
        draw();
    }

}

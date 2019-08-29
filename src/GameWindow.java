import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameWindow {

    public static int[] WINDOW_SIZE = {320, 320};

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

    void createStage() {
        Group root = new Group();
        Scene scene = new Scene(root, WINDOW_SIZE[0], WINDOW_SIZE[1]);

        // Create canvas to draw on
        canvas = new Canvas(WINDOW_SIZE[0], WINDOW_SIZE[1]);
        gc = canvas.getGraphicsContext2D();

        // Start refreshing screen.
        gameLoop.play();

        // Launch window.
        root.getChildren().add(canvas);
        stage.setTitle("Minesweeper");
        stage.setScene(scene);
        stage.show();

    }

    void draw () {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                gc.setFill(Color.rgb(51, 51, 51));
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                float w = (float) WINDOW_SIZE[0] / 8;
                float h = (float) WINDOW_SIZE[1] / 8;
                gc.fillRect(w*i, h*j, w, h);
                gc.strokeRect(w*i, h*j, w, h);
            }
        }
    }

    void gameUpdate() {
        gc.clearRect(0,0,250,250);
        draw();
    }

}

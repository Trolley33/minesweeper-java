import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Cell
{
    private int i;
    private int j;

    private float w;
    private float h;

    private int state = 0;
    private boolean is_mine;
    private int nearbyMines;

    private static Color[] COLORS = {
            null,
            Color.CYAN,
            Color.LIMEGREEN,
            Color.RED,
            Color.MEDIUMPURPLE,
            Color.WHITE,
            Color.YELLOW,
            Color.HOTPINK,
            Color.FUCHSIA,
    };

    static Image[] images;

    Cell(int i, int j) {
        this.i = i;
        this.j = j;

        w = (float) GameWindow.WINDOW_SIZE[0] / GameWindow.BOARD_SIZE[0];
        h = (float) GameWindow.WINDOW_SIZE[1] / GameWindow.BOARD_SIZE[1];
    }

    void draw(GraphicsContext gc) {
        gc.drawImage (images[state], w*i, h*j, w, h);
        if (state == 2)
            gc.drawImage (images[3], w*i, h*j, w, h);
        if (state == 1 && getNearby () > 0) {
            gc.setFill (COLORS[getNearby ()]);
            gc.setFont (Font.font ("trebuchet ms", FontWeight.BOLD, w*0.4f));
            gc.setTextAlign (TextAlignment.CENTER);
            gc.setTextBaseline (VPos.CENTER);
            gc.fillText (Integer.toString (getNearby ()), w*i + (w/2), h*j + (h/2));
       }
    }

    void drawBorder(GraphicsContext gc) {
        if (state == 0)
        {
            gc.setStroke (Color.rgb (255, 255, 255));
            gc.setLineWidth (3);
        }
        else if (state == 1)
        {
            gc.setStroke (Color.rgb (175, 175, 175));
            gc.setLineWidth (1);
        }
        else if (state == 2)
        {
            gc.setStroke (Color.rgb (255, 120, 120));
            gc.setLineWidth (3);
        }
        gc.strokeRect (w*i, h*j, w, h);
    }

    void setState(int new_state) {
        this.state = new_state;
    }

    int getState() {
        return this.state;
    }

    boolean isMine() {
        return is_mine;
    }

    void setMine() {
        is_mine = true;
    }

    int getI() {
        return i;
    }

    int getJ() {
        return j;
    }

    void setNearby(int n) {
        nearbyMines = n;
    }

    int getNearby() {
        return nearbyMines;
    }
}

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class character2 {
    // Без static: у каждой машины теперь свои координаты.
    public double x;
    public double y;
    public int lane;
    public boolean passed;
    public boolean isVisible;
    public BufferedImage image;

    character2() //конструктор класса
    {
        x = 318;
        y = 0;
        isVisible = true;
        try {
            image = ImageIO.read(new File("src/car2.png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }




    public void move(int dx, int dy)
    {
        x-=dx;
        y-=dy;
    }
}

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class character {
    public double x;
    public double y;
    public boolean isVisible;
    public BufferedImage image;

     character() //конструктор класса
    {
        x = 684;
        y = 550;
        isVisible = true;
        try {
            image = ImageIO.read(new File("src/car.png"));
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



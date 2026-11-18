import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class menu extends JFrame {
    public start button1 = new start();
    public control button2 = new control();
    public garage button3 = new garage();
    public exit button4 = new exit();
    public BufferedImage fon;

    public JPanel pole = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(fon, 0, 0, this);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 42));
            g.drawString("НА ГРАНИ", 65, 135);
            g.drawString("СЦЕПЛЕНИЯ", 65, 190);
            g.drawImage(button1.image, button1.x, button1.y, this);
            g.drawImage(button2.image, button2.x, button2.y, this);
            g.drawImage(button3.image, button3.x, button3.y, this);
            g.drawImage(button4.image, button4.x, button4.y, this);
        }
    };

    public boolean onButton(BufferedImage image, int x, int y, MouseEvent e) {
        int dx = e.getX() - x;
        int dy = e.getY() - y;
        if (dx < 0 || dy < 0 || dx >= image.getWidth() || dy >= image.getHeight()) {
            return false;
        }
        return true;
    }

    public MouseListener ml = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (onButton(button1.image, button1.x, button1.y, e)) {
                new test2();
                dispose();
            }
            else if (onButton(button4.image, button4.x, button4.y, e)) dispose();
            else if (onButton(button2.image, button2.x, button2.y, e)) {
                new control2();
                dispose();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) { }
        @Override
        public void mouseReleased(MouseEvent e) { }
        @Override
        public void mouseEntered(MouseEvent e) { }
        @Override
        public void mouseExited(MouseEvent e) { }
    };

    menu() {
        try {
            fon = ImageIO.read(new File("src/fon2menu.png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setTitle("На грани сцепления");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pole.setPreferredSize(new Dimension(1280, 720));
        pole.addMouseListener(ml);
        setContentPane(pole);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new menu();
    }
}

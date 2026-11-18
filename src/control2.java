import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class control2 extends JFrame {
    public BufferedImage fon;
    public BufferedImage backImage;
    public int buttonX = 65;
    public int buttonY = 605;

    public JPanel pole = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(fon, 0, 0, this);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("УПРАВЛЕНИЕ", 65, 110);

            g.setFont(new Font("Arial", Font.BOLD, 21));
            g.drawString("A / D или ← / →", 65, 205);
            g.drawString("W или ↑", 65, 267);
            g.drawString("S или ↓", 65, 329);
            g.drawString("Esc", 65, 391);
            g.drawString("R / Enter", 65, 453);

            g.setFont(new Font("Arial", Font.PLAIN, 21));
            g.drawString("Поворот", 285, 205);
            g.drawString("Газ", 285, 267);
            g.drawString("Тормоз", 285, 329);
            g.drawString("Пауза", 285, 391);
            g.drawString("Ещё заезд после аварии", 285, 453);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawImage(backImage, buttonX, buttonY, this);
        }
    };

    public MouseListener ml = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            if (x >= buttonX && x < buttonX + backImage.getWidth()
                    && y >= buttonY && y < buttonY + backImage.getHeight()) {
                openMenu();
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

    public KeyListener kl = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) { }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) openMenu();
        }

        @Override
        public void keyReleased(KeyEvent e) { }
    };

    control2() {
        try {
            fon = ImageIO.read(new File("src/fon2menu.png"));
            backImage = ImageIO.read(new File("src/mainMenu.png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setTitle("На грани сцепления — Управление");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pole.setPreferredSize(new Dimension(1280, 720));
        pole.setFocusable(true);
        pole.addMouseListener(ml);
        pole.addKeyListener(kl);
        setContentPane(pole);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        pole.requestFocusInWindow();
    }

    public void openMenu() {
        if (!isDisplayable()) return;
        new menu();
        dispose();
    }

    public static void main(String[] args) {
        new control2();
    }
}

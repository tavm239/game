import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class test2 extends JFrame {
    public game race = new game();
    public Timer timer;
    public long lastTime;
    public boolean escapeDown;

    public JPanel pole = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            race.draw((Graphics2D) g);
        }
    };

    public KeyListener kl = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) { }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ESCAPE) {
                if (!escapeDown) race.changePause();
                escapeDown = true;
                pole.repaint();
                return;
            }
            if (race.finished && (key == KeyEvent.VK_R || key == KeyEvent.VK_ENTER)) {
                race.restart();
                return;
            }
            if (race.paused || race.finished) return;

            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) race.left = true;
            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) race.right = true;
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) race.gas = true;
            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) race.brake = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ESCAPE) escapeDown = false;
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) race.left = false;
            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) race.right = false;
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) race.gas = false;
            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) race.brake = false;
        }
    };

    public MouseListener ql = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            if (race.finished) {
                if (x >= 448 && x <= 832 && y >= 405 && y <= 455) race.restart();
                else if (x >= 448 && x <= 832 && y >= 480 && y <= 530) openMenu();
            }
            else if (race.paused) {
                if (x >= 486 && x <= 794 && y >= 340 && y <= 389) race.changePause();
                else if (x >= 486 && x <= 794 && y >= 404 && y <= 453) openMenu();
            }
            else if (x >= race.button1.x && x < race.button1.x + race.button1.image.getWidth()
                    && y >= race.button1.y && y < race.button1.y + race.button1.image.getHeight()) {
                race.changePause();
            }
            pole.requestFocusInWindow();
            pole.repaint();
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

    test2() {
        setTitle("На грани сцепления");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pole.setPreferredSize(new Dimension(1280, 720));
        pole.setFocusable(true);
        pole.addKeyListener(kl);
        pole.addMouseListener(ql);
        setContentPane(pole);
        pack();
        setLocationRelativeTo(null);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                race.releaseKeys();
                escapeDown = false;
                if (!race.finished) race.paused = true;
                pole.repaint();
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                pole.requestFocusInWindow();
            }
        });

        lastTime = System.nanoTime();
        timer = new Timer(20, e -> {
            long now = System.nanoTime();
            double dt = (now - lastTime) / 1000000000.0;
            lastTime = now;
            race.update(dt);
            pole.repaint();
        });
        setVisible(true);
        pole.requestFocusInWindow();
        timer.start();
    }

    public void openMenu() {
        dispose();
        new menu();
    }

    @Override
    public void dispose() {
        if (timer != null) timer.stop();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new test2());
    }
}

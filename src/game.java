import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;


public class game {
    public character pers = new character();
    public character2 pers2 = new character2();
    public character2 pers3 = new character2();
    public character2 pers4 = new character2();
    public character2 pers5 = new character2();
    public character2 pers6 = new character2();
    public character2 pers7 = new character2();
    public pause button1 = new pause();
    public pause2 pausePanel = new pause2();
    public BufferedImage fon;
    public Random random = new Random();

    public boolean left, right, gas, brake;
    public boolean paused, finished;
    public double speed, distance, angle, sideSpeed, roadY, slideTime;
    public int overtakes, carsInGroup, freeLane, lastTurn;
    public int width = 1280, height = 720;
    public int groupGap = 850;
    public String crashReason;

    game() {
        try {
            fon = ImageIO.read(new File("src/fon.png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        restart();
    }

    public void restart() {
        pers.x = 734 - pers.image.getWidth() / 2.0;
        pers.y = 550;
        speed = 80;
        distance = 0;
        angle = 0;
        sideSpeed = 0;
        roadY = 0;
        slideTime = 0;
        overtakes = 0;
        carsInGroup = 1;
        freeLane = 2;
        lastTurn = 0;
        paused = false;
        finished = false;
        crashReason = "";
        releaseKeys();
        newGroup(pers2, pers3, pers4, -160);
        newGroup(pers5, pers6, pers7, -160 - groupGap);
    }

    public void releaseKeys() {
        left = false;
        right = false;
        gas = false;
        brake = false;
    }

    public void changePause() {
        if (!finished) {
            paused = !paused;
            releaseKeys();
        }
    }

    public int laneCenter(int lane) {
        if (lane == 0) return 368;
        if (lane == 1) return 546;
        if (lane == 2) return 734;
        return 912;
    }

    public void placeCar(character2 car, int lane, double y, boolean visible) {
        car.lane = lane;
        car.x = laneCenter(lane) - car.image.getWidth() / 2.0;
        car.y = y;
        car.isVisible = visible;
        car.passed = false;
    }

    public void newGroup(character2 a, character2 b, character2 c, double y) {

        freeLane += random.nextInt(3) - 1;
        if (freeLane < 0) freeLane = 0;
        if (freeLane > 3) freeLane = 3;

        int first = random.nextInt(3);
        int second = (first + 1) % 3;
        int third = (first + 2) % 3;
        if (first >= freeLane) first++;
        if (second >= freeLane) second++;
        if (third >= freeLane) third++;

        placeCar(a, first, y, true);
        placeCar(b, second, y, carsInGroup >= 2);
        placeCar(c, third, y, carsInGroup >= 3);
    }

    public void update(double dt) {
        if (paused || finished || dt <= 0) return;

        if (dt > 0.05) dt = 0.05;         // При задержке окна не делаем огромный скачок за один кадр.



        if (brake) speed -= 60 * dt;
        else if (gas) speed += 30 * dt;
        else if (speed < 80) speed = Math.min(80, speed + 12 * dt);
        else if (speed > 80) speed = Math.max(80, speed - 8 * dt);
        if (speed < 0) speed = 0;
        if (speed > 180) speed = 180;

        int turn = 0;
        if (left && !right) turn = -1;
        if (right && !left) turn = 1;

        if (speed > 120 && turn != 0 && turn != lastTurn) slideTime = 0.65;         // На большой скорости резкое изменение руления ослабляет сцепление
        lastTurn = turn;
        slideTime = Math.max(0, slideTime - dt);

        angle += (turn * 22 - angle) * Math.min(1, 7 * dt);
        double wantedSideSpeed = Math.sin(Math.toRadians(angle)) * speed * 5;
        double grip = 7;
        if (slideTime > 0) grip = 2.2;
        sideSpeed += (wantedSideSpeed - sideSpeed) * Math.min(1, grip * dt);
        pers.x += sideSpeed * dt;

        double wantedY = 550 - Math.abs(angle) * 1.2;
        pers.y += (wantedY - pers.y) * Math.min(1, 5 * dt);   // смещение машины вверх при повороте
        distance += speed / 3.6 * dt;

        carsInGroup = 1;
        if (distance >= 250) carsInGroup = 2;
        if (distance >= 500) carsInGroup = 3;

        roadY = (roadY + speed / 3.6 * 12 * dt) % height;
        double step = speed / 3.6 * 6 * dt;
        moveCar(pers2, step);
        moveCar(pers3, step);
        moveCar(pers4, step);
        moveCar(pers5, step);
        moveCar(pers6, step);
        moveCar(pers7, step);

        if (pers2.y > height) newGroup(pers2, pers3, pers4, pers5.y - groupGap);
        if (pers5.y > height) newGroup(pers5, pers6, pers7, pers2.y - groupGap);

        Rectangle border = playerShape(false).getBounds();
        if (border.x < 289 || border.x + border.width > 989) {
            finish("Столкновение с ограждением");
        }
        if (hits(pers2) || hits(pers3) || hits(pers4) ||
                hits(pers5) || hits(pers6) || hits(pers7)) {
            finish("Столкновение с автомобилем");
        }
    }

    public void moveCar(character2 car, double step) {
        car.y += step;
        if (car.isVisible && !car.passed && car.y > pers.y + pers.image.getHeight()) {
            car.passed = true;
            overtakes++;
        }
    }

    public Area playerShape(boolean smaller) {
        int inset = 0;
        if (smaller) inset = 9;
        Rectangle body = new Rectangle((int) pers.x + inset, (int) pers.y + inset,
                pers.image.getWidth() - 2 * inset, pers.image.getHeight() - 2 * inset);
        AffineTransform rotation = AffineTransform.getRotateInstance(Math.toRadians(angle),
                pers.x + pers.image.getWidth() / 2.0, pers.y + pers.image.getHeight() / 2.0);
        return new Area(rotation.createTransformedShape(body));
    }

    public boolean hits(character2 car) {
        if (!car.isVisible) return false;
        // Немного уменьшаем прямоугольники, чтобы прозрачные края не мешали.
        Area body = playerShape(true);
        body.intersect(new Area(new Rectangle((int) car.x + 9, (int) car.y + 9,
                car.image.getWidth() - 18, car.image.getHeight() - 18)));
        return !body.isEmpty();
    }

    public void finish(String reason) {
        finished = true;
        crashReason = reason;
        releaseKeys();
    }

    public void draw(Graphics2D original) {
        Graphics2D g = (Graphics2D) original.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        int y = (int) roadY;
        g.drawImage(fon, 0, y, null);
        g.drawImage(fon, 0, y - height, null);
        drawCar(g, pers2);
        drawCar(g, pers3);
        drawCar(g, pers4);
        drawCar(g, pers5);
        drawCar(g, pers6);
        drawCar(g, pers7);

        Graphics2D carGraphics = (Graphics2D) g.create();
        carGraphics.rotate(Math.toRadians(angle), pers.x + pers.image.getWidth() / 2.0,
                pers.y + pers.image.getHeight() / 2.0);
        carGraphics.drawImage(pers.image, (int) pers.x, (int) pers.y, null);
        carGraphics.dispose();

        g.setColor(new Color(12, 28, 44, 220));
        g.fillRect(0, 0, width, 100);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Скорость: " + (int) speed + " км/ч", 25, 40);
        g.drawString("Расстояние: " + (int) distance + " м", 350, 40);
        g.drawString("Обгоны: " + overtakes, 760, 40);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Машин в группе: " + carsInGroup, 25, 78);
        if (slideTime > 0 && Math.abs(sideSpeed) > 30) {
            g.setColor(new Color(185, 255, 90));
            g.drawString("Занос: отпусти газ и выровняй машину", 350, 78);
        }
        g.drawImage(button1.image, button1.x, button1.y, null);

        g.setColor(new Color(12, 28, 44, 210));
        g.fillRoundRect(15, 575, 250, 130, 16, 16);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 17));
        g.drawString("A / D или ← / → — поворот", 27, 603);
        g.drawString("W / ↑ — газ", 27, 630);
        g.drawString("S / ↓ — тормоз", 27, 657);
        g.drawString("Esc — пауза", 27, 684);

        if (paused || finished) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, width, height);
        }
        if (paused && !finished) g.drawImage(pausePanel.image, pausePanel.x, pausePanel.y, null);
        if (finished) drawResult(g);
        g.dispose();
    }

    public void drawCar(Graphics2D g, character2 car) {
        if (car.isVisible) g.drawImage(car.image, (int) car.x, (int) car.y, null);
    }

    public void drawResult(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(384, 175, 512, 390, 20, 20);
        g.setColor(new Color(20, 36, 50));
        g.setFont(new Font("Arial", Font.BOLD, 34));
        g.drawString("Авария", 580, 235);
        g.setFont(new Font("Arial", Font.PLAIN, 21));
        g.drawString(crashReason, 425, 280);
        g.drawString("Расстояние: " + (int) distance + " м", 425, 325);
        g.drawString("Обгоны: " + overtakes, 425, 360);
        g.setColor(new Color(185, 255, 90));
        g.fillRoundRect(448, 405, 384, 50, 12, 12);
        g.setColor(new Color(20, 36, 50));
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Ещё заезд (R)", 555, 438);
        g.fillRoundRect(448, 480, 384, 50, 12, 12);
        g.setColor(Color.WHITE);
        g.drawString("Главное меню", 557, 513);
    }
}

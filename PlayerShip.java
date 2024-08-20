import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerShip {
    private boolean left, right, up, down, firing, recovering;
    private BufferedImage imageUp, image, imageUpTrans;
    private Color color1, color2;
    private int requiredPower[] = {1, 2, 3, 4, 5, 6, 7, 8};
    private int x, y, r, dx, dy, speed, lives, score, powerLevel, power;
    private final int maxlife = 3;
    private long firingTimer, firingDelay, recoveryTimer;

    public PlayerShip() {
        x = GameLogic.width / 2;
        y = GameLogic.height - 100;
        r = 25;

        dx = 0;
        dy = 0;
        speed = 5;
        lives = maxlife;

        color1 = Color.WHITE;
        color2 = Color.RED;

        firing = false;
        firingTimer = System.nanoTime();
        firingDelay = 200;

        recovering = false;
        recoveryTimer = System.nanoTime();

        score = 0;

        if (imageUp == null) imageUp = new ImageManager().loadImg("/img/hero/02.png");

        if (imageUpTrans == null) imageUpTrans = new ImageManager().loadImg("/img/hero/02_trans.png");
        image = imageUp;
    }

    public void update() {
        if (left) dx = -speed;
        if (right) dx = speed;
        if (up) dy = -speed;
        if (down) dy = speed;

        x += dx;
        y += dy;

        if (x < r) x = r;
        if (y < r) y = r;
        if (x > GameLogic.width - r) x = GameLogic.width - r;
        if (y > GameLogic.height - r) y = GameLogic.height - r;

        dx = 0;
        dy = 0;

        if (firing) {
            long elapsed = (System.nanoTime() - firingTimer) / 1000000;
            if (elapsed > firingDelay) {
                firingTimer = System.nanoTime();
                if (powerLevel < 2)
                    GameLogic.bullets.add(new Lazer(270, (x + 10), y));
                else if (powerLevel < 3) {
                    GameLogic.bullets.add(new Lazer(270, (x + 10) + 5, y));
                    GameLogic.bullets.add(new Lazer(270, (x + 10) - 5, y));
                } else if (powerLevel < 4) {
                    GameLogic.bullets.add(new Lazer(265, (x + 10) - 5, y));
                    GameLogic.bullets.add(new Lazer(270, (x + 10), y));
                    GameLogic.bullets.add(new Lazer(277, (x + 10) + 5, y));
                } else {
                    GameLogic.bullets.add(new Lazer(264, (x + 10) - 3, y));
                    GameLogic.bullets.add(new Lazer(267, (x + 10) - 3, y));
                    GameLogic.bullets.add(new Lazer(270, (x + 10), y));
                    GameLogic.bullets.add(new Lazer(273, (x + 10) + 3, y));
                    GameLogic.bullets.add(new Lazer(276, (x + 10) + 3, y));
                }
            }
        }

        if (recovering) {
            long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
            if (elapsed > 1000) {
                recovering = false;
                recoveryTimer = 0;
            }
        }
    }

    public void draw(Graphics2D g) {
        if (recovering)
            image = imageUpTrans;
        else
            image = imageUp;

        g.drawImage(image, x - r-10, y - r, null);
    }

    public void setLife(int life) {
        lives = life;
    }

    public void setLeft(boolean direction) {
        left = direction;
    }

    public void setRight(boolean direction) {
        right = direction;
    }

    public void setUp(boolean direction) {
        up = direction;
    }

    public void setDown(boolean direction) {
        down = direction;
    }

    public void setFiring(boolean fire) {
        firing = fire;
    }

    public void setScore(int sc) {
        score += sc;
    }

    public boolean isDead() {
        return lives <= 0;
    }

    public boolean isRecovering() {
        return recovering;
    }

    public int getLives() {
        return lives;
    }

    public int getPower() {
        return power;
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public int getR() {
        return r;
    }

    public int getRequiredPower() {
        return requiredPower[powerLevel];
    }

    public int getScore() {
        return score;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void loseLife() {
        lives--;
        recovering = true;
        recoveryTimer = System.nanoTime();
    }

    public void increasePower(int pwr) {

        if (power < 6) {
            power += pwr;
            if (power >= requiredPower[powerLevel]) {
                power -= requiredPower[powerLevel];
                powerLevel++;
            }
        }
    }

    public void damageHealth(int pwr) {

        lives -= pwr;

    }

}
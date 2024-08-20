import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class GameLogic extends JPanel implements Runnable, KeyListener, MouseListener {
    public final static int width = 800;
    public final static int height = 600;

    SoundEffect SE = new SoundEffect();

    Font minecraft_font;

    private boolean running = true, waveStart, keyControl = false, hs = false, menu = false;
    public boolean loopFlag = true, looper = false, reset = false;
    public BufferedImage image, imgPlayer, background1, background2;
    private double averageFps;
    private Graphics2D g;
    private int fps = 60, waveDelay = 2000, limitLevel = 36;

    public int mouseX;
    public int mouseY;

    public int gamestate;
    public final int titlescreen = 0;
    public final int play = 1;
    public final int highscore = 2;
    public final int gameover = 3;
    public final int finishgame = 4;

    int currentTime = 0;

    private int waveNumber;

    private long waveStartTimer, waveStartTimerDiff;
    private Thread thread;

    public static PlayerShip player;
    public static ArrayList<Lazer> bullets;
    public static ArrayList<EnemyShip> enemies;
    public static ArrayList<PlayerBuffs> powerUps;

    public static ArrayList<Destruction> explosions;
    public static ArrayList<TextManager> texts;

    public ArrayList<Integer> arrayScore = new ArrayList<>();

    private File flFile = null;
    private FileReader frRead = null;
    private BufferedReader brRead = null;

    public GameLogic() {
        super();

        InputStream f = getClass().getResourceAsStream("/font/minecraft_font.ttf");

        try {
            minecraft_font = Font.createFont(Font.TRUETYPE_FONT, f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        background1 = new ImageManager().loadImg("/img/stage/background.png");
        background2 = new ImageManager().loadImg("/img/stage/background1.jpg");

        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();
    }

    public void addNotify() {
        super.addNotify();

        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }

        addKeyListener(this);
    }

    public void run() {

        addMouseListener(this);
        gamestate = titlescreen;
        loadscore();

        do {

            running = true;

            if (gamestate == titlescreen) {
                menu();
                if (mouseX >= 265 && mouseX <= 528 && mouseY >= 307 && mouseY <= 353) {
                    gamestate = play;
                } else if (mouseX >= 250 && mouseX <= 545 && mouseY >= 380 && mouseY <= 422) {
                    try {

                        savescore();

                    } catch (IOException e) {

                    }
                    gamestate = highscore;
                } else if (mouseX >= 351 && mouseX <= 442 && mouseY >= 452 && mouseY <= 491) {

                    System.exit(0);

                }
            }

            if (gamestate == play) {
                PlayState();
                gamestate = gameover;
            }

            if (gamestate == highscore) {
                PlaySource();

                HighScoreState();

                if (mouseX >= 340 && mouseX <= 450 && mouseY >= 490 && mouseY <= 530) {
                    gamestate = titlescreen;
                }
            }


            if (gamestate == finishgame) {
                FinishGame();
                if (mouseX >= 340 && mouseX <= 450 && mouseY >= 490 && mouseY <= 530) {
                    arrayScore.add(player.getScore());
                    gamestate = titlescreen;

                }
            }

            if (gamestate == gameover) {

                GameOverState();

                if (mouseX >= 340 && mouseX <= 450 && mouseY >= 490 && mouseY <= 530) {
                    arrayScore.add(player.getScore());
                    gamestate = titlescreen;

                }
            }


        } while (loopFlag);

    }

    public void PlaySource() {

        if (imgPlayer == null) imgPlayer = new ImageManager().loadImg("/img/features/life.png");

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        player = new PlayerShip();
        bullets = new ArrayList<Lazer>();
        enemies = new ArrayList<EnemyShip>();
        powerUps = new ArrayList<PlayerBuffs>();
        explosions = new ArrayList<Destruction>();
        texts = new ArrayList<TextManager>();

        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;

    }

    public void PlayState() {

        looper = true;

        if (imgPlayer == null) imgPlayer = new ImageManager().loadImg("/img/features/life.png");

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        player = new PlayerShip();
        bullets = new ArrayList<Lazer>();
        enemies = new ArrayList<EnemyShip>();
        powerUps = new ArrayList<PlayerBuffs>();
        explosions = new ArrayList<Destruction>();
        texts = new ArrayList<TextManager>();

        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;

        long startTime;
        long URDTimeMillis;
        long waitTime = 0;
        long totalTime = 0;
        long targetTime = 1000 / fps;

        int frameCount = 0;
        int maxFrameCount = 30;


        while (running) {
            startTime = System.nanoTime();

            gameUpdate();
            gameRender();
            gameDraw();

            if (waveNumber == 10) {
                gamestate = finishgame;
                running = false;
                waveNumber = 0;
            }

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTimeMillis;
            try {
                Thread.sleep(waitTime);
            } catch (Exception ex) {
                totalTime += System.nanoTime() - startTime;
                frameCount++;
                if (frameCount == maxFrameCount) {
                    averageFps = 1000 / ((totalTime / frameCount) / 1000000);
                    frameCount = 0;
                    totalTime = 0;
                }
            }

        }

    }

    public void menu() {

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();

        background1 = new ImageManager().loadImg("/img/stage/stage3.png");

        g.drawImage(background1, 0, 0, null);
        g.setColor(Color.WHITE);
        g.setFont(minecraft_font.deriveFont(100F));

        String s = "MISPACE";
        int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        int x = (width - length) / 2 + 20;
        int y = height / 4;
        g.drawString(s, x, y);

        int in = 200;

        g.setFont(minecraft_font.deriveFont(40F));
        s = "Start Game";
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        x = (width - length) / 2;
        y = height / 4;
        g.drawString(s, x, y + in);
        in += 70;

        s = "High Scores";
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        x = (width - length) / 2;
        y = height / 4;
        g.drawString(s, x, y + in);
        in += 70;

        s = "Exit";
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        x = (width - length) / 2;
        y = height / 4;
        g.drawString(s, x, y + in);

        gameDraw();

    }

    public void HighScoreState() {

        player.update();

        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, width, height);

        g.setColor(Color.WHITE);
        g.setFont(minecraft_font.deriveFont(70F));

        String s = "High score";
        int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (width - length) / 2, 150);

        g.setFont(minecraft_font.deriveFont(36F));

        int y = 280, index = 1;

        Collections.sort(arrayScore, Collections.reverseOrder());

        if (arrayScore.size() == 0) {
            loadscore();
        }
        if (arrayScore.size() == 1) {
            s = index + ". " + arrayScore.get(0);
            length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
            g.drawString(s, (width - length) / 2, y);
        }
        if (arrayScore.size() == 2) {
            for (int i = 0; i <= 1; i++) {

                s = index + ". " + arrayScore.get(i);
                length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
                g.drawString(s, (width - length) / 2, y);
                index++;
                y += 75;
            }
        }

        if (arrayScore.size() == 3 || arrayScore.size() >= 3) {
            for (int i = 0; i <= 2; i++) {

                s = index + ". " + arrayScore.get(i);
                length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
                g.drawString(s, (width - length) / 2, y);
                index++;
                y += 75;
            }
        }

        s = "Back";
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (width - length) / 2, 525);

        gameDraw();


    }

    public void FinishGame() {

        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, width, height);

        g.setColor(Color.WHITE);
        g.setFont(minecraft_font.deriveFont(42F));

        String s = "Congratulation!";
        int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (width - length) / 2, 150);

        s = "You've finished your game!";
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (width - length) / 2, 230);

        g.setFont(minecraft_font.deriveFont(36F));

        s = "Score: " + player.getScore();
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (width - length) / 2, 300);

        s = "Back";
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (width - length) / 2, 525);

        gameDraw();

    }

    public void GameOverState() {

        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, width, height);

        g.setColor(Color.WHITE);
        g.setFont(minecraft_font.deriveFont(70F));

        String s = "Gameover";
        int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (width - length) / 2, 150);

        g.setFont(minecraft_font.deriveFont(36F));

        s = "Score: " + player.getScore();
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (width - length) / 2, 300);

        s = "Back";
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (width - length) / 2, 525);

        gameDraw();

    }

    private void createNewEnemies() {
        enemies.clear();
        EnemyShip e;

        if (waveNumber >= 1 && waveNumber <= 4) {
            for (int i = 0; i < waveNumber; i++)
                enemies.add(new EnemyShip(1, 1));
            for (int i = 0; i < 3; i++)
                enemies.add(new EnemyShip(1, waveNumber));
        }
        if (waveNumber >= 5 && waveNumber <= 8) {
            for (int i = 0; i < 5; i++)
                enemies.add(new EnemyShip(2, 1));
            for (int i = 0; i < 3; i++)
                enemies.add(new EnemyShip(2, (waveNumber - 4)));
        }
        if (waveNumber >= 9 && waveNumber <= 12) {
            for (int i = 0; i < 7; i++)
                enemies.add(new EnemyShip(3, 1));
            for (int i = 0; i < 2; i++)
                enemies.add(new EnemyShip(3, (waveNumber - 8)));
        }
        if (waveNumber >= 13 && waveNumber <= 16) {
            for (int i = 0; i < 7; i++)
                enemies.add(new EnemyShip(4, 1));
            for (int i = 0; i < 2; i++)
                enemies.add(new EnemyShip(4, (waveNumber - 8)));
        }
        if (waveNumber >= 17 && waveNumber <= 20) {
            for (int i = 0; i < 7; i++)
                enemies.add(new EnemyShip(5, 1));
            for (int i = 0; i < 2; i++)
                enemies.add(new EnemyShip(5, (waveNumber - 8)));
        }
        if (waveNumber >= 21 && waveNumber <= 24) {
            for (int i = 0; i < 7; i++)
                enemies.add(new EnemyShip(6, 1));
            for (int i = 0; i < 2; i++)
                enemies.add(new EnemyShip(6, (waveNumber - 8)));
        }
        if (waveNumber >= 25 && waveNumber <= 28) {
            for (int i = 0; i < 7; i++)
                enemies.add(new EnemyShip(7, 1));
            for (int i = 0; i < 2; i++)
                enemies.add(new EnemyShip(7, (waveNumber - 8)));
        }
        if (waveNumber >= 29 && waveNumber <= 32) {
            for (int i = 0; i < 7; i++)
                enemies.add(new EnemyShip(8, 1));
            for (int i = 0; i < 2; i++)
                enemies.add(new EnemyShip(8, (waveNumber - 8)));
        }
        if (waveNumber >= 33 && waveNumber <= 36) {
            for (int i = 0; i < 7; i++)
                enemies.add(new EnemyShip(9, 1));
            for (int i = 0; i < 2; i++)
                enemies.add(new EnemyShip(9, (waveNumber - 8)));
        } else if (waveNumber > limitLevel) running = false;


    }

    private void gameUpdate() {
        if (waveStartTimer == 0 && enemies.size() == 0) {
            waveNumber++;
            waveStart = false;
            waveStartTimer = System.nanoTime();
        } else {
            waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
            if (waveStartTimerDiff > waveDelay) {
                waveStart = true;
                waveStartTimer = 0;
                waveStartTimerDiff = 0;
            }
        }

        if (waveStart && enemies.size() == 0) createNewEnemies();

        player.update();

        for (int i = 0; i < bullets.size(); i++) {
            boolean remove = bullets.get(i).update();
            if (remove) {
                bullets.remove(i);
                i--;
            }
        }

        for (int i = 0; i < explosions.size(); i++) {
            boolean remove = explosions.get(i).update();
            if (remove) {
                explosions.remove(i);
                i--;
            }
        }

        for (int i = 0; i < powerUps.size(); i++) {
            boolean remove = powerUps.get(i).update();
            if (remove) {
                powerUps.remove(i);
                i--;
            }
        }

        for (int i = 0; i < texts.size(); i++) {
            boolean remove = texts.get(i).update();
            if (remove) {
                texts.remove(i);
                i--;
            }
        }

        for (int i = 0; i < enemies.size(); i++)
            enemies.get(i).update();

        for (int i = 0; i < bullets.size(); i++) {
            Lazer b = bullets.get(i);
            double bx = b.getX();
            double by = b.getY();
            double br = b.getR();
            for (int j = 0; j < enemies.size(); j++) {
                EnemyShip e = enemies.get(j);
                double ex = e.getX();
                double ey = e.getY();
                double er = e.getR();

                double dx = bx - ex;
                double dy = by - ey;

                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < br + er) {
                    e.hit();
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }


        if (player.isDead()) {
            running = false;
        }



        for (int i = 0; i < enemies.size(); i++) {


            currentTime += 1;

            if (currentTime == 250) {
                for (int j = 0; j < enemies.size(); j++) {

                    EnemyShip e = enemies.get(j);
                    powerUps.add(new PlayerBuffs(3, e.getX() - 50, e.getY()));

                }
                currentTime = 0;
            } else if (currentTime == 125 && enemies.size() <= 3) {


                for (int j = 0; j < enemies.size(); j++) {

                    EnemyShip e = enemies.get(j);
                    powerUps.add(new PlayerBuffs(3, e.getX() - 50, e.getY()));

                }
                currentTime = 0;

            }

            if (enemies.get(i).isDead()) {

                EnemyShip e = enemies.get(i);

                int rand = (int) (Math.random() * 5);


                if (rand == 1) powerUps.add(new PlayerBuffs(1, e.getX(), e.getY()));


                if (rand == 4) powerUps.add(new PlayerBuffs(2, e.getX(), e.getY()));


                player.setScore(e.getType() + e.getRank());
                enemies.remove(i);
                i--;

                e.explode();
                SEplay(1);
                explosions.add(new Destruction(e.getX(), e.getY(), e.getR(), e.getR() + 30));
            }

        }

        if (!player.isRecovering()) {
            int px = player.getX();
            int py = player.getY();
            int pr = player.getR();
            for (int i = 0; i < enemies.size(); i++) {
                EnemyShip e = enemies.get(i);
                double ex = e.getX();
                double ey = e.getY();
                double er = e.getR();

                double dx = px - ex;
                double dy = py - ey;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < pr + er) player.loseLife();
            }
        }

        int px = player.getX();
        int py = player.getY();
        int pr = player.getR();

        for (int i = 0; i < powerUps.size(); i++) {
            PlayerBuffs p = powerUps.get(i);

            int type = p.getType();

            double x = p.getX();
            double y = p.getY();
            double r = p.getR();
            double dx = px - x;
            double dy = py - y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < pr + r) {


                if (type == 1) {
                    player.setLife(player.getLives() + 1);
                    SEplay(2);
                    texts.add(new TextManager(player.getX(), player.getY(), 1000, "Life +1"));
                }
                if (type == 2) {
                    player.increasePower(1);
                    SEplay(2);
                    texts.add(new TextManager(player.getX(), player.getY(), 1000, "Power +1"));
                }
                if (type == 3 && !player.isRecovering()) {
                    player.damageHealth(1);
                    texts.add(new TextManager(player.getX(), player.getY(), 1000, "-1 HP"));
                }

                powerUps.remove(i);
                i--;

            }


        }
    }

    public void savescore() throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter("scores.txt"));

        if (arrayScore.size() > 0) {
            for (int i = 0; i < arrayScore.size(); i++) {
                bw.write(String.valueOf(arrayScore.get(i)));
                bw.newLine();
            }
        }
        bw.close();
    }

    public void loadscore() {

        try {

            BufferedReader br = new BufferedReader(new FileReader("scores.txt"));

            try {
                String sc1 = br.readLine();
                arrayScore.add(Integer.parseInt(sc1));
            } catch (Exception e) {

            }
            try {
                String sc2 = br.readLine();
                arrayScore.add(Integer.parseInt(sc2));
            } catch (Exception e) {

            }
            try {
                String sc3 = br.readLine();
                arrayScore.add(Integer.parseInt(sc3));

            } catch (Exception e) {

            }

            br.close();

        } catch (Exception e) {

        }


    }

    public void SEplay(int i) {
        SE.File(i);
        SE.play();
    }

    private void gameRender() {

        g.drawImage(background2, 0, 0, null);

        player.draw(g);

        for (int i = 0; i < bullets.size(); i++)
            bullets.get(i).draw(g);

        for (int i = 0; i < enemies.size(); i++)
            enemies.get(i).draw(g);

        for (int i = 0; i < powerUps.size(); i++)
            powerUps.get(i).draw(g);

        for (int i = 0; i < explosions.size(); i++)
            explosions.get(i).draw(g);

        for (int i = 0; i < texts.size(); i++)
            texts.get(i).draw(g);

        if (waveStartTimer != 0 && waveNumber <= limitLevel) {
            g.setFont(minecraft_font.deriveFont(70F));
            String s = "Level " + waveNumber;
            g.setColor(new Color(255, 255, 255, 255));
            g.drawString(s, 250, 300);
        }

        g.setColor(Color.YELLOW);
        g.fillRect(20, 60, player.getPower() * 8, 8);
        g.setColor(Color.YELLOW.darker());
        g.setStroke(new BasicStroke(3));
        for (int i = 0; i < player.getRequiredPower(); i++)
            g.drawRect(20 + 8 * i, 60, 8, 8);
        g.setStroke(new BasicStroke(1));

        for (int i = 0; i < player.getLives(); i++)
            g.drawImage(imgPlayer, 25 + (35 * i), 20, 30, 30, null);

        g.setColor(Color.WHITE);
        g.setFont(minecraft_font.deriveFont(16F));
        g.drawString("Score: " + player.getScore(), width - 150, 30);
        g.setFont(minecraft_font.deriveFont(16F));
        g.drawString("Level: " + waveNumber, width - 250, 30);

    }

    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (gamestate == play) {

            if (keyCode == KeyEvent.VK_A) player.setLeft(true);
            if (keyCode == KeyEvent.VK_D) player.setRight(true);
            if (keyCode == KeyEvent.VK_W) player.setUp(true);
            if (keyCode == KeyEvent.VK_S) player.setDown(true);
            if (keyCode == KeyEvent.VK_SPACE) player.setFiring(true);

        }

    }

    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_A) {
            player.setLeft(false);
        }
        if (keyCode == KeyEvent.VK_D) {
            player.setRight(false);
        }
        if (keyCode == KeyEvent.VK_W) {
            player.setUp(false);
        }
        if (keyCode == KeyEvent.VK_S) {
            player.setDown(false);
        }
        if (keyCode == KeyEvent.VK_SPACE) {
            player.setFiring(false);
        }

    }

    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        mouseX = e.getX();
        mouseY = e.getY();

        System.out.println(mouseX + " , " + mouseY);


    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
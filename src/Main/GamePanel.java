package Main;

import Entity.Player;
import Entity.Player2;
import Entity.entity;
import tile.TileManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    final int orignalTileSize = 16;
    final int scale = 3;

    public final int tileSize = orignalTileSize * scale;
    final int maxScreenCol = 32;
    final int maxScreenRow = 18;
    public final int screenWidth = tileSize * maxScreenCol;   // 1536
    public final int screenHeight = tileSize * maxScreenRow;  // 864
    public final int groundY = screenHeight - 96;             // where feet rest

    int FPS = 60;

    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    UI ui = new UI(this);
    Thread gameThread;
    public Player player1;
    public Player2 player2;

    // ---- game states ----
    public static final int MENU = 0, PLAY = 1, PAUSE = 2, ROUND = 3, MATCH = 4;
    public int gameState = MENU;

    // ---- match / round bookkeeping ----
    public final int roundsToWin = 2;
    public final int roundSeconds = 60;
    public int roundTimer;                 // in ticks
    public int bannerTimer;
    public String bannerText = "", bannerSub = "";
    private entity pendingWinner;
    public entity matchWinner;

    Sound sound = new Sound();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        player1 = new Player(this, keyH);
        player2 = new Player2(this, keyH);
    }

    public void gameSetup() {
        playMusic(0);
        gameState = MENU;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // -------- match flow --------
    public void startMatch() {
        player1.fullReset();
        player2.fullReset();
        matchWinner = null;
        newRound();
    }

    private void newRound() {
        player1.reset();
        player2.reset();
        roundTimer = roundSeconds * FPS;
        gameState = PLAY;
    }

    private void setBanner(String text, String sub, int seconds) {
        bannerText = text;
        bannerSub = sub;
        bannerTimer = seconds * FPS;
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long last = System.nanoTime();

        while (gameThread != null) {
            long now = System.nanoTime();
            delta += (now - last) / drawInterval;
            last = now;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            } else {
                try { Thread.sleep(1); } catch (InterruptedException e) { /* ignore */ }
            }
        }
    }

    public void update() {
        switch (gameState) {
            case MENU -> { if (keyH.consumeEnter()) startMatch(); }
            case PLAY -> {
                player1.update(player2);
                player2.update(player1);
                if (roundTimer > 0) roundTimer--;
                checkRoundEnd();
            }
            case ROUND -> {
                player1.update(player2);
                player2.update(player1);
                if (bannerTimer > 0) bannerTimer--;
                else advanceAfterRound();
            }
            case MATCH -> {
                player1.update(player2);
                player2.update(player1);
                if (keyH.consumeEnter()) startMatch();
            }
            case PAUSE -> { /* frozen */ }
        }
    }

    private void checkRoundEnd() {
        boolean p1dead = player1.isDeathDone();
        boolean p2dead = player2.isDeathDone();
        boolean timeUp = roundTimer <= 0;
        if (!(p1dead || p2dead || timeUp)) return;

        entity winner;
        if (p1dead && p2dead)      winner = null;
        else if (p2dead)           winner = player1;
        else if (p1dead)           winner = player2;
        else if (player1.hp > player2.hp) winner = player1;
        else if (player2.hp > player1.hp) winner = player2;
        else                       winner = null;

        if (winner != null) winner.roundsWon++;
        pendingWinner = winner;

        if (winner == null) setBanner("DRAW", "", 2);
        else setBanner(winner.getName() + " WINS THE ROUND", "", 2);
        gameState = ROUND;
    }

    private void advanceAfterRound() {
        if (pendingWinner != null && pendingWinner.roundsWon >= roundsToWin) {
            matchWinner = pendingWinner;
            setBanner(matchWinner.getName() + " WINS!", "Press ENTER for a rematch", 999);
            gameState = MATCH;
        } else {
            newRound();
        }
    }

    public void togglePause() {
        if (gameState == PLAY) gameState = PAUSE;
        else if (gameState == PAUSE) gameState = PLAY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        tileM.draw(g2);

        if (gameState != MENU) {
            player1.draw(g2);
            player2.draw(g2);
        }

        ui.draw(g2);
    }

    public void playMusic(int i) {
        sound.setFile(i);
        sound.play();
        sound.loop();
    }

    public void stopMusic() {
        sound.stop();
    }
}

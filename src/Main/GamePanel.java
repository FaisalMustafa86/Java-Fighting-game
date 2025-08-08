package Main;

import Entity.Player;
import Entity.Player2;
import tile.TileManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    final int orignalTileSize = 16;
    final int scale = 3;

    public final int tileSize = orignalTileSize * scale;
    final int maxScreenCol = 32;
    final int maxScreenRow = 18;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    int FPS = 60;

    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    Thread gameThread;
    public Player player1;
    public Player2 player2;

    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2;

    Sound sound = new Sound();

    public void gameSetup(){

        playMusic(0);

        gameState = playState;
    }

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        // one KeyListener for both players
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Initialize that one KeyListener
        player1 = new Player(this, keyH);
        player2 = new Player2(this, keyH);


    }



    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameThread != null) {
            update();
            repaint();
            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(){

        // updates if gamestate is play
if(gameState == playState){
    player1.update();
    player2.update();

}
// updating stops if gamestate is pause
        if (gameState == pauseState){
           //nothing updates
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2= (Graphics2D) g;

        tileM.draw(g2);


        player1.draw(g2);
        player2.draw(g2);

        g2.dispose();
    }

    public void playMusic(int i){
        sound.setFile(i);
        sound.play();
        sound.loop();
    }
    public void stopMusic(){
        sound.stop();
    }

}
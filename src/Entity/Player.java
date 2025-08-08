package Entity;

import Main.GamePanel;
import Main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player extends entity {

    GamePanel gp;
    KeyHandler KeyH;
    private int frameCounter = 0;
    private int walkFrame = 0;
    private boolean walking = false;
    private BufferedImage[] walkImages = new BufferedImage[8];
    private BufferedImage[] deadImages = new BufferedImage[8];

    // Attack cooldown variables
    private long lastAttackTime = 0;
    private final int attackCooldown = 500; // milliseconds between attacks

    public Player(GamePanel gp, KeyHandler KeyH) {
        this.gp = gp;
        this.KeyH = KeyH;

        solidArea = new Rectangle(gp.tileSize, gp.tileSize * 2, gp.tileSize * 3, gp.tileSize * 3);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = 100;
        y = 600;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage() {
        try {
            // Load attack animation frames image loading method 1)
            a1 = ImageIO.read(getClass().getResourceAsStream("/Player1/sA_1.png"));
            a2 = ImageIO.read(getClass().getResourceAsStream("/Player1/sA_2.png"));
            a3 = ImageIO.read(getClass().getResourceAsStream("/Player1/sA_3.png"));
            a4 = ImageIO.read(getClass().getResourceAsStream("/Player1/sA_4.png"));
            a5 = ImageIO.read(getClass().getResourceAsStream("/Player1/sA_5.png"));

            // Load walking animation frames  image loading method 2)
            for (int i = 0; i < 8; i++) {
                walkImages[i] = ImageIO.read(getClass().getResourceAsStream("/Player1/sW_" + (i + 1) + ".png"));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void update() {
        int nextX = x;
        int nextY = y;
        boolean moved = false;

        // Movement input
        if (KeyH.leftPressed) { nextX -= speed; direction = "left"; moved = true; }
        else if (KeyH.rightPressed) { nextX += speed; direction = "right"; moved = true; }

        walking = moved;

        // Boundary checks (working)
        if (nextX < 0) { nextX = 0; }
        if (nextX > gp.screenWidth - gp.tileSize * 5) { nextX = gp.screenWidth - gp.tileSize * 5; }
        if (nextY < 0) { nextY = 0; }
        if (nextY > gp.screenHeight - gp.tileSize * 5) { nextY = gp.screenHeight - gp.tileSize * 5; }

        // Player-to-player collision (working)
        Rectangle player1NextSolidArea = new Rectangle(nextX + solidArea.x, nextY + solidArea.y, solidArea.width, solidArea.height);
        Rectangle player2CurrentSolidArea = new Rectangle(gp.player2.x + gp.player2.solidArea.x, gp.player2.y + gp.player2.solidArea.y, gp.player2.solidArea.width, gp.player2.solidArea.height);

        if (player1NextSolidArea.intersects(player2CurrentSolidArea)) {
            nextX = x;
            nextY = y;
        }

        x = nextX;
        y = nextY;

        // Attack animation trigger with cooldown (not working)
        if (KeyH.attackPressed && !attacking &&
                System.currentTimeMillis() - lastAttackTime > attackCooldown) {
            attacking = true;
            attackFrame = 0;
            frameCounter = 0;
            lastAttackTime = System.currentTimeMillis(); // Record attack time
        }

        // Animation update logic
        if (attacking) {
            frameCounter++;
            if (frameCounter >= 5) { // Control attack animation speed
                attackFrame++;
                frameCounter = 0;
            }
            if (attackFrame >= 5) { // Reset attack animation
                attacking = false;
                attackFrame = 0;
            }
        } else if (walking) {
            frameCounter++;
            if (frameCounter >= 5) { // Control walking animation speed
                walkFrame = (walkFrame + 1) % 8;
                frameCounter = 0;
            }
        } else {
            walkFrame = 0;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage imageToDraw = getImageBasedOnState();

        if (imageToDraw != null) {
            g2.drawImage(imageToDraw, x, y, gp.tileSize * 5, gp.tileSize * 5, null);
        }
    }

    private BufferedImage getImageBasedOnState() {
        if (attacking) {
            return switch (attackFrame) {
                case 0 -> a1; case 1 -> a2; case 2 -> a3; case 3 -> a4; case 4 -> a5;
                default -> a4;
            };
        } else if (walking) {
            return walkImages[walkFrame];
        } else {
            return a1;
        }
    }
}
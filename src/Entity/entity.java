package Entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class entity {
    public int x, y;
    public int speed;

    // Animation frames
    public BufferedImage a1, a2, a3, a4, a5;
    public String direction;

    // Attack system (not working atm)
    public boolean attacking = false;
    public int attackFrame = 0;
    private long lastAttackTime = 0;
    private final int attackCooldown = 500; // milliseconds

    // Collision area (working)
    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY;

    // New method to check if attack can be started (not in use atm)
    public boolean canAttack() {
        return !attacking && (System.currentTimeMillis() - lastAttackTime > attackCooldown);
    }

    // New method to start an attack
    public void startAttack() {
        attacking = true;
        attackFrame = 0;
        lastAttackTime = System.currentTimeMillis();
    }

    // New method to update attack state
    public void updateAttack() {
        if (attacking) {
            attackFrame++;
            if (attackFrame >= 5) { // Assuming 5 attack frames (a1-a5)
                attacking = false;
                attackFrame = 0;
            }
        }
    }
}
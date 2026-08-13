package Entity;

import Main.GamePanel;
import Main.KeyHandler;

import java.awt.Color;

/** Player 2 — controls: LEFT/RIGHT move, UP jump, DOWN block, SHIFT attack. */
public class Player2 extends entity {

    public Player2(GamePanel gp, KeyHandler key) {
        super(gp, key);
    }

    @Override
    protected void configure() {
        name = "PLAYER 2";
        basePath = "/Player2/";
        code = "s2";
        nWalk = 9; nAttack = 4; nHurt = 3; nDead = 6;
        spawnX = gp.screenWidth - 200 - W;
        spawnFacingRight = false;
        artFacesRight = false;   // P2's sprite art naturally points left
        accent = new Color(255, 95, 85);    // red
    }

    @Override
    protected void pollInput() {
        inLeft   = key.left2Pressed;
        inRight  = key.right2Pressed;
        inUp     = key.up2Pressed;
        inDown   = key.down2Pressed;
        inAttack = key.attack2Pressed;
    }
}

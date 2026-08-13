package Entity;

import Main.GamePanel;
import Main.KeyHandler;

import java.awt.Color;

/** Player 1 — controls: A/D move, W jump, S block, SPACE attack. */
public class Player extends entity {

    public Player(GamePanel gp, KeyHandler key) {
        super(gp, key);
    }

    @Override
    protected void configure() {
        name = "PLAYER 1";
        basePath = "/Player1/";
        code = "s";
        nWalk = 8; nAttack = 5; nHurt = 3; nDead = 5;
        spawnX = 200;
        spawnFacingRight = true;
        accent = new Color(80, 190, 255);   // cyan
    }

    @Override
    protected void pollInput() {
        inLeft   = key.leftPressed;
        inRight  = key.rightPressed;
        inUp     = key.upPressed;
        inDown   = key.downPressed;
        inAttack = key.attackPressed;
    }
}

package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    // Player 1: A/D move, W jump, S block, SPACE attack
    public boolean upPressed, downPressed, leftPressed, rightPressed, attackPressed;

    // Player 2: arrows move/jump, DOWN block, SHIFT attack
    public boolean up2Pressed, down2Pressed, left2Pressed, right2Pressed, attack2Pressed;

    // Edge-triggered menu / rematch confirm
    private boolean enterEdge;

    GamePanel gp;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Player 1
        if (code == KeyEvent.VK_W)     upPressed = true;
        if (code == KeyEvent.VK_S)     downPressed = true;
        if (code == KeyEvent.VK_A)     leftPressed = true;
        if (code == KeyEvent.VK_D)     rightPressed = true;
        if (code == KeyEvent.VK_SPACE) attackPressed = true;

        // Player 2
        if (code == KeyEvent.VK_UP)    up2Pressed = true;
        if (code == KeyEvent.VK_DOWN)  down2Pressed = true;
        if (code == KeyEvent.VK_LEFT)  left2Pressed = true;
        if (code == KeyEvent.VK_RIGHT) right2Pressed = true;
        if (code == KeyEvent.VK_SHIFT) attack2Pressed = true;

        // System
        if (code == KeyEvent.VK_P)     gp.togglePause();
        if (code == KeyEvent.VK_ENTER) enterEdge = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W)     upPressed = false;
        if (code == KeyEvent.VK_S)     downPressed = false;
        if (code == KeyEvent.VK_A)     leftPressed = false;
        if (code == KeyEvent.VK_D)     rightPressed = false;
        if (code == KeyEvent.VK_SPACE) attackPressed = false;

        if (code == KeyEvent.VK_UP)    up2Pressed = false;
        if (code == KeyEvent.VK_DOWN)  down2Pressed = false;
        if (code == KeyEvent.VK_LEFT)  left2Pressed = false;
        if (code == KeyEvent.VK_RIGHT) right2Pressed = false;
        if (code == KeyEvent.VK_SHIFT) attack2Pressed = false;
    }

    /** Returns true once per Enter press. */
    public boolean consumeEnter() {
        if (enterEdge) { enterEdge = false; return true; }
        return false;
    }
}

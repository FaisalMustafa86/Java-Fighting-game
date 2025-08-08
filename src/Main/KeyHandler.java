package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public class KeyHandler implements KeyListener {
    // Player 1 Controls
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean attackPressed;

    GamePanel gp;

    // Player 2 Controls
    public boolean up2Pressed, down2Pressed, left2Pressed, right2Pressed;
    public boolean attack2Pressed;

    // System Controls (basically to check which keys are being pressed atm)
    private final Set<Integer> pressedKeys = new HashSet<>();

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        pressedKeys.add(code);

        // Player 1 Controls
        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
        if (code == KeyEvent.VK_SPACE) attackPressed = true;
        if (code == KeyEvent.VK_P) {
            if(gp.gameState == gp.playState){
                gp.gameState = gp.pauseState;
            }
            else if(gp.gameState == gp.pauseState){
                gp.gameState = gp.playState;
            }
        }

        // Player 2 Controls
        if (code == KeyEvent.VK_UP) up2Pressed = true;
        if (code == KeyEvent.VK_DOWN) down2Pressed = true;
        if (code == KeyEvent.VK_LEFT) left2Pressed = true;
        if (code == KeyEvent.VK_RIGHT) right2Pressed = true;
        if (code == KeyEvent.VK_SHIFT) attack2Pressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        pressedKeys.remove(code);

        // Player 1 Controls
        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        if (code == KeyEvent.VK_SPACE) attackPressed = false;

        // Player 2 Controls
        if (code == KeyEvent.VK_UP) up2Pressed = false;
        if (code == KeyEvent.VK_DOWN) down2Pressed = false;
        if (code == KeyEvent.VK_LEFT) left2Pressed = false;
        if (code == KeyEvent.VK_RIGHT) right2Pressed = false;
        if (code == KeyEvent.VK_SHIFT) attack2Pressed = false;
    }

    // New method to check if a key is currently pressed
    public boolean isPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    // New method to reset a specific key state
    public void resetKey(int keyCode) {
        pressedKeys.remove(keyCode);
        switch (keyCode) {
            case KeyEvent.VK_W -> upPressed = false;
            case KeyEvent.VK_S -> downPressed = false;
            case KeyEvent.VK_A -> leftPressed = false;
            case KeyEvent.VK_D -> rightPressed = false;
            case KeyEvent.VK_SPACE -> attackPressed = false;
            case KeyEvent.VK_UP -> up2Pressed = false;
            case KeyEvent.VK_DOWN -> down2Pressed = false;
            case KeyEvent.VK_LEFT -> left2Pressed = false;
            case KeyEvent.VK_RIGHT -> right2Pressed = false;
            case KeyEvent.VK_SHIFT -> attack2Pressed = false;
        }
    }
}
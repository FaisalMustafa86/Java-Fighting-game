package Main;

import Entity.entity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class UI {

    GamePanel gp;
    Graphics2D g2;

    private final Font fHuge  = new Font("SansSerif", Font.BOLD, 90);
    private final Font fBig   = new Font("SansSerif", Font.BOLD, 46);
    private final Font fMed   = new Font("SansSerif", Font.BOLD, 26);
    private final Font fSmall = new Font("SansSerif", Font.PLAIN, 22);

    public UI(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        switch (gp.gameState) {
            case GamePanel.MENU -> drawMenu();
            case GamePanel.PLAY -> drawHUD();
            case GamePanel.PAUSE -> { drawHUD(); drawPause(); }
            case GamePanel.ROUND, GamePanel.MATCH -> { drawHUD(); drawBanner(); }
        }
    }

    // ------------------------------------------------------------------ HUD
    private void drawHUD() {
        int margin = 40, barW = 560, barH = 34, top = 42;

        drawHealth(gp.player1, margin, top, barW, barH, true);
        drawHealth(gp.player2, gp.screenWidth - margin - barW, top, barW, barH, false);

        // names
        g2.setFont(fMed);
        g2.setColor(Color.WHITE);
        g2.drawString(gp.player1.getName(), margin, top - 12);
        String n2 = gp.player2.getName();
        g2.drawString(n2, gp.screenWidth - margin - textW(n2), top - 12);

        // round win pips
        for (int i = 0; i < gp.roundsToWin; i++) {
            drawPip(margin + i * 26, top + barH + 16, i < gp.player1.roundsWon, gp.player1.accent);
            drawPip(gp.screenWidth - margin - 14 - i * 26, top + barH + 16, i < gp.player2.roundsWon, gp.player2.accent);
        }

        // timer
        int secs = Math.max(0, (gp.roundTimer + gp.FPS - 1) / gp.FPS);
        g2.setFont(fBig);
        String t = String.valueOf(secs);
        g2.setColor(secs <= 10 ? new Color(255, 90, 80) : Color.WHITE);
        g2.drawString(t, gp.screenWidth / 2 - textW(t) / 2, top + 40);
    }

    private void drawHealth(entity p, int x, int y, int w, int h, boolean leftAnchored) {
        double ratio = (double) p.hp / p.maxHP;
        double dRatio = (double) p.displayHP / p.maxHP;
        int fw = (int) (w * ratio);
        int dw = (int) (w * dRatio);

        // frame background
        g2.setColor(new Color(20, 20, 24, 220));
        g2.fillRoundRect(x - 4, y - 4, w + 8, h + 8, 12, 12);

        // damage trail (fades from the last hit)
        g2.setColor(new Color(230, 210, 90));
        if (leftAnchored) g2.fillRect(x, y, dw, h);
        else g2.fillRect(x + w - dw, y, dw, h);

        // current health
        g2.setColor(healthColor(ratio));
        if (leftAnchored) g2.fillRect(x, y, fw, h);
        else g2.fillRect(x + w - fw, y, fw, h);

        // accent border
        g2.setColor(p.accent);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x - 4, y - 4, w + 8, h + 8, 12, 12);
    }

    private Color healthColor(double r) {
        if (r > 0.5) return new Color(80, 210, 90);
        if (r > 0.25) return new Color(230, 180, 50);
        return new Color(220, 70, 60);
    }

    private void drawPip(int x, int y, boolean filled, Color c) {
        if (filled) { g2.setColor(c); g2.fillOval(x, y, 14, 14); }
        else { g2.setColor(new Color(255, 255, 255, 70)); g2.drawOval(x, y, 14, 14); }
    }

    // ------------------------------------------------------------------ screens
    private void drawMenu() {
        dim(180);
        g2.setColor(Color.WHITE);
        g2.setFont(fHuge);
        center("VAGABOND", gp.screenHeight / 2 - 130);

        g2.setFont(fMed);
        g2.setColor(new Color(255, 220, 120));
        center("Press ENTER to fight", gp.screenHeight / 2 - 40);

        g2.setFont(fSmall);
        g2.setColor(new Color(80, 190, 255));
        int cy = gp.screenHeight / 2 + 40;
        line("PLAYER 1", gp.screenWidth / 2 - 320, cy, true);
        g2.setColor(Color.WHITE);
        line("A / D  move    W  jump    S  block    SPACE  attack", gp.screenWidth / 2 - 320, cy + 34, false);

        g2.setColor(new Color(255, 95, 85));
        line("PLAYER 2", gp.screenWidth / 2 - 320, cy + 90, true);
        g2.setColor(Color.WHITE);
        line("← / →  move    ↑  jump    ↓  block    SHIFT  attack", gp.screenWidth / 2 - 320, cy + 124, false);
    }

    private void drawPause() {
        dim(150);
        g2.setColor(Color.WHITE);
        g2.setFont(fHuge);
        center("PAUSED", gp.screenHeight / 2 - 30);
        g2.setFont(fMed);
        g2.setColor(new Color(220, 220, 220));
        center("Press P to resume", gp.screenHeight / 2 + 40);
    }

    private void drawBanner() {
        dim(120);
        g2.setColor(Color.WHITE);
        g2.setFont(fBig);
        center(gp.bannerText, gp.screenHeight / 2 - 10);
        if (!gp.bannerSub.isEmpty()) {
            g2.setFont(fMed);
            g2.setColor(new Color(255, 220, 120));
            center(gp.bannerSub, gp.screenHeight / 2 + 50);
        }
    }

    // ------------------------------------------------------------------ helpers
    private void dim(int alpha) {
        g2.setColor(new Color(0, 0, 0, alpha));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }

    private void center(String text, int y) {
        g2.drawString(text, gp.screenWidth / 2 - textW(text) / 2, y);
    }

    private void line(String text, int x, int y, boolean bold) {
        g2.drawString(text, x, y);
    }

    private int textW(String text) {
        return g2.getFontMetrics().stringWidth(text);
    }
}

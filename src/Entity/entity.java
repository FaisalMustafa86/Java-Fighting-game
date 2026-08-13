package Entity;

import Main.GamePanel;
import Main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Base fighter. Holds all shared physics, combat, animation and rendering so that
 * the two players are just thin configuration on top (sprite set + control keys).
 */
public abstract class entity {

    // ---- tuning constants (shared by both fighters) ----
    protected static final double SPEED      = 5.0;   // walk speed  (px / tick)
    protected static final double GRAVITY    = 0.9;
    protected static final double JUMP       = -19.0;
    protected static final int    ATTACK_DMG = 9;
    protected static final double KB         = 16.0;  // knockback on a clean hit
    protected static final double BLOCK_KB   = 6.0;   // knockback while blocking
    protected static final int    ATTACK_SPEED = 4;   // ticks per attack frame
    protected static final int    WALK_SPEED   = 5;
    protected static final int    HURT_SPEED   = 6;
    protected static final int    DEAD_SPEED   = 8;
    protected static final int    ATTACK_CD    = 8;   // recovery ticks after a swing
    protected static final int    DRAW_H       = 300; // on-screen sprite height

    // logical body box (independent of the art, so hits are consistent)
    public final int W = 110, H = 240;

    // ---- collaborators ----
    protected GamePanel gp;
    protected KeyHandler key;

    // ---- configuration (filled by subclass configure()) ----
    protected String name;
    protected String basePath;   // e.g. "/Player1/"
    protected String code;       // e.g. "s" or "s2"
    protected int nWalk, nAttack, nHurt, nDead;
    protected double spawnX;
    protected boolean spawnFacingRight;
    protected boolean artFacesRight = true;  // which way the source sprite art points
    public Color accent = Color.WHITE;

    // ---- sprites ----
    protected BufferedImage[] walk, attack, hurt, dead;

    // ---- physics / position ----
    public double x, y, vx, vy;
    protected boolean onGround;

    // ---- stats ----
    public int maxHP = 100;
    public int hp;
    public double displayHP;   // eased value, drives the health-bar "damage trail"
    public int roundsWon;

    // ---- state machine ----
    public enum State { IDLE, WALK, JUMP, ATTACK, HURT, BLOCK, DEAD }
    public State state = State.IDLE;
    protected int animIndex, animTick;
    public boolean facingRight = true;
    protected boolean deathDone;

    // ---- combat bookkeeping ----
    protected boolean hitApplied;   // did the current swing already connect?
    protected int attackCooldown;
    public int blinkTimer;          // brief post-hit invulnerability flicker

    // ---- input (filled each tick by pollInput) ----
    protected boolean inLeft, inRight, inUp, inDown, inAttack;
    protected boolean prevUp, prevAttack;

    protected entity(GamePanel gp, KeyHandler key) {
        this.gp = gp;
        this.key = key;
        configure();
        loadSprites();
        reset();
    }

    /** Subclass supplies sprite set, control mapping and spawn. */
    protected abstract void configure();

    /** Subclass copies its control keys into the in* fields. */
    protected abstract void pollInput();

    // ------------------------------------------------------------------ setup
    private void loadSprites() {
        walk   = load("W", nWalk);
        attack = load("A", nAttack);
        hurt   = load("H", nHurt);
        dead   = load("D", nDead);
    }

    private BufferedImage[] load(String cat, int n) {
        BufferedImage[] arr = new BufferedImage[n];
        for (int i = 0; i < n; i++) {
            String path = basePath + code + cat + "_" + (i + 1) + ".png";
            try {
                arr[i] = ImageIO.read(getClass().getResourceAsStream(path));
            } catch (Exception e) {
                System.err.println("Missing sprite: " + path);
            }
        }
        return arr;
    }

    /** Reset for a new round (keeps rounds won). */
    public void reset() {
        hp = maxHP;
        displayHP = maxHP;
        x = spawnX;
        y = gp.groundY - H;
        vx = vy = 0;
        onGround = true;
        state = State.IDLE;
        animIndex = animTick = 0;
        facingRight = spawnFacingRight;
        deathDone = false;
        hitApplied = false;
        blinkTimer = 0;
        attackCooldown = 0;
    }

    /** Reset for a brand-new match. */
    public void fullReset() {
        roundsWon = 0;
        reset();
    }

    // ------------------------------------------------------------------ update
    public void update(entity opp) {
        pollInput();

        if (displayHP > hp) displayHP = Math.max(hp, displayHP - 0.7);
        if (blinkTimer > 0) blinkTimer--;
        if (attackCooldown > 0) attackCooldown--;

        double prevX = x;

        switch (state) {
            case DEAD   -> tickDead();
            case HURT   -> tickHurt();
            case ATTACK -> tickAttack(opp);
            case BLOCK  -> tickBlock(opp);
            default     -> tickActionable(opp);   // IDLE / WALK / JUMP
        }

        applyPhysics(opp, prevX);

        prevAttack = inAttack;
        prevUp = inUp;
    }

    private void tickActionable(entity opp) {
        facingRight = opp.centerX() > centerX();

        if (onGround && inDown && !inLeft && !inRight) {   // start blocking
            state = State.BLOCK;
            vx = 0;
            animIndex = 0;
            return;
        }
        if (inAttack && !prevAttack && attackCooldown == 0) {
            startAttack();
            return;
        }
        if (onGround && inUp && !prevUp) {                 // jump
            vy = JUMP;
            onGround = false;
        }

        double m = 0;
        if (inLeft)  m -= SPEED;
        if (inRight) m += SPEED;
        vx = m;

        if (!onGround) {
            state = State.JUMP;
        } else if (m != 0) {
            state = State.WALK;
            if (++animTick >= WALK_SPEED) { animTick = 0; animIndex = (animIndex + 1) % nWalk; }
        } else {
            state = State.IDLE;
            animIndex = 0;
        }
    }

    private void startAttack() {
        state = State.ATTACK;
        animIndex = animTick = 0;
        hitApplied = false;
        vx = 0;
    }

    private void tickAttack(entity opp) {
        vx = 0;
        if (++animTick >= ATTACK_SPEED) { animTick = 0; animIndex++; }

        int active = activeFrame();
        if (!hitApplied && animIndex >= active && animIndex <= active + 1
                && attackBox().intersects(opp.bodyBox())) {
            hitApplied = true;
            opp.takeHit(ATTACK_DMG, this);
        }

        if (animIndex >= nAttack) {
            state = State.IDLE;
            animIndex = 0;
            attackCooldown = ATTACK_CD;
        }
    }

    private void tickHurt() {
        vx *= 0.85;
        if (++animTick >= HURT_SPEED) { animTick = 0; animIndex++; }
        if (animIndex >= nHurt) { state = State.IDLE; animIndex = 0; }
    }

    private void tickBlock(entity opp) {
        facingRight = opp.centerX() > centerX();
        vx = 0;
        animIndex = 0;
        if (inAttack && !prevAttack && attackCooldown == 0) { startAttack(); return; }
        if (!inDown || !onGround) state = State.IDLE;
    }

    private void tickDead() {
        vx *= 0.8;
        if (animIndex < nDead - 1 && ++animTick >= DEAD_SPEED) {
            animTick = 0;
            animIndex++;
            if (animIndex >= nDead - 1) deathDone = true;
        }
    }

    private void applyPhysics(entity opp, double prevX) {
        vy += GRAVITY;
        x += vx;
        y += vy;

        double floor = gp.groundY - H;
        if (y >= floor) { y = floor; vy = 0; onGround = true; }
        else onGround = false;

        if (x < 0) x = 0;
        if (x > gp.screenWidth - W) x = gp.screenWidth - W;

        if (bodyBox().intersects(opp.bodyBox())) x = prevX;   // no walking through each other
    }

    /** Take a hit from attacker. */
    public void takeHit(int dmg, entity attacker) {
        if (state == State.DEAD) return;

        boolean blocking = (state == State.BLOCK);
        int applied = blocking ? Math.max(1, dmg / 5) : dmg;   // chip damage while blocking
        hp = Math.max(0, hp - applied);

        double dir = (attacker.centerX() < centerX()) ? 1 : -1; // knock away from attacker
        vx = dir * (blocking ? BLOCK_KB : KB);
        vy = blocking ? -2 : -7;
        onGround = false;
        blinkTimer = 14;

        if (hp == 0) {
            state = State.DEAD;
            animIndex = animTick = 0;
            deathDone = false;
        } else if (!blocking) {
            state = State.HURT;
            animIndex = animTick = 0;
        }
    }

    // ------------------------------------------------------------------ draw
    public void draw(Graphics2D g2) {
        // ground shadow
        g2.setColor(new Color(0, 0, 0, 90));
        int cx = (int) centerX();
        g2.fillOval(cx - 65, gp.groundY - 14, 130, 26);

        BufferedImage img = currentImage();
        if (img == null) return;

        // invulnerability flicker
        if (blinkTimer > 0 && (blinkTimer / 2) % 2 == 0) return;

        double sc = (double) DRAW_H / img.getHeight();
        int dw = (int) (img.getWidth() * sc);
        int dh = DRAW_H;
        int dx = (int) (centerX() - dw / 2.0);
        int dy = (int) ((y + H) - dh);   // feet anchored to the bottom of the body box

        if (facingRight == artFacesRight) {
            g2.drawImage(img, dx, dy, dw, dh, null);
        } else { // horizontal flip
            g2.drawImage(img, dx + dw, dy, dx, dy + dh, 0, 0, img.getWidth(), img.getHeight(), null);
        }
    }

    private BufferedImage currentImage() {
        return switch (state) {
            case WALK   -> frame(walk, nWalk);
            case JUMP   -> walk[Math.min(2, nWalk - 1)];
            case ATTACK -> frame(attack, nAttack);
            case HURT   -> frame(hurt, nHurt);
            case DEAD   -> dead[Math.min(animIndex, nDead - 1)];
            default     -> walk[0]; // IDLE / BLOCK
        };
    }

    private BufferedImage frame(BufferedImage[] arr, int n) {
        return arr[Math.min(animIndex, n - 1)];
    }

    // ------------------------------------------------------------------ helpers
    protected int activeFrame() { return Math.max(1, nAttack / 2); }

    public double centerX() { return x + W / 2.0; }

    public Rectangle bodyBox() { return new Rectangle((int) x, (int) y, W, H); }

    public Rectangle attackBox() {
        int aw = 95, ah = 140, ay = (int) y + 30;
        int ax = facingRight ? (int) (x + W - 10) : (int) (x - aw + 10);
        return new Rectangle(ax, ay, aw, ah);
    }

    public boolean isDeathDone() { return deathDone; }
    public String getName() { return name; }
}

package tile;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;

public class TileManager {

    GamePanel gp;
    Tile tile;

    public TileManager(GamePanel gp){
        this.gp = gp;
        tile = new Tile();
        getTileImage();
    }

    public void getTileImage(){
        try{
            tile = new Tile();
            tile.image = ImageIO.read(getClass().getResourceAsStream("/tiles/g_bg_2.png"));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2){
        g2.drawImage(tile.image, 0, 0, gp.screenWidth, gp.screenHeight, null);

        // subtle ground shade so fighters read against the backdrop
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRect(0, gp.groundY + 8, gp.screenWidth, gp.screenHeight - gp.groundY);
    }
}
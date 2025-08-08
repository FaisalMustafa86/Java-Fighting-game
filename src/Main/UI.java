package Main;

import java.awt.*;

public class UI {

    GamePanel gp;
    Graphics2D g2;

    public void draw(Graphics2D g2){

        this.g2 = g2;
    g2.setFont(new Font("Arial", Font.PLAIN, 40));
    g2.setColor(Color.BLACK);

    if(gp.gameState == gp.playState){

        //Do other playStater stuuf later

    }
        if (gp.gameState == gp.pauseState) {
            drawPauseScreen();

        }
    }

    public void drawPauseScreen(){
        String text ="PAUSED";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight / 2;

        g2.drawString (text, x, y);
    }

    public int getXforCenteredText(String text){
        int length = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        int x = gp.screenWidth/2 - length/2;
return x;
    }

}

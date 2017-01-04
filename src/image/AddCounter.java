package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by zsjanos on 2016.12.20..
 */
public class AddCounter {


    private static void addCounter(String image, int number, String out){
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(image));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Graphics g = img.getGraphics();

        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(255, 216, 180, 255));
        g2d.setFont(g.getFont().deriveFont(30f));
        g2d.drawString("000" + String.valueOf(number), 450, 500);
        g.dispose();

        try {
            ImageIO.write(img, "png", new File(out));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        for (int i = 4550; i < 4850; ++i){
            addCounter("./data/image/belepo.png", i, "./data/image/out/" + i + ".png");
        }
    }
}
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


    public static void huge(String iPath, int pages, int offset) {

        int w = 396;
        int h = 270;


        for (int page = 0; page < pages; ++page) {

            System.out.println(page);

            BufferedImage result = new BufferedImage(w * 4, h * 4, BufferedImage.TYPE_INT_RGB);
            Graphics g = result.getGraphics();

            int x = 0;
            int y = 0;

            for (int i = 0; i < 16; ++i) {

                if (i > 0 && i % 4 == 0) {
                    y += h;
                    x = 0;
                }

                BufferedImage bi = null;
                try {
                    bi = addCounter(ImageIO.read(new File(iPath)), offset);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                g.drawImage(bi, x, y, null);

                offset += 1;

                x += w;
            }

            try {
                ImageIO.write(result, "png", new File(iPath.replace(".jpg", "") + "_" + page + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param image
     * @param number
     * @return
     */
    private static BufferedImage addCounter(BufferedImage image, int number) {
        Graphics g = image.getGraphics();

        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(255, 255, 255, 255));
        g2d.setFont(g.getFont().deriveFont(26f));
        g2d.drawString("0" + String.valueOf(number), 309, 240);
        g.dispose();

        return image;
    }

    public static void main(String[] args) {
//        int i = 799;
//        addCounter("./data/drop_ev.jpg", i, "./data/drop/" + i + ".png");
//        for (int i = 799; i < 1000; ++i) {
//            addCounter("./data/drop_ev.jpg", i, "./data/drop/" + i + ".png");
//        }


        huge("./data/ketto/bsw_2.jpg", 13, 7777);
    }
}

package pbgLecture6lab_wrapperForJBox2D.images;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RatherBadImageLoader {

    private static final String path = "src/pbgLecture6lab_wrapperForJBox2D/images/";

    public static BufferedImage loadImage(String fname) throws IOException {
        BufferedImage img;
        img = ImageIO.read(new File(path + fname + ".png"));
        images.put(fname, img);
        return img;
    }

    private static final Map<String, BufferedImage> images = new HashMap<>();

    public static Map<String, BufferedImage> get_images(){
        return new HashMap<>(images);
    }

    static {
        try {
            loadImage("BraveryStick");
            loadImage("BraveryStickFlipped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Credit to https://stackoverflow.com/a/57619016
     * @param bimg
     * @param angle
     * @return
     */
    public static BufferedImage rotate(BufferedImage bimg, double angle) {

        int w = bimg.getWidth();
        int h = bimg.getHeight();

        BufferedImage rotated = new BufferedImage(w, h, bimg.getType());
        Graphics2D graphic = rotated.createGraphics();
        graphic.rotate(angle, w/2.0, h/2.0);
        graphic.drawImage(bimg, null, 0, 0);
        graphic.dispose();
        return rotated;
    }
}

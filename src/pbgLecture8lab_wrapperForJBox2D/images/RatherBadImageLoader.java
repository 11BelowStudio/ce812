package pbgLecture8lab_wrapperForJBox2D.images;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RatherBadImageLoader {

    private static final String path = "src/pbgLecture8lab_wrapperForJBox2D/images/";

    public static BufferedImage loadImage(String fname) throws IOException {
        BufferedImage img;
        img = ImageIO.read(new File(path + fname + ".png"));
        images.put(fname, img);
        return img;
    }

    private static final Map<String, Image> images = new HashMap<>();

    public static Map<String, Image> get_images(){
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

}

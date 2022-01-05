package crappyGame.assets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Loosely based on the ImageManager code provided by Dr. Dimitri Ognibene
 * for CE218 Computer Game Programming (back in 2019),
 * and has since been modified heavily several times over by me.
 */
public final class ImageManager {

    private static String path = "src/crappyGame/assets/";

    public static BufferedImage BG1;
    public static BufferedImage BG2;
    public static BufferedImage BG3;
    public static BufferedImage BG4;
    public static BufferedImage BG5;
    public static BufferedImage BG6;

    public static BufferedImage loadImage(final String fname) throws IOException {
        BufferedImage img = ImageIO.read(Objects.requireNonNull(ImageManager.class.getResource(path + fname + ".png")));
        images.put(fname, img);
        return img;
    }

    @SuppressWarnings("StaticCollection")
    private static final Map<String, Image> images = new HashMap<>();

    public static Map<String, Image> getImages(){
        return new HashMap<>(images);
    }

    static {
        try {
            BG1 = loadImage("bg1");
            BG2 = loadImage("bg2");
            BG3 = loadImage("bg3");
            BG4 = loadImage("bg4");
            BG5 = loadImage("bg5");
            BG6 = loadImage("bg6");
        } catch (IOException e){
            System.out.println("oh no!");
        }
    }

}

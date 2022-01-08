package crappyGame.assets;

import crappy.utils.lazyFinal.LazyFinal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Loosely based on the ImageManager code provided by Dr. Dimitri Ognibene
 * for CE218 Computer Game Programming (back in 2019),
 * and has since been modified heavily several times over by me.
 */
public final class ImageManager {

    private static final String path = "/crappyGame/assets/images/";

    public static final LazyFinal<BufferedImage> BG1 = new LazyFinal<>();
    public static final LazyFinal<BufferedImage> BG2 = new LazyFinal<>();
    public static final LazyFinal<BufferedImage> BG3 = new LazyFinal<>();
    public static final LazyFinal<BufferedImage> BG4 = new LazyFinal<>();
    public static final LazyFinal<BufferedImage> BG5 = new LazyFinal<>();
    public static final LazyFinal<BufferedImage> BG6 = new LazyFinal<>();
    public static final LazyFinal<BufferedImage> TITLE=new LazyFinal<>();

    public static BufferedImage loadImage(final String fname) throws IOException {
        BufferedImage img = ImageIO.read(ImageManager.class.getResourceAsStream(path + fname + ".png"));
        images.put(fname, img);
        return img;
    }

    @SuppressWarnings("StaticCollection")
    private static final Map<String, BufferedImage> images = new HashMap<>();

    public static Map<String, BufferedImage> getImages(){
        return new HashMap<>(images);
    }

    static {
        try {
            BG1.set(loadImage("bg1"));
            BG2.set(loadImage("bg2"));
            BG3.set(loadImage("bg3"));
            BG4.set(loadImage("bg4"));
            BG5.set(loadImage("bg5"));
            BG6.set(loadImage("bg6"));
            TITLE.set(loadImage("title"));
        } catch (IOException e){
            System.out.println("oh no!");
        }
    }



}

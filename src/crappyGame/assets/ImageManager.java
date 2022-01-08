package crappyGame.assets;

import crappy.utils.lazyFinal.ILazyFinal;
import crappy.utils.lazyFinal.LazyFinal;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Loosely based on the ImageManager code provided by Dr. Dimitri Ognibene
 * for CE218 Computer Game Programming (back in 2019),
 * and has since been modified heavily several times over by me.
 */
public final class ImageManager {

    private ImageManager(){}

    private static final String path = "/crappyGame/assets/images/";

    public static final ILazyFinal<BufferedImage> BG1 = new LazyFinal<>();
    public static final ILazyFinal<BufferedImage> BG2 = new LazyFinal<>();
    public static final ILazyFinal<BufferedImage> BG3 = new LazyFinal<>();
    public static final ILazyFinal<BufferedImage> BG4 = new LazyFinal<>();
    public static final ILazyFinal<BufferedImage> BG5 = new LazyFinal<>();
    public static final ILazyFinal<BufferedImage> BG6 = new LazyFinal<>();
    public static final ILazyFinal<BufferedImage> TITLE=new LazyFinal<>();

    private static final List<BufferedImage> ICON_IMAGES = new ArrayList<>();

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

    public static List<BufferedImage> GET_ICON_IMAGES(){
        return Collections.unmodifiableList(ICON_IMAGES);
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

            ICON_IMAGES.add(loadImage("icon64"));
            ICON_IMAGES.add(loadImage("icon32"));
            ICON_IMAGES.add(loadImage("icon16"));

        } catch (IOException e){
            System.out.println("oh no!");
        }
    }



}

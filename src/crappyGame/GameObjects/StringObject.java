package crappyGame.GameObjects;

import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappyGame.Drawable;
import crappyGame.misc.AttributeString;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.function.Function;

public class StringObject implements Drawable {

    private String words = "";

    private Vect2D position;

    private Rot2D rotation = Rot2D.IDENTITY;

    private ALIGNMENT_ENUM alignment;

    private static final Font theFont = new Font(Font.SANS_SERIF, Font.BOLD,16);

    public enum ALIGNMENT_ENUM{
        LEFT,
        RIGHT,
        MIDDLE
    }

    public StringObject(final String words, final Vect2D pos, final Rot2D rot, final ALIGNMENT_ENUM alignment){
        this.words = words;
        position = pos;
        rotation = rot;
        this.alignment = alignment;
    }

    public void updateWords(final String newWords){
        this.words = newWords;
    }

    public void updatePos(final Vect2D newPos){
        this.position = newPos;
    }

    public void updateRotation(final Rot2D newRot){
        this.rotation = newRot;
    }

    public void updateAlignment(final ALIGNMENT_ENUM newAlignment){
        this.alignment = newAlignment;
    }

    public String getWords(){
        return this.words;
    }

    public Vect2D getPos() {
        return position;
    }

    public Rot2D getRotation(){
        return rotation;
    }

    public ALIGNMENT_ENUM getAlignment(){
        return alignment;
    }

    @Override
    public void draw(Graphics2D g) {
        final Font f = g.getFont();
        g.setFont(theFont);
        final FontMetrics fm = g.getFontMetrics(g.getFont());


        //final Rectangle2D bounds = g.getFontRenderContext().

        final int w = fm.stringWidth(words);
        final int h = fm.getHeight();

        int x = (int) position.getX();
        int y = (int) position.getY();



        switch (alignment){
            case LEFT:
                break;
            case RIGHT:
                x -= w;
                break;
            case MIDDLE:
                x -= (w/2);
                break;
        }

        final AffineTransform at = g.getTransform();

        g.rotate(rotation.angle(), (int)position.x, (int)position.y);



        g.setColor(Color.BLACK);
        g.drawString(words, x+1, y+1);
        g.drawString(words, x+1, y-1);
        g.drawString(words, x-1, y+1);
        g.drawString(words, x-1, y-1);

        g.setColor(Color.WHITE);
        g.drawString(words, x, y);
        g.setFont(f);
        g.setTransform(at);
    }


    public static class AttributeStringObject<T> implements Drawable{


        private final AttributeString<T> as;

        private final StringObject s;

        /**
         * Whether the StringObject should be updated to reflect the current state of the data in the
         * AttributeString every single time this is rendered.
         */
        private boolean refreshConstantly = false;


        /**
         * Constructor
         * @param as the AttributeString this will show
         * @param pos position of the stringobject
         * @param rot rotation of the stringobject
         * @param alignment alignment of the stringobject
         */
        public AttributeStringObject(
                final AttributeString<T> as, final Vect2D pos,
                final Rot2D rot, final ALIGNMENT_ENUM alignment
        ){
            this.as = as;
            this.s = new StringObject(as.toString(), pos, rot, alignment);
            refreshConstantly = false;
        }

        /**
         * Constructor
         * @param as the AttributeString this will show
         * @param pos position of the stringobject
         * @param rot rotation of the stringobject
         * @param alignment alignment of the stringobject
         * @param refreshConstantly should the words shown by the stringObject be updated every frame?
         */
        @SuppressWarnings("BooleanParameter")
        public AttributeStringObject(
                final AttributeString<T> as, final Vect2D pos,
                final Rot2D rot, final ALIGNMENT_ENUM alignment,
                final boolean refreshConstantly
        ){
            this(as, pos, rot, alignment);
            this.refreshConstantly = refreshConstantly;
        }

        public void updatePos(final Vect2D newPos){
            s.updatePos(newPos);
            s.updateWords(as.toString());
        }

        public void updateRotation(final Rot2D newRot){
            s.updateRotation(newRot);
            s.updateWords(as.toString());
        }

        public void updateAlignment(final ALIGNMENT_ENUM newAlignment){
            s.updateAlignment(newAlignment);
            s.updateWords(as.toString());
        }

        public void setPrefix(String newPrefix){
            as.setPrefix(newPrefix);
            s.updateWords(as.toString());
        }

        public void setSuffix(String newSuffix){
            as.setSuffix(newSuffix);
            s.updateWords(as.toString());
        }

        public void setData(T newData){
            as.setData(newData);
            s.updateWords(as.toString());
        }

        public void setFormatter(Function<T, String> newFormatter){
            as.setFormatter(newFormatter);
            s.updateWords(as.toString());
        }

        public void setRefreshConstantly(boolean refreshConstantly){
            this.refreshConstantly = refreshConstantly;
        }

        public String getPrefix(){
            return as.getPrefix();
        }

        public String getSuffix(){
            return as.getSuffix();
        }

        public T getData(){
            return as.getData();
        }

        public Vect2D getPos() {
            return s.getPos();
        }

        public Rot2D getRotation(){
            return s.getRotation();
        }

        public ALIGNMENT_ENUM getAlignment(){
            return s.getAlignment();
        }

        @Override
        public void draw(Graphics2D g) {
            if (refreshConstantly) {
                s.updateWords(as.toString());
            }
            s.draw(g);
        }
    }
}

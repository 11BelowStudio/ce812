package crappyGame.GameObjects;

import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappyGame.UI.Drawable;
import crappyGame.misc.AttributeString;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class StringObject implements Drawable {

    String words = "";

    Vect2D position;

    Rot2D rotation = Rot2D.IDENTITY;

    ALIGNMENT_ENUM alignment;

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
        final FontMetrics fm = g.getFontMetrics(g.getFont());

        final int w = fm.stringWidth(words);
        final int h = fm.getHeight();

        System.out.println(position);

        int x = (int) position.getX();
        int y = (int) position.getY();



        switch (alignment){
            case LEFT:
                break;
            case RIGHT:
                x -= w;
                break;
            case MIDDLE:
                x -= (w/2.0);
                break;
        }

        AffineTransform at = g.getTransform();


        //g.rotate(rotation.angle());


        g.setColor(Color.BLACK);
        g.drawString(words, x+1, y+1);
        g.drawString(words, x+1, y-1);
        g.drawString(words, x-1, y+1);
        g.drawString(words, x-1, y-1);

        g.setColor(Color.WHITE);
        g.drawString(words, x, y);

        g.setTransform(at);
    }


    public static class AttributeStringObject<T> implements Drawable{


        final AttributeString<T> as;

        final StringObject s;


        public AttributeStringObject(final AttributeString<T> as, final Vect2D pos,
                                     final Rot2D rot, final ALIGNMENT_ENUM alignment
        ){
            this.as = as;
            this.s = new StringObject(as.toString(), pos, rot, alignment);
        }

        public void updatePos(final Vect2D newPos){
            s.updatePos(newPos);
        }

        public void updateRotation(final Rot2D newRot){
            s.updateRotation(newRot);
        }

        public void updateAlignment(final ALIGNMENT_ENUM newAlignment){
            s.updateAlignment(newAlignment);
        }

        public void setPrefix(String newPrefix){
            as.setPrefix(newPrefix);
        }

        public void setSuffix(String newSuffix){
            as.setSuffix(newSuffix);
        }

        public void setData(T newData){
            as.setData(newData);
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
            s.updateWords(as.toString());
            s.draw(g);
        }
    }
}

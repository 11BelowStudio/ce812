package pbgLecture4lab;

import java.awt.*;

public class StringObject implements Drawable, Updatable, HasPosAndVel{


    Vect2D pos;

    Vect2D vel;

    Color col;

    /**
     * The string that is actually being displayed.
     */
    String theString;

    @Override
    public Vect2D getPos() {
        return pos;
    }

    @Override
    public Vect2D getVel() {
        return vel;
    }

    @Override
    public void setPos(Vect2D newPos) {
        pos = newPos;
    }

    @Override
    public void setVel(Vect2D newVel) {
        vel = newVel;
    }


    /**
     * Possible alignment values for the text
     */
    public static enum ALIGNMENT_ENUM {
        LEFT_ALIGN,
        RIGHT_ALIGN,
        CENTER_ALIGN
    }

    /**
     * Actual alignment value for this StringObject
     */
    private ALIGNMENT_ENUM alignment;


    /**
     * Constructor given initial string, and alignment
     * @param s initial text
     * @param a alignment
     */
    public StringObject(String s, ALIGNMENT_ENUM a){
        this(new Vect2D(),s,a);
    }

    /**
     * Constructor given initial position, string, and alignment
     * @param p initial position
     * @param s initial text
     * @param a alignment
     */
    public StringObject(Vect2D p, String s, ALIGNMENT_ENUM a){
        this(p,a);
        setText(s);
    }

    /**
     * Constructor given a position and initial string for this StringObject
     * @param p position for this StringObject
     * @param s string initially displayed by this StringObject
     */
    public StringObject(Vect2D p, String s){
        this(p);
        setText(s);
    }

    /**
     * Constructor given a position and alignment for this StringObject
     * @param p position for this StringObject
     * @param a alignment for this StringObject
     */
    public StringObject(Vect2D p, ALIGNMENT_ENUM a){
        this(p);
        alignment = a;
    }

    /**
     * Constructor given a position
     * @param p position for this StringObject
     */
    public StringObject(Vect2D p){
        this(p, new Vect2D());
    }

    public StringObject(Vect2D p, Vect2D v, String s, ALIGNMENT_ENUM a){
        this(p, v);
        alignment = a;
        theString = s;
    }

    /**
     * Constructor given a position and velocity
     * @param p position of the StringObject
     * @param v velocity of the StringObject
     */
    public StringObject(Vect2D p, Vect2D v){
        pos = p;
        vel = v;
        alignment = ALIGNMENT_ENUM.LEFT_ALIGN;
        theString = "";
        col = Color.WHITE;
    }

    @Override
    public void update(double gravity, double deltaT) {
        setPos(getPos().addScaled(getVel(), deltaT));
    }

    /**
     * it's not clickable
     * @param clickLocation where the click was
     * @return false
     */
    public boolean isClicked(Point clickLocation){ return false; } // not clickable


    /**
     * Finds the offset, renders a fake outline in black, then renders this text.
     * @param g the graphics context being used.
     */
    @Override
    public void draw(Graphics2D g) {
        final int screenX = BasicPhysicsEngine.convertWorldXtoScreenX(pos.x);
        final int screenY = BasicPhysicsEngine.convertWorldYtoScreenY(pos.y);

        final Font tempFont = g.getFont();
        g.setFont(tempFont.deriveFont(Font.BOLD)); // we're using bold text
        g.setColor(Color.black);
        final FontMetrics metrics = g.getFontMetrics(g.getFont());
        final int w = metrics.stringWidth(theString);
        //int h = metrics.getHeight();

        int widthOffset = screenX;
        // basically moves the horizontal position of the rendered text to make it appear 'aligned' a certain way
        switch (alignment) {
            case LEFT_ALIGN:
                // it's left-aligned by default
                break;
            case RIGHT_ALIGN:
                widthOffset -= w; // makes it basically go to the right
                break;
            case CENTER_ALIGN:
                widthOffset -= (w / 2); // we move it halfway so the midpoint of the string is at the position
                break;
        }
        // we draw 4 slightly-offset copies of the string as a totes legit outline
        g.drawString(theString,widthOffset+1,screenY +1);
        g.drawString(theString,widthOffset-1,screenY +1);
        g.drawString(theString,widthOffset-1,screenY -1);
        g.drawString(theString,widthOffset+1,screenY -1);

        // now we draw the actual string, not as an outline
        g.setColor(col);
        g.drawString(theString,widthOffset,screenY);
        g.setFont(tempFont);
        // and yeah here's the area rectangle which we don't use.
        // why was it updated in the draw operation? because we can only get the fontmetrics in the draw operation.
        //areaRectangle = new Rectangle((int)position.x - (w/2), (int)position.y + heightOffset,w,h);
    }

    /**
     * Overwrites the text in the string held in this StringObject
     * @param s the new string to display
     * @return this StringObject
     */
    public StringObject setText(String s){ theString = s; return this;}


}

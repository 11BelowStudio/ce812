package pbgLecture3lab;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class BasicSnookerBall extends BasicParticle implements Pottable {

    private final boolean striped;

    private final int ballNumber;

    private final String ballNumberString;

    private static final Color BEIGE = new Color(245, 245, 220);


    public BasicSnookerBall(Vect2D pos, double radius, Color col, double mass, int ballNumber, boolean striped) {
        super(pos, new Vect2D(), radius, true, col, mass, true);
        this.ballNumber = ballNumber;
        this.ballNumberString = String.valueOf(ballNumber);
        this.striped = striped;

    }

    @Override
    public int getValue() {
        return ballNumber;
    }

    @Override
    public boolean isStriped(){
        return striped;
    }

    @Override
    public void draw(Graphics2D g) {
        if (inactive){
            return;
        }

        int x = BasicPhysicsEngine.convertWorldXtoScreenX(getPos().x);
        int y = BasicPhysicsEngine.convertWorldYtoScreenY(getPos().y);

        if (striped){

            g.setColor(BEIGE);
            g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);
            final Shape currentClip = g.getClip();

            final Ellipse2D circle = new Ellipse2D.Double();
            circle.setFrame(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);
            g.setClip(circle);

            g.setColor(col);
            g.fillRect(x - SCREEN_RADIUS/3, y - SCREEN_RADIUS, 5* SCREEN_RADIUS/6, 2 * SCREEN_RADIUS);
            g.setClip(currentClip);


        } else {
            g.setColor(col);
            g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);
        }
        g.setColor(Color.BLACK);
        final FontMetrics fm = g.getFontMetrics();
        final int fontY = y + (fm.getHeight()/4);
        final int fontX = x - (fm.stringWidth(ballNumberString)/2);
        g.drawString(ballNumberString, fontX+1, fontY+1);
        g.drawString(ballNumberString, fontX-1, fontY+1);
        g.drawString(ballNumberString, fontX-1, fontY-1);
        g.drawString(ballNumberString, fontX+1, fontY-1);
        g.setColor(Color.WHITE);
        g.drawString(ballNumberString, fontX, fontY);

    }
}

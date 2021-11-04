package pbgLecture4lab;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class ParticleWithWordsOnIt extends BasicParticle {

    final String theWords;

    double rotationAngle;

    double thisFrameRotation;

    double lastFrameRotation;

    public ParticleWithWordsOnIt(Vect2D pos, Vect2D vel, double radius, boolean improvedEuler, Color col, double mass, double dragForce, String words) {
        super(pos, vel, radius, improvedEuler, col, mass, dragForce);
        theWords = words;
        rotationAngle = 0;
        thisFrameRotation = 0;
        //lastFrameRotation = vel.angle();
    }

    @Override
    public void resetTotalForce() {
        super.resetTotalForce();
        thisFrameRotation = 0;
    }

    @Override
    public void update(double gravity, double deltaT) {
        final Vect2D initialVel = getVel();
        super.update(gravity, deltaT);

        double initialAngle = initialVel.angle();
        double newAngle = getVel().angle();
        //System.out.println(initialAngle + ", " + newAngle);

        thisFrameRotation = newAngle - initialAngle;
        //System.out.println(thisFrameRotation);
        //System.out.println(rotationAngle);
        rotationAngle -= (thisFrameRotation);
        //System.out.println(rotationAngle);
        //System.out.println("---");
        //lastFrameRotation = newAngle;
    }


    public void draw(Graphics2D g){
        super.draw(g);
        final AffineTransform at = g.getTransform();
        final int x = BasicPhysicsEngine.convertWorldXtoScreenX(getPos().x);
        final int y = BasicPhysicsEngine.convertWorldYtoScreenY(getPos().y);
        final Font f = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));
        final FontMetrics fm = g.getFontMetrics(g.getFont());

        final int widthOffset = -fm.stringWidth(theWords)/2;
        final int heightOffset = fm.getHeight()/4;

        g.translate(x, y);
        g.rotate(rotationAngle);

        g.setColor(Color.BLACK);
        g.drawString(theWords, widthOffset -1, heightOffset-1);
        g.drawString(theWords, widthOffset -1, heightOffset+1);
        g.drawString(theWords, widthOffset +1, heightOffset+1);
        g.drawString(theWords, widthOffset +1, heightOffset-1);

        g.setColor(Color.WHITE);
        g.drawString(theWords, widthOffset, heightOffset);

        g.setTransform(at);
        g.setFont(f);


    }
}

package pbgLecture3lab;

import java.awt.*;

public class BasicSnookerHole implements Drawable {

    final Vect2D pos;

    final double radius;

    final int worldX;

    final int worldY;

    final int worldDiameter;

    public BasicSnookerHole(Vect2D position, double radius){
        pos = position;
        this.radius = radius;

        final int worldRadius = BasicPhysicsEngine.convertWorldRadiusToScreenRadius(radius);

        worldX = BasicPhysicsEngine.convertWorldXtoScreenX(position.x) - worldRadius;
        worldY = BasicPhysicsEngine.convertWorldYtoScreenY(position.y) - worldRadius;

        worldDiameter = BasicPhysicsEngine.convertWorldRadiusToScreenRadius(radius * 2);
    }


    public boolean checkIfBallIsPotted(Pottable ball){
        return (Vect2D.minus(ball.getPos(), pos).mag() <= radius + ball.getRadius());
    }


    @Override
    public void draw(Graphics2D g) {

        g.setColor(Color.BLACK);
        g.fillOval(worldX, worldY, worldDiameter, worldDiameter);

    }
}

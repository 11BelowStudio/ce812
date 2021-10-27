package pbgLecture3lab;

import java.awt.*;

public class BasicCueBall extends BasicParticle{

    Vect2D launchVector;

    private boolean upcomingLaunch;

    public BasicCueBall(Vect2D pos, double radius, boolean improvedEuler, double mass) {
        super(pos, new Vect2D(), radius, improvedEuler, Color.WHITE, mass, true);

        launchVector = new Vect2D(0,0);
        upcomingLaunch = false;
    }

    public void updateFromAction(ActionView theAction){

        if (upcomingLaunch){
            if (!theAction.isMousePressed()){
                upcomingLaunch = false;

                System.out.println("launch!");
                System.out.println(getVel());
                System.out.println(launchVector);

                setVel(getVel().addScaled(launchVector,1));

                System.out.println(getVel());

            }
        } else if (theAction.isMousePressed()){
            upcomingLaunch = true;
        }

        if (upcomingLaunch){

            final Vect2D mousePos = theAction.getMouseLocation();

            final Vect2D mouseToPos = Vect2D.minus(getPos(), mousePos);

            launchVector = mouseToPos;

            System.out.println(launchVector);
        }

    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);

        if (upcomingLaunch){

            final int posX = BasicPhysicsEngine.convertWorldXtoScreenX(getPos().x);
            final int posY = BasicPhysicsEngine.convertWorldYtoScreenY(getPos().y);

            final int launchX = BasicPhysicsEngine.convertWorldXtoScreenX(launchVector.x) + posX;
            final int launchY = BasicPhysicsEngine.convertWorldYtoScreenY(launchVector.y) - posY;

            g.setColor(Color.RED);
            g.drawLine(posX, posY, launchX, launchY);

        }

    }
}

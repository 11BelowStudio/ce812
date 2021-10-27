package pbgLecture3lab;

import java.awt.*;

public class BasicCueBall extends BasicParticle implements Pottable{

    Vect2D launchVector;

    final Vect2D resetPosition;

    private boolean upcomingLaunch;

    public BasicCueBall(Vect2D pos, double radius, double mass) {
        super(pos, new Vect2D(), radius, true, Color.WHITE, mass, true);

        resetPosition = pos;
        launchVector = new Vect2D(0,0);
        upcomingLaunch = false;

    }

    public void reset(){
        setPos(resetPosition);
        setVel(new Vect2D());
        setActive(true);
        upcomingLaunch = false;
    }

    public void updateFromAction(ActionView theAction){

        if (upcomingLaunch){
            if (!theAction.isMousePressed()){
                upcomingLaunch = false;

                System.out.println("launch!");
                System.out.println(getVel());
                System.out.println(launchVector);

                setVel(getVel().addScaled(launchVector,5));

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
        if (inactive){
            return;
        }
        super.draw(g);

        if (upcomingLaunch){

            final int posX = BasicPhysicsEngine.convertWorldXtoScreenX(getPos().x);
            final int posY = BasicPhysicsEngine.convertWorldYtoScreenY(getPos().y);

            final int launchX = BasicPhysicsEngine.convertWorldXtoScreenX(launchVector.x + getPos().x);
            final int launchY = BasicPhysicsEngine.convertWorldYtoScreenY(launchVector.y + getPos().y);

            g.setColor(Color.RED);
            g.drawLine(posX, posY, launchX, launchY);

        }

    }


}

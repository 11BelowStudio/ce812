package pbgLecture3lab;

import pbgLecture3lab.stuffThatsBeenPutToOneSide.SnookerEngine;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Controller implements MouseListener, MouseMotionListener {


    private final Action theAction;

    Controller(){
        theAction = new Action();
    }

    public ActionView getCurrentAction(){
        return theAction;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point mouseLocation = e.getPoint();
        double mouseX = BasicPhysicsEngine.convertScreenXtoWorldX(mouseLocation.x);
        double mouseY = BasicPhysicsEngine.convertScreenYtoWorldY(mouseLocation.y);
        theAction.setMousePressed(new Vect2D(mouseX, mouseY));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        theAction.setMouseReleased();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point mouseLocation = e.getPoint();
        double mouseX = BasicPhysicsEngine.convertScreenXtoWorldX(mouseLocation.x);
        double mouseY = BasicPhysicsEngine.convertScreenYtoWorldY(mouseLocation.y);
        theAction.mouseDragged(new Vect2D(mouseX, mouseY));
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}

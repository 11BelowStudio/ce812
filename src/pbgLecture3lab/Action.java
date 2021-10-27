package pbgLecture3lab;

public class Action implements ActionView {

    private boolean mousePressed;

    private Vect2D currentMouseLocation;

    private Vect2D mouseDragVector;

    Action(){
        mousePressed = false;
        currentMouseLocation = new Vect2D();
        mouseDragVector = new Vect2D();
    }

    void setMousePressed(Vect2D mouseLocation){
        mousePressed = true;
        currentMouseLocation = mouseLocation;
        mouseDragVector = new Vect2D();
    }

    void mouseDragged(Vect2D mouseLocation){
        final Vect2D lastLocation = currentMouseLocation;
        currentMouseLocation = mouseLocation;
        mouseDragVector = Vect2D.minus(currentMouseLocation, lastLocation);
    }

    void setMouseReleased(){
        mousePressed = false;
    }

    @Override
    public boolean isMousePressed() {
        return mousePressed;
    }

    @Override
    public Vect2D getMouseLocation() {
        return currentMouseLocation;
    }

    @Override
    public Vect2D getMouseDrag() {
        return mouseDragVector;
    }
}

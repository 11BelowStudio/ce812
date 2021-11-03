package pbgLecture4lab;

import java.awt.*;

public class AttributeStringObject<T> extends StringObject {

    private final AttributeString<T> theText;


    public AttributeStringObject(Vect2D pos, AttributeString<T> theAttributeString, ALIGNMENT_ENUM a){
        this(pos, new Vect2D(), theAttributeString, a);
    }

    public AttributeStringObject(Vect2D pos, Vect2D vel, AttributeString<T> theAttributeString, ALIGNMENT_ENUM a){
        super(pos, vel, theAttributeString.toString(), a);
        theText = theAttributeString;
    }

    public AttributeString<T> getTheAttributeString(){
        return theText;
    }


    @Override
    public void draw(Graphics2D g) {
        super.setText(theText.toString());
        super.draw(g);
    }
}
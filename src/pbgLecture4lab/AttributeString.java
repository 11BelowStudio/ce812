package pbgLecture4lab;

/**
 * This is a utilty class which can be used to show a value with a name for it
 * bit easier than trying to include methods to completely rewrite every individual string
 * whenever the value associated with that string is updated by any amount, y'know?
 * @param <T> whatever the type of the value needs to be
 *
 * @author Rachel Lowe
 */
public class AttributeString<T>{


    /**
     * the words before the value
     */
    private String attributeName;

    /**
     * the generic value held by this object
     */
    private T value;


    /**
     * the words after the value
     */
    private String suffix;

    /**
     * basically the attributeName, the value, and the suffix put together in one string
     */
    private String theString;

    /**
     * Sets the attributeName, value, and suffix, updating the text of this respectively
     * @param attributeName what the attributeName will be. String. Use an empty string if you don't want one
     * @param value Whatever the value being held by this AttributeString will be
     * @param suffix A suffix for this AttributeString. Use an empty string if you don't want one
     */
    public AttributeString(String attributeName, T value, String suffix){
        this.attributeName = attributeName;
        this.value = value;
        this.suffix = suffix;
        updateText();
    }

    /**
     * Sets the attributeName and the value of the AttributeString accordingly.
     * Suffix will be blank.
     * if you just want a value and a suffix, use the 3-argument constructor,
     * with a blank string for attributeName (less messy that way)
     * @param attributeName the prefix
     * @param value whatever the value is meant to be
     */
    public AttributeString(String attributeName, T value){
        this(attributeName,value,"");
    }


    /**
     * Sets the value for the attributeString.
     * attributeName and suffix will be empty strings
     * @param value the value you want this attributeString to store
     */
    public AttributeString(T value){
        this("",value,"");
    }


    /**
     * Sets a specified value for this attributeString
     * @param value new value to show
     * @return the string representation of this AttributeString
     */
    public String showValue(T value){
        this.value = value;
        return updateText();
    }

    /**
     * Renames the attributeName of this AttributeString
     * @param attributeName what name you want to use
     * @return the toString representation of this AttributeString
     */
    public String rename(String attributeName){
        this.attributeName = attributeName;
        return updateText();
    }

    /**
     * Renames the suffix of this AttributeString
     * @param newSuffix the new suffix you want to use
     * @return the toString representation of this AttributeString
     */
    public String changeSuffix(String newSuffix){
        this.suffix = newSuffix;
        return updateText();
    }

    /**
     * basically updates the string representation of this AttributeString and returns it
     * @return new string representation of this AttributeString
     */
    private String updateText(){
        return theString = (attributeName + value + suffix);
    } //the string is the attributeName followed by the value then followed by the suffix

    /**
     * returns the value of this AttributeString
     * @return the value
     */
    public T getValue(){ return value; }

    /**
     * returns the attributeName of this AttributeString
     * @return attributeName
     */
    public String getAttributeName(){ return attributeName; }

    /**
     * returns the suffix of this AttributeString
     * @return suffix
     */
    public String getSuffix(){ return suffix; }

    /**
     * returns the string representation of this AttributeString
     * @return the string representation of this AttributeString
     */
    @Override
    public String toString(){ return theString; }


}

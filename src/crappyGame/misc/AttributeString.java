package crappyGame.misc;

import java.util.function.Function;

/**
 * A utility class, originally created by me for 'Epic Gamer Moment: The Game' (back in 2019),
 * and has since been bastardized several times over by me. This is the newest iteration, and hopefully
 * most of the code bloat has been removed from it.
 * @param <T>
 */
public class AttributeString<T> {

    /**
     * The data held in (to be shown by) the AttributeString
     */
    private T attribute;

    /**
     * The words that are before the data
     */
    private String prefix;

    /**
     * The words that are after the data
     */
    private String suffix;

    /**
     * The formatting rule being used to render the data as a string.
     * If not specified, this will be set to 'Object::toString'
     */
    private Function<T, String> formatter;

    /**
     * A constructor (which will render the data via its toString() method)
     * @param data the data being held in this
     * @param prefix words that go before the data
     * @param suffix words that go after the data
     */
    public AttributeString(final T data, final String prefix, final String suffix){
        this(data, prefix, suffix, Object::toString);
    }

    /**
     * A constructor allowing a specified formatting method for the data held in this string
     * @param data the data itself
     * @param prefix words that go before the formatted data
     * @param suffix words that go after the formatted data
     * @param dataFormatter a function that produces your favoured variety
     *                      of string representation for the data held in this AttributeString.
     */
    public AttributeString(final T data, final String prefix, final String suffix, final Function<T, String> dataFormatter){
        attribute = data;
        this.prefix = prefix;
        this.suffix = suffix;
        this.formatter = dataFormatter;
    }

    public AttributeString(final T data, final String prefix){
        this(data, prefix, "");
    }

    public AttributeString(final T data){
        this(data, "", "");
    }

    public void setPrefix(final String newPrefix){
        prefix = newPrefix;
    }

    public void setSuffix(final String newSuffix){
        suffix = newSuffix;
    }

    public void setData(final T newData){
        attribute = newData;
    }

    public void setFormatter(final Function<T, String> newFormatter){
        formatter = newFormatter;
    }

    public T getData(){
        return attribute;
    }

    public String getPrefix(){
        return prefix;
    }

    public String getSuffix(){
        return suffix;
    }

    public String toString(){
        return prefix + formatter.apply(attribute) + suffix;
    }
}

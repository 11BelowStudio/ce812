package crappyGame.misc;

import java.util.function.Function;

/**
 * A utility class, originally created by me for 'Epic Gamer Moment: The Game' (back in 2019),
 * and has since been bastardized several times over by me. This is the newest iteration, and hopefully
 * most of the code bloat has been removed from it.
 * @param <T>
 */
public class AttributeString<T> {

    private T attribute;

    private String prefix;

    private String suffix;

    private final Function<T, String> formatter;

    public AttributeString(T data, String prefix, String suffix){
        this(data, prefix, suffix, Object::toString);
    }

    public AttributeString(T data, String prefix, String suffix, Function<T, String> dataFormatter){
        attribute = data;
        this.prefix = prefix;
        this.suffix = suffix;
        this.formatter = dataFormatter;
    }

    public AttributeString(T data, String prefix){
        this(data, prefix, "");
    }

    public AttributeString(T data){
        this(data, "", "");
    }

    public void setPrefix(final String newPrefix){
        prefix = newPrefix;
    }

    public void setSuffix(final String newSuffix){
        suffix = newSuffix;
    }

    public void setData(T newData){
        attribute = newData;
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

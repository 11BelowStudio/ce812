package crappyGame.misc;

public class AttributeString<T> {

    private T attribute;

    private String prefix;

    private String suffix;

    public AttributeString(T data, String prefix, String suffix){
        attribute = data;
        this.prefix = prefix;
        this.suffix = suffix;
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


    public String toString(){
        return prefix + attribute.toString() + suffix;
    }
}

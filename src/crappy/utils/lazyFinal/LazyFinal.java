package crappy.utils.lazyFinal;

import java.util.Objects;
import java.util.Optional;

/**
 * Basically a workaround to allow me to use a final object that I'll just declare later on.
 *
 * Based on https://stackoverflow.com/a/38290652
 *
 * @see <a href="https://stackoverflow.com/a/38290652">https://stackoverflow.com/a/38290652</a>
 */
public final class LazyFinal<T> implements ILazyFinal<T> {

    /**
     * This thing will actually hold our data (when it arrives)
     */
    private ILazyData<T> data;

    /**
     * No-arg constructor, feel free to set the lazyData later.
     */
    public LazyFinal(){}

    /**
     * Constructor with data to hold in this
     * @param dataToHold the data to store
     * @throws IllegalArgumentException if it's a mcfucking null
     * @see #set(Object)
     */
    public LazyFinal(final T dataToHold) throws IllegalArgumentException{
        set(dataToHold);
    }

    /**
     * A less nullpointer-y exception-y method for external stuff to check whether or not this has data
     * @return true if data isn't null.
     */
    public boolean hasData(){
        return data != null;
    }


    /**
     * Obtains the data held in this LazyFinal, or null if it's not been set yet.
     * @return the data held inside, or null if it hasn't been set yet
     * @apiNote If you would prefer it as an Optional, please use {@link #getOptional()}.
     * @see #getOptional()
     */
    public T get() {
        if (data != null){
            return data.getTheData();
        }
        //noinspection ReturnOfNull
        return null;
    }

    /**
     * Obtains the data, asserting that it's there
     * @return the data.
     */
    public T getAssert(){
        assert (data != null);
        return data.getTheData();
    }

    /**
     * Obtains the data that may or may not be held inside this as an Optional
     * @return an Optional holding the data held inside this.
     * @apiNote if you want to skip faffing around with optionals, please use {@link #get()}
     * @see #get()
     */
    public Optional<T> getOptional(){
        if (data != null){
            return data.getOptData();
        }
        return Optional.empty();
    }

    /**
     * Attempts to write the write-once-read-only variable
     * @param newData the data we're attempting to store
     * @throws IllegalStateException if a value is already being stored
     * @throws IllegalArgumentException if you're attempting to write a mcfucking null
     */
    public void set(final T newData) throws IllegalStateException, IllegalArgumentException {
        if (data != null) {
            // initial check if the data has been set without needing to wait for thread-safety stuff
            throw new IllegalStateException("A value has already been set!");
        } else if (newData == null){
            throw new IllegalArgumentException("You're seriously trying to set this to null???");
        }
        synchronized (this) { // now we wait to double-check it in a thread-safe way
            if (data != null) {
                throw new IllegalStateException("A value has already been set!");
            }
            data = new LazyData<>(newData);
        }


    }

    @Override
    public String toString() {
        if (data != null){
            return data.getTheData().toString();
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(data, ((LazyFinal<?>) o).data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }


}

package crappy.utils.lazyFinal;

import java.util.Optional;

/**
 * An interface for the LazyFinal write-once-read-many data structure.
 * @param <T>
 */
public interface I_LazyFinal<T> {

    /**
     * A less nullpointer-y exception-y method for external stuff to check whether or not this has data
     * @return true if data isn't null.
     */
    boolean hasData();

    /**
     * Obtains the data held in this LazyFinal, or null/default value if it's not been set yet.
     * @return the data held inside, or null if it hasn't been set yet
     * @apiNote If you would prefer it as an Optional, please use {@link #getOptional()}.
     * @see #getOptional()
     */
    T get();

    /**
     * Obtains the data that may or may not be held inside this as an Optional
     * @return an Optional holding the data held inside this.
     * @apiNote if you want to skip faffing around with optionals, please use {@link #get()}
     * @see #get()
     * @implNote If you are writing an implementation that uses default values in the LazyFinal,
     * this should still return an empty optional, to indicate that the real value hasn't arrived yet.
     */
    Optional<T> getOptional();

    /**
     * Obtains the data, asserting that it's there
     * @return the data.
     */
    T getAssert();

    /**
     * Attempts to write the write-once-read-only variable
     * @param newData the data we're attempting to store
     * @throws IllegalStateException if a value is already being stored
     * @throws IllegalArgumentException if you're attempting to write a mcfucking null
     */
    void set(final T newData) throws IllegalStateException, IllegalArgumentException;
}

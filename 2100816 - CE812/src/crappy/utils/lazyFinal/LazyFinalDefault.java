package crappy.utils.lazyFinal;

import java.util.Optional;

/**
 * Like LazyFinal, but supports a non-null default value.
 * @param <T> datatype for the data it holds
 * @author Rachel Lowe
 */
public final class LazyFinalDefault<T> implements ILazyFinal<T> {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */


    /**
     * This thing will actually hold our data (when it arrives)
     */
    private ILazyData<T> finalData = null;

    /**
     * This is the default value to return when finalData hasn't been given.
     */
    private T defaultData;

    /**
     * Constructs this with some default data
     * @param defaultData the data to put in this by default.
     * @throws IllegalArgumentException if you attempt giving it null.
     */
    public LazyFinalDefault(final T defaultData) throws IllegalArgumentException{
        if (defaultData != null) {
            this.defaultData = defaultData;
            this.finalData = null;
        } else {
            throw new IllegalArgumentException("Giving a default value of null? Seriously???");
        }
    }

    /**
     * Creates this, holding the final data already
     * @param finalData the final data held within this
     * @param o placeholder so this is a different constructor
     * @throws IllegalArgumentException if you attempt giving it null as a final value.
     */
    public LazyFinalDefault(final T finalData, final Object o) throws IllegalArgumentException{
        if (finalData != null) {
            this.finalData = new LazyData<>(finalData);
            this.defaultData = null;
        } else {
            throw new IllegalArgumentException("Giving a final value of null? Seriously???");
        }
    }

    /**
     * A less nullpointer-y exception-y method for external stuff to check whether or not this has data
     *
     * @return true if finalData isn't null.
     */
    @Override
    public boolean hasData() {
        return finalData != null;
    }

    /**
     * Obtains the data held in this LazyFinal, or null/default value if it's not been set yet.
     * @return the data held inside, or null if it hasn't been set yet
     * @apiNote If you would prefer it as an Optional, please use {@link #getOptional()}.
     * @see #getOptional()
     */
    public T get(){
        if (finalData != null){
            return finalData.getTheData();
        }
        return defaultData;
    }

    /**
     * Obtains the data that may or may not be held inside this as an Optional
     *
     * @return an Optional holding the data held inside this.
     *
     * @apiNote if you want to skip faffing around with optionals, please use {@link #get()}
     * @implNote If you are writing an implementation that uses default values in the LazyFinal, this should still
     * return an empty optional, to indicate that the real value hasn't arrived yet.
     * @see #get()
     */
    @Override
    public Optional<T> getOptional() {
        if (finalData != null){
            return finalData.getOptData();
        }
        return Optional.empty();
    }

    /**
     * Obtains the data, asserting that it's there
     *
     * @return the data.
     */
    @Override
    public T getAssert() {
        if (finalData != null){
            return finalData.getTheData();
        }
        return defaultData;
    }


    /**
     * Attempts to write the write-once-read-only variable
     *
     * @param newData the data we're attempting to store
     *
     * @throws IllegalStateException    if a value is already being stored
     * @throws IllegalArgumentException if you're attempting to write a mcfucking null
     */
    @Override
    public void set(final T newData) throws IllegalStateException, IllegalArgumentException {
        if (finalData != null) {
            // initial check if the data has been set without needing to wait for thread-safety stuff
            throw new IllegalStateException("A value has already been set!");
        } else if (newData == null){
            throw new IllegalArgumentException("You're seriously trying to set this to null???");
        }
        synchronized (this) { // now we wait to double-check it in a thread-safe way
            if (finalData != null) {
                throw new IllegalStateException("A value has already been set!");
            }
            finalData = new LazyData<>(newData);
            defaultData = null; // YEET!
        }
    }

}

package crappy.utils.lazyFinal;

import java.util.Optional;

/**
 * A class that provides a way for data to be stored and overwritten but in a way that somewhat makes it
 * harder to mess around with
 * @param <T>
 */
public final class ProtectedOverwrite<T> implements I_ProtectedOverwrite<T>, I_ProtectedOverwriter<T> {

    /**
     * The actual data held inside
     */
    private LazyData<T> data;

    /**
     * Current lock mode
     */
    private I_ProtectedOverwrite.ProtectedOverwriteLockMode lock;

    /**
     * Constructor with specified default data and a lock mode
     * @param data the data to store
     * @param lock lock mode
     */
    public ProtectedOverwrite(final T data, final I_ProtectedOverwrite.ProtectedOverwriteLockMode lock){
        this.data = new LazyData<>(data);
        this.lock = lock;
    }

    /**
     * Method that gives new data to this, and sets a lock mode of READ_ONLY.
     * @param data the data to give to this.
     */
    public ProtectedOverwrite(final T data){
        this(data, ProtectedOverwriteLockMode.READ_ONLY);
    }


    /**
     * A less nullpointer-y exception-y method for external stuff to check whether or not this has data
     *
     * @return true if data isn't null.
     */
    @Override
    public boolean hasData() {
        return true;
    }

    /**
     * Obtains the data held in this LazyFinal, or null/default value if it's not been set yet.
     *
     * @return the data held inside, or null if it hasn't been set yet
     *
     * @apiNote If you would prefer it as an Optional, please use {@link #getOptional()}.
     * @see #getOptional()
     */
    @Override
    public T get() {
        return data.getTheData();
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
        return data.getOptData();
    }

    /**
     * Obtains the data, asserting that it's there
     *
     * @return the data.
     */
    @Override
    public T getAssert() {
        return data.getTheData();
    }

    /**
     * Attempts to write to this VIA THE PUBLIC INTERFACE.
     *
     * @param newData the data we're attempting to store
     *
     * @throws IllegalStateException    if overwriting is currently disabled
     * @throws IllegalArgumentException if you're attempting to write a mcfucking null
     */
    @Override
    public void set(final T newData) throws IllegalStateException, IllegalArgumentException {
        if (newData != null){
            synchronized (this){
                if (lock == ProtectedOverwriteLockMode.PUBLIC_WRITEABLE){
                    data = new LazyData<>(newData);
                } else {
                    throw new IllegalStateException("This object is currently closed for public writing!");
                }
            }
        } else {
            throw new IllegalArgumentException("pls don't give nulls >:(");
        }
    }

    /**
     * Obtains the current status of the overwrite lock
     *
     * @return current status of overwrite lock
     */
    @Override
    public ProtectedOverwriteLockMode getLockMode() {
        return lock;
    }

    /**
     * Private override for lock mode
     *
     * @param newData the new data to store in the instance of this object
     * @throws IllegalStateException    if this is READ_ONLY
     * @throws IllegalArgumentException if you're attempting to write a mcfucking null
     */
    @Override
    public void setOverride(final T newData) throws IllegalStateException, IllegalArgumentException {
        if (newData != null){
            synchronized (this){
                if (lock == ProtectedOverwriteLockMode.READ_ONLY){
                    throw new IllegalStateException("Object is currently read-only for everyone!");
                }
                data = new LazyData<>(newData);
            }
        } else {
            throw new IllegalArgumentException("pls don't give nulls >:(");
        }
    }

    /**
     * Allows the lock mode to be set.
     *
     * @param mode the lock mode
     */
    @Override
    public void setLockMode(final ProtectedOverwriteLockMode mode) {
        synchronized (this){
            this.lock = mode;
        }
    }
}

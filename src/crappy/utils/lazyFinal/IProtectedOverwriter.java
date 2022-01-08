package crappy.utils.lazyFinal;

/**
 * The 'private' interface for I_ProtectedOverwrite, permitting one to
 * overwrite the data when it's normally locked for editing, and change the lock settings.
 * @param <T>
 */
public interface IProtectedOverwriter<T> extends IProtectedOverwrite<T> {

    /**
     * Private override for lock mode
     * @param newData the new data to store in the instance of this object
     */
    void setOverride(final T newData);

    /**
     * Allows the lock mode to be set.
     * @param mode the lock mode
     */
    void setLockMode(final IProtectedOverwrite.ProtectedOverwriteLockMode mode);

}
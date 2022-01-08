package crappy.utils.lazyFinal;

import java.util.Optional;

/**
 * An interface for the inner container object within the I_LazyFinal implementations
 * @param <T> type of data stored in this
 */
public interface ILazyData<T>{

    /**
     * Obtains the data within the I_LazyData as-is.
     * @return the data, as-is.
     */
    T getTheData();

    /**
     * Obtains the data within the I_LazyData wrapped in an Optional
     * @return the data, in an optional.
     */
    Optional<T> getOptData();
}

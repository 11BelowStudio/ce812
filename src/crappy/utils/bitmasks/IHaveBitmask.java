package crappy.utils.bitmasks;

import java.util.function.IntSupplier;

/**
 * An interface providing a view of a bitmask belonging to a class.
 * It implements IntSupplier, meaning that it can be used as an IntSupplier if needed,
 * but merely overrides getAsInt with a default method that returns getBitmask, so,
 * if you need to use this for a subclass that needs an int that isn't a bitmask as well for some reason,
 * you can still do that.
 */
@FunctionalInterface
public interface IHaveBitmask extends IntSupplier {

    /**
     * Obtains the bitmask of the object of the implementing class
     * @return the bitmask of this instance
     */
    int getBitmask();

    /**
     * Usable as an IntSupplier, returning the bitmask of this object.
     * @return the bitmask of this object
     */
    @Override
    default int getAsInt(){ return getBitmask(); }

    /**
     * Creates a value which is the result of ORing all of these bitmasks together
     * @param bmOwners the objects with the bitmasks to combine
     * @return result of performing an OR operation over all of these bitmasks.
     */
    public static int COMBINE_BITMASKS_OR(final IHaveBitmask... bmOwners){
        int res = 0;
        for (IHaveBitmask b: bmOwners) {
            res |= b.getBitmask();
        }
        return res;
    }

    /**
     * Simply performs a binary or on the bitmasks for two IHaveBitmask objects (a or b), returning the result
     * @param a first bitmask thing
     * @param b second bitmask thing
     * @return result of a | b
     */
    public static int COMBINE_BITMASKS_OR2(final IHaveBitmask a, final IHaveBitmask b){
        return a.getBitmask() | b.getBitmask();
    }

    /**
     * Combines int bitmask with bitmask of a bitmask haver, using or
     * @param bm raw int bitmask
     * @param b object that has a bitmask
     * @return result of bm | b.getBitmask()
     */
    public static int COMBINE_BITMASKS_OR2(final int bm, final IHaveBitmask b){
        return bm | b.getBitmask();
    }

    /**
     * Attempts to combine all of the given bitmasks into a single bitmask value
     * @param bmOwners objects that have bitmasks to be anded together
     * @return result of performing an AND operation on all of these bitmasks.
     */
    public static int COMBINE_BITMASKS_AND(final IHaveBitmask... bmOwners){
        int res = bmOwners[0].getBitmask();
        for (int i = bmOwners.length-1; (i > 0) && (res != 0) ; i--) {
            res &= bmOwners[i].getBitmask();
        }
        return res;
    }

    /**
     * Simply performs a binary and on the bitmasks for two IHaveBitmask objects (a and b), returning the result
     * @param a first bitmask thing
     * @param b second bitmask thing
     * @return result of a & b
     */
    public static int COMBINE_BITMASKS_AND2(final IHaveBitmask a, final IHaveBitmask b){
        return a.getBitmask() & b.getBitmask();
    }

    /**
     * Combines int bitmask with bitmask of a bitmask haver, using and
     * @param bm raw int bitmask
     * @param b object that has a bitmask
     * @return result of bm & b.getBitmask()
     */
    public static int COMBINE_BITMASKS_AND2(final int bm, final IHaveBitmask b){
        return bm | b.getBitmask();
    }

}


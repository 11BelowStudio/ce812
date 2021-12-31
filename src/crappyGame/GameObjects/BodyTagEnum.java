package crappyGame.GameObjects;

import crappy.utils.bitmasks.IHaveAndConsumeBitmask;
import crappy.utils.bitmasks.IHaveBitmask;

public enum BodyTagEnum implements IHaveAndConsumeBitmask {

    WORLD,
    SHIP,
    TOWABLE,
    FINISH_LINE,
    DECORATIVE_PARTICLE;

    public final int bitmask;

    private static int intsToBitmask(int... ints){
        int out = 0;
        for (int i: ints){
            out += (0b1 << i);
        }
        return out;
    }

    BodyTagEnum(){
        bitmask = 0b1 << ordinal();
    }

    /**
     * Consumes an IHaveBitmask. INTENDED TO HAVE SIDE EFFECTS ON THIS OBJECT!
     *
     * @param bitmaskHaver the IHaveBitmask to consume.
     */
    @Override
    public void accept(IHaveBitmask bitmaskHaver) {}

    /**
     * Do any bits in this object's bitmask and the other object's bitmask match?
     *
     * @param other the other object with a bitmask
     *
     * @return true if there are any 1s in the result of anding this bitmask with the other one.
     */
    @Override
    public boolean anyMatchInBitmasks(IHaveBitmask other) {
        return IHaveAndConsumeBitmask.super.anyMatchInBitmasks(other);
    }

    /**
     * Obtains the bitmask of the object of the implementing class
     *
     * @return the bitmask of this instance
     */
    @Override
    public int getBitmask() {
        return bitmask;
    }
}

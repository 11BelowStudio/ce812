package crappy.utils;

/**
 * Enum for whether or not the state of something that needs to be changed has to be changed
 */
public enum PendingStateChange {

    /**
     * If it's supposed to become true
     */
    PENDING_TRUE,
    /**
     * If it's supposed to become false
     */
    PENDING_FALSE,
    /**
     * If it's supposed to stay the same
     */
    SAME_AS_IT_EVER_WAS;

    /**
     * Turns this (current state, new state) thing into a PendingStateChange enum describing it
     * @param currentState the current state
     * @param new_state what the new state for it is supposed to be
     * @return PendingStateChange describing this relationship.
     */
    public static PendingStateChange TO_PENDING_STATE_CHANGE(final boolean currentState, final boolean new_state){

        if (currentState == new_state){
            return SAME_AS_IT_EVER_WAS;
        } else if (new_state){
            return PENDING_TRUE;
        } else {
            return PENDING_FALSE;
        }

    }

    /**
     * Processes a PendingStateChange from a currentState, based on a pendingStateChange enum
     * @param currentState current state
     * @param psc pending state change describing the pending state change
     * @return new boolean state
     */
    public static boolean PROCESS_STATE_CHANGE(final boolean currentState, final PendingStateChange psc){
        switch (psc){
            case PENDING_TRUE:
                return true;
            case PENDING_FALSE:
                return false;
            case SAME_AS_IT_EVER_WAS:
                return currentState;
        }
        return currentState;
    }
}

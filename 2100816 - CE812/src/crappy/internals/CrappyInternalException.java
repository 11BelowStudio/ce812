/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.internals;

/**
 * A runtime exception for internal stuff within Crappy, which an end-user/end-programmer (hopefully) won't encounter.
 * Mostly here so I can kick myself in the arse on your behalf.
 */
public class CrappyInternalException extends RuntimeException{

    /**
     * no-arg version, message is just 'CRAPPY EXCEPTION!'
     */
    public CrappyInternalException(){
        super("CRAPPY EXCEPTION!");
    }

    /**
     * version that appends 'CRAPPY EXCEPTION! ' at the start of the message
     * @param message the actual exception message
     */
    public CrappyInternalException(final String message){
        super("CRAPPY EXCEPTION! " + message);
    }

    /**
     * version that appends 'CRAPPY EXCEPTION! ' at the start of the message and includes the throwable
     * @param message the actual exception message
     * @param cause the cause of the exception
     */
    public CrappyInternalException(final String message, final Throwable cause){
        super("CRAPPY EXCEPTION! " + message, cause);
    }

}
package crappy.internals;

import java.lang.annotation.*;


/**
 * Basically here for use as a warning for certain methods.
 *
 * @author Rachel Lowe
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE_USE})
@Documented
public @interface CrappyWarning {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * A warning message of sorts
     * @return the warning message
     */
    String value() default "You probably shouldn't be using this!";

}

package crappy.utils;

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

    /**
     * A warning message of sorts
     * @return the warning message
     */
    String message() default "You probably shouldn't be using this!";

}

package crappy.utils;

import java.lang.annotation.*;


/**
 * Basically here for use as a warning for certain methods.
 *
 * @author Rachel Lowe
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Documented
public @interface CrappyWarning {

    /**
     * A warning message of sorts
     * @return the warning message
     */
    String value() default "You probably shouldn't be using this!";

}

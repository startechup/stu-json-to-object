package com.startechup.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation class will help linking the values gathered from a json api response using
 * the json key to its setter method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SetterSpec {
    /**
     * The json key for getting the value from the json object.
     * This will link the gathered value from the json to the setter method which will be invoked
     * to assign the value to its designated field inside the class.
     *
     * @return Returns a string representing the json key.
     */
    String jsonKey();
}

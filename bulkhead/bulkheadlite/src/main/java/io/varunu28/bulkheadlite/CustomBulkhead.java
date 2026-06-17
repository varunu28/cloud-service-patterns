package io.varunu28.bulkheadlite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to apply the Bulkhead pattern to a method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomBulkhead {

    /**
     * A unique name for the bulkhead (so different endpoints can have different limits)
     */
    String name();

    /**
     * The maximum number of concurrent threads allowed to execute the method
     */
    int maxConcurrent() default 5;
}

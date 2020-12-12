/*
 * @(#)NonNull.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * The NonNull annotation indicates that a null value must not be
 * assigned to the annotated element.
 * <o>
 * Code that retrieves a null value from the element must throw a
 * {@link NullPointerException}.
 */
@Documented
@Retention(CLASS)
@Target({TYPE_USE, TYPE_PARAMETER, FIELD, METHOD, PARAMETER, LOCAL_VARIABLE})
public @interface NonNull {
}

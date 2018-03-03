/* @(#)Nullable.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 *
 * Note: This annotation is declared in the checkerframework package, to indicate
 * that the annotation used by JHotDraw has the same semantics.
 */
package org.checkerframework.checker.nullness.qual;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * {@code NonNull} is a 'type use' annotation that indicates that the value is never null 
 * after the class has been fully initialized.
 *
 * @author Werner Randleshofer
 */
@Documented
@Retention(value = RUNTIME)
@Target(value = {TYPE_USE, TYPE_PARAMETER})
public @interface NonNull {

}

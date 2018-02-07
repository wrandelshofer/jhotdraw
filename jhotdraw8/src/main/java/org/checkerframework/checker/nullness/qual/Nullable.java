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
 * {@code Nullable} is a 'type use' annotation that indicates that the value is not known to be non-null.
 * <p>
 * Only type uses with a {@code Nullable} annotation are expected to be assigned (or to evaluate to)
 * a {@code null} value.
 * <p>
 * Instance variables without {@code Nullable} annotation are expected to never hold a
 * {@code null} value after the instance has been fully initialized.
 * <p>
 * Likewise, class variables without {@code Nullable} annotation are expected to never hold
 * a {@code null} value after the containing class has been fully initialized.
 *
 * @author Werner Randleshofer
 */
@Documented
@Retention(value = RUNTIME)
@Target(value = {TYPE_USE, TYPE_PARAMETER})
public @interface Nullable {

}

/* @(#)NotNull.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.annotation;

/**
 * Indicates that null must not be assigned or returned.
 * <p>
 * Semantics:
 * <ul>
 * <li>Variable (field, variable, parameter): The value null must not be assigned to the variable.</li>
 * <li>Method: The method must not return null.</li>
 * </ul>
 * 
 * @author Werner Randelshofer
 */
public @interface NotNull {
    
}

/* @(#)Nullable.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.annotation;

/**
 * Indicates that null can be assigned or returned.
 * <p>
 * Semantics:
 * <ul>
 * <li>Variable (field, variable, parameter): The value null can be assigned to the variable.</li>
 * <li>Method: The method may return null.</li>
 * </ul>
 * 
 * @author Werner Randelshofer
 */
public @interface Nullable {
    
}

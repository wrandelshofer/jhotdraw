/* @(#)ResourceDecoder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util;


import javax.annotation.Nonnull;

/**
 * Decodes a resource value.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ResourceDecoder {

    /**
     * Returns true if this resource handler can decode the specified property
     * value.
     *
     * @param key The property key
     * @param propertyValue the property value
     * @param type The desired type
     * @return true if the property can be decoded
     */
    boolean canDecodeValue( String key,  String propertyValue,  Class<?> type);

    /**
     * Decodes the property value.
     *
     * @param key The property key
     * @param propertyValue The property value
     * @param type The desired type
     * @param baseClass The base class to be used if the property value is a
     * resource uri
     * @param <T> The type of the decoded object
     * @return the decoded object
     */
    @Nonnull
    <T> T decode(String key, String propertyValue, Class<T> type, Class<?> baseClass);
}

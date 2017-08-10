/* @(#)StyleablePropertyBean.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.MapAccessor;

/**
 * {@code StyleablePropertyBean} provides styleable properties.
 * <p>
 * A {@code StyleablePropertyBean} provides a separate storage space for each
 * {@code javafx.css.StyleOrigin}.
 * <p>
 * The interface {@code PropertyBean} is used to access the
 * {@code StyleOrigin.USER} origin.
 * <p>
 * The other origins can be accessed using
 * {@code getStyleableProperty(key).applyStyle(origin, value)}.
 * <p>
 * Method {@code getStyled(key);} returns the styled value. The style origins
 * have the precedence as defined in {@link StyleableProperty} which is
 * {@code INLINE, AUTHOR, USER, USER_AGENT}.
 *
 *
 * @author Werner Randelshofer
 */
public interface StyleablePropertyBean extends PropertyBean, Styleable {

    /**
     * Returns the styleable property.
     *
     * @param <T> The value type
     * @param key The property key
     * @return The styleable property.
     */
    <T> StyleableProperty<T> getStyleableProperty(MapAccessor<T> key);

    /**
     * Returns the styled value.
     *
     * @param <T> The value type
     * @param key The property key
     * @return The styled value.
     */
    public <T> T getStyled(MapAccessor<T> key);
   /**
     * Returns the styled value.
     *
     * @param <T> The value type
     * @param key The property key
     * @param origin The style origin
     * @return The styled value.
     */
    public <T> T getStyled(StyleOrigin origin, MapAccessor<T> key);

    /**
     * Sets a styled value.
     *
     * @param <T> The value type
     * @param origin The style origin
     * @param key The property key
     * @param value The new value
     * @return The old value of that origin
     */
    public <T> T setStyled(StyleOrigin origin, MapAccessor<T> key, T value);

    /**
     * Removes a value.
     *
     * @param <T> The value type
     * @param origin The origin.
     * @param key The property key.
     * @return The removed value.
     */
    public <T> T remove(StyleOrigin origin, MapAccessor<T> key);

    /**
     * Removes all values of that style origin.
     *
     * @param origin The origin.
     */
    public void removeAll(StyleOrigin origin);
    
       /**
     * Returns if a key is present for that style origin.
     *
     * @param <T> The value type
     * @param key The property key
     * @param origin The style origin
     * @return True if a value is present.
     */
    public <T> boolean containsKey(StyleOrigin origin, MapAccessor<T> key);


}

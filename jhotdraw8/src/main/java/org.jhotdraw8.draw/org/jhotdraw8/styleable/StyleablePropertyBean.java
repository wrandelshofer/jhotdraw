/*
 * @(#)StyleablePropertyBean.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonnullMapAccessor;

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
 * @author Werner Randelshofer
 * @version $Id$
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
    @Nullable <T> T getStyled(@Nonnull MapAccessor<T> key);

    /**
     * Returns the styled value.
     *
     * @param <T> The value type
     * @param key The property key
     * @return The styled value.
     */
    @Nonnull
    default <T> T getStyledNonnull(@Nonnull NonnullMapAccessor<T> key) {
        T value = getStyled(key);
        if (value == null) {
            throw new NullPointerException("key:" + key + "+value is null");
        }
        return value;
    }

    /**
     * Returns the styled value.
     *
     * @param <T>    The value type
     * @param key    The property key
     * @param origin The style origin
     * @return The styled value.
     */
    @Nullable <T> T getStyled(@Nonnull StyleOrigin origin, @Nonnull MapAccessor<T> key);

    /**
     * Sets a styled value.
     *
     * @param <T>    The value type
     * @param origin The style origin
     * @param key    The property key
     * @param value  The new value
     * @return The old value of that origin
     */
    @Nullable <T> T setStyled(@Nonnull StyleOrigin origin, @Nonnull MapAccessor<T> key, @Nullable T value);

    /**
     * Removes a value.
     *
     * @param <T>    The value type
     * @param origin The origin.
     * @param key    The property key.
     * @return The removed value.
     */
    @Nullable <T> T remove(@Nonnull StyleOrigin origin, @Nonnull MapAccessor<T> key);

    /**
     * Removes all values of that style origin.
     *
     * @param origin The origin.
     */
    void removeAll(@Nonnull StyleOrigin origin);

    /**
     * Returns if a key is present for that style origin.
     *
     * @param <T>    The value type
     * @param key    The property key
     * @param origin The style origin
     * @return True if a value is present.
     */
    <T> boolean containsKey(@Nonnull StyleOrigin origin, @Nonnull MapAccessor<T> key);

}

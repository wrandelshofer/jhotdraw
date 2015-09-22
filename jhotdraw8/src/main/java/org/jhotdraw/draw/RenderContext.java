/* @(#)RenderContext.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.scene.Node;
import org.jhotdraw.collection.Key;

/**
 * RenderContext.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface RenderContext {
   // ---
    // constant declarations
    // ---
    /**
     * The name of the "renderingHints" property.
     */
    public final String RENDERING_HINTS_PROPERTY = "renderingHints";

    // ---
    // property methods
    // ---
    /**
     * Returns an observable map of rendering hint keys and their values.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> renderingHints();

    /**
     * Sets a rendering hint value.
     *
     * @param <T> the value type
     * @param key the key
     * @param newValue the value
     * @return the old value
     */
    default <T> T setRenderingHint(Key<T> key, T newValue) {
        return key.put(renderingHints(), newValue);
    }

    /**
     * Gets a rendering hint value.
     *
     * @param <T> the value type
     * @param key the key
     * @return the value
     */
    default <T> T getRenderingHint(Key<T> key) {
        return key.get(renderingHints());
    }

    /**
     * Gets the JavaFX node which is used to render the specified figure by this
     * {@code RenderContext}.
     *
     * @param f The figure
     * @return The JavaFX node associated to the figure
     */
    public Node getNode(Figure f);

}

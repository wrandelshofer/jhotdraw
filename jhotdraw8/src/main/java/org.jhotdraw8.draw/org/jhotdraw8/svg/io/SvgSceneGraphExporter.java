/*
 * @(#)SvgSceneGraphExporter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.io;

import javafx.scene.Node;
import org.jhotdraw8.collection.NonNullObjectKey;

public interface SvgSceneGraphExporter {
    /**
     * If {@link Node#getProperties()} contains a String property with this name,
     * the {@code SvgSceneGraphExporter} exports a {@literal <title>}
     * element with the property value as its content.
     * <p>
     * The value of this constant is: {@value #TITLE_PROPERTY_NAME}.
     */
    String TITLE_PROPERTY_NAME = "title";
    /**
     * If {@link Node#getProperties()} contains a String property with this name,
     * the {@code SvgSceneGraphExporter} exports a {@literal <desc>}
     * element with the property value as its content.
     * <p>
     * The value of this constant is: {@value #DESC_PROPERTY_NAME}.
     */
    String DESC_PROPERTY_NAME = "desc";
    NonNullObjectKey<Boolean> EXPORT_INVISIBLE_ELEMENTS_KEY = new NonNullObjectKey<>("exportInvisibleElements", Boolean.class, false);
    NonNullObjectKey<Boolean> RELATIVIZE_PATHS_KEY = new NonNullObjectKey<>("relativizePaths", Boolean.class, false);
}

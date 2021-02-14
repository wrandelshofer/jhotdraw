/*
 * @(#)RenderContext.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.render;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.ReadOnlyPropertyBean;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.NonNullKey;
import org.jhotdraw8.collection.NonNullObjectKey;
import org.jhotdraw8.collection.SimpleNullableKey;
import org.jhotdraw8.css.DefaultSystemColorConverter;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.css.SystemColorConverter;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Page;

import java.time.Instant;

/**
 * RenderContext.
 *
 * @author Werner Randelshofer
 */
public interface RenderContext extends ReadOnlyPropertyBean {

    // ---
    // keys
    // ---
    NonNullKey<RenderingIntent> RENDERING_INTENT = new NonNullObjectKey<>("renderingIntent", RenderingIntent.class, RenderingIntent.EDITOR);

    /**
     * The dots per inch of the rendering device.
     */
    NonNullKey<Double> DPI = new NonNullObjectKey<>("dpi", Double.class, 96.0);
    /**
     * Contains a non-null value if the rendering is clipped. The clip bounds are given in world coordinates.
     */
    @NonNull
    Key<Bounds> CLIP_BOUNDS = new SimpleNullableKey<>("clipBounds", Bounds.class, null);
    /**
     * Number of nodes that can be rendered per layer in the drawing editor..
     */
    NonNullObjectKey<Integer> MAX_NODES_PER_LAYER = new NonNullObjectKey<>("maxNodesPerLayer", Integer.class, 10_000);

    @NonNull
    Key<Page> RENDER_PAGE = new SimpleNullableKey<>("renderPage", Page.class, null);
    Key<Integer> RENDER_PAGE_NUMBER = new SimpleNullableKey<>("renderPageNumber", Integer.class, 0);
    Key<Integer> RENDER_NUMBER_OF_PAGES = new SimpleNullableKey<>("renderNumberOfPages", Integer.class, 1);
    Key<Integer> RENDER_PAGE_INTERNAL_NUMBER = new SimpleNullableKey<>("renderPageInternalNumber", Integer.class, 0);
    Key<Instant> RENDER_TIMESTAMP = new SimpleNullableKey<>("renderTimestamp", Instant.class, Instant.now());

    NonNullObjectKey<UnitConverter> UNIT_CONVERTER_KEY = new NonNullObjectKey<>("unitConverter", UnitConverter.class, new DefaultUnitConverter());
    NonNullObjectKey<SystemColorConverter> SYSTEM_COLOR_CONVERTER_KEY = new NonNullObjectKey<>("colorConverter", SystemColorConverter.class, new DefaultSystemColorConverter());
    // ---
    // behavior
    // ---

    /**
     * Gets the JavaFX node which is used to render the specified figure by this
     * {@code RenderContext}.
     *
     * @param f The figure
     * @return The JavaFX node associated to the figure
     */
    @Nullable Node getNode(Figure f);

}

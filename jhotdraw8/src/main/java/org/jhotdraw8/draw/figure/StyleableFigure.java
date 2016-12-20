/* @(#)StyleableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.collection.ImmutableObservableSet;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.ObservableWordListFigureKey;
import org.jhotdraw8.draw.key.SimpleFigureKey;

/**
 * {@code StyleableFigure} supports styling of a figure using
 * {@code FigureMapAccessor}s.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 */
public interface StyleableFigure extends Figure {

    /**
     * Defines the id for styling the figure with CSS.
     *
     * Default value: {@code null}.
     */
    public static SimpleFigureKey<String> ID = new SimpleFigureKey<String>("id", String.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.STYLE), null);
    /**
     * Defines the style class of the figure. The style class is used for
     * styling a figure with CSS.
     *
     * Default value: {@code null}.
     */
    public static ObservableWordListFigureKey STYLE_CLASS = new ObservableWordListFigureKey("class", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.STYLE), ImmutableObservableList.emptyList());
    /**
     * Defines the pseudo class states of the figure. The pseudo class states
     * are used for styling a figure with CSS.
     *
     * Default value: {@code null}.
     */
    public static SimpleFigureKey<ImmutableObservableSet<PseudoClass>> PSEUDO_CLASS_STATES = new SimpleFigureKey<>("pseudoClassStates", ImmutableObservableSet.class, new Class<?>[]{PseudoClass.class}, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.STYLE), ImmutableObservableSet.emptySet());
    /**
     * Defines the style of the figure. The style is used for styling a figure
     * with CSS.
     *
     * Default value: {@code null}.
     */
    public static SimpleFigureKey<String> STYLE = new SimpleFigureKey<>("style", String.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.STYLE), null);

    /**
     * Updates a figure node with all style and effect properties defined in
     * this interface.
     * <p>
     * Applies the following properties: {@code ID}, {@code VISIBLE}.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param ctx the render context
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applyStyleableFigureProperties(RenderContext ctx, Node node) {
        if (ctx.get(RenderContext.RENDERING_INTENT) == RenderingIntent.EXPORT) {
            String styleId = getId();
            node.setId(styleId == null ? "" : styleId);
            node.getStyleClass().setAll(getStyleClass());
            node.getStyleClass().add(getTypeSelector());
        }
    }

    @Override
    default String getStyle() {
        return get(STYLE);
    }

    @Override
    default ObservableList<String> getStyleClass() {
        return get(STYLE_CLASS);
    }

    @Override
    default ObservableSet<PseudoClass> getPseudoClassStates() {
        return get(PSEUDO_CLASS_STATES);
    }

    @Override
    default String getId() {
        return get(ID);
    }
}

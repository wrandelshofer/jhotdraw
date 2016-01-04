/* @(#)StyleableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.draw.RenderingIntent;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.ObservableWordListFigureKey;
import org.jhotdraw.draw.key.SimpleFigureKey;
import org.jhotdraw.draw.key.FigureMapAccessor;

/**
 * {@code StyleableFigure} supports styling of a figure using
 * {@code FigureMapAccessor}s.
 *
 * @author Werner Randelshofer
 */
public interface StyleableFigure extends Figure {

    /**
     * Defines the id for styling the figure with CSS.
     *
     * Default value: {@code null}.
     */
    public static FigureMapAccessor<String> STYLE_ID = new SimpleFigureKey<String>("id", String.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT, DirtyBits.STYLE), null);
    /**
     * Defines the style class of the figure. The style class is used for
     * styling a figure with CSS.
     *
     * Default value: {@code null}.
     */
    public static ObservableWordListFigureKey STYLE_CLASS = new ObservableWordListFigureKey("class", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT, DirtyBits.STYLE), FXCollections.emptyObservableList());
    /**
     * Defines the pseudo class states of the figure. The pseudo class states
     * are used for styling a figure with CSS.
     *
     * Default value: {@code null}.
     */
    public static SimpleFigureKey<ObservableSet<PseudoClass>> PSEUDO_CLASS_STATES = new SimpleFigureKey<>("pseudoClassStates", ObservableSet.class, new Class<?>[]{PseudoClass.class}, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT, DirtyBits.STYLE), FXCollections.emptyObservableSet());
    /**
     * Defines the style of the figure. The style is used for styling a figure
     * with CSS.
     *
     * Default value: {@code null}.
     */
    public static SimpleFigureKey<String> STYLE = new SimpleFigureKey<>("style", String.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT, DirtyBits.STYLE), null);

    /**
     * Updates a figure node with all style and effect properties defined in
     * this interface.
     * <p>
     * Applies the following properties: {@code STYLE_ID}, {@code VISIBLE}.
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
        return get(STYLE_ID);
    }
}

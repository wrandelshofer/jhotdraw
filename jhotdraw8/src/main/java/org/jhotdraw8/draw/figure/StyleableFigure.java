/* @(#)StyleableFigure.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableSet;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.ObservableWordListFigureKey;
import org.jhotdraw8.draw.key.SimpleFigureKey;
import org.jhotdraw8.draw.key.StringReadOnlyStyleableFigureKey;

/**
 * {@code StyleableFigure} supports styling ofCollection a figure using
 {@code FigureMapAccessor}s.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface StyleableFigure extends Figure {

    /**
     * Defines the id for styling the figure with CSS.
     *
     * Default value: {@code null}.
     */
    @javax.annotation.Nullable
    public static StringReadOnlyStyleableFigureKey ID = new StringReadOnlyStyleableFigureKey("id", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.STYLE),null );
    /**
     * Defines the style class of the figure. The style class is used for
     * styling a figure with CSS.
     *
     * Default value: {@code null}.
     */
    public static ObservableWordListFigureKey STYLE_CLASS = new ObservableWordListFigureKey("class", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.STYLE), ImmutableList.emptyList());
    /**
     * Defines the pseudo class states of the figure. The pseudo class states
     * are used for styling a figure with CSS. The should not be made persistent.
     *
     * Default value: {@code null}.
     */
    public static SimpleFigureKey<ImmutableSet<PseudoClass>> PSEUDO_CLASS_STATES = new SimpleFigureKey<>("pseudoClassStates", ImmutableSet.class, new Class<?>[]{PseudoClass.class}, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.STYLE), ImmutableSet.emptySet());
    /**
     * Defines the style of the figure. The style is used for styling a figure
     * with CSS.
     *
     * Default value: {@code null}.
     */
    @javax.annotation.Nullable
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
    default void applyStyleableFigureProperties(@Nonnull RenderContext ctx, @Nonnull Node node) {
        if (ctx.get(RenderContext.RENDERING_INTENT) == RenderingIntent.EXPORT) {
            String styleId = getId();
            node.setId(styleId == null ? "" : styleId);
            node.getStyleClass().setAll(getStyleClass());
            node.getStyleClass().add(getTypeSelector());
        }
    }

    @Override @Nullable
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

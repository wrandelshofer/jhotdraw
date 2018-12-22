/* @(#)StyleableFigure.java
 * Copyright © by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableSet;
import org.jhotdraw8.draw.key.NullableObjectFigureKey;
import org.jhotdraw8.draw.key.ObjectFigureKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.ObservableWordListFigureKey;
import org.jhotdraw8.draw.key.StringReadableStyleableFigureKey;

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
    @Nonnull
    StringReadableStyleableFigureKey ID = new StringReadableStyleableFigureKey("id", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.STYLE),null );
    /**
     * Defines the style class of the figure. The style class is used for
     * styling a figure with CSS.
     *
     * Default value: {@code null}.
     */
    @Nonnull
    ObservableWordListFigureKey STYLE_CLASS = new ObservableWordListFigureKey("class", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.STYLE), ImmutableList.emptyList());
    /**
     * Defines the pseudo class states of the figure. The pseudo class states
     * are used for styling a figure with CSS. The should not be made persistent.
     *
     * Default value: {@code null}.
     */
    @Nonnull
    ObjectFigureKey<ImmutableSet<PseudoClass>> PSEUDO_CLASS_STATES = new ObjectFigureKey<>("pseudoClassStates", ImmutableSet.class, new Class<?>[]{PseudoClass.class}, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.STYLE), ImmutableSet.emptySet());
    /**
     * Defines the style of the figure. The style is used for styling a figure
     * with CSS.
     *
     * Default value: {@code null}.
     */
    @Nonnull
    NullableObjectFigureKey<String> STYLE = new NullableObjectFigureKey<>("style", String.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.STYLE), null);

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
        return getNonnull(STYLE_CLASS).asObservableList();
    }

    @Override
    default ObservableSet<PseudoClass> getPseudoClassStates() {
        return getNonnull(PSEUDO_CLASS_STATES).asObservableSet();
    }

    @Override
    default String getId() {
        return get(ID);
    }
}

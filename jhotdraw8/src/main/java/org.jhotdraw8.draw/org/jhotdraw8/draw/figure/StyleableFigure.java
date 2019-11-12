/*
 * @(#)StyleableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ImmutableSet;
import org.jhotdraw8.collection.ImmutableSets;
import org.jhotdraw8.draw.key.NullableObjectKey;
import org.jhotdraw8.draw.key.NullableStringStyleableKey;
import org.jhotdraw8.draw.key.ObjectFigureKey;
import org.jhotdraw8.draw.key.ObservableWordListKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;

/**
 * {@code StyleableFigure} provides user-editable "id", "style class" and "style" properties,
 * and a non-user-editable "pseudo-class" property.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface StyleableFigure extends Figure {

    /**
     * Defines the id for styling the figure with CSS.
     * <p>
     * Default value: {@code null}.
     */
    @NonNull
    NullableStringStyleableKey ID = new NullableStringStyleableKey("id");
    /**
     * Defines the style class of the figure. The style class is used for
     * styling a figure with CSS.
     * <p>
     * Default value: {@code null}.
     */
    @NonNull
    ObservableWordListKey STYLE_CLASS = new ObservableWordListKey("class", ImmutableLists.emptyList());
    /**
     * Defines the pseudo class states of the figure. The pseudo class states
     * are used for styling a figure with CSS.
     * This property should not be made persistent because it is a computed value.
     * <p>
     * Default value: {@code null}.
     */
    @NonNull
    ObjectFigureKey<ImmutableSet<PseudoClass>> PSEUDO_CLASS_STATES = new ObjectFigureKey<>("pseudoClassStates", ImmutableSet.class, new Class<?>[]{PseudoClass.class}, ImmutableSets.emptySet());
    /**
     * Defines the style of the figure. The style is used for styling a figure
     * with CSS.
     * <p>
     * Default value: {@code null}.
     */
    @NonNull
    NullableObjectKey<String> STYLE = new NullableObjectKey<>("style", String.class, null);

    /**
     * Updates a figure node with all style and effect properties defined in
     * this interface.
     * <p>
     * Applies the following properties: {@code ID}, {@code VISIBLE}.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param ctx  the render context
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applyStyleableFigureProperties(@NonNull RenderContext ctx, @NonNull Node node) {
        if (ctx.get(RenderContext.RENDERING_INTENT) == RenderingIntent.EXPORT) {
            String styleId = getId();
            node.setId(styleId == null ? "" : styleId);
            node.getStyleClass().setAll(getStyleClass());
            node.getStyleClass().add(getTypeSelector());
        }
    }

    @Override
    @Nullable
    default String getStyle() {
        return get(STYLE);
    }

    @Override
    default ObservableList<String> getStyleClass() {
        return getNonNull(STYLE_CLASS).asObservableList();
    }

    @Override
    default ObservableSet<PseudoClass> getPseudoClassStates() {
        return getNonNull(PSEUDO_CLASS_STATES).asObservableSet();
    }

    @Nullable
    @Override
    default String getId() {
        return get(ID);
    }
}

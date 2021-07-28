/*
 * @(#)LayerFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.geom.FXGeom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.max;

/**
 * LayerFigure.
 *
 * @author Werner Randelshofer
 */
public class LayerFigure extends AbstractCompositeFigure
        implements Layer, StyleableFigure, HideableFigure, LockableFigure, NonTransformableFigure, CompositableFigure {

    private static final int MIN_NODES_FOR_CLIPPING = 100;

    @Override
    public void reshapeInLocal(@NonNull Transform transform) {
        for (Figure child : getChildren()) {
            child.reshapeInLocal(transform);
        }
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        // empty
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Group n = (Group) node;
        applyHideableFigureProperties(ctx, n);
        RenderingIntent renderingIntent = ctx.get(RenderContext.RENDERING_INTENT);
        if (!isVisible() && renderingIntent == RenderingIntent.EDITOR) {
            return;
        }
        applyStyleableFigureProperties(ctx, n);
        applyCompositableFigureProperties(ctx, n);


        List<Node> childNodes;
        int maxNodesPerLayer = ctx.getNonNull(RenderContext.MAX_NODES_PER_LAYER);
        final Bounds clipBounds = ctx.get(RenderContext.CLIP_BOUNDS);
        if (renderingIntent == RenderingIntent.EDITOR
                && clipBounds != null && getChildren().size() > MIN_NODES_FOR_CLIPPING) {
            childNodes = getChildren().stream()
                    .parallel()
                    .filter(child -> child.getVisualBoundsInWorld().intersects(clipBounds))
                    .collect(Collectors.toList()).stream()
                    .map(ctx::getNode)// cannot be done in parallel
                    .collect(Collectors.toList());

            if (childNodes.size() > maxNodesPerLayer) {
                updateNodeWithErrorMessage(ctx, childNodes, clipBounds);
            }
        } else {
            childNodes = new ArrayList<>();
            for (Figure child : getChildren()) {
                childNodes.add(ctx.getNode(child));
            }
        }

        ObservableList<Node> groupChildren = n.getChildren();
        if (!groupChildren.equals(childNodes)) {
            groupChildren.setAll(childNodes);
        }
    }

    public void updateNodeWithErrorMessage(@NonNull RenderContext ctx, List<Node> childNodes, Bounds clipBounds) {
        Drawing drawing = getDrawing();
        Bounds b = (drawing != null) ? drawing.getLayoutBounds() : new BoundingBox(0, 0, 100, 100);
        Rectangle r = new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
        UnitConverter unitConverter = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        double delta = unitConverter.convert(1, UnitConverter.VIEWPORT_MIN_PERCENTAGE, UnitConverter.DEFAULT);
        Color red = new Color(1, 0, 0, 0.1);

        Text text = new Text();
        // We must set the font before we set the text, so that JavaFx does not need to retrieve
        // the system default font, which on Windows requires that the JavaFx Toolkit is launched.
        double fontSize = unitConverter.convert(1.6, UnitConverter.VIEWPORT_MIN_PERCENTAGE, UnitConverter.DEFAULT);
        text.setFont(CssFont.font("Arial", FontWeight.NORMAL, FontPosture.REGULAR,
                fontSize).getFont());
        text.setText("Layer \"" + getId() + "\" has too many children: " + getChildren().size() + ".");
        text.setFill(Color.RED);
        text.setStroke(Color.WHITE);
        text.setStrokeWidth(unitConverter.convert(0.2, UnitConverter.VIEWPORT_MIN_PERCENTAGE, UnitConverter.DEFAULT));
        text.setStrokeType(StrokeType.OUTSIDE);
        text.setX(max(fontSize, clipBounds.getMinX() + fontSize));
        if (getParent() != null) {
            int countVisibleLayersBeforeMe = 0;
            for (Figure child : getParent().getChildren()) {
                if (Boolean.TRUE.equals(child.get(HideableFigure.VISIBLE))) {
                    countVisibleLayersBeforeMe++;
                }
                if (child == this) {
                    break;
                }
            }
            text.setY(max(0, clipBounds.getMinY()) + text.getLayoutBounds().getHeight() * 1.2 * (1 + countVisibleLayersBeforeMe));
        } else {
            text.setY(max(0, clipBounds.getMinY()) + text.getLayoutBounds().getHeight() * 1.2);
        }

        Bounds tb = text.getLayoutBounds();
        tb = FXGeom.grow(tb, unitConverter.convert(0.1, UnitConverter.VIEWPORT_MIN_PERCENTAGE, UnitConverter.DEFAULT));
        r.setX(tb.getMinX());
        r.setY(tb.getMinY());
        r.setWidth(tb.getWidth());
        r.setHeight(tb.getHeight());
        Color transparent = Color.TRANSPARENT;
        r.setFill(new LinearGradient(r.getX(), r.getY(), r.getX() + delta, r.getY() + delta, false, CycleMethod.REPEAT, new Stop(0.5, red), new Stop(0.5, transparent)));

        childNodes.clear();
        childNodes.add(r);
        childNodes.add(text);
    }

    @Override
    public @NonNull Node createNode(@NonNull RenderContext ctx) {
        Group n = new Group();
        n.setManaged(false);
        n.setAutoSizeChildren(false);
        return n;
    }

    /**
     * This method throws an illegal argument exception if the new parent is not
     * an instance of Drawing.
     *
     * @param newValue the desired parent
     */
    protected void checkNewParent(@Nullable Figure newValue) {
        if (newValue != null && !(newValue instanceof Drawing) && !(newValue instanceof Clipping)) {
            throw new IllegalArgumentException("A Layer can only be added as a child to a Drawing. Illegal parent: "
                    + newValue);
        }
    }

    /**
     * Layer figures always return false for isSelectable.
     *
     * @return false
     */
    @Override
    public boolean isSelectable() {
        return false;
    }


    /**
     * Layers never create handles.
     */
    @Override
    public void createHandles(@NonNull HandleType handleType, @NonNull List<Handle> list) {
        // empty
    }

    @Override
    public boolean isSuitableChild(@NonNull Figure newChild) {
        return true;
    }

    @Override
    public String toString() {
        return "LayerFigure@" + Integer.toHexString(System.identityHashCode(this)) + "{" + getId() + "}";
    }

    /**
     * A layer always has the following bounds [0,0,MAX_VALUE,MAX_VALUE].
     *
     * @return [0, 0, MAX_VALUE, MAX_VALUE].
     */
    @Override
    public @NonNull Bounds getBoundsInLocal() {
        return new BoundingBox(0, 0, Double.MAX_VALUE, Double.MAX_VALUE);
    }
}

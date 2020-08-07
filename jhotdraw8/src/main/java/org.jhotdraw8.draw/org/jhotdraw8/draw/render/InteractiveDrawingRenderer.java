/*
 * @(#)InteractiveDrawingRenderer.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.render;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.AbstractPropertyBean;
import org.jhotdraw8.beans.NonNullProperty;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.model.DrawingModelEvent;
import org.jhotdraw8.draw.model.SimpleDrawingModel;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.tree.TreeModelEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InteractiveDrawingRenderer extends AbstractPropertyBean implements RenderContext {
    @NonNull
    private final NonNullProperty<DrawingModel> model //
            = new NonNullProperty<>(this, "model", new SimpleDrawingModel());

    @NonNull
    private final Group node = new Group();
    @NonNull
    private final Listener<DrawingModelEvent> drawingModelListener = this::onDrawingModelEvent;
    @NonNull
    private final Listener<TreeModelEvent<Figure>> treeModelListener = this::onTreeModelEvent;

    private final ObjectProperty<Bounds> clipBounds = new SimpleObjectProperty<>(this, "clipBounds",
            new BoundingBox(0, 0, 800, 600));
    /**
     * This is the set of figures which are out of sync with their JavaFX node.
     */
    private final Set<Figure> dirtyFigureNodes = new HashSet<>();

    public InteractiveDrawingRenderer() {
        node.setManaged(false);
        model.addListener(this::onDrawingModelChanged);
        clipBounds.addListener(this::onClipBoundsChanged);
    }

    private void onClipBoundsChanged(Observable observable) {
        invalidateLayerNodes();
        repaint();
    }

    private void invalidateLayerNodes() {
        Drawing drawing = getDrawing();
        if (drawing != null) {
            dirtyFigureNodes.addAll(drawing.getChildren());
        }
    }

    public Node getNode() {
        return node;
    }

    @Nullable
    public Drawing getDrawing() {
        return getModel() == null ? null : getModel().getDrawing();
    }

    public DrawingModel getModel() {
        return model.get();
    }

    public @NonNull NonNullProperty<DrawingModel> modelProperty() {
        return model;
    }

    public void setModel(DrawingModel model) {
        this.model.set(model);
    }

    private void onDrawingModelEvent(DrawingModelEvent event) {

        Figure f = event.getNode();
        switch (event.getEventType()) {
        case LAYOUT_CHANGED:
            onLayoutChanged(f);
            break;
        case STYLE_CHANGED:
            onStyleChanged(f);
            break;
        case PROPERTY_VALUE_CHANGED:
            onPropertyValueChanged(f);
            break;
        case TRANSFORM_CHANGED:
            onTransformChanged(f);
            break;
        default:
            throw new UnsupportedOperationException(event.getEventType()
                    + " not supported");
        }

    }

    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, "zoomFactor", 1.0);

    @NonNull

    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    public void setZoomFactor(double newValue) {
        zoomFactorProperty().set(newValue);
    }

    public double getZoomFactor() {
        return zoomFactorProperty().get();
    }

    private void onTransformChanged(Figure f) {
        invalidateFigureNode(f);
        repaint();
    }

    private void onPropertyValueChanged(Figure f) {
        invalidateFigureNode(f);
        repaint();
    }

    private void onStyleChanged(Figure f) {
        invalidateFigureNode(f);
        repaint();
    }

    private void onLayoutChanged(Figure f) {
        invalidateFigureNode(f);
        repaint();
    }

    private void onDrawingModelChanged(Observable o, @Nullable DrawingModel oldValue, @Nullable DrawingModel newValue) {
        if (oldValue != null) {
            oldValue.removeTreeModelListener(treeModelListener);
            oldValue.removeDrawingModelListener(drawingModelListener);
        }
        if (newValue != null) {
            newValue.addDrawingModelListener(drawingModelListener);
            newValue.addTreeModelListener(treeModelListener);
            onRootChanged(newValue.getDrawing());
        }
    }

    private void onTreeModelEvent(TreeModelEvent<Figure> event) {
        Figure f = event.getNode();
        switch (event.getEventType()) {
        case NODE_ADDED_TO_PARENT:
            onFigureAdded(f);
            break;
        case NODE_REMOVED_FROM_PARENT:
            onFigureRemoved(f);
            break;
        case NODE_ADDED_TO_TREE:
            onFigureRemovedFromDrawing(f);
            break;
        case NODE_REMOVED_FROM_TREE:
            onNodeRemovedFromTree(f);
            break;
        case NODE_CHANGED:
            onNodeChanged(f);
            break;
        case ROOT_CHANGED:
            onRootChanged(f);
            break;
        case SUBTREE_NODES_CHANGED:
            onSubtreeNodesChanged(f);
            break;
        default:
            throw new UnsupportedOperationException(event.getEventType()
                    + " not supported");
        }
    }

    private void onSubtreeNodesChanged(@NonNull Figure figure) {
        for (Figure f : figure.preorderIterable()) {
            dirtyFigureNodes.add(f);
        }
        repaint();
    }

    private void onNodeRemovedFromTree(@NonNull Figure f) {
    }

    private void onFigureAdded(@NonNull Figure figure) {
        for (Figure f : figure.preorderIterable()) {
            invalidateFigureNode(f);
        }
        repaint();
    }

    private void invalidateFigureNode(Figure f) {
        dirtyFigureNodes.add(f);
    }

    private void onFigureRemoved(@NonNull Figure figure) {
        for (Figure f : figure.preorderIterable()) {
            removeNode(f);
        }
    }

    private void removeNode(Figure f) {
        Node oldNode = figureToNodeMap.remove(f);
        if (oldNode != null) {
            nodeToFigureMap.remove(oldNode);
        }
        dirtyFigureNodes.remove(f);
    }

    private void onFigureRemovedFromDrawing(@NonNull Figure figure) {

    }

    private void onNodeChanged(@NonNull Figure figure) {
        invalidateFigureNode(figure);
        repaint();
    }

    private void onRootChanged(Figure f) {
        ObservableList<Node> children = node.getChildren();
        children.setAll(getNode(f));
        dirtyFigureNodes.clear();
        dirtyFigureNodes.add(f);
        repaint();
    }

    private final Map<Figure, Node> figureToNodeMap = new HashMap<>();

    private final Map<Node, Figure> nodeToFigureMap = new HashMap<>();
    @Nullable
    private Runnable repainter = null;

    @Nullable
    public Node getNode(@Nullable Figure f) {
        if (f == null) {
            return null;
        }
        Node n = figureToNodeMap.get(f);
        if (n == null) {
            n = f.createNode(this);
            figureToNodeMap.put(f, n);
            nodeToFigureMap.put(n, f);
            dirtyFigureNodes.add(f);
            repaint();
        }
        return n;
    }

    public void repaint() {
        if (repainter == null) {
            repainter = () -> {
                repainter = null;
                paint();
            };
            Platform.runLater(repainter);
        }
    }

    private void paint() {
        set(RenderContext.CLIP_BOUNDS, getClipBounds());
        DefaultUnitConverter units = new DefaultUnitConverter(90, 1.0, 1024.0 / getZoomFactor(), 768 / getZoomFactor());
        set(RenderContext.UNIT_CONVERTER_KEY, units);
        updateNodes();

    }

    private void updateNodes() {
        Bounds visibleRectInWorld = getClipBounds();

        // create copies of the lists to allow for concurrent modification
        Figure[] copyOfDirtyFigureNodes = dirtyFigureNodes.toArray(new Figure[0]);
        dirtyFigureNodes.clear();
        for (Figure f : copyOfDirtyFigureNodes) {
            if (!f.isShowing() && !hasNode(f)
                    || !f.getBoundsInWorld().intersects(visibleRectInWorld)
            ) {
                // wont be updated and thus remains dirty!
                dirtyFigureNodes.add(f);
                continue;
            }
            Node node = getNode(f);
            if (node != null) {
                f.updateNode(this, node);
            }
        }
    }

    private boolean hasNode(Figure f) {
        return figureToNodeMap.containsKey(f);
    }

    public Bounds getClipBounds() {
        return clipBounds.get();
    }

    public ObjectProperty<Bounds> clipBoundsProperty() {
        return clipBounds;
    }

    public void setClipBounds(Bounds clipBounds) {
        this.clipBounds.set(clipBounds);
    }
}

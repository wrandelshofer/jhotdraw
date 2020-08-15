/*
 * @(#)InteractiveHandleRenderer.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.render;

import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.NonNullObjectProperty;
import org.jhotdraw8.collection.ReversedList;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.SimpleDrawingViewNew;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.model.DrawingModelEvent;
import org.jhotdraw8.draw.model.SimpleDrawingModel;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.geom.FXGeom;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.tree.TreeModelEvent;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class InteractiveHandleRenderer {
    private static final String RENDER_CONTEXT = "renderContenxt";
    private static final String DRAWING_VIEW = "drawingView";
    private final static double SQRT2 = Math.sqrt(2);
    @NonNull
    final
    private Group handlesPane = new Group();
    @NonNull
    final private ObjectProperty<DrawingView> drawingView = new SimpleObjectProperty<>(this, DRAWING_VIEW);
    /**
     * This is the set of handles which are out of sync with their JavaFX node.
     */
    private final Set<Figure> dirtyHandles = new HashSet<>();
    /**
     * The selectedFiguresProperty holds the list of selected figures in the
     * sequence they were selected by the user.
     */
    private final SetProperty<Figure> selectedFigures = new SimpleSetProperty<>(this, DrawingView.SELECTED_FIGURES_PROPERTY, FXCollections.observableSet(new LinkedHashSet<Figure>()));
    @NonNull
    final private ObjectProperty<DrawingEditor> editor = new SimpleObjectProperty<>(this, DrawingView.EDITOR_PROPERTY, null);
    /**
     * Maps each JavaFX node to a handle in the drawing view.
     */
    private final Map<Node, Handle> nodeToHandleMap = new LinkedHashMap<>();
    @NonNull
    private final Listener<TreeModelEvent<Figure>> treeModelListener = this::onTreeModelEvent;
    /**
     * The set of all handles which were produced by selected figures.
     */
    private final Map<Figure, List<Handle>> handles = new LinkedHashMap<>();
    /**
     * The set of all secondary handles. One handle at a time may create
     * secondary handles.
     */
    private final ArrayList<Handle> secondaryHandles = new ArrayList<>();
    private final ObjectProperty<Bounds> clipBounds = new SimpleObjectProperty<>(this, "clipBounds",
            new BoundingBox(0, 0, 800, 600));
    @NonNull
    private final NonNullObjectProperty<DrawingModel> model //
            = new NonNullObjectProperty<>(this, "model", new SimpleDrawingModel());
    private NonNullObjectProperty<RenderContext> renderContext = new NonNullObjectProperty<>(this, RENDER_CONTEXT, new SimpleRenderContext());
    private boolean recreateHandles;
    private boolean handlesAreValid;
    @Nullable
    private Runnable repainter = null;

    public InteractiveHandleRenderer() {
        handlesPane.setManaged(false);
        handlesPane.setAutoSizeChildren(false);
        model.addListener(this::onDrawingModelChanged);
        clipBounds.addListener(this::onClipBoundsChanged);
        selectedFigures.addListener(new SetChangeListener<Figure>() {
            @Override
            public void onChanged(Change<? extends Figure> change) {
                recreateHandles();
            }
        });

    }

    private void onDrawingModelChanged(Observable o, @Nullable DrawingModel oldValue, @Nullable DrawingModel newValue) {
        if (oldValue != null) {
            oldValue.removeTreeModelListener(treeModelListener);
        }
        if (newValue != null) {
            newValue.addTreeModelListener(treeModelListener);
            onRootChanged();
        }
    }

    /**
     * Returns true if the node contains the specified point within a a
     * tolerance.
     *
     * @param node      The node
     * @param point     The point in local coordinates
     * @param tolerance The maximal distance the point is allowed to be away
     *                  from the node
     * @return true if the node contains the point
     */
    public static boolean contains(@NonNull Node node, @NonNull Point2D point, double tolerance) {
        double toleranceInLocal = tolerance / node.getLocalToSceneTransform().deltaTransform(SQRT2, SQRT2).magnitude();

        if (node instanceof Shape) {
            Shape shape = (Shape) node;
            if (shape.contains(point)) {
                return true;
            }

            double widthFactor;
            switch (shape.getStrokeType()) {
            case CENTERED:
            default:
                widthFactor = 0.5;
                break;
            case INSIDE:
                widthFactor = 0;
                break;
            case OUTSIDE:
                widthFactor = 1;
                break;
            }
            if (FXGeom.grow(shape.getBoundsInParent(), tolerance, tolerance).contains(point)) {
                int cap = switch (shape.getStrokeLineCap()) {
                    case SQUARE -> BasicStroke.CAP_SQUARE;
                    case BUTT -> BasicStroke.CAP_BUTT;
                    case ROUND -> BasicStroke.CAP_ROUND;
                };
                int join = switch (shape.getStrokeLineJoin()) {
                    case MITER -> BasicStroke.JOIN_MITER;
                    case BEVEL -> BasicStroke.JOIN_BEVEL;
                    case ROUND -> BasicStroke.JOIN_ROUND;
                };
                return new BasicStroke(2f * (float) (shape.getStrokeWidth() * widthFactor + toleranceInLocal),
                        cap, join, (float) shape.getStrokeMiterLimit()
                ).createStrokedShape(Shapes.awtShapeFromFX(shape))
                        .contains(new java.awt.geom.Point2D.Double(point.getX(), point.getY()));
            } else {
                return false;
            }
        } else if (node instanceof Group) {
            if (FXGeom.contains(node.getBoundsInLocal(), point, toleranceInLocal)) {
                for (Node child : ((Group) node).getChildren()) {
                    if (contains(child, child.parentToLocal(point), tolerance)) {
                        return true;
                    }
                }
            }
            return false;
        } else { // foolishly assumes that all other nodes are rectangular and opaque
            return FXGeom.contains(node.getBoundsInLocal(), point, tolerance);
        }
    }

    /**
     * Creates selection handles and adds them to the provided list.
     *
     * @param handles The provided list
     */
    protected void createHandles(@NonNull Map<Figure, List<Handle>> handles) {
        List<Figure> selection = new ArrayList<>(getSelectedFigures());
        if (selection.size() > 1) {
            if (getEditor().getAnchorHandleType() != null) {
                Figure anchor = selection.get(0);
                List<Handle> list = handles.computeIfAbsent(anchor, k -> new ArrayList<>());
                anchor.createHandles(getEditor().getAnchorHandleType(), list);
            }
            if (getEditor().getLeadHandleType() != null) {
                Figure anchor = selection.get(selection.size() - 1);
                List<Handle> list = handles.computeIfAbsent(anchor, k -> new ArrayList<>());
                anchor.createHandles(getEditor().getLeadHandleType(), list);
            }
        }
        HandleType handleType = getEditor().getHandleType();
        ArrayList<Handle> list = new ArrayList<>();
        for (Figure figure : selection) {
            figure.createHandles(handleType, list);
        }
        for (Handle h : list) {
            Figure figure = h.getOwner();
            handles.computeIfAbsent(figure, k -> new ArrayList<>()).add(h);
        }
    }


    @NonNull
    public ObjectProperty<DrawingEditor> editorProperty() {
        return editor;
    }


    @NonNull
    public ObjectProperty<DrawingView> drawingViewProperty() {
        return drawingView;
    }

    @Nullable
    public Handle findHandle(double vx, double vy) {
        if (recreateHandles) {
            return null;
        }
        final double tolerance = getEditor().getTolerance();
        for (Map.Entry<Node, Handle> e : new ReversedList<>(nodeToHandleMap.entrySet())) {
            final Node node = e.getKey();
            final Handle handle = e.getValue();
            if (!handle.isSelectable()) {
                continue;
            }
            if (handle.contains(getDrawingViewNonNull(), vx, vy, tolerance)) {
                return handle;
            } else {
                if (false) {
                    if (contains(node, new Point2D(vx, vy), tolerance)) {
                        return handle;
                    }
                }
            }
        }
        return null;
    }

    private DrawingView getDrawingViewNonNull() {
        return Objects.requireNonNull(drawingView.get());
    }

    DrawingEditor getEditor() {
        return editorProperty().get();
    }

    @NonNull

    public Set<Figure> getFiguresWithCompatibleHandle(@NonNull Collection<Figure> figures, Handle master) {
        validateHandles();
        Map<Figure, Figure> result = new HashMap<>();
        for (Map.Entry<Figure, List<Handle>> entry : handles.entrySet()) {
            if (figures.contains(entry.getKey())) {
                for (Handle h : entry.getValue()) {
                    if (h.isCompatible(master)) {
                        result.put(entry.getKey(), null);
                        break;
                    }
                }
            }
        }
        return result.keySet();
    }

    public Node getNode() {
        return handlesPane;
    }

    ObservableSet<Figure> getSelectedFigures() {
        return selectedFiguresProperty().get();
    }

    private void invalidateFigureNode(Figure f) {
        if (handles.containsKey(f)) {
            handlesAreValid = false;
            dirtyHandles.add(f);
        }
    }

    private void invalidateHandleNodes() {
        handlesAreValid = false;
        dirtyHandles.addAll(handles.keySet());
    }

    public void invalidateHandles() {
        handlesAreValid = false;
    }

    public void revalidateHandles() {
        if (handlesAreValid) {
            handlesAreValid = false;
            repaint();
        }
    }

    public void jiggleHandles() {
        validateHandles();
        List<Handle> copiedList = handles.values().stream().flatMap(List::stream).collect(Collectors.toList());

        // We scale the handles back and forth.
        double amount = 0.1;
        Transition flash = new Transition() {
            {
                setCycleDuration(Duration.millis(100));
                setCycleCount(2);
                setAutoReverse(true);
            }

            @Override
            protected void interpolate(double frac) {
                for (Handle h : copiedList) {
                    Node node = h.getNode(getDrawingViewNonNull());
                    node.setScaleX(1 + frac * amount);
                    node.setScaleY(1 + frac * amount);
                }
            }
        };
        flash.play();
    }

    public Property<DrawingModel> modelProperty() {
        return model;
    }

    private void onClipBoundsChanged(Observable observable) {
        invalidateHandles();
        repaint();
    }

    private void onDrawingModelChanged(@Nullable DrawingModel oldValue, @Nullable DrawingModel newValue) {
        if (oldValue != null) {
            oldValue.removeTreeModelListener(treeModelListener);
        }
        if (newValue != null) {
            newValue.addTreeModelListener(treeModelListener);
            onRootChanged();
        }
    }

    private void onDrawingModelEvent(DrawingModelEvent drawingModelEvent) {
    }

    private void onFigureRemoved(@NonNull Figure figure) {
        invalidateHandles();
    }

    private void onRootChanged() {
        //clearSelection() // is performed by DrawingView
        repaint();
    }

    private void onSubtreeNodesChanged(@NonNull Figure figure) {
        for (Figure f : figure.preorderIterable()) {
            dirtyHandles.add(f);
        }
    }

    private void onTreeModelEvent(TreeModelEvent<Figure> event) {
        Figure f = event.getNode();
        switch (event.getEventType()) {
        case NODE_ADDED_TO_PARENT:
            break;
        case NODE_REMOVED_FROM_PARENT:
            onFigureRemoved(f);
            break;
        case NODE_ADDED_TO_TREE:
            break;
        case NODE_REMOVED_FROM_TREE:
            break;
        case NODE_CHANGED:
            onNodeChanged(f);
            break;
        case ROOT_CHANGED:
            onRootChanged();
            break;
        case SUBTREE_NODES_CHANGED:
            onSubtreeNodesChanged(f);
            break;
        default:
            throw new UnsupportedOperationException(event.getEventType()
                    + " not supported");
        }
    }

    private void onNodeChanged(Figure f) {
        if (selectedFigures.contains(f)) {
            dirtyHandles.add(f);
            revalidateHandles();
        }
    }

    private void onVisibleRectChanged(Observable o) {
        invalidateHandles();
        repaint();
    }

    private void onZoomFactorChanged(ObservableValue<? extends Number> observable, Number oldValue, @NonNull Number newValue) {
        invalidateHandleNodes();
        repaint();
    }

    public void recreateHandles() {
        handlesAreValid = false;
        recreateHandles = true;
        repaint();
    }

    public void repaint() {
        if (repainter == null) {
            repainter = () -> {
                repainter = null;// must be set at the beginning, because we may need to repaint again
                //updateRenderContext();
                validateHandles();
            };
            Platform.runLater(repainter);
        }
    }

    /**
     * The selected figures.
     * <p>
     * Note: The selection is represent by a {@code LinkedHasSet} because the
     * sequence of the selection is important.
     *
     * @return a list of the selected figures
     */
    @NonNull ReadOnlySetProperty<Figure> selectedFiguresProperty() {
        return selectedFigures;
    }

    public void setDrawingView(SimpleDrawingViewNew newValue) {
        drawingView.set(newValue);
    }

    private void updateHandles() {
        if (recreateHandles) {
            // FIXME - We create and destroy many handles here!!!
            for (Map.Entry<Figure, List<Handle>> entry : handles.entrySet()) {
                for (Handle h : entry.getValue()) {
                    h.dispose();
                }
            }
            nodeToHandleMap.clear();
            handles.clear();
            handlesPane.getChildren().clear();
            dirtyHandles.clear();

            createHandles(handles);
            recreateHandles = false;


            // Bounds visibleRect = getVisibleRect();

            for (Map.Entry<Figure, List<Handle>> entry : handles.entrySet()) {
                for (Handle handle : entry.getValue()) {
                    Node n = handle.getNode(getDrawingViewNonNull());
                    handle.updateNode(getDrawingViewNonNull());
                    //  if (visibleRect.intersects(n.getBoundsInParent())) {
                    if (nodeToHandleMap.put(n, handle) == null) {
                        handlesPane.getChildren().add(n);
                    }
                    //  }
                }
            }
        } else {
            Figure[] copyOfDirtyHandles = dirtyHandles.toArray(new Figure[0]);
            dirtyHandles.clear();
            for (Figure f : copyOfDirtyHandles) {
                List<Handle> hh = handles.get(f);
                if (hh != null) {
                    for (Handle h : hh) {
                        h.updateNode(getDrawingViewNonNull());
                    }
                }
            }
        }

        for (Handle h : secondaryHandles) {
            h.updateNode(getDrawingViewNonNull());
        }
    }

    private void updateLayout() {
        invalidateHandleNodes();
    }

    /**
     * Validates the handles.
     */
    private void validateHandles() {
        // Validate handles only, if they are invalid/*, and if
        // the DrawingView has a DrawingEditor.*/
        if (!handlesAreValid) {
            handlesAreValid = true;
            updateHandles();
        }
    }

    public void setSelectedFigures(ObservableSet<Figure> selectedFigures) {
        this.selectedFigures.set(selectedFigures);
    }


}

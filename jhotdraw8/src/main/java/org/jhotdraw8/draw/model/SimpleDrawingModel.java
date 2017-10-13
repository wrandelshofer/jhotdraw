/* @(#)SimpleDrawingModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.model;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.FigurePropertyChangeEvent;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.FigureKey;
import static org.jhotdraw8.draw.model.DrawingModel.ROOT_PROPERTY;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.graph.DirectedGraphBuilder;
import org.jhotdraw8.graph.DirectedGraphs;
import org.jhotdraw8.tree.TreeModelEvent;

/**
 * A DrawingModel for drawings which contains {@code TransformableFigure}s and
 * layout observing figures, like {@code LineConnectionFigure}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawingModel extends AbstractDrawingModel {

    public SimpleDrawingModel() {
        this.listenOnDrawing = false;
    }

    public SimpleDrawingModel(boolean listenOnDrawing) {
        this.listenOnDrawing = listenOnDrawing;
    }

    @Override
    public void invalidated() {
        // empty
    }

    private class MapProxy extends AbstractMap<Key<?>, Object> {

        private Map<Key<?>, Object> target = null;
        private Figure figure = null;

        @Override
        public Set<Entry<Key<?>, Object>> entrySet() {
            // FIXME should listen on changes of the entry set!
            return target.entrySet();
        }

        public Figure getFigure() {
            return figure;
        }

        public void setFigure(Figure figure) {
            this.figure = figure;
        }

        public Map<Key<?>, Object> getTarget() {
            return target;
        }

        public void setTarget(Map<Key<?>, Object> target) {
            this.target = target;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object put(Key<?> key, Object value) {
            Object oldValue = target.put(key, value);
            onPropertyChanged(figure, (Key<Object>) key, oldValue, oldValue);
            return oldValue;
        }

    }
    private MapProxy mapProxy = new MapProxy();

    private boolean isValidating = false;
    private boolean valid = true;
    private Map<Figure, DirtyMask> dirties = new LinkedHashMap<>();
    private final Listener<FigurePropertyChangeEvent> propertyChangeHandler = this::onPropertyChanged;
    private final ObjectProperty<Drawing> root = new SimpleObjectProperty<Drawing>(this, ROOT_PROPERTY) {
        @Override
        public void set(Drawing newValue) {
            Drawing oldValue = get();
            if (newValue == null && oldValue != null) {
                throw new IllegalArgumentException("null");
            }
            super.set(newValue);
            onRootChanged(oldValue, newValue);
        }
    };
    private BiFunction<? super DirtyMask, ? super DirtyMask, ? extends DirtyMask> mergeDirtyMask
            = (a, b) -> a.add(b);

    private void invalidate() {
        if (valid) {
            valid = false;
            fireDrawingModelInvalidated();
        }
    }
    private final boolean listenOnDrawing;

    private void onRootChanged(Drawing oldValue, Drawing newValue) {
        if (listenOnDrawing) {
            if (oldValue != null) {
                oldValue.getPropertyChangeListeners().remove(propertyChangeHandler);
            }
            if (newValue != null) {
                newValue.getPropertyChangeListeners().add(propertyChangeHandler);
            }
        }
        fireTreeModelEvent(TreeModelEvent.rootChanged(this, newValue));
    }
    private Set<Figure> layoutSubjectChange = new HashSet<>();

    @SuppressWarnings("unchecked")
    private void onPropertyChanged(FigurePropertyChangeEvent event) {
        if (event.getType() == FigurePropertyChangeEvent.EventType.WILL_CHANGE) {
            Key<?> k = event.getKey();
            if (k instanceof FigureKey && ((FigureKey<?>) k).getDirtyMask().containsOneOf(DirtyBits.LAYOUT_SUBJECT)) {
                layoutSubjectChange.clear();
                layoutSubjectChange.addAll(event.getSource().getLayoutSubjects());
            }
        }
        if (event.getType() == FigurePropertyChangeEvent.EventType.CHANGED) {
            fireDrawingModelEvent(DrawingModelEvent.propertyValueChanged(this, event.getSource(),
                    (Key<Object>) event.getKey(), event.getOldValue(),
                    event.getNewValue()));
            Key<?> k = event.getKey();
            if (k instanceof FigureKey && ((FigureKey<?>) k).getDirtyMask().containsOneOf(DirtyBits.LAYOUT_SUBJECT)) {
                // The layout subject may change its style if a layout observer is added/removed
                if (event.getOldValue() instanceof Figure) {
                    fireStyleInvalidated((Figure) event.getOldValue());
                }
                if (event.getNewValue() instanceof Figure) {
                    fireStyleInvalidated((Figure) event.getNewValue());
                }
                fireDrawingModelEvent(DrawingModelEvent.layoutSubjectChanged(this, event.getSource()));
                layoutSubjectChange.addAll(event.getSource().getLayoutSubjects());
                for (Figure f : new ArrayList<>(layoutSubjectChange)) {
                    fireDrawingModelEvent(DrawingModelEvent.layoutSubjectChanged((DrawingModel) this, f));
                }
                layoutSubjectChange.clear();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void onPropertyChanged(Figure figure, Key<T> key, T oldValue, T newValue) {
        {
            if (key instanceof FigureKey && ((FigureKey<?>) key).getDirtyMask().containsOneOf(DirtyBits.LAYOUT_SUBJECT)) {
                layoutSubjectChange.clear();
                layoutSubjectChange.addAll(figure.getLayoutSubjects());
            }
        }
        {
            fireDrawingModelEvent(DrawingModelEvent.propertyValueChanged(this, figure,
                    (Key<Object>) key, oldValue, newValue));
            if (key instanceof FigureKey && ((FigureKey<?>) key).getDirtyMask().containsOneOf(DirtyBits.LAYOUT_SUBJECT)) {
                fireDrawingModelEvent(DrawingModelEvent.layoutSubjectChanged(this, figure));
                layoutSubjectChange.addAll(figure.getLayoutSubjects());
                for (Figure f : new ArrayList<>(layoutSubjectChange)) {
                    fireDrawingModelEvent(DrawingModelEvent.layoutSubjectChanged((DrawingModel) this, f));
                }
                layoutSubjectChange.clear();
            }
        }
    }

    private void markDirty(Figure figure, DirtyBits... bits) {
        dirties.merge(figure, DirtyMask.of(bits), mergeDirtyMask);
    }

    private void markDirty(Figure figure, DirtyMask mask) {
        dirties.merge(figure, mask, mergeDirtyMask);
    }

    private void removeDirty(Figure figure) {
        dirties.remove(figure);
    }

    @Override
    public ObjectProperty<Drawing> drawingProperty() {
        return root;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ObjectProperty<Figure> rootProperty() {
        return (ObjectProperty<Figure>) (ObjectProperty<?>) root;
    }

    @Override
    public void removeFromParent(Figure child) {
        Figure oldRoot = child.getRoot();
        Figure parent = child.getParent();
        if (parent != null) {
            int index = parent.getChildren().indexOf(child);
            if (index != -1) {
                parent.getChildren().remove(index);
                fireTreeModelEvent(TreeModelEvent.nodeRemovedFromParent((DrawingModel) this, child, parent, index));
                fireTreeModelEvent(TreeModelEvent.nodeInvalidated((DrawingModel) this, parent));
            }
        }
        Figure newRoot = child.getRoot();
        if (oldRoot != newRoot) {
            if (oldRoot != null) {
                for (Figure f : child.preorderIterable()) {
                    fireTreeModelEvent(TreeModelEvent.nodeRemovedFromTree((DrawingModel) this, oldRoot, f));
                }
            }
            if (newRoot != null) { // must be null!!!
                for (Figure f : child.preorderIterable()) {
                    fireTreeModelEvent(TreeModelEvent.nodeAddedToTree((DrawingModel) this, newRoot, f));
                }
            }
        }
    }

    @Override
    public void insertChildAt(Figure child, Figure parent, int index) {
        Figure oldRoot = child.getRoot();
        Figure oldParent = child.getParent();
        if (oldParent != null) {
            int oldChildIndex = oldParent.getChildren().indexOf(child);
            oldParent.removeChild(child);
            fireTreeModelEvent(TreeModelEvent.nodeRemovedFromParent((DrawingModel) this, child, oldParent, oldChildIndex));
            fireTreeModelEvent(TreeModelEvent.nodeInvalidated((DrawingModel) this, oldParent));
        }
        parent.getChildren().add(index, child);
        Figure newRoot = child.getRoot();
        if (oldRoot != newRoot) {
            if (oldRoot != null) {
                for (Figure f : child.preorderIterable()) {
                    fireTreeModelEvent(TreeModelEvent.nodeRemovedFromTree((DrawingModel) this, oldRoot, f));
                }
            }
            if (newRoot != null) {
                for (Figure f : child.preorderIterable()) {
                    fireTreeModelEvent(TreeModelEvent.nodeAddedToTree((DrawingModel) this, newRoot, f));
                }
            }
        }
        fireTreeModelEvent(TreeModelEvent.nodeAddedToParent((DrawingModel) this, child, parent, index));
        fireTreeModelEvent(TreeModelEvent.nodeInvalidated((DrawingModel) this, parent));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T set(Figure figure, MapAccessor<T> key, T newValue) {
        if (key instanceof Key<?>) {
            T oldValue = figure.set(key, newValue);
            // event will be fired by method onPropertyChanged
            onPropertyChanged(figure, (Key<Object>) key, oldValue, newValue);
            return oldValue;
        } else {
            mapProxy.setFigure(figure);
            mapProxy.setTarget(figure.getProperties());
            T oldValue = key.put(mapProxy, newValue);
            // event will be fired by mapProxy
            mapProxy.setFigure(null);
            mapProxy.setTarget(null);
            return oldValue;
        }

    }

    @Override
    public <T> T remove(Figure figure, Key<T> key) {
        T oldValue = figure.remove(key);
        // event will be fired by method onPropertyChanged
        onPropertyChanged(figure, key, oldValue, key.getDefaultValue());
        return oldValue;
    }

    @Override
    public void reshapeInLocal(Figure f, Transform transform) {
        f.reshapeInLocal(transform);
        fireDrawingModelEvent(DrawingModelEvent.layoutChanged(this, f));
    }

    @Override
    public void reshapeInParent(Figure f, Transform transform) {
        f.reshapeInParent(transform);
        fireDrawingModelEvent(DrawingModelEvent.layoutChanged(this, f));
    }

    @Override
    public void transformInParent(Figure f, Transform transform) {
        f.transformInParent(transform);
        fireDrawingModelEvent(DrawingModelEvent.transformChanged(this, f));
    }

    @Override
    public void transformInLocal(Figure f, Transform transform) {
        f.transformInLocal(transform);
        fireDrawingModelEvent(DrawingModelEvent.transformChanged(this, f));
    }

    @Override
    public void reshape(Figure f, double x, double y, double width, double height) {
        f.reshapeInLocal(x, y, width, height);
        fireDrawingModelEvent(DrawingModelEvent.layoutChanged(this, f));
    }

    @Override
    public void layout(Figure f) {
        f.layoutNotify();
        fireDrawingModelEvent(DrawingModelEvent.layoutChanged(this, f));
    }

    @Override
    public void disconnect(Figure f) {
        f.disconnect();
    }

    @Override
    public void updateCss(Figure figure) {
        figure.stylesheetNotify();
    }

    private void transitivelyCollectDependentFigures(Collection<Figure> todo, Set<Figure> done) {
        while (true) {
            List<Figure> todoNext = new ArrayList<>();
            for (Figure figure : todo) {
                for (Figure d : figure.getLayoutObservers()) {
                    if (done.add(d)) {
                        todoNext.add(d);
                    }
                }
            }
            if (todoNext.isEmpty()) {
                break;
            } else {
                todo = todoNext;
            }
        }
    }

    private void collectLayoutableAncestors(Collection<Figure> todo, Set<Figure> done) {
        for (Figure figure : todo) {
            for (Figure ancestor = figure; ancestor != null && ancestor.isLayoutable(); ancestor = ancestor.getParent()) {
                if (!done.add(ancestor)) {
                    // The ancestor must be laid out last
                    done.remove(ancestor);
                    done.add(ancestor);
                }
            }
        }
    }

    @Override
    public void validate() {
        if (!valid) {
            isValidating = true;

            // all figures with dirty bit "STYLE"
            // invoke stylesheetNotify
            // induce a dirty bit "TRANSFORM", "NODE" and "LAYOUT
            final Set<Figure> visited = new HashSet<>((int)(dirties.size()*1.4));
            DirtyMask dmStyle = DirtyMask.of(DirtyBits.STYLE);
            for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
                Figure f = entry.getKey();
                DirtyMask dm = entry.getValue();
                if (dm.intersects(dmStyle) && visited.add(f)) {
                    f.stylesheetNotify();
                    markDirty(f, DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.LAYOUT);
                }
            }

            // all figures with dirty bit "TRANSFORM"
            // induce dirty bits "TRANSFORM" and "LAYOUT_OBSERVERS" on all descendants which implement the TransformableFigure interface.
            visited.clear();
            DirtyMask dmTransform = DirtyMask.of(DirtyBits.TRANSFORM);
            for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
                Figure f = entry.getKey();
                DirtyMask dm = entry.getValue();
                if (dm.intersects(dmTransform) && visited.add(f)) {
                    for (Figure a : f.preorderIterable()) {
                        if (visited.add(a)) {
                            if (a instanceof TransformableFigure) {
                                markDirty(a, DirtyBits.TRANSFORM, DirtyBits.LAYOUT_OBSERVERS);
                            }
                        }
                    }
                }
            }
            // all figures with dirty bit "TRANSFORM"
            // invoke transformNotify
            for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
                Figure f = entry.getKey();
                DirtyMask dm = entry.getValue();
                if (dm.intersects(dmTransform)) {
                    f.transformNotify();
                }
            }

            // all figures with dirty bit "LAYOUT" must be laid out
            // all observers of figures with dirty bit "LAYOUT_OBBSERVERS" must be laid out.
            visited.clear();
            DirtyMask dmLayout = DirtyMask.of(DirtyBits.LAYOUT);
            DirtyMask dmLayoutObservers = DirtyMask.of(DirtyBits.LAYOUT_OBSERVERS);
            Set<Figure> todo = new LinkedHashSet<>(dirties.size()); // FIXME will probably be more than dirties.size!
            for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
                Figure f = entry.getKey();
                DirtyMask dm = entry.getValue();
                if (dm.intersects(dmLayout) && visited.add(f)) {
                    for (Figure a : f.preorderIterable()) {
                        todo.add(a);
                    }
                } else if (dm.intersects(dmLayoutObservers) && visited.add(f)) {
                    for (Figure obs : f.getLayoutObservers()) {
                        todo.add(obs);
                    }
                }
            }
            // build a graph which includes all figures that must be laid out and all their observers
            // transitively
            visited.clear();
            DirectedGraphBuilder<Figure> graphBuilder = new DirectedGraphBuilder<>();
            while (!todo.isEmpty()) {
                Figure f = todo.iterator().next();
                todo.remove(f);
                if (visited.add(f)) {
                    graphBuilder.addVertex(f);
                    for (Figure obs : f.getLayoutObservers()) {
                        graphBuilder.addVertex(obs);
                        graphBuilder.addEdge(f, obs);
                        todo.add(obs);
                    }
                }
            }
            visited.clear();
            if (graphBuilder.getVertexCount() > 0) {
                for (Figure f : DirectedGraphs.sortTopologically(graphBuilder)) {
                    if (visited.add(f)) {
                        f.layoutNotify();
                        markDirty(f, DirtyBits.NODE);
                    }
                }
            }

            // For all figures with dirty flag Node 
            // we must fireNodeInvalidated node invalidation
            DirtyMask dmNode = DirtyMask.of(DirtyBits.NODE);
            for (Map.Entry<Figure, DirtyMask> entry : dirties.entrySet()) {
                Figure f = entry.getKey();
                DirtyMask dm = entry.getValue();
                if (dm.intersects(dmNode)) {
                    fireNodeInvalidated(f);
                }
            }

            for (Map.Entry<Figure, DirtyMask> entry : dirties.entrySet()) {
                Figure f = entry.getKey();
                DirtyMask dm = entry.getValue();
                if (dm.intersects(dmTransform)) {
                    f.transformNotify();
                }
            }
            dirties.clear();

            isValidating = false;
            valid = true;
        }
    }

    @Override
    public void fireDrawingModelEvent(DrawingModelEvent event) {
        super.fireDrawingModelEvent(event);
        handleDrawingModelEvent(event);
    }

    @Override
    public void fireTreeModelEvent(TreeModelEvent<Figure> event) {
        super.fireTreeModelEvent(event);
        handleTreeModelEvent(event);
    }

    protected void handleDrawingModelEvent(DrawingModelEvent event) {
        if (isValidating) {
            return;
        }

        final Figure figure = event.getNode();

        switch (event.getEventType()) {
            case LAYOUT_SUBJECT_CHANGED:
                figure.layoutSubjectChangedNotify();
                markDirty(figure, DirtyBits.LAYOUT_OBSERVERS);
                invalidate();
                break;
            case TRANSFORM_CHANGED:
                markDirty(figure, DirtyBits.TRANSFORM);
                invalidate();
                break;
            case PROPERTY_VALUE_CHANGED: {
                Key<?> key = event.getKey();
                if (key instanceof FigureKey) {
                    FigureKey<?> fk = (FigureKey<?>) key;
                    final DirtyMask dm = fk.getDirtyMask();
                    if (!dm.isEmpty()) {
                        markDirty(figure, dm);
                        invalidate();
                    }
                }
                break;
            }
            case LAYOUT_CHANGED:
                // A layout change also changes the transform of the figure, because its center may have moved
                markDirty(figure, DirtyBits.LAYOUT, DirtyBits.TRANSFORM);
                invalidate();
                break;
            case STYLE_CHANGED:
                markDirty(figure, DirtyBits.STYLE);
                invalidate();
                break;
            default:
                throw new UnsupportedOperationException(event.getEventType()
                        + "not supported");
        }
    }

    protected void handleTreeModelEvent(TreeModelEvent<Figure> event) {
        if (isValidating) {
            return;
        }

        final Figure figure = event.getNode();

        switch (event.getEventType()) {
            case NODE_ADDED_TO_PARENT:
                markDirty(figure, DirtyBits.LAYOUT, DirtyBits.STYLE);
                invalidate();
                break;
            case NODE_ADDED_TO_TREE:
                if (event.getRoot() instanceof Drawing)
                figure.addNotify((Drawing) event.getRoot());
                break;
            case NODE_REMOVED_FROM_TREE:
                if (event.getRoot() instanceof Drawing)
                figure.removeNotify((Drawing) event.getRoot());
                removeDirty(figure);
                break;
            case NODE_REMOVED_FROM_PARENT:
                markDirty(event.getParent(), DirtyBits.LAYOUT_OBSERVERS, DirtyBits.NODE);
                invalidate();
                break;
            case NODE_CHANGED:
                break;
            case ROOT_CHANGED:
                dirties.clear();
                valid = true;
                break;
            case SUBTREE_NODES_CHANGED:
                break;
            default:
                throw new UnsupportedOperationException(event.getEventType()
                        + "not supported");
        }
    }

    private void recursivelyInvalidateTransforms(Figure f) {
        if (f.transformNotify()) {
            for (Figure child : f.getChildren()) {
                recursivelyInvalidateTransforms(child);
            }
        }
    }
}

/* @(#)SimpleDrawingModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.FigurePropertyChangeEvent;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.graph.DirectedGraphBuilder;
import org.jhotdraw8.graph.GraphSearch;
import org.jhotdraw8.tree.TreeModelEvent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

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

        @Nullable
        private Map<Key<?>, Object> target = null;
        @Nullable
        private Figure figure = null;

        @Nonnull
        @Override
        public Set<Entry<Key<?>, Object>> entrySet() {
            // FIXME should listen on changes of the entry set!
            return target.entrySet();
        }

        @Nullable
        public Figure getFigure() {
            return figure;
        }

        public void setFigure(Figure figure) {
            this.figure = figure;
        }

        @Nullable
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
            handlePropertyChanged(figure, (Key<Object>) key, oldValue, oldValue);
            return oldValue;
        }

    }

    @Nonnull
    private MapProxy mapProxy = new MapProxy();

    private boolean isValidating = false;
    private boolean valid = true;
    @Nonnull
    private Map<Figure, DirtyMask> dirties = new LinkedHashMap<>();
    private final Listener<FigurePropertyChangeEvent> propertyChangeHandler = this::handlePropertyChanged;
    @Nullable
    private final ObjectProperty<Drawing> root = new SimpleObjectProperty<Drawing>(this, ROOT_PROPERTY) {
        @Override
        public void set(@Nullable Drawing newValue) {
            Drawing oldValue = get();
            if (newValue == null && oldValue != null) {
                throw new IllegalArgumentException("null");
            }
            super.set(newValue);
            onRootChanged(oldValue, newValue);
        }
    };
    @Nonnull
    private BiFunction<? super DirtyMask, ? super DirtyMask, ? extends DirtyMask> mergeDirtyMask
            = (a, b) -> a.add(b);

    private void invalidate() {
        if (valid) {
            valid = false;
            fireDrawingModelInvalidated();
        }
    }

    private final boolean listenOnDrawing;

    private void onRootChanged(@Nullable Drawing oldValue, @Nullable Drawing newValue) {
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

    @Nonnull
    private Set<Figure> layoutSubjectChange = new HashSet<>();

    @SuppressWarnings("unchecked")
    private void handlePropertyChanged(FigurePropertyChangeEvent event) {
        if (!Objects.equals(event.getOldValue(), event.getNewValue())) {
            fireDrawingModelEvent(DrawingModelEvent.propertyValueChanged(this, event.getSource(),
                    (Key<Object>) event.getKey(), event.getOldValue(),
                    event.getNewValue()));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void handlePropertyChanged(Figure figure, Key<T> key, @Nullable T oldValue, @Nullable T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            fireDrawingModelEvent(DrawingModelEvent.propertyValueChanged(this, figure,
                    (Key<Object>) key, oldValue, newValue));

        }
    }

    private void markDirty(Figure figure, DirtyBits... bits) {
        dirties.merge(figure, DirtyMask.of(bits), mergeDirtyMask);
    }

    private void markDirty(Figure figure, @Nonnull DirtyMask mask) {
        dirties.merge(figure, mask, mergeDirtyMask);
    }

    private void removeDirty(Figure figure) {
        dirties.remove(figure);
    }

    @Nullable
    @Override
    public ObjectProperty<Drawing> drawingProperty() {
        return root;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public ObjectProperty<Figure> rootProperty() {
        return (ObjectProperty<Figure>) (ObjectProperty<?>) root;
    }

    @Override
    public void removeFromParent(@Nonnull Figure child) {
        Figure oldRoot = child.getRoot();
        Figure parent = child.getParent();
        if (parent != null) {
            int index = parent.getChildren().indexOf(child);
            if (index != -1) {
                parent.getChildren().remove(index);
                fireTreeModelEvent(TreeModelEvent.nodeRemovedFromParent(this, child, parent, index));
                fireTreeModelEvent(TreeModelEvent.nodeInvalidated(this, parent));
            }
        }
        Figure newRoot = child.getRoot();
        if (oldRoot != newRoot) {
            if (oldRoot != null) {
                for (Figure f : child.preorderIterable()) {
                    fireTreeModelEvent(TreeModelEvent.nodeRemovedFromTree(this, oldRoot, f));
                }
            }
            if (newRoot != null) { // must be null!!!
                for (Figure f : child.preorderIterable()) {
                    fireTreeModelEvent(TreeModelEvent.nodeAddedToTree(this, newRoot, f));
                }
            }
        }
    }

    @Override
    public void insertChildAt(@Nonnull Figure child, @Nonnull Figure parent, int index) {
        Figure oldRoot = child.getRoot();
        Figure oldParent = child.getParent();
        if (oldParent != null) {
            int oldChildIndex = oldParent.getChildren().indexOf(child);
            oldParent.removeChild(child);
            fireTreeModelEvent(TreeModelEvent.nodeRemovedFromParent(this, child, oldParent, oldChildIndex));
            fireTreeModelEvent(TreeModelEvent.nodeInvalidated(this, oldParent));
        }
        parent.getChildren().add(index, child);
        Figure newRoot = child.getRoot();
        if (oldRoot != newRoot) {
            if (oldRoot != null) {
                for (Figure f : child.preorderIterable()) {
                    fireTreeModelEvent(TreeModelEvent.nodeRemovedFromTree(this, oldRoot, f));
                }
            }
            if (newRoot != null) {
                for (Figure f : child.preorderIterable()) {
                    fireTreeModelEvent(TreeModelEvent.nodeAddedToTree(this, newRoot, f));
                }
            }
        }
        fireTreeModelEvent(TreeModelEvent.nodeAddedToParent(this, child, parent, index));
        fireTreeModelEvent(TreeModelEvent.nodeInvalidated(this, parent));
    }

    @Override
    @Nonnull
    public <T> T setNonnull(@Nonnull Figure figure, @Nonnull NonnullMapAccessor<T> key, @Nonnull T newValue) {
        T v = set(figure, key, newValue);
        if (v == null) {
            throw new NullPointerException("return value is null");
        }
        return v;
    }

    @Override
    public <T> T set(@Nonnull Figure figure, @Nonnull MapAccessor<T> key, @Nullable T newValue) {
        if (key instanceof Key<?>) {
            T oldValue = figure.set(key, newValue);
            // event will be fired by method handlePropertyChanged if newValue differs from oldValue
            @SuppressWarnings("unchecked")
            Key<Object> keyObject = (Key<Object>) key;
            handlePropertyChanged(figure, keyObject, oldValue, newValue);
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
    public <T> T remove(@Nonnull Figure figure, @Nonnull Key<T> key) {
        T oldValue = figure.remove(key);
        // event will be fired by method handlePropertyChanged
        handlePropertyChanged(figure, key, oldValue, key.getDefaultValue());
        return oldValue;
    }

    @Override
    public void reshapeInLocal(@Nonnull Figure f, Transform transform) {
        f.reshapeInLocal(transform);
        fireDrawingModelEvent(DrawingModelEvent.layoutChanged(this, f));
    }

    @Override
    public void reshapeInParent(@Nonnull Figure f, Transform transform) {
        f.reshapeInParent(transform);
        fireDrawingModelEvent(DrawingModelEvent.layoutChanged(this, f));
    }

    @Override
    public void translateInParent(@Nonnull Figure f, CssPoint2D delta) {
        f.translateInParent(delta);
        fireDrawingModelEvent(DrawingModelEvent.layoutChanged(this, f));
    }

    @Override
    public void transformInParent(@Nonnull Figure f, Transform transform) {
        f.transformInParent(transform);
        fireDrawingModelEvent(DrawingModelEvent.transformChanged(this, f));
    }

    @Override
    public void transformInLocal(@Nonnull Figure f, Transform transform) {
        f.transformInLocal(transform);
        fireDrawingModelEvent(DrawingModelEvent.transformChanged(this, f));
    }

    @Override
    public void reshapeInLocal(@Nonnull Figure f, double x, double y, double width, double height) {
        f.reshapeInLocal(x, y, width, height);
        fireDrawingModelEvent(DrawingModelEvent.layoutChanged(this, f));
    }

    @Override
    public void reshapeInLocal(@Nonnull Figure f, CssSize x, CssSize y, CssSize width, CssSize height) {
        f.reshapeInLocal(x, y, width, height);
        fireDrawingModelEvent(DrawingModelEvent.layoutChanged(this, f));
    }

    @Override
    public void layout(@Nonnull Figure f, RenderContext ctx) {
        f.layoutNotify(ctx);
        fireDrawingModelEvent(DrawingModelEvent.layoutChanged(this, f));
    }

    @Override
    public void disconnect(@Nonnull Figure f) {
        f.disconnect();
    }

    @Override
    public void updateCss(@Nonnull Figure figure) {
        figure.stylesheetNotify(new SimpleRenderContext());
    }

    private void transitivelyCollectDependentFigures(Collection<Figure> todo, @Nonnull Set<Figure> done) {
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

    private void collectLayoutableAncestors(Collection<Figure> todo, @Nonnull Set<Figure> done) {
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
    public void validate(RenderContext ctx) {
        if (!valid) {
            isValidating = true;

            // all figures with dirty bit LAYOUT_SUBJECT
            // invoke layoutSubjectChangedNotify
            // all figures with dirty bit LAYOUT_OBSERVERS
            // invoke layoutSubjectChangedNotify
            DirtyMask dmLayoutSubject = DirtyMask.of(DirtyBits.LAYOUT_SUBJECT);
            DirtyMask dmLayoutObserversAddRemove = DirtyMask.of(DirtyBits.LAYOUT_OBSERVERS_ADDED_OR_REMOVED);
            for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
                DirtyMask dm = entry.getValue();
                if (dm.intersects(dmLayoutSubject)) {
                    Figure f = entry.getKey();
                    f.layoutSubjectChangedNotify();
                }
                if (dm.intersects(dmLayoutObserversAddRemove)) {
                    Figure f = entry.getKey();
                    f.layoutObserverChangedNotify();
                }
            }

            // all figures with dirty bit "STYLE"
            // invoke stylesheetNotify
            // induce a dirty bit "TRANSFORM", "NODE" and "LAYOUT
            final Set<Figure> visited = new HashSet<>((int) (dirties.size() * 1.4));
            DirtyMask dmStyle = DirtyMask.of(DirtyBits.STYLE);
            for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
                DirtyMask dm = entry.getValue();
                Figure f = entry.getKey();
                if (dm.intersects(dmStyle) && visited.add(f)) {
                    f.stylesheetNotify(ctx);
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

            // for all figures with dirty bit "LAYOUT" we must also update the node of their layoutable parents
            DirtyMask dmLayout = DirtyMask.of(DirtyBits.LAYOUT);
            for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
                Figure f = entry.getKey();
                DirtyMask dm = entry.getValue();
                if (dm.intersects(dmLayout)) {
                    for (Figure p : f.ancestorIterable()) {
                        if (p == f) {
                            continue;
                        }
                        if (p.isLayoutable()) {
                            markDirty(p, DirtyBits.LAYOUT, DirtyBits.NODE);
                        } else {
                            break;
                        }
                    }
                }
            }

            // all figures with dirty bit "LAYOUT" must be laid out
            // all observers of figures with dirty bit "LAYOUT_OBBSERVERS" must be laid out.
            // all layoutable parents must be laid out.
            visited.clear();
            DirtyMask dmLayoutObservers = DirtyMask.of(DirtyBits.LAYOUT_OBSERVERS);
            Set<Figure> todo = new LinkedHashSet<>(dirties.size()); // FIXME will probably be more than dirties.size!
            for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
                Figure f = entry.getKey();
                DirtyMask dm = entry.getValue();

                if (visited.add(f)) {
                    if (dm.intersects(dmLayout)) {
                        for (Figure a : f.preorderIterable()) {
                            todo.add(a);
                        }
                    } else if (dm.intersects(dmLayoutObservers)) {
                        todo.addAll(f.getLayoutObservers());
                    }
                }
            }
            // build a graph which includes all figures that must be laid out and all their observers
            // transitively
            visited.clear();
            DirectedGraphBuilder<Figure, Figure> graphBuilder = new DirectedGraphBuilder<>();
            while (!todo.isEmpty()) {
                Figure f = todo.iterator().next();
                todo.remove(f);
                if (visited.add(f)) {
                    graphBuilder.addVertex(f);
                    for (Figure obs : f.getLayoutObservers()) {
                        graphBuilder.addVertex(obs);
                        graphBuilder.addArrow(f, obs, f);
                        todo.add(obs);
                    }
                }
            }
            visited.clear();
            if (graphBuilder.getVertexCount() > 0) {
                for (Figure f : GraphSearch.sortTopologically(graphBuilder)) {
                    if (visited.add(f)) {
                        f.layoutNotify(ctx);
                        markDirty(f, DirtyBits.NODE);
                    }
                }
            }

            // For all figures with dirty flag Node
            // we must fireNodeInvalidated node
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
    public void fireDrawingModelEvent(@Nonnull DrawingModelEvent event) {
        super.fireDrawingModelEvent(event);
        handleDrawingModelEvent(event);
    }

    @Override
    public void fireTreeModelEvent(@Nonnull TreeModelEvent<Figure> event) {
        super.fireTreeModelEvent(event);
        handleTreeModelEvent(event);
    }

    protected void handleDrawingModelEvent(@Nonnull DrawingModelEvent event) {
        if (isValidating) {
            return;
        }

        final Figure figure = event.getNode();

        switch (event.getEventType()) {
            case TRANSFORM_CHANGED:
                markDirty(figure, DirtyBits.TRANSFORM);
                invalidate();
                break;
            case PROPERTY_VALUE_CHANGED: {
                Key<Object> key = (Key<Object>) event.getKey();
                Object oldValue = event.getOldValue();
                Object newValue = event.getNewValue();
                figure.propertyChangedNotify(key, oldValue, newValue);

                //final DirtyMask dm = fk.getDirtyMask().add(DirtyBits.STYLE);
                final DirtyMask dm = DirtyMask.of(DirtyBits.STYLE,
                        DirtyBits.LAYOUT, DirtyBits.NODE, DirtyBits.TRANSFORM,
                        DirtyBits.LAYOUT_OBSERVERS
                );
                    if (!dm.isEmpty()) {
                        markDirty(figure, dm);
                        invalidate();
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

    protected void handleTreeModelEvent(@Nonnull TreeModelEvent<Figure> event) {
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
                if (event.getRoot() instanceof Drawing) {
                    figure.addNotify((Drawing) event.getRoot());
                }
                break;
            case NODE_REMOVED_FROM_TREE:
                if (event.getRoot() instanceof Drawing) {
                    figure.removeNotify((Drawing) event.getRoot());
                }
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

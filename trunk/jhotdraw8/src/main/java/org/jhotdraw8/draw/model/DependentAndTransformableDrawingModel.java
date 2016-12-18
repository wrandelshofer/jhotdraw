/* @(#)DependentAndTransformableDrawingModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.FigurePropertyChangeEvent;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.FigureKey;
import static org.jhotdraw8.draw.model.DrawingModel.ROOT_PROPERTY;
import org.jhotdraw8.event.Listener;

/**
 * A DrawingModel for drawings which contains {@code TransformableFigure}s and
 * dependent figures, like {@code LineConnectionFigure}.
 *
 * @author Werner Randelshofer
 * @version $Id: DependentAndTransformableDrawingModel.java 1139 2016-09-17
 * 23:38:39Z rawcoder $
 */
public class DependentAndTransformableDrawingModel extends AbstractDrawingModel {

  private boolean isValidating = false;
  private boolean valid = true;
  private Map<Figure, DirtyMask> dirties = new LinkedHashMap<>();
  private final Listener<FigurePropertyChangeEvent> propertyChangeHandler = this::onPropertyChanged;
  private final ObjectProperty<Drawing> root = new SimpleObjectProperty<Drawing>(this, ROOT_PROPERTY) {
    @Override
    public void set(Drawing newValue) {
      Drawing oldValue = get();
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

  private void onRootChanged(Drawing oldValue, Drawing newValue) {
    if (oldValue != null) {
      oldValue.removePropertyChangeListener(propertyChangeHandler);
    }
    if (newValue != null) {
      newValue.addPropertyChangeListener(propertyChangeHandler);
    }
    fire(DrawingModelEvent.rootChanged(this, newValue));
  }
  private Set<Figure> dependencyChange = new HashSet<>();

  @SuppressWarnings("unchecked")
  private void onPropertyChanged(FigurePropertyChangeEvent event) {
    if (event.getType() == FigurePropertyChangeEvent.EventType.WILL_CHANGE) {
      Key<?> k = event.getKey();
      if (k instanceof FigureKey && ((FigureKey<?>) k).getDirtyMask().containsOneOf(DirtyBits.DEPENDENCY)) {
        dependencyChange.clear();
        dependencyChange.addAll(event.getSource().getProvidingFigures());
      }
    }
    if (event.getType() == FigurePropertyChangeEvent.EventType.CHANGED) {
      fire(DrawingModelEvent.propertyValueChanged(this, event.getSource(),
              (Key<Object>) event.getKey(), event.getOldValue(),
              event.getNewValue()));
      Key<?> k = event.getKey();
      if (k instanceof FigureKey && ((FigureKey<?>) k).getDirtyMask().containsOneOf(DirtyBits.DEPENDENCY)) {
        fire(DrawingModelEvent.dependencyChanged(this, event.getSource()));
        dependencyChange.addAll(event.getSource().getProvidingFigures());
        for (Figure f : new ArrayList<>(dependencyChange)) {
          fire(DrawingModelEvent.dependencyChanged((DrawingModel) this, f));
        }
        dependencyChange.clear();
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
  public ObjectProperty<Drawing> rootProperty() {
    return root;
  }

  @Override
  public void removeFromParent(Figure child) {
    Drawing oldDrawing = child.getDrawing();
    Figure parent = child.getParent();
    if (parent != null) {
      int index = parent.getChildren().indexOf(child);
      if (index != -1) {
        parent.getChildren().remove(index);
        fire(DrawingModelEvent.figureRemovedFromParent((DrawingModel) this, child, parent, index));
        fire(DrawingModelEvent.nodeInvalidated((DrawingModel) this, parent));
      }
    }
    Drawing newDrawing = child.getDrawing();
    if (oldDrawing != newDrawing) {
      if (oldDrawing != null) {
        fire(DrawingModelEvent.figureRemovedFromDrawing((DrawingModel) this, oldDrawing, child));
      }
      if (newDrawing != null) { // must be null!!!
        fire(DrawingModelEvent.figureAddedToDrawing((DrawingModel) this, newDrawing, child));
      }
    }
  }

  @Override
  public void insertChildAt(Figure child, Figure parent, int index) {
    Drawing oldDrawing = child.getDrawing();
    Figure oldParent = child.getParent();
    if (oldParent != null) {
      int oldChildIndex = oldParent.getChildren().indexOf(child);
      oldParent.remove(child);
      fire(DrawingModelEvent.figureRemovedFromParent((DrawingModel) this, child, oldParent, oldChildIndex));
      fire(DrawingModelEvent.nodeInvalidated((DrawingModel) this, oldParent));
    }
    parent.getChildren().add(index, child);
    Drawing newDrawing = child.getDrawing();
    if (oldDrawing != newDrawing) {
      if (oldDrawing != null) {
        fire(DrawingModelEvent.figureRemovedFromDrawing((DrawingModel) this, oldDrawing, child));
      }
      if (newDrawing != null) {
        fire(DrawingModelEvent.figureAddedToDrawing((DrawingModel) this, newDrawing, child));
      }
    }
    fire(DrawingModelEvent.figureAddedToParent((DrawingModel) this, child, parent, index));
    fire(DrawingModelEvent.nodeInvalidated((DrawingModel) this, parent));
  }

  @Override
  public <T> T set(Figure figure, MapAccessor<T> key, T newValue) {
    return figure.set(key, newValue);
    // event will be fired by method onPropertyChanged
  }
  @Override
  public void remove(Figure figure, Key<?> key) {
     figure.remove(key);
    // event will be fired by method onPropertyChanged
  }

  @Override
  public void reshapeInLocal(Figure f, Transform transform) {
    f.reshapeInLocal(transform);
    fire(DrawingModelEvent.layoutChanged(this, f));
  }
  @Override
  public void reshapeInParent(Figure f, Transform transform) {
    f.reshapeInParent(transform);
    fire(DrawingModelEvent.layoutChanged(this, f));
  }
  @Override
  public void transformInParent(Figure f, Transform transform) {
    f.transformInParent(transform);
    fire(DrawingModelEvent.transformChanged(this, f));
  }
  @Override
  public void transformInLocal(Figure f, Transform transform) {
    f.transformInLocal(transform);
    fire(DrawingModelEvent.transformChanged(this, f));
  }

  @Override
  public void reshape(Figure f, double x, double y, double width, double height) {
    f.reshape(x, y, width, height);
    fire(DrawingModelEvent.layoutChanged(this, f));
  }

  @Override
  public void layout(Figure f) {
    f.layoutNotify();
    fire(DrawingModelEvent.layoutChanged(this, f));
  }

  @Override
  public void disconnect(Figure f) {
    f.disconnect();
  }

  @Override
  public void updateCss(Figure f) {
    f.stylesheetNotify();
  }

  private void transitivelyCollectDependentFigures(Collection<Figure> todo, Set<Figure> done) {
    while (true) {
      List<Figure> todoNext = new ArrayList<>();
      for (Figure figure : todo) {
        for (Figure d : figure.getDependentFigures()) {
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

      // all figures with dirty bit "TRANSFORM" or "LAYOUT"
      // induce a dirty bit "TRANSFORM" on all ancestors which implement the TransformableFigure interface.
      DirtyMask dmTransformLayout = DirtyMask.of(DirtyBits.TRANSFORM, DirtyBits.LAYOUT);
      for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
        Figure f = entry.getKey();
        DirtyMask dm = entry.getValue();
        if (dm.intersects(dmTransformLayout)) {
          for (Figure a : f.ancestorIterable()) {
            if (a instanceof TransformableFigure) {
              markDirty(a, DirtyBits.NODE, DirtyBits.TRANSFORM);
            }
          }
        }
      }
      // all figures with dirty bit "TRANSFORM"
      // induce a dirty bit "DEPENDENT_LAYOUT" and "TRANSFORM_NOTIFY"  on all descendants
      DirtyMask dmTransform = DirtyMask.of(DirtyBits.TRANSFORM);
      DirtyMask dmTransformNotify = DirtyMask.of(DirtyBits.TRANSFORM_NOTIFY);
      for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
        Figure f = entry.getKey();
        DirtyMask dm = entry.getValue();
        if (dm.intersects(dmTransform) && ! dm.intersects(dmTransformNotify)) {
          for (Figure a : f.preorderIterable()) {
              markDirty(a, DirtyBits.DEPENDENT_LAYOUT, DirtyBits.TRANSFORM_NOTIFY);
          }
        }
      }
      // all figures with dirty bit "TRANSFORM_NOTIFY"
      // invoke transformNotify
      for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
        Figure f = entry.getKey();
        DirtyMask dm = entry.getValue();
        if (dm.intersects(dmTransformNotify)) {
            f.transformNotify();
        }
      }

      // all figures with dirty flag "LAYOUT"
      // induce a dirty flag "DEPENDENT_LAYOUT" on dependent figures,
      // and induce a dirty Flag "NODE" on ancestors which implement the TransformableFigure interface. // really??
      DirtyMask dmLayout = DirtyMask.of(DirtyBits.LAYOUT);
      List<Figure> todo = new ArrayList<>();
      for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
        Figure f = entry.getKey();
        DirtyMask dm = entry.getValue();
        if (dm.intersects(dmLayout)) {
          for (Figure subtree : f.preorderIterable()) {
            for (Figure d : subtree.getDependentFigures()) {
              todo.add(d);
            }
          }
          for (Figure a : f.ancestorIterable()) {
            if (a instanceof TransformableFigure) {
              markDirty(a, DirtyBits.NODE);
            }
          }
        }
        if (dm.intersects(DirtyBits.DEPENDENT_LAYOUT)) {
          todo.add(f);
        }
      }

      // all figures with dirty flag "DEPENDENT_LAYOUT" must be laid out
      // transitively, including all of their layoutable ancestors.
      // We must then fix the transformation matrices again.
      LinkedHashSet<Figure> transitive = new LinkedHashSet<>(todo);
      transitivelyCollectDependentFigures(todo, transitive);
      collectLayoutableAncestors(new ArrayList<>(transitive), transitive);
      for (Figure f : transitive) {
        this.layout(f);
        markDirty(f, DirtyBits.NODE);
      }

      DirtyMask dmStyle = DirtyMask.of(DirtyBits.STYLE);
      for (Map.Entry<Figure, DirtyMask> entry : new ArrayList<>(dirties.entrySet())) {
        Figure f = entry.getKey();
        DirtyMask dm = entry.getValue();
        if (dm.intersects(dmStyle)) {
          f.stylesheetNotify();
          for (Figure subf : f.preorderIterable()) {
            markDirty(subf, DirtyBits.NODE);
          }
        }
      }

      // For all figures with dirty flag Node 
      // we must fire node invalidation
      DirtyMask dmNode = DirtyMask.of(DirtyBits.NODE);
      for (Map.Entry<Figure, DirtyMask> entry : dirties.entrySet()) {
        Figure f = entry.getKey();
        DirtyMask dm = entry.getValue();
        if (dm.intersects(dmNode)) {
          fireNodeInvalidated(f);
        }
      }

      /*
            for (Map.Entry<Figure, DirtyMask> entry : dirties.entrySet()) {
                Figure f = entry.getKey();
                DirtyMask dm = entry.getValue();
                if (dm.intersects(dmTransform)) {
                    recursivelyInvalidateTransforms(f);
                }
            }*/
      dirties.clear();

      isValidating = false;
      valid = true;
    }
  }

  @Override
  public void fire(DrawingModelEvent event) {
    for (Listener<DrawingModelEvent> l : getDrawingModelListeners()) {
      l.handle(event);
    }
    handle(event);
  }

  protected void handle(DrawingModelEvent event) {
    if (isValidating) {
      return;
    }

    final Figure figure = event.getFigure();

    switch (event.getEventType()) {
      case FIGURE_ADDED_TO_PARENT:
        markDirty(figure, DirtyBits.LAYOUT, DirtyBits.STYLE);
        invalidate();
        break;
      case SUBTREE_ADDED_TO_DRAWING:
        figure.addNotify(event.getDrawing());
        break;
      case SUBTREE_REMOVED_FROM_DRAWING:
        figure.removeNotify(event.getDrawing());
        removeDirty(figure);
        break;
      case DEPENDENCY_CHANGED:
        figure.dependencyNotify();
        markDirty(figure, DirtyBits.DEPENDENT_LAYOUT);
        invalidate();
        break;
      case TRANSFORM_CHANGED:
        markDirty(figure, DirtyBits.TRANSFORM);
        invalidate();
        break;
      case FIGURE_REMOVED_FROM_PARENT:
        markDirty(event.getParent(), DirtyBits.DEPENDENT_LAYOUT, DirtyBits.NODE);
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
      }
      break;
      case LAYOUT_CHANGED:
        markDirty(figure, DirtyBits.LAYOUT);
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

  private void recursivelyInvalidateTransforms(Figure f) {
    System.out.println("D&TDrawingModel invalidateTransform: " + f.getId());
    if (f.transformNotify()) {
      for (Figure child : f.getChildren()) {
        recursivelyInvalidateTransforms(child);
      }
    }
  }
}

/* @(#)FigureTreePresentationModel.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.model;

import java.util.HashMap;
import javafx.scene.control.TreeItem;
import org.jhotdraw8.beans.NonnullProperty;
import org.jhotdraw8.draw.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.event.Listener;

/**
 * The {@code FigureTreePresentationModel} can be used to present a
 * {@code DrawingModel} in a {@code TreeView} or a {@code TreeTableView}.
 * <p>
 * Maps {@code DrawingModel} to a {@code TreeItem&lt;Figure&gt;} hierarchy.
 * <p>
 * Note: for performance reasons we do not expand the tree nodes by default.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class FigureTreePresentationModel {

    /**
     * The name of the model property.
     */
    public final static String MODEL_PROPERTY = "model";

    private final TreeItem<Figure> root = new TreeItem<Figure>();

    private final HashMap<Figure, TreeItem<Figure>> items = new HashMap<>();

    private boolean reversed = true;
    
    private int updating;

    /**
     * The drawingProperty holds the drawing that is presented by this drawing
     * view.
     */
    private final NonnullProperty<DrawingModel> model //
            = new NonnullProperty<DrawingModel>(this, MODEL_PROPERTY, new SimpleDrawingModel()) {
        private DrawingModel oldValue = null;

        @Override
        protected void fireValueChangedEvent() {
            DrawingModel newValue = get();
            super.fireValueChangedEvent();
            handleNewDrawingModel(oldValue, newValue);
            oldValue = newValue;
        }
    };

    private final Listener<DrawingModelEvent> modelHandler = new Listener<DrawingModelEvent>() {
        @Override
        public void handle(DrawingModelEvent event) {
          updating++;
          try {
          
            boolean structuralChange = false;
            Figure f = event.getFigure();
            switch (event.getEventType()) {
                case FIGURE_ADDED_TO_PARENT:
                    handleFigureAdded(f, event.getParent(), event.getIndex());
                    structuralChange = true;
                    break;
                case FIGURE_REMOVED_FROM_PARENT:
                    handleFigureRemoved(f, event.getParent(), event.getIndex());
                    structuralChange = true;
                    break;
                case SUBTREE_ADDED_TO_DRAWING:
                    handleSubtreeAddedToDrawing(f, event.getParent(), event.getIndex());
                    structuralChange = true;
                    break;
                case SUBTREE_REMOVED_FROM_DRAWING:
                    handleSubtreeRemovedFromDrawing(f);
                    structuralChange = true;
                    break;
                case NODE_CHANGED:
                    handleNodeInvalidated(f);
                    break;
                case LAYOUT_CHANGED:
                    break;
                case STYLE_CHANGED:
                    break;
                case ROOT_CHANGED:
                    handleRootChanged();
                    structuralChange = true;
                    break;
                case SUBTREE_NODES_CHANGED:
                    break;
                case DEPENDENCY_CHANGED:
                case TRANSFORM_CHANGED:
                case PROPERTY_VALUE_CHANGED:
                    break;
                default:
                    throw new UnsupportedOperationException(event.getEventType()
                            + " not supported");
            }
          } finally {
            updating--;
          }
        }
    };

    public DrawingModel getModel() {
        return model.get();
    }

    public void setDrawingModel(DrawingModel newValue) {
        model.set(newValue);
    }

    public NonnullProperty<DrawingModel> modelProperty() {
        return model;
    }

    private void handleNewDrawingModel(DrawingModel oldValue, DrawingModel newValue) {
        if (oldValue != null) {
            oldValue.removeDrawingModelListener(modelHandler);
        }
        newValue.addDrawingModelListener(modelHandler);
        handleRootChanged();
    }

    private void handleRootChanged() {
        Drawing drawing = getModel().getRoot();
        root.setValue(drawing);
        root.getChildren().clear();
        items.clear();
        items.put(drawing, root);
        int childIndex = 0;
        for (Figure child : drawing.getChildren()) {
            handleSubtreeAddedToDrawing(child, drawing, childIndex);
            handleFigureAdded(child, drawing, childIndex);
            childIndex++;
        }
    }

    private void handleFigureAdded(Figure f, Figure parentFigure, int index) {
        TreeItem<Figure> item = items.get(f);
        TreeItem<Figure> newParent = items.get(parentFigure);
        if (reversed) {
            newParent.getChildren().add(newParent.getChildren().size() - index, item);
        } else {
            newParent.getChildren().add(index, item);
        }
    }

    private void handleFigureRemoved(Figure f, Figure parentFigure, int index) {
        TreeItem<Figure> parent = items.get(parentFigure);
        if (reversed) {
            parent.getChildren().remove(parent.getChildren().size() - 1 - index);
        } else {
            parent.getChildren().remove(index);
        }
    }

    private void handleSubtreeAddedToDrawing(Figure f, Figure parentFigure, int index) {
        TreeItem<Figure> item = new TreeItem<>(f);
        item.setExpanded(false);
        items.put(f, item);
        int childIndex = 0;
        for (Figure child : f.getChildren()) {
            handleSubtreeAddedToDrawing(child, f, childIndex);
            handleFigureAdded(child, f, childIndex);
            childIndex++;
        }
    }

    private void handleSubtreeRemovedFromDrawing(Figure f) {
        items.remove(f);
    }

    private void handleNodeInvalidated(Figure f) {
        TreeItem<Figure> node = items.get(f);
        if (node != null) {
        node.setValue(f);
        }
    }

    public TreeItem<Figure> getRoot() {
        return root;
    }

    public TreeItem<Figure> getTreeItem(Figure f) {
        return items.get(f);
    }
    public Figure getFigure(TreeItem<Figure> item) {
        return item.getValue();
    }
    
    public boolean isUpdating() {
      return updating > 0;
    }
}

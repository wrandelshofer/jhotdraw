/* @(#)FigureHierarchyInspector.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.StyleableFigure;
import org.jhotdraw.draw.model.DrawingModelFigureProperty;
import org.jhotdraw.draw.model.FigureTreePresentationModel;
import org.jhotdraw.draw.model.SimpleDrawingModel;
import org.jhotdraw.util.Resources;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class FigureHierarchyInspector extends AbstractDrawingViewInspector {

    @FXML
    private TreeTableView<Figure> treeView;

    @FXML
    private TreeTableColumn<Figure, String> typeColumn;

    @FXML
    private TreeTableColumn<Figure, String> idColumn;

    private DrawingView drawingView;
    private Node node;
    private FigureTreePresentationModel model;
    private boolean isUpdatingSelection;
    private final SetChangeListener<Figure> viewSelectionHandler = change -> {
        updateSelectionInTree();
    };
    private final InvalidationListener treeSelectionHandler = change -> {
        updateSelectionInView();
    };
    private ChangeListener<Boolean>  modelUpdateHandler = (o,oldValue,newValue)->{
        isUpdatingSelection=newValue;
        if (!newValue) updateSelectionInTree();
    };

    public FigureHierarchyInspector() {
        this(FigureHierarchyInspector.class.getResource("FigureHierarchyInspector.fxml"),
                Resources.getBundle("org.jhotdraw.draw.gui.Labels"));
    }

    public FigureHierarchyInspector(URL fxmlUrl, ResourceBundle resources) {
        init(fxmlUrl, resources);
    }

    private void init(URL fxmlUrl, ResourceBundle resources) {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        loader.setResources(resources);
        try (InputStream in = fxmlUrl.openStream()) {
            node = loader.load(in);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        model = new FigureTreePresentationModel();
        typeColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(
                cell.getValue().getValue().getTypeSelector())
        );
        idColumn.setCellValueFactory(
                cell -> new DrawingModelFigureProperty<String>(model.getModel(),
                        cell.getValue().getValue(), StyleableFigure.STYLE_ID)
        );
        idColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        treeView.setRoot(model.getRoot());
        model.getRoot().setExpanded(true);
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treeView.getSelectionModel().getSelectedCells().addListener(treeSelectionHandler);
       
        model.updatingProperty().addListener(modelUpdateHandler);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    protected void onDrawingViewChanged(DrawingView oldValue, DrawingView newValue) {
        if (oldValue != null) {
            oldValue.getSelectedFigures().removeListener(viewSelectionHandler);
        }
        drawingView = newValue;
        if (newValue != null) {
            model.setDrawingModel(newValue.getModel());
            newValue.getSelectedFigures().addListener(viewSelectionHandler);
        } else {
            model.setDrawingModel(new SimpleDrawingModel());
        }
    }

    private void updateSelectionInTree() {
        if (!isUpdatingSelection) {
            isUpdatingSelection = true;
            TreeTableView.TreeTableViewSelectionModel<Figure> selectionModel = treeView.getSelectionModel();
            selectionModel.clearSelection();
            for (Figure f:new ArrayList<>(drawingView.getSelectedFigures())) {
                selectionModel.select(model.getTreeItem(f));
            }
        isUpdatingSelection = false;
        }
    }

    private void updateSelectionInView() {
        if (!isUpdatingSelection) {
            isUpdatingSelection = true;
            TreeTableView.TreeTableViewSelectionModel<Figure> selectionModel = treeView.getSelectionModel();
            Set<Figure> newSelection = new LinkedHashSet<>();
            for (TreeItem<Figure> item:new ArrayList<>(selectionModel.getSelectedItems())) {
                if (item != null) {
                newSelection.add(item.getValue());
                }
            }
            drawingView.getSelectedFigures().retainAll(newSelection);
            drawingView.getSelectedFigures().addAll(newSelection);
        isUpdatingSelection = false;
        }
    }

}

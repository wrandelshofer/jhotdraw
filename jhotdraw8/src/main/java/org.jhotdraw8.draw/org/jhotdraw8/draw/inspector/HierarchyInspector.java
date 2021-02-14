/*
 * @(#)HierarchyInspector.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ImmutableSet;
import org.jhotdraw8.collection.ImmutableSets;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.model.DrawingModelFigureProperty;
import org.jhotdraw8.draw.model.SimpleDrawingModel;
import org.jhotdraw8.gui.BooleanPropertyCheckBoxTreeTableCell;
import org.jhotdraw8.text.CachingCollator;
import org.jhotdraw8.text.OSXCollator;
import org.jhotdraw8.text.StringConverterAdapter;
import org.jhotdraw8.tree.ExpandedTreeItemIterator;
import org.jhotdraw8.tree.SimpleTreePresentationModel;
import org.jhotdraw8.tree.TreePresentationModel;
import org.jhotdraw8.xml.text.XmlWordListConverter;
import org.jhotdraw8.xml.text.XmlWordSetConverter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 */
public class HierarchyInspector extends AbstractDrawingViewInspector {

    private final CachingCollator collator = new CachingCollator(new OSXCollator());

    private @Nullable DrawingView drawingView;
    @FXML
    private TreeTableColumn<Figure, String> idColumn;
    private boolean isUpdatingSelectionInView;
    @FXML
    private TreeTableColumn<Figure, Boolean> lockedColumn;
    private TreePresentationModel<Figure> model;
    private Node node;
    @FXML
    private TreeTableColumn<Figure, ImmutableSet<String>> pseudoClassesColumn;
    @FXML
    private TreeTableColumn<Figure, ImmutableList<String>> styleClassesColumn;
    private final InvalidationListener treeSelectionHandler = change -> {
        if (model.isUpdating()) {
//        updateSelectionInTree();
        } else {
            updateSelectionInDrawingView();
        }
    };
    @FXML
    private TreeTableView<Figure> treeView;
    @FXML
    private TreeTableColumn<Figure, String> typeColumn;
    private final SetChangeListener<Figure> viewSelectionHandler = this::updateSelectionInTreeLater;
    @FXML
    private TreeTableColumn<Figure, Boolean> visibleColumn;
    private boolean willUpdateSelectionInTree;

    private final @NonNull XmlWordListConverter wordListConverter = new XmlWordListConverter();
    private final XmlWordSetConverter wordSetConverter = new XmlWordSetConverter();

    public HierarchyInspector() {
        this(HierarchyInspector.class.getResource("HierarchyInspector.fxml"),
                InspectorLabels.getResources().asResourceBundle());
    }

    public HierarchyInspector(@NonNull URL fxmlUrl, ResourceBundle resources) {
        init(fxmlUrl, resources);
    }

    @Override
    public Node getNode() {
        return node;
    }

    private void init(@NonNull URL fxmlUrl, ResourceBundle resources) {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        loader.setResources(resources);
        try (InputStream in = fxmlUrl.openStream()) {
            node = loader.load(in);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        model = new SimpleTreePresentationModel<>();
        typeColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(
                cell.getValue() == null ? null : cell.getValue().getValue() == null ? null : cell.getValue().getValue().getTypeSelector())
        );

        idColumn.setCellValueFactory(
                cell -> new DrawingModelFigureProperty<>((DrawingModel) model.getTreeModel(),
                        cell.getValue() == null ? null : cell.getValue().getValue(), StyleableFigure.ID));

        visibleColumn.setCellValueFactory(
                cell -> new DrawingModelFigureProperty<>((DrawingModel) model.getTreeModel(),
                        cell.getValue() == null ? null : cell.getValue().getValue(), HideableFigure.VISIBLE)
        );
        lockedColumn.setCellValueFactory(
                cell -> new DrawingModelFigureProperty<>((DrawingModel) model.getTreeModel(),
                        cell.getValue() == null ? null : cell.getValue().getValue(), LockableFigure.LOCKED)
        );
        // Type arguments needed for Java 8!
        styleClassesColumn.setCellValueFactory(cell -> new DrawingModelFigureProperty<ImmutableList<String>>((DrawingModel) model.getTreeModel(),
                        cell.getValue() == null ? null : cell.getValue().getValue(), StyleableFigure.STYLE_CLASS) {
            @Override
            public @Nullable ImmutableList<String> getValue() {
                return figure == null ? null : ImmutableLists.ofCollection(figure.getStyleClass());
            }
                }
        );
        // Type arguments needed for Java 8!
        pseudoClassesColumn.setCellValueFactory(cell -> new DrawingModelFigureProperty<ImmutableSet<String>>((DrawingModel) model.getTreeModel(),
                        cell.getValue() == null ? null : cell.getValue().getValue(), StyleableFigure.PSEUDO_CLASS) {
            @Override
            public @Nullable ImmutableSet<String> getValue() {
                return figure == null ? null : ImmutableSets.ofCollection(figure.getPseudoClass());
            }
                }
        );

        // This cell factory ensures that only styleable figures support editing of ids.
        // And it ensures, that the users sees the computed id, and not the one that he entered.
        idColumn.setCellFactory(
                // Type arguments needed for Java 8!
                new Callback<TreeTableColumn<Figure, String>, TreeTableCell<Figure, String>>() {

                    @Override
                    public @NonNull TreeTableCell<Figure, String> call(TreeTableColumn<Figure, String> paramTableColumn) {
                        // Type arguments needed for Java 8!
                        return new TextFieldTreeTableCell<Figure, String>(new DefaultStringConverter()) {
                            @Override
                            public void cancelEdit() {
                                super.cancelEdit();
                                updateItem(getItem(), false);
                            }

                            @Override
                            public void updateItem(String t, boolean empty) {
                                super.updateItem(t, empty);
                                TreeTableRow<Figure> row = getTreeTableRow();
                                boolean isEditable = false;
                                if (row != null) {
                                    Figure item = row.getItem();
                                    //Test for disable condition
                                    if (item != null && item.isEditableKey(StyleableFigure.ID)) {
                                        isEditable = true;
                                    }

                                    // show the computed  id!
                                    if (item != null) {
                                        setText(item.getId());
                                    }
                                }
                                if (isEditable) {
                                    setEditable(true);
                                    this.setStyle(null);
                                } else {
                                    setEditable(false);
                                    this.setStyle("-fx-text-fill: grey");
                                }
                            }
                        };
                    }

                });

        // This cell factory ensures that only styleable figures support editing of style classes.
        // And it ensures, that the users sees the computed style classes, and not the ones that he entered.
        // And it ensures, that the synthetic synthetic style classes are not stored in the STYLE_CLASSES attribute.
        // Type arguments needed for Java 8!
        styleClassesColumn.setCellFactory(new Callback<TreeTableColumn<Figure, ImmutableList<String>>, TreeTableCell<Figure, ImmutableList<String>>>() {

            @Override
            public @NonNull TreeTableCell<Figure, ImmutableList<String>> call(TreeTableColumn<Figure, ImmutableList<String>> paramTableColumn) {
                // Type arguments needed for Java 8!
                return new TextFieldTreeTableCell<Figure, ImmutableList<String>>() {
                    {
                        setConverter(new StringConverterAdapter<>(wordListConverter));
                    }

                    private final @NonNull Set<String> syntheticClasses = new HashSet<>();

                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        updateItem(getItem(), false);
                        syntheticClasses.clear();
                    }

                    @Override
                    public void commitEdit(@NonNull ImmutableList<String> newValue) {
                        ImmutableList<String> newValueSet = ImmutableLists.removeAll(newValue, syntheticClasses);
                        super.commitEdit(newValueSet);
                    }

                    @Override
                    public void startEdit() {
                        Figure figure = getTreeTableRow().getItem();
                        figure.get(StyleableFigure.STYLE_CLASS);
                        syntheticClasses.clear();
                        syntheticClasses.addAll(figure.getStyleClass());
                        syntheticClasses.removeAll(figure.getNonNull(StyleableFigure.STYLE_CLASS).asList());
                        super.startEdit();
                    }

                    @Override
                    public void updateItem(ImmutableList<String> t, boolean empty) {
                        super.updateItem(t, empty);
                        TreeTableRow<Figure> row = getTreeTableRow();
                        boolean isEditable = false;
                        if (row != null) {
                            Figure figure = row.getItem();
                            //Test for disable condition
                            if (figure != null && figure.isEditableKey(StyleableFigure.STYLE_CLASS)) {
                                isEditable = true;
                            }
                            // show the computed  classes!
                            if (figure != null) {
                                setText(wordListConverter.toString(ImmutableLists.ofCollection(figure.getStyleClass())));
                            }
                        }
                        if (isEditable) {
                            setEditable(true);
                            this.setStyle(null);
                        } else {
                            setEditable(false);
                            this.setStyle("-fx-text-fill: grey");
                        }
                    }
                };
            }
        });
        // Type arguments needed for Java 8!
        pseudoClassesColumn.setCellFactory(paramTableColumn -> new TextFieldTreeTableCell<Figure, ImmutableSet<String>>() {
            {
                setConverter(new StringConverterAdapter<ImmutableSet<String>>(wordSetConverter));
            }

        });

        final Comparator<String> comparator = collator;
        typeColumn.setComparator(comparator);
        idColumn.setComparator(comparator);

        visibleColumn.setCellFactory(BooleanPropertyCheckBoxTreeTableCell.forTreeTableColumn(InspectorStyleClasses.VISIBLE_CHECK_BOX));
        lockedColumn.setCellFactory(BooleanPropertyCheckBoxTreeTableCell.forTreeTableColumn(InspectorStyleClasses.LOCKED_CHECK_BOX));
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treeView.getSelectionModel().getSelectedCells().addListener(treeSelectionHandler);
        treeView.setRowFactory(tv -> {
            TreeTableRow<Figure> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Figure rowData = row.getItem();
                    DrawingView myDrawingView = this.drawingView;
                    if (myDrawingView != null) {
                        myDrawingView.scrollFigureToVisible(rowData);
                    }
                }
            });
            return row;
        });

        //treeView.setFixedCellSize(22);

        treeView.setRoot(model.getRoot());
        model.getRoot().setExpanded(true);
    }

    @Override
    protected void onDrawingViewChanged(ObservableValue<? extends DrawingView> observable, @Nullable DrawingView oldValue, @Nullable DrawingView newValue) {
        if (oldValue != null) {
            oldValue.getSelectedFigures().removeListener(viewSelectionHandler);
            treeView.getProperties().put(EditableComponent.EDITABLE_COMPONENT, null);
        }
        drawingView = newValue;
        if (newValue != null) {
            model.setTreeModel(newValue.getModel());
            newValue.getSelectedFigures().addListener(viewSelectionHandler);
            treeView.getProperties().put(EditableComponent.EDITABLE_COMPONENT, drawingView);
        } else {
            model.setTreeModel(new SimpleDrawingModel());
        }
    }

    private void updateSelectionInDrawingView() {
        if (!isUpdatingSelectionInView) {
            isUpdatingSelectionInView = true;
            TreeTableView.TreeTableViewSelectionModel<Figure> selectionModel = treeView.getSelectionModel();
            Set<Figure> newSelection = new LinkedHashSet<>();
            for (TreeItem<Figure> item : selectionModel.getSelectedItems()) {
                if (item != null) {
                    newSelection.add(item.getValue());
                }
            }
            DrawingView myDrawingView = this.drawingView;
            if (myDrawingView != null) {
                myDrawingView.getSelectedFigures().retainAll(newSelection);
                myDrawingView.getSelectedFigures().addAll(newSelection);
            }
            isUpdatingSelectionInView = false;
        }
    }

    private void updateSelectionInTree() {
        willUpdateSelectionInTree = false;
        if (!isUpdatingSelectionInView) {
            isUpdatingSelectionInView = true;
            TreeTableView.TreeTableViewSelectionModel<Figure> selectionModel = treeView.getSelectionModel();
            // Performance: collecting all indices and then setting them all at once is
            // much faster than invoking selectionModel.select(Object) for each item.
            DrawingView myDrawingView = this.drawingView;
            Set<Figure> selection = myDrawingView == null ? Collections.emptySet() : myDrawingView.getSelectedFigures();
            switch (selection.size()) {
            case 0:
                selectionModel.clearSelection();
                break;
            case 1:
                selectionModel.clearSelection();
                final TreeItem<Figure> treeItem = model.getTreeItem(selection.iterator().next());
                if (treeItem != null) {
                    selectionModel.select(treeItem);
                }
                    break;
                default:
                    int index = 0;
                    int count = 0;
                    final int size = selection.size();
                    for (TreeItem<Figure> node : (Iterable<TreeItem<Figure>>) () -> new ExpandedTreeItemIterator<>(model.getRoot())) {
                        boolean isSelected = selection.contains(node.getValue());
                        if (isSelected != selectionModel.isSelected(index)) {
                            if (isSelected) {
                                selectionModel.select(index);
                            } else {
                                selectionModel.clearSelection(index);
                            }
                        }
                        if (isSelected && ++count == size) {
                            break;
                        }
                        index++;
                    }
            }
            isUpdatingSelectionInView = false;
        }
    }

    private void updateSelectionInTreeLater(SetChangeListener.Change<? extends Figure> change) {
        if (!willUpdateSelectionInTree && !isUpdatingSelectionInView) {
            willUpdateSelectionInTree = true;
            Platform.runLater(this::updateSelectionInTree);
        }
    }

}

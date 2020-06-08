/*
 * @(#)TabbedAccordionDock.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dockold;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Control;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.gui.CustomSkin;

import static java.lang.Double.max;

/**
 * TabbedAccordionDock.
 *
 * @author Werner Randelshofer
 */
public class TabbedAccordionDock extends Control implements Dock {

    @NonNull
    private ObjectProperty<Track> track = new SimpleObjectProperty<>();

    @NonNull
    private final TabPane tabPane = new TabPane();
    @NonNull
    private final Accordion accordion = new Accordion();
    @NonNull
    private final TitledPane titlePane = new TitledPane();
    @NonNull
    private final ResizePane resizePane = new ResizePane();
    @NonNull
    private final StackPane stackPane = new StackPane() {
        @Override
        protected void layoutChildren() {
            if (!rotated) {
                for (Node child : getChildren()) {
                    child.getTransforms().clear();
                }
                super.layoutChildren();
            } else {
                for (Node child : getChildren()) {
                    Rotate rotate = new Rotate(90, 0, 0);
                    Translate translate = new Translate(getWidth(), 0);
                    child.getTransforms().setAll(translate, rotate);
                    child.resizeRelocate(0, 0, getHeight(), getWidth());
                }

            }
        }

        @Override
        protected double computeMaxWidth(double height) {
            return rotated ? super.computeMaxHeight(height) : super.computeMaxWidth(height);
        }

        @Override
        protected double computeMaxHeight(double width) {
            return rotated ? super.computeMaxWidth(width) : super.computeMaxHeight(width);
        }

        @Override
        protected double computePrefWidth(double height) {
            return rotated ? super.computePrefHeight(height) : super.computePrefWidth(height);
        }

        @Override
        protected double computePrefHeight(double width) {
            return rotated ? super.computePrefWidth(width) : super.computePrefHeight(width);
        }
    };
    @NonNull
    private ObservableList<DockItem> items = FXCollections.observableArrayList();
    private boolean rotated = false;

    public TabbedAccordionDock() {
        accordion.setManaged(true);
        sceneProperty().addListener((o, oldv, newv) -> {
            rotated = false;
            if (true) {
                return;
            }
            for (Node node = getParent(); node != null; node = node.getParent()) {
                if (node instanceof HBox) {
                    rotated = true;
                    break;
                } else if (node instanceof VBox) {
                    rotated = false;
                    break;
                } else if (node instanceof Track) {
                    Orientation orientation = ((Track) node).getOrientation();
                    rotated = orientation == Orientation.HORIZONTAL;
                    break;
                } else if (node instanceof DockRoot) {
                    rotated = true;
                    break;
                }
            }
        });

        setSkin(new CustomSkin<>(this));
        getStyleClass().add("dock");
        getChildren().add(accordion);
        accordion.getPanes().add(titlePane);
        accordion.setExpandedPane(titlePane);
        stackPane.getChildren().add(resizePane);

        SplitPane.setResizableWithParent(this, Boolean.FALSE);
        VBox.setVgrow(this, Priority.SOMETIMES);
        HBox.setHgrow(this, Priority.SOMETIMES);

        accordion.setStyle("-fx-background-color:transparent;-fx-border:none;-fx-padding:0;");
        titlePane.setStyle("-fx-background-color:transparent;-fx-border:none;-fx-padding:0;");
        accordion.setMinWidth(100.0);
        accordion.setMaxWidth(Double.MAX_VALUE);

        getItems().addListener((ListChangeListener<DockItem>) c -> {
            while (c.next()) {
                for (DockItem remitem : c.getRemoved()) {
                    remitem.setDock(null);
                }
                for (DockItem additem : c.getAddedSubList()) {
                    additem.setDock(TabbedAccordionDock.this);
                }
            }
            onDockChildrenChanged();
        });

        trackProperty().addListener((o, oldv, newv) -> {
            resizePane.setUserResizable(newv != null && !newv.resizesItems());
        });
    }

    @Override
    protected double computePrefHeight(double width) {
        return rotated ? accordion.prefWidth(width) : accordion.prefHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return rotated ? accordion.prefHeight(height) : accordion.prefWidth(height);
    }

    @NonNull
    @Override
    public ObservableList<DockItem> getItems() {
        return items;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @NonNull
    @Override
    public ObjectProperty<Track> trackProperty() {
        return track;
    }

    private void onDockChildrenChanged() {
        switch (items.size()) {
        case 0: {
            resizePane.setCenter(null);
            tabPane.getTabs().clear();
            titlePane.setText(null);
            titlePane.setGraphic(null);
            titlePane.setContent(null);
            stackPane.getChildren().clear();
            break;
        }
        case 1: {
            tabPane.getTabs().clear();
            DockItem i = items.get(0);
            titlePane.setText(i.getText());
            titlePane.setGraphic(i.getGraphic());
            stackPane.getChildren().clear();
            stackPane.getChildren().add(resizePane);
            titlePane.setContent(stackPane);
            resizePane.setCenter(i.getContent());
            break;
        }
        default: {
            resizePane.setCenter(tabPane);
            titlePane.setGraphic(null);
            tabPane.getTabs().setAll(items);
            StringBuilder b = new StringBuilder();
            double minHeight = 0;
            for (DockItem i : items) {
                Node content = i.getContent();
                if (content instanceof Region) {
                    minHeight = max(minHeight, content.minHeight(-1));
                }
                if (b.length() > 0) {
                    b.append(", ");
                }
                b.append(i.getText());
            }
            tabPane.setMinHeight(minHeight + 44);
            titlePane.setText(b.toString());
            stackPane.getChildren().clear();
            stackPane.getChildren().add(resizePane);
            titlePane.setContent(stackPane);
            break;
        }
        }
    }


    @Override
    protected void layoutChildren() {
        if (rotated) {
            Rotate rotate = new Rotate(270, 0, 0);
            Translate translate = new Translate(0, getHeight());
            accordion.getTransforms().setAll(translate, rotate);
            accordion.resizeRelocate(0, 0, getHeight(), getWidth());
        } else {
            accordion.getTransforms().clear();
            super.layoutChildren();
        }
    }
}

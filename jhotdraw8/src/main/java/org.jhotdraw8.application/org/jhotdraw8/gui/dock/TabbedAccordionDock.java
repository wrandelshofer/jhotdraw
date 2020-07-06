/*
 * @(#)TabbedAccordionDock.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
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

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import static java.lang.Double.max;

public class TabbedAccordionDock extends AbstractDock {

    private final Map<DockNode, Tab> tabMap = new WeakHashMap<>();
    private final BooleanProperty rotated = new SimpleBooleanProperty(true);
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
            if (!isRotated()) {
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
            return isRotated() ? super.computeMaxHeight(height) : super.computeMaxWidth(height);
        }

        @Override
        protected double computeMaxHeight(double width) {
            return isRotated() ? super.computeMaxWidth(width) : super.computeMaxHeight(width);
        }

        @Override
        protected double computePrefWidth(double height) {
            return isRotated() ? super.computePrefHeight(height) : super.computePrefWidth(height);
        }

        @Override
        protected double computePrefHeight(double width) {
            return isRotated() ? super.computePrefWidth(width) : super.computePrefHeight(width);
        }
    };

    public TabbedAccordionDock() {
        accordion.getPanes().add(titlePane);
        accordion.setExpandedPane(titlePane);
        titlePane.setContent(stackPane);
        stackPane.getChildren().add(resizePane);

        getChildren().add(accordion);
        resizePane.setContent(tabPane);
        SplitPane.setResizableWithParent(this, Boolean.FALSE);
        VBox.setVgrow(this, Priority.NEVER);
        HBox.setHgrow(this, Priority.NEVER);
        dockChildren.addListener((ListChangeListener<? super DockNode>) change -> onDockChildrenChanged());

        accordion.setStyle("-fx-background-color:transparent;-fx-border:none;-fx-padding:0;");
        titlePane.setStyle("-fx-background-color:transparent;-fx-border:none;-fx-padding:0;");
    }

    @NonNull
    private Tab makeTab(DockNode c) {
        if (c instanceof Dockable) {
            Dockable k = (Dockable) c;
            Tab tab = new Tab(k.getText(), k.getNode());
            if (getDockPane() != null) {
                tab.graphicProperty().bind(k.graphicProperty());
            }
            return tab;
        } else {
            return new Tab("-", c.getNode());
        }
    }

    private void onDockChildrenChanged() {
        List<Dockable> dockables = dockChildren.stream()
                .filter(d -> d instanceof Dockable)
                .map(d -> (Dockable) d)
                .collect(Collectors.toList());
        switch (dockables.size()) {
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
            Dockable i = dockables.get(0);
            titlePane.setText(i.getText());
            titlePane.setGraphic(i.getGraphic());
            stackPane.getChildren().clear();
            stackPane.getChildren().add(resizePane);
            titlePane.setContent(stackPane);
            resizePane.setCenter(i.getNode());
            break;
        }
        default: {
            resizePane.setCenter(tabPane);
            titlePane.setGraphic(null);
            List<Tab> tabs = dockChildren.stream()
                    .map(d -> tabMap.computeIfAbsent(d, this::makeTab))
                    .collect(Collectors.toList());
            tabMap.keySet().retainAll(dockables);
            tabPane.getTabs().setAll(tabs);
            StringBuilder b = new StringBuilder();
            double minHeight = 0;
            for (Dockable i : dockables) {
                Node content = i.getNode();
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
    public boolean isEditable() {
        return true;
    }

    @NonNull
    @Override
    public DockAxis getDockAxis() {
        return DockAxis.Z;
    }

    @Override
    public boolean isResizesDockChildren() {
        return true;
    }

    public boolean isRotated() {
        return rotated.get();
    }

    public BooleanProperty rotatedProperty() {
        return rotated;
    }

    public void setRotated(boolean rotated) {
        this.rotated.set(rotated);
    }

    @Override
    protected void layoutChildren() {
        if (isRotated()) {
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

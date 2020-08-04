/*
 * @(#)TabbedAccordionDock.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.binding.Binding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
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
import org.jhotdraw8.binding.CustomBinding;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Double.max;

public class TabbedAccordionDock extends AbstractDockParent implements Dock {

    private final BooleanProperty rotated = new SimpleBooleanProperty(false);
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
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        accordion.getPanes().add(titlePane);
        accordion.setExpandedPane(titlePane);
        titlePane.setContent(stackPane);
        stackPane.getChildren().add(resizePane);

        getChildren().add(accordion);
        resizePane.setContent(tabPane);
        SplitPane.setResizableWithParent(this, Boolean.FALSE);
        VBox.setVgrow(this, Priority.NEVER);
        HBox.setHgrow(this, Priority.NEVER);
        accordion.setStyle("-fx-background-color:transparent;-fx-border:none;-fx-padding:0;");
        titlePane.setStyle("-fx-background-color:transparent;-fx-border:none;-fx-padding:0;");

        dockParentProperty().addListener(onParentChanged());
        dockChildren.addListener((ListChangeListener<? super DockItem>) change -> onDockChildrenChanged());
        CustomBinding.bindContent(tabPane.getTabs(), getDockChildren(),
                this::makeTab, k -> ((TabPaneDock.MyTab) k).dispose());
        Binding<Boolean> expandedAndShowing = showingProperty().and(accordion.expandedPaneProperty().isNotNull());
        CustomBinding.bind(tabPane.getSelectionModel().selectedItemProperty(), t -> ((TabPaneDock.MyTab) t).showingProperty(), expandedAndShowing, false);
    }

    @NonNull
    private TabPaneDock.MyTab makeTab(DockChild c) {
        if (c instanceof DraggableDockChild) {
            DraggableDockChild k = (DraggableDockChild) c;
            TabPaneDock.MyTab tab = new TabPaneDock.MyTab(c, k.getText(), k.getNode());
            tab.graphicProperty().bind(CustomBinding.<Node>compute(k::getGraphic, k.graphicProperty(), editableProperty()));
            return tab;
        } else {
            return new TabPaneDock.MyTab(c, "-", c.getNode());
        }
    }

    @NonNull
    protected ChangeListener<DockParent> onParentChanged() {
        return (o, oldv, newv) -> {
            resizePane.setUserResizable(newv != null && !newv.isResizesDockChildren());
            resizePane.setResizeAxis(newv == null ? DockAxis.Y : newv.getDockAxis());
            // setRotated(newv != null&&newv.getDockAxis()==DockAxis.X);
        };
    }

    private void onDockChildrenChanged() {
        List<DraggableDockChild> dockables = dockChildren.stream()
                .filter(d -> d instanceof DraggableDockChild)
                .map(d -> (DraggableDockChild) d)
                .collect(Collectors.toList());
        switch (dockables.size()) {
        case 0: {
            resizePane.setCenter(null);
            titlePane.setText(null);
            titlePane.setGraphic(null);
            titlePane.setContent(null);
            stackPane.getChildren().clear();
            break;
        }
        case 1: {
            DraggableDockChild i = dockables.get(0);
            titlePane.setText(i.getText());

            // this detaches the graphics from the tab!
            titlePane.setGraphic(i.getGraphic());

            stackPane.getChildren().clear();
            stackPane.getChildren().add(resizePane);
            titlePane.setContent(stackPane);
            resizePane.setCenter(i.getNode());
            break;
        }
        default: {
            // The tabPane will reattach the graphics by itself, but the
            // titlePane needs an explicit detach.
            titlePane.setGraphic(null);

            resizePane.setCenter(tabPane);
            StringBuilder b = new StringBuilder();
            double minHeight = 0;
            for (DraggableDockChild i : dockables) {
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

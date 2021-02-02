/*
 * @(#)TabbedAccordionTrack.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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
import org.jhotdraw8.binding.CustomBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Double.max;

/**
 * This track stacks {@link Dockable}s on the Z-axis into a tab pane inside
 * an accordion.
 * <p>
 * If this track has only one {@link Dockable}, it is added directly to
 * the accordion without a tab pane in between.
 */
public class TabbedAccordionTrack extends AbstractDockParent implements Track {

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
    };

    public TabbedAccordionTrack() {
        accordion.getPanes().add(titlePane);
        accordion.setExpandedPane(titlePane);
        titlePane.setContent(stackPane);
        titlePane.setBorder(null);
        stackPane.getChildren().add(resizePane);
        stackPane.setBorder(null);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        getChildren().add(accordion);
        resizePane.setCenter(tabPane);
        SplitPane.setResizableWithParent(this, Boolean.FALSE);
        VBox.setVgrow(this, Priority.NEVER);
        HBox.setHgrow(this, Priority.NEVER);
        getStyleClass().add("track");

        dockParentProperty().addListener(onParentChanged());
        dockChildren.addListener((ListChangeListener<? super DockNode>) change -> onDockChildrenChanged());
        CustomBinding.bindContent(tabPane.getTabs(), getDockChildren(),
                this::makeTab, k -> ((TabPaneTrack.MyTab) k).dispose());
        Binding<Boolean> expandedAndShowing = showingProperty().and(accordion.expandedPaneProperty().isNotNull());
        CustomBinding.bind(tabPane.getSelectionModel().selectedItemProperty(), t -> ((TabPaneTrack.MyTab) t).showingProperty(), expandedAndShowing, false);
    }

    @NonNull
    private TabPaneTrack.MyTab makeTab(DockChild c) {
        if (c instanceof Dockable) {
            Dockable k = (Dockable) c;
            TabPaneTrack.MyTab tab = new TabPaneTrack.MyTab(c, k.getText(), k.getNode());
            tab.graphicProperty().bind(CustomBinding.<Node>compute(k::getGraphic, k.graphicProperty(), editableProperty()));
            return tab;
        } else {
            return new TabPaneTrack.MyTab(c, "-", c.getNode());
        }
    }

    @NonNull
    protected ChangeListener<DockParent> onParentChanged() {
        return (o, oldv, newv) -> {
            resizePane.setUserResizable(newv != null && !newv.isResizesDockChildren());
            resizePane.setResizeAxis(newv == null ? TrackAxis.Y : newv.getDockAxis());
            setRotated(newv != null && newv.getDockAxis() == TrackAxis.X);
        };
    }

    private void onDockChildrenChanged() {
        List<Dockable> dockables = dockChildren.stream()
                .filter(d -> d instanceof Dockable)
                .map(d -> (Dockable) d)
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
            Dockable i = dockables.get(0);
            titlePane.setText(i.getText());


            // this detaches the graphics and the content from the tab!
            titlePane.setGraphic(i.getGraphic());
            resizePane.setCenter(i.getNode());

            stackPane.getChildren().clear();
            stackPane.getChildren().add(resizePane);
            titlePane.setContent(stackPane);
            break;
        }
        default: {
            // The tabPane will reattach the graphics and the content by itself - hopefully
            titlePane.setGraphic(null);
            resizePane.setCenter(null);
            ArrayList<Tab> col = new ArrayList<>(tabPane.getTabs());
            tabPane.getTabs().clear();
            tabPane.getTabs().setAll(col);

            resizePane.setCenter(tabPane);
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


    @NonNull
    @Override
    public TrackAxis getDockAxis() {
        return TrackAxis.Z;
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

/*
 * @(#)TabPaneTrack.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.binding.CustomBinding;

/**
 * This track stacks its children on the Z-axis into a tab pane.
 */
public class TabPaneTrack extends AbstractDockParent implements Track {

    private final TabPane tabPane = new TabPane();
    private @NonNull ResizePane resizePane = new ResizePane();

    static class MyTab extends Tab {
        private final DockableDragHandler dockableDragHandler;
        @NonNull DockChild dockChild;

        MyTab(@NonNull DockChild dockChild, @Nullable String text, @Nullable Node graphic) {
            super(text, graphic);
            this.dockChild = dockChild;
            if (dockChild instanceof Dockable) {
                dockableDragHandler = new DockableDragHandler((Dockable) dockChild);
            } else {
                dockableDragHandler = null;
            }
        }

        BooleanProperty showingProperty() {
            return dockChild.showingProperty();
        }

        void dispose() {
            if (dockableDragHandler != null) {
                dockableDragHandler.dispose();
            }
        }

        @Override
        public String toString() {
            return "MyTab@" + Integer.toHexString(hashCode()) + "{" +
                    dockChild +
                    '}';
        }
    }

    public TabPaneTrack() {
        getChildren().add(resizePane);
        resizePane.setCenter(tabPane);
        SplitPane.setResizableWithParent(this, Boolean.FALSE);
        VBox.setVgrow(this, Priority.NEVER);
        HBox.setHgrow(this, Priority.NEVER);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        dockParentProperty().addListener(onParentChanged());
        CustomBinding.bindContentBidirectional(tabPane.getTabs(), getDockChildren(),
                this::makeTab, k -> ((MyTab) k).dispose(),
                tab -> ((MyTab) tab).dockChild, null);
        CustomBinding.bind(tabPane.getSelectionModel().selectedItemProperty(),
                t -> ((MyTab) t).showingProperty(), showingProperty(),
                false);
    }

    protected @NonNull ChangeListener<DockParent> onParentChanged() {
        return (o, oldv, newv) -> {
            resizePane.setUserResizable(newv != null && !newv.isResizesDockChildren());
            resizePane.setResizeAxis(newv == null ? TrackAxis.Y : newv.getDockAxis());
        };
    }

    private @NonNull MyTab makeTab(DockChild c) {
        if (c instanceof Dockable) {
            Dockable k = (Dockable) c;
            MyTab tab = new MyTab(k, k.getText(), k.getNode());
            tab.graphicProperty().bind(CustomBinding.<Node>compute(k::getGraphic, k.graphicProperty(), editableProperty()));
            return tab;
        } else {
            return new MyTab(c, "-", c.getNode());
        }
    }


    @Override
    public @NonNull TrackAxis getDockAxis() {
        return TrackAxis.Z;
    }

    @Override
    public boolean isResizesDockChildren() {
        return true;
    }

}

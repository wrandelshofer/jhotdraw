/*
 * @(#)TabPaneDock.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
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

public class TabPaneDock extends AbstractDockParent implements Dock {

    private final TabPane tabPane = new TabPane();
    @NonNull
    private ResizePane resizePane = new ResizePane();

    static class MyTab extends Tab {
        private final DockableDragHandler dockableDragHandler;
        @NonNull
        private DockChild dockChild;

        MyTab(@NonNull DockChild dockChild, @Nullable String text, @Nullable Node graphic) {
            super(text, graphic);
            this.dockChild = dockChild;
            if (dockChild instanceof DraggableDockChild) {
                dockableDragHandler = new DockableDragHandler((DraggableDockChild) dockChild);
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

    public TabPaneDock() {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        getChildren().add(resizePane);
        resizePane.setContent(tabPane);
        SplitPane.setResizableWithParent(this, Boolean.FALSE);
        VBox.setVgrow(this, Priority.NEVER);
        HBox.setHgrow(this, Priority.NEVER);


        dockParentProperty().addListener(onParentChanged());
        CustomBinding.bindContent(tabPane.getTabs(), getDockChildren(),
                this::makeTab, k -> ((MyTab) k).dispose());
        CustomBinding.bind(tabPane.getSelectionModel().selectedItemProperty(),
                t -> ((MyTab) t).showingProperty(), showingProperty(),
                false);
    }

    @NonNull
    protected ChangeListener<DockParent> onParentChanged() {
        return (o, oldv, newv) -> {
            resizePane.setUserResizable(newv != null && !newv.isResizesDockChildren());
            resizePane.setResizeAxis(newv == null ? DockAxis.Y : newv.getDockAxis());
        };
    }

    @NonNull
    private MyTab makeTab(DockChild c) {
        if (c instanceof DraggableDockChild) {
            DraggableDockChild k = (DraggableDockChild) c;
            MyTab tab = new MyTab(k, k.getText(), k.getNode());
            tab.graphicProperty().bind(CustomBinding.<Node>compute(k::getGraphic, k.graphicProperty(), editableProperty()));
            return tab;
        } else {
            return new MyTab(c, "-", c.getNode());
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

}

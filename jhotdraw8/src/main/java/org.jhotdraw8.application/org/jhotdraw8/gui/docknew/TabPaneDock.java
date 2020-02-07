package org.jhotdraw8.gui.docknew;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;

import java.util.Map;
import java.util.WeakHashMap;

public class TabPaneDock
        extends AbstractDock {

    private final Map<DockNode, Tab> tabMap = new WeakHashMap<>();
    private final TabPane tabPane = new TabPane();
    @NonNull
    private ResizePane resizePane = new ResizePane();

    public TabPaneDock() {
        getChildren().add(resizePane);
        resizePane.setContent(tabPane);
        CustomBinding.bindContent(tabPane.getTabs(), getDockChildren(),
                k -> tabMap.computeIfAbsent(k, this::makeTab));
        dockParentProperty().addListener(onParentChanged());
        SplitPane.setResizableWithParent(this, Boolean.FALSE);
        VBox.setVgrow(this, Priority.NEVER);
        HBox.setHgrow(this, Priority.NEVER);
    }

    @NonNull
    protected ChangeListener<Dock> onParentChanged() {
        return (o, oldv, newv) -> {
            resizePane.setUserResizable(newv != null && !newv.isResizesDockChildren());
            resizePane.setResizeAxis(newv == null ? DockAxis.Y : newv.getDockAxis());
            boolean hasRootPane = newv != null && newv.getDockPane() != null;
            ObservableList<DockNode> dockChildren = getDockChildren();
            for (int i = 0, n = dockChildren.size(); i < n; i++) {
                Tab tab = tabPane.getTabs().get(i);
                tab.graphicProperty().unbind();
                DockNode dockNode = dockChildren.get(i);
                if (hasRootPane && (dockNode instanceof Dockable)) {
                    tab.graphicProperty().bind(((Dockable) dockNode).graphicProperty());
                } else {
                    tab.setGraphic(null);
                }
            }
        };
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

}

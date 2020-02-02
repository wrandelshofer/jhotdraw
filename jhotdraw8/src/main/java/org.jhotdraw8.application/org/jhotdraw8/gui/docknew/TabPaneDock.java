package org.jhotdraw8.gui.docknew;

import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.gui.CustomSkin;

import java.util.Map;
import java.util.WeakHashMap;

public class TabPaneDock
        extends AbstractDock {

    private final Map<DockComponent, Tab> tabMap = new WeakHashMap<>();
    private final TabPane tabPane = new TabPane();

    public TabPaneDock() {
        setSkin(new CustomSkin<>(this));
        getChildren().add(tabPane);
        SplitPane.setResizableWithParent(this, Boolean.FALSE);
        CustomBinding.bindContent(tabPane.getTabs(), getChildComponents(),
                k -> tabMap.computeIfAbsent(k, this::makeTab));
    }
    @NonNull
    private Tab makeTab(DockComponent c) {
        if (c instanceof DockItem) {
            DockItem k = (DockItem) c;
            Tab tab = new Tab(k.getText(), k.getContent());
            tab.setGraphic(k.getGraphic());
            return tab;
        } else {
            return new Tab("-", c.getContent());
        }
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @NonNull
    @Override
    public DockAxis getAxis() {
        return DockAxis.Z;
    }


    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        tabPane.resizeRelocate(0, 0, getWidth(), getHeight());
    }

    @Override
    protected double computePrefHeight(double width) {
        return tabPane.prefHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return tabPane.prefWidth(height);
    }
}

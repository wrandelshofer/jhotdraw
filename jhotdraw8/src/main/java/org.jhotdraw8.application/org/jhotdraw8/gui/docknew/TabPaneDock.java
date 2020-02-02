package org.jhotdraw8.gui.docknew;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.collection.ReadOnlyListWrapper;
import org.jhotdraw8.gui.CustomSkin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TabPaneDock
        extends AbstractDock {
    protected final ObservableList<DockComponent> children = FXCollections.observableArrayList();

    @Override
    public ReadOnlyList<DockComponent> getChildComponentsReadOnly() {
        return new ReadOnlyListWrapper<>(children);
    }

    @NonNull
    @Override
    public ObservableList<DockComponent> getChildComponents() {
        return children;
    }

    private final Map<DockComponent, Tab> tabMap = new LinkedHashMap<>();
    private final TabPane tabPane = new TabPane();

    public TabPaneDock() {
        setSkin(new CustomSkin<>(this));
        getChildren().add(tabPane);
        getChildComponents().addListener(this::onChildrenChanged);
        SplitPane.setResizableWithParent(this, Boolean.FALSE);

        ChangeListener<Node> changeListener = (o, oldv, newv) -> System.out.println(this + " parent " + oldv + " -> " + newv);
        parentProperty().addListener(changeListener);
    }

    @NonNull
    private void onChildrenChanged(ListChangeListener.Change<? extends DockComponent> change) {
        ObservableList<Tab> dest = tabPane.getTabs();
        while (change.next()) {
            int from = change.getFrom();
            int to = change.getTo();
            List<? extends DockComponent> s = change.getList();
            if (change.wasPermutated()) {
                dest.remove(from, to);
                List<Tab> tabs = new ArrayList<>(to - from);
                for (int i = from; i < to; i++) {
                    tabs.add(tabMap.computeIfAbsent(s.get(i),
                            this::makeTab));
                }
                dest.addAll(from, tabs);
            }
            if (change.wasRemoved()) {
                tabMap.keySet().removeAll(change.getRemoved());
                dest.remove(from, from + change.getRemovedSize());
                for (DockComponent removed : change.getRemoved()) {
                    if (removed.getParentComponent() == this) {
                        removed.setParentComponent(null);
                    }
                }
            }
            if (change.wasAdded()) {
                List<Tab> tabs = new ArrayList<>(to - from);
                for (DockComponent n : change.getAddedSubList()) {
                    tabs.add(tabMap.computeIfAbsent(n,
                            this::makeTab));
                    n.setParentComponent(this);
                }
                dest.addAll(from, tabs);

            }
        }
    }

    @NonNull
    private Tab makeTab(DockComponent c) {
        if (c instanceof DockItem) {
            DockItem k = (DockItem) c;
            Tab tab = new Tab(k.getText(), k.getContent());
            tab.setGraphic(k.getGraphic());
            return tab;
        } else {
            Tab tab = new Tab("-", c.getContent());
            return tab;
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

/*
 * @(#)TabPaneDock.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dockold;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.gui.CustomSkin;

/**
 * A TabPaneDock.
 *
 * @author Werner Randelshofer
 */
public class TabPaneDock extends Control implements Dock {

    @NonNull
    private TabPane tabPane = new TabPane();

    public TabPaneDock() {
        setSkin(new CustomSkin<>(this));
        getChildren().add(tabPane);
        SplitPane.setResizableWithParent(this, Boolean.FALSE);

        setMinWidth(10);
        setMinHeight(10);
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);

        getItems().addListener(new ListChangeListener<DockItem>() {
            @Override
            public void onChanged(@NonNull ListChangeListener.Change<? extends DockItem> c) {
                while (c.next()) {
                    for (DockItem remitem : c.getRemoved()) {
                        remitem.setDock(null);
                    }
                    for (DockItem additem : c.getAddedSubList()) {
                        additem.setDock(TabPaneDock.this);
                    }
                }
            }
        });
    }

    @Override
    protected double computePrefHeight(double width) {
        return tabPane.prefHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return tabPane.prefWidth(height);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public ObservableList<DockItem> getItems() {
        return (ObservableList<DockItem>) (ObservableList<?>) tabPane.getTabs();
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        tabPane.resizeRelocate(0, 0, getWidth(), getHeight());
    }

    @NonNull
    private ObjectProperty<Track> track = new SimpleObjectProperty<>();

    @NonNull
    @Override
    public ObjectProperty<Track> trackProperty() {
        return track;
    }

}

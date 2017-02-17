/* @(#)TabPaneDock.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jhotdraw8.gui.CustomSkin;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;

/**
 * A TabPaneDock.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class TabPaneDock extends Control implements Dock {

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
            public void onChanged(ListChangeListener.Change<? extends DockItem> c) {
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
private ObjectProperty<Track> track=new SimpleObjectProperty<>();
    @Override
    public ObjectProperty<Track> trackProperty() {
        return track;
    }


}

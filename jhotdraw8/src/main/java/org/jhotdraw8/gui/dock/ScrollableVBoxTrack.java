/* @(#)ScrollableVBoxTrack.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.gui.dock;

import org.jhotdraw8.gui.dock.Track;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * ScrollableVBoxTrack.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ScrollableVBoxTrack extends Control implements Track {

    private final VBox vbox = new VBox();
    private ScrollPane scrollPane = new ScrollPane(vbox);

    public ScrollableVBoxTrack() {
        getChildren().add(scrollPane);
        setMinWidth(10);
        setMinHeight(10);
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
        SplitPane.setResizableWithParent(this, Boolean.FALSE);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(true);
        scrollPane.setBorder(Border.EMPTY);
        scrollPane.setStyle("-fx-background-color:transparent;-fx-border-width:0,0;-fx-padding:0;");

        getItems().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> c) {
                while (c.next()) {
                    for (Node remitem : c.getRemoved()) {
                    }
                    for (Node additem : c.getAddedSubList()) {
                        // VBox.setVgrow(additem, Priority.SOMETIMES);
                    }
                }
                updateResizableWithParent();
            }

        });
    }

    private void updateResizableWithParent() {
        boolean resizeableWithParent = false;
        for (Node n : getItems()) {
            if (SplitPane.isResizableWithParent(n)) {
                resizeableWithParent = true;
                break;
            }
        }
        SplitPane.setResizableWithParent(ScrollableVBoxTrack.this, resizeableWithParent);
    }

    @Override
    public ObservableList<Node> getItems() {
        return vbox.getChildren();
    }

    @Override
    public Orientation getOrientation() {
        return Orientation.VERTICAL;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        scrollPane.resizeRelocate(0, 0, getWidth(), getHeight());
    }

    @Override
    protected double computePrefHeight(double width) {
        return scrollPane.prefHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return scrollPane.prefWidth(height);
    }

}

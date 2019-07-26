/*
 * @(#)ScrollableVBoxTrack.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.gui.CustomSkin;

/**
 * ScrollableVBoxTrack.
 *
 * @author Werner Randelshofer
 */
public class ScrollableVBoxTrack extends Control implements Track {

    private final VBox vbox = new VBox();
    @Nonnull
    private ScrollPane scrollPane = new ScrollPane(vbox);

    public ScrollableVBoxTrack() {
        setSkin(new CustomSkin<>(this));
        getStyleClass().add("track");
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
                        if (remitem instanceof Dock) {
                            Dock d = (Dock) remitem;
                            d.setTrack(null);
                        }
                    }
                    for (Node additem : c.getAddedSubList()) {
                        if (additem instanceof Dock) {
                            Dock d = (Dock) additem;
                            d.setTrack(ScrollableVBoxTrack.this);
                        }
                    }
                }

                updateResizableWithParent();
            }

        });
    }

    @Override
    protected double computePrefHeight(double width) {
        return scrollPane.prefHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return scrollPane.prefWidth(height);
    }

    @Override
    public ObservableList<Node> getItems() {
        return vbox.getChildren();
    }

    @Nonnull
    @Override
    public Orientation getOrientation() {
        return Orientation.VERTICAL;
    }

    @Override
    protected void layoutChildren() {
        for (Node child : getChildren()) {
            child.resizeRelocate(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public boolean resizesItems() {
        return false;
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

}

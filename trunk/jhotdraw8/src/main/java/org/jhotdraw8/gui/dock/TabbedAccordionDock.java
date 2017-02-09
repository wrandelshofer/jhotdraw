/* @(#)TabbedAccordionDock.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.gui.dock;

import org.jhotdraw8.gui.CustomSkin;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.Control;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * TabbedAccordionDock.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class TabbedAccordionDock extends Control implements Dock {

    private TabPane tabPane = new TabPane();
    private Accordion accordion = new Accordion();
    private TitledPane titlePane = new TitledPane();
    private ObservableList<DockItem> items = FXCollections.observableArrayList();

    public TabbedAccordionDock() {

        setSkin(new CustomSkin<>(this));
        getChildren().add(accordion);
        accordion.getPanes().add(titlePane);
        accordion.setExpandedPane(titlePane);
//        setMaxHeight(Double.MAX_VALUE);
//        setMaxWidth(Double.MAX_VALUE);
//        setMinWidth(10);
        //   setMinHeight(10);
        SplitPane.setResizableWithParent(this, Boolean.FALSE);
        VBox.setVgrow(this, Priority.SOMETIMES);

        accordion.setStyle("-fx-background-color:transparent;-fx-border:none;-fx-padding:0;");
        titlePane.setStyle("-fx-background-color:transparent;-fx-border:none;-fx-padding:0;");

        getItems().addListener(new ListChangeListener<DockItem>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends DockItem> c) {
                while (c.next()) {
                    for (DockItem remitem : c.getRemoved()) {
                        remitem.setDock(null);
                    }
                    for (DockItem additem : c.getAddedSubList()) {
                        additem.setDock(TabbedAccordionDock.this);
                    }
                }
                updateView();
            }

        });

//        titlePane.expandedProperty().addListener((o, oldv, newv) -> {
//            if (newv) {
//                setMaxHeight(Double.MAX_VALUE);
//                setMinHeight(titlePane.getMinHeight()+26);
//            } else {
//                setMaxHeight(accordion.prefHeight(-1));
//                setMinHeight(26);
//                //setMaxHeight(26);
//            }
//            titlePane.requestLayout();
//        });
    }

    @Override
    protected double computePrefHeight(double width) {
        return accordion.prefHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return accordion.prefWidth(height);
    }

    @Override
    public ObservableList<DockItem> getItems() {
        return items;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    private void updateView() {
        try {
            doUpdateView();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void doUpdateView() {
        switch (items.size()) {
            case 0:
                titlePane.setText(null);
                titlePane.setGraphic(null);
                titlePane.setContent(null);
                tabPane.getTabs().clear();
                titlePane.requestLayout();
                break;
            case 1: {
                DockItem i = items.get(0);
                tabPane.getTabs().clear();
                titlePane.setText(i.getText());
                titlePane.setGraphic(i.getGraphic());
                titlePane.setContent(i.getContent());
                titlePane.requestLayout();
                break;
            }
            default: {
                titlePane.setGraphic(null);
                titlePane.setContent(tabPane);
                tabPane.getTabs().setAll(items);
                StringBuilder b = new StringBuilder();
                for (DockItem i : items) {
                    if (b.length() > 0) {
                        b.append(", ");
                    }
                    b.append(i.getText());
                }
                titlePane.setText(b.toString());
                tabPane.requestLayout();
                break;
            }
        }
        requestLayout();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        accordion.resizeRelocate(0, 0, getWidth(), getHeight());
    }
}

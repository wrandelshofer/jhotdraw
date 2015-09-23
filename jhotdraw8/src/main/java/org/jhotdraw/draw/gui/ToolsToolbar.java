/* @(#)ToolsToolbar.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.tool.Tool;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class ToolsToolbar extends GridPane {

    private ToggleGroup group = new ToggleGroup();
    private ObjectProperty<DrawingEditor> editor = new SimpleObjectProperty<>(this, "editor");

    {
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue != null && getDrawingEditor() != null) {
                    getDrawingEditor().setActiveTool((Tool) newValue.getUserData());
                }
            }
        });
    }

    {
        editor.addListener(new ChangeListener<DrawingEditor>() {

            @Override
            public void changed(ObservableValue<? extends DrawingEditor> observable, DrawingEditor oldValue, DrawingEditor newValue) {
                if (newValue != null && group.getSelectedToggle() != null) {
                    newValue.setActiveTool((Tool) group.getSelectedToggle().getUserData());
                }
            }
        });
    }

    public void addTool(Tool tool, int gridx, int gridy) {
        ToggleButton button = new ToggleButton();
        if (tool.get(Tool.LARGE_ICON_KEY) != null) {
            button.setGraphic(tool.get(Tool.LARGE_ICON_KEY));
            if (tool.get(Tool.SHORT_DESCRIPTION) != null) {
                button.setTooltip(new Tooltip(tool.get(Tool.SHORT_DESCRIPTION)));
            }
        } else {
            button.setText(tool.getName());
        }
        button.setUserData(tool);
        button.setFont(new Font("System", 11.0));
        if (group.getToggles().isEmpty()) {
            button.setSelected(true);
        }
        group.getToggles().add(button);
        add(button, gridx, gridy);
    }

    public ObjectProperty<DrawingEditor> drawingEditor() {
        return editor;
    }

    public DrawingEditor getDrawingEditor() {
        return editor.get();
    }

    public void setDrawingEditor(DrawingEditor editor) {
        this.editor.set(editor);
    }
}

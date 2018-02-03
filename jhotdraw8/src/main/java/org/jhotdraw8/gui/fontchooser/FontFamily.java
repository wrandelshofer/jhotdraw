/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhotdraw8.gui.fontchooser;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;

/**
 * FontFamily.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class FontFamily {
  private final StringProperty name = new SimpleStringProperty();

    private final ObservableList<FontTypeface> typefaces=FXCollections.observableArrayList();

    public ObservableList<FontTypeface> getTypefaces() {
        return typefaces;
    }
    
    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public StringProperty nameProperty() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
}

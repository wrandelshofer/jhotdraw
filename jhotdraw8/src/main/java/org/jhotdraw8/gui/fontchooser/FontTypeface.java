/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhotdraw8.gui.fontchooser;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;

/**
 * FontTypeface.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class FontTypeface {

  private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty regular = new SimpleBooleanProperty();
    private final StringProperty shortName = new SimpleStringProperty();

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public String getShortName() {
        return shortName.get();
    }

    public void setShortName(String value) {
        shortName.set(value);
    }

    public boolean isRegular() {
        return regular.get();
    }

    public void setRegular(boolean value) {
        regular.set(value);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public BooleanProperty regularProperty() {
        return regular;
    }

    public StringProperty shortNameProperty() {
        return shortName;
    }
    @Override
    public String toString() {
        return getShortName();
    }

}

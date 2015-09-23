/* @(#)AbstractDisableable.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.app;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;

/**
 * AbstractDisableable.
 * <p>
 * Binds {@code disabled} to {@code disablers.emptyProperty().not()}.
 * <p>
 * If a subclass wants to bind {@code disabled} to additional reasons,
 * it must unbind {@code disabled} first.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AbstractDisableable implements Disableable {
    protected final ReadOnlyBooleanWrapper disabled = new ReadOnlyBooleanWrapper();
    protected final SetProperty<Object> disablers = new SimpleSetProperty<Object>(FXCollections.observableSet());

    public AbstractDisableable() {
        disabled.bind(disablers.emptyProperty().not());
    }
    @Override
    public ReadOnlyBooleanProperty disabledProperty() {
        return disabled.getReadOnlyProperty();
    }

   @Override
    public SetProperty<Object> disablersProperty() {
        return disablers;
    }

}

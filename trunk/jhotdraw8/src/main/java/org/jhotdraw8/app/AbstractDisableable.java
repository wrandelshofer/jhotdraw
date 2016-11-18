/* @(#)AbstractDisableable.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * AbstractDisableable.
 * <p>
 * Binds {@code disabled} to {@code disablers.emptyProperty().not()}.
 * <p>
 * If a subclass wants to bind {@code disabled} to additional reasons, it must
 * unbind {@code disabled} first.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AbstractDisableable implements Disableable {

  /**
   * Holds the disablers.
   * <p>
   * This field is protected, so that it can be accessed by subclasses.
   */
  protected final ObservableSet<Object> disablers = FXCollections.observableSet();
  /**
   * Holds the disabled state.
   * <p>
   * This field is protected, so that it can be bound to or-combinations of disablers.
   */
  protected final ReadOnlyBooleanWrapper disabled =new ReadOnlyBooleanWrapper(this, DISABLED_PROPERTY);;
  {
   disabled.bind(Bindings.isNotEmpty(disablers));
  }

  @Override
  public ReadOnlyBooleanProperty disabledProperty() {
    return disabled.getReadOnlyProperty();
  }

  @Override
  public ObservableSet<Object> disablers() {
    return disablers;
  }
}

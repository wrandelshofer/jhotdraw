/* @(#)ToggleBooleanAction.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app.action.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ProjectView;
import org.jhotdraw8.app.action.AbstractViewAction;
import org.jhotdraw8.util.Resources;

/**
 * This action toggles the state of its boolean property.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ToggleBooleanAction<V extends ProjectView<V>> extends AbstractViewAction<V> {

  public final static String VALUE_PROPERTY = "value";
  private final BooleanProperty value;

  public ToggleBooleanAction(Application<V> app, V view, String id, Resources labels) {
    this(app,view,id,labels,null);
    
  }
  public ToggleBooleanAction(Application<V> app, V view, String id, Resources labels, BooleanProperty value) {
    super(app, view);
    if (labels != null && id != null) {
      labels.configureAction(this, id);
    }
    this.value = value == null ? new SimpleBooleanProperty(this, VALUE_PROPERTY) : value;
this.    value.bindBidirectional(getProperty(SELECTED_KEY));
  }

  @Override
  protected void onActionPerformed(ActionEvent event) {
    setValue(!getValue());
  }

  public BooleanProperty valueProperty() {
    return value;
  }

  public boolean getValue() {
    return value.get();
  }

  public void setValue(boolean newValue) {
    value.set(newValue);
  }

}

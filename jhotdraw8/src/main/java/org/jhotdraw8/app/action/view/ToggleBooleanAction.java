/* @(#)ToggleBooleanAction.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app.action.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractProjectAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Project;

/**
 * This action toggles the state of its boolean property.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ToggleBooleanAction extends AbstractProjectAction<Project> {

  public final static String VALUE_PROPERTY = "value";
  private final BooleanProperty value;

  public ToggleBooleanAction(Application app, Project view, String id, Resources labels) {
    this(app,view,id,labels,null);
    
  }
  public ToggleBooleanAction(Application app, Project view, String id, Resources labels, BooleanProperty value) {
    super(app, view,null);
    if (labels != null && id != null) {
      labels.configureAction(this, id);
    }
    this.value = value == null ? new SimpleBooleanProperty(this, VALUE_PROPERTY) : value;
this.    value.bindBidirectional(selectedProperty());
  }

  @Override
  protected void handleActionPerformed(ActionEvent event, Project project) {
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

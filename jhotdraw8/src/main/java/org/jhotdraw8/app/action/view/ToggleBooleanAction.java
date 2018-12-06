/* @(#)ToggleBooleanAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.view;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javax.annotation.Nullable;

import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractViewControllerAction;
import org.jhotdraw8.util.Resources;

/**
 * This action toggles the state of its boolean property.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ToggleBooleanAction extends AbstractViewControllerAction<Activity> {
    private BooleanProperty value;
  public ToggleBooleanAction(Application app, Activity view, @Nullable String id, @Nullable Resources labels, BooleanProperty value) {
    super(app, view,null);
    if (labels != null && id != null) {
      labels.configureAction(this, id);
    }
    this.value=value;
selectedProperty().bind(value);
  }

  @Override
  protected void handleActionPerformed(ActionEvent event, Activity view) {
    value.set(!value.get());
  }
}

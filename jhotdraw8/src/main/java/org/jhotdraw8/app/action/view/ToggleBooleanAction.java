/* @(#)ToggleBooleanAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
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
 * @version $Id$
 */
public class ToggleBooleanAction extends AbstractProjectAction<Project> {
    private BooleanProperty value;
  public ToggleBooleanAction(Application app, Project view, String id, Resources labels, BooleanProperty value) {
    super(app, view,null);
    if (labels != null && id != null) {
      labels.configureAction(this, id);
    }
    this.value=value;
selectedProperty().bind(value);
  }

  @Override
  protected void handleActionPerformed(ActionEvent event, Project project) {
    value.set(!value.get());
  }
}

/* @(#)ToggleViewPropertyAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.view;

import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractProjectAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Activity;

/**
 * ToggleViewPropertyAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ToggleViewPropertyAction extends AbstractProjectAction<Activity> {

    private static final long serialVersionUID = 1L;
    private BooleanProperty property;
    private final Function<Activity, Node> nodeGetter;

    public ToggleViewPropertyAction(Application app, Activity view, BooleanProperty property, String id, Resources labels) {
        super(app, view,null);
        labels.configureAction(this, id);
        this.property = property;
        this.nodeGetter = null;
selectedProperty().bindBidirectional(  property);
    }

    public ToggleViewPropertyAction(Application app, Activity view, Function<Activity, Node> nodeGetter, String id, Resources labels) {
        super(app, view,null);
        labels.configureAction(this, id);
        this.property = null;
        this.nodeGetter = nodeGetter;
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, Activity project) {
        if (property != null) {
            property.set(!property.get());
        } else {
            Node node = nodeGetter.apply(project);
            node.setVisible(!node.isVisible());
            this.setSelected(node.isVisible());
        }
    }

}

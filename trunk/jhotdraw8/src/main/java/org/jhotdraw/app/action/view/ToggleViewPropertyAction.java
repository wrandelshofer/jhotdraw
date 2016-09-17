/* @(#)ToggleViewPropertyAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.app.action.view;

import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.util.Resources;
import org.jhotdraw.app.ProjectView;

/**
 * ToggleViewPropertyAction.
 * @author Werner Randelshofer
 */
public class ToggleViewPropertyAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;
    private BooleanProperty property;
    private final Function<ProjectView,Node> nodeGetter;

    public ToggleViewPropertyAction(Application app, ProjectView view, BooleanProperty property, String id, Resources labels) {
        super(app, view);
        labels.configureAction(this, id);
        this.property=property;
        this.nodeGetter=null;
        property.addListener((o,oldValue,newValue)->set(SELECTED_KEY, newValue));
        set(SELECTED_KEY,property.get());
    }
    public ToggleViewPropertyAction(Application app, ProjectView view, Function<ProjectView,Node> nodeGetter, String id, Resources labels) {
        super(app, view);
        labels.configureAction(this, id);
        this.property=null;
        this.nodeGetter=nodeGetter;
    }

    @Override
    protected void onActionPerformed(ActionEvent event) {
        if (property!=null)
        
        property.set(!property.get());
        else {
            Node node = nodeGetter.apply(getActiveView());
            node.setVisible(!node.isVisible());
            this.set(SELECTED_KEY, node.isVisible());
        }
    }

}

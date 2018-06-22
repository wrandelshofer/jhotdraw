/* @(#)ToggleViewPropertyAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.view;

import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractViewControllerAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.ViewController;

/**
 * ToggleViewPropertyAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ToggleViewPropertyAction extends AbstractViewControllerAction<ViewController> {

    private static final long serialVersionUID = 1L;
    @Nullable
    private BooleanProperty property;
    @Nullable
    private final Function<ViewController, Node> nodeGetter;

    public ToggleViewPropertyAction(Application app, ViewController view, @NonNull BooleanProperty property, String id, Resources labels) {
        super(app, view,null);
        labels.configureAction(this, id);
        this.property = property;
        this.nodeGetter = null;
selectedProperty().bindBidirectional(  property);
    }

    public ToggleViewPropertyAction(Application app, ViewController view, Function<ViewController, Node> nodeGetter, String id, Resources labels) {
        super(app, view,null);
        labels.configureAction(this, id);
        this.property = null;
        this.nodeGetter = nodeGetter;
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, ViewController view) {
        if (property != null) {
            property.set(!property.get());
        } else {
            Node node = nodeGetter.apply(view);
            node.setVisible(!node.isVisible());
            this.setSelected(node.isVisible());
        }
    }

}

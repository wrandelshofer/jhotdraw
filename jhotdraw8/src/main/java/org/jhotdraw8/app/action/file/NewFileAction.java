/* @(#)NewFileAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import javax.annotation.Nonnull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentOrientedActivity;
import org.jhotdraw8.app.Labels;
import org.jhotdraw8.app.action.AbstractApplicationAction;

/**
 * Creates a new view.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NewFileAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "file.new";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public NewFileAction(Application app) {
        this(app, ID);
    }

    public NewFileAction(Application app, String id) {
        super(app);
        Labels.getLabels().configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent evt, @Nonnull Application app) {
        app.createView().thenAccept(newView-> {
            DocumentOrientedActivity newDOView= (DocumentOrientedActivity) newView;
            app.add(newDOView);
            newDOView.clear().thenRun(() -> {
                newDOView.clearModified();
            });
        });
    }
}

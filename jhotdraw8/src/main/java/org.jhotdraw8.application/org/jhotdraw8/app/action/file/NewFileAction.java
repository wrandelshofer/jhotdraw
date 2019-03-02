/* @(#)NewFileAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.DocumentBasedActivity;
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
        ApplicationLabels.getResources().configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent evt, @Nonnull Application app) {
        app.createView().thenAccept(newView -> {
            DocumentBasedActivity newDOView = (DocumentBasedActivity) newView;
            app.add(newDOView);
            newDOView.clear().thenRun(() -> {
                newDOView.clearModified();
            });
        });
    }
}

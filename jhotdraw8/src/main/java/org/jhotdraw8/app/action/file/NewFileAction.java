/* @(#)NewFileAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.DocumentOrientedViewModel;

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
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent evt, @NonNull Application app) {
        app.createView().thenAccept(newView-> {
            DocumentOrientedViewModel newDOView= (DocumentOrientedViewModel) newView;
            app.add(newDOView);
            newDOView.clear().thenRun(() -> {
                newDOView.clearModified();
            });
        });
    }
}

/*
 * @(#)NewFileAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.FileBasedActivity;
import org.jhotdraw8.app.action.AbstractApplicationAction;

/**
 * Creates a new view.
 * <p>
 *
 * @author Werner Randelshofer
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
    protected void onActionPerformed(ActionEvent evt, @NonNull Application app) {
        app.createActivity().thenAccept(newView -> {
            FileBasedActivity newDOView = (FileBasedActivity) newView;
            app.add(newDOView);
            newDOView.clear().thenRun(() -> {
                newDOView.clearModified();
            });
        });
    }
}

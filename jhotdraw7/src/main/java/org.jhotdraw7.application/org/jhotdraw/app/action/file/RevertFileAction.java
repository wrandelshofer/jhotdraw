/* @(#)ClearFileAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.app.action.file;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw.gui.BackgroundTask;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.gui.event.SheetEvent;
import org.jhotdraw.gui.event.SheetListener;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.io.IOException;
import java.net.URI;

/**
 * Reverts the contents of the active view.
 * <p>
 * This action is called when the user selects the Revert item in the File
 * menu. The menu item is automatically created by the application.
 * <p>
 * If you want this behavior in your application, you have to create it
 * and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RevertFileAction extends AbstractSaveUnsavedChangesAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "file.revert";

    /**
     * Creates a new instance.
     */
    public RevertFileAction(Application app, @Nullable View view) {
        super(app, view);
        ResourceBundleUtil labels = ApplicationLabels.getLabels();
        labels.configureAction(this, ID);
    }

    @Override
    public void doIt(final View view) {
        URI uri = view.getURI();
        view.setEnabled(false);
        view.execute(new BackgroundTask() {
            @Override
            public void construct() throws IOException {
                if (uri == null) {
                    view.clear();
                } else {
                    view.read(uri, null);
                }
            }

            @Override
            public void finished() {
                view.setEnabled(true);
            }
            @Override
            protected void failed(Throwable value) {
                value.printStackTrace();

                ResourceBundleUtil labels = ApplicationLabels.getLabels();
                JSheet.showMessageSheet(view.getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css")
                                + "<b>" + labels.getFormatted("file.load.couldntLoad.message", URIUtil.getName(uri)) + "</b><p>"
                                + ((value == null) ? "" : value),
                        JOptionPane.ERROR_MESSAGE, new SheetListener() {

                            @Override
                            public void optionSelected(SheetEvent evt) {
                                view.clear();
                                view.setEnabled(true);
                            }
                        });
            }
        });
    }
}

/* @(#)ClearRecentFilesMenuAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import java.beans.PropertyChangeListener;
import javafx.event.ActionEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractApplicationAction;

/**
 * Clears (empties) the Recent Files sub-menu in the File menu.
 * <p>
 * This action is called when the user selects the Clear Recent Files item in
 * the Recent Files sub-menu of the File menu. The action and the menu item is
 * automatically created by the application, when the {@code ApplicationModel}
 * provides a {@code LoadFileAction} or a {@code OpenFileAction}.
 *
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class ClearRecentFilesMenuAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "file.clearRecentFiles";

    private PropertyChangeListener applicationListener;

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public ClearRecentFilesMenuAction(Application app) {
        super(app);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
        //     updateEnabled();
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, @NonNull Application app) {
        app.recentUrisProperty().clear();
    }

    /**
     * Installs listeners on the application object. /
     *
     * @Override protected void installApplicationListeners(Application app) {
     * super.installApplicationListeners(app); if (applicationListener == null)
     * { applicationListener = createApplicationListener(); }
     * app.addPropertyChangeListener(applicationListener); } private
     * PropertyChangeListener createApplicationListener() { return new
     * PropertyChangeListener() {
     * @Override public void propertyChange(PropertyChangeEvent evt) { if
     * (evt.getPropertyName() == Application.RECENT_URIS_PROPERTY) { // Strings
     * get interned updateEnabled(); } } }; } /** Installs listeners on the
     * application object. /
     * @Override protected void uninstallApplicationListeners(Application app) {
     * super.uninstallApplicationListeners(app);
     * app.removePropertyChangeListener(applicationListener); }
     *
     * @Override public void actionPerformed(ActionEvent e) {
     * getApplication().clearRecentURIs(); }
     *
     * private void updateEnabled() {
     * setEnabled(getApplication().getRecentURIs().size() > 0);
     *
     * }
     */
}

/* @(#)EditGridAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.app.Application;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.GridConstrainer;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.prefs.PreferencesUtil;

import javax.swing.JDialog;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;

/**
 * EditGridAction.
 * <p>
 * XXX - We shouldn't have a dependency to the application framework
 * from within the drawing framework.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EditGridAction extends AbstractDrawingViewAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "view.editGrid";
    private JDialog dialog;
    private EditGridPanel settingsPanel;
    private PropertyChangeListener propertyChangeHandler;
    private Application app;

    /** Creates a new instance. */
    public EditGridAction(Application app, DrawingEditor editor) {
        super(editor);
        this.app = app;
        ResourceBundleUtil labels = DrawLabels.getLabels();
        labels.configureAction(this, ID);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getDialog().setVisible(true);
    }

   @Override protected void updateViewState() {
        if (getView() != null && settingsPanel != null) {
            settingsPanel.setConstrainer((GridConstrainer) getView().getVisibleConstrainer());
    }
    }

    protected Application getApplication() {
        return app;
        }

    protected JDialog getDialog() {
        if (dialog == null) {
            ResourceBundleUtil labels = DrawLabels.getLabels();
            dialog = new JDialog();
            dialog.setTitle(labels.getString("editGrid"));
            dialog.setResizable(false);
            settingsPanel = new EditGridPanel();
            dialog.add(settingsPanel);
            dialog.pack();
            Preferences prefs = PreferencesUtil.userNodeForPackage(getClass());
            PreferencesUtil.installFramePrefsHandler(prefs, "editGrid", dialog);
            getApplication().addWindow(dialog, null);
        }
            settingsPanel.setConstrainer((GridConstrainer) getView().getVisibleConstrainer());
        return dialog;
    }
}

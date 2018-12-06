/* @(#)MinimizeWindowAction.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.app.action.window;

import javax.annotation.Nullable;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import javax.swing.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;

/**
 * Minimizes the Frame of the current view.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class MinimizeWindowAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "window.minimize";

    /** Creates a new instance. */
    public MinimizeWindowAction(Application app, @Nullable View view) {
        super(app, view);
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.app.Labels"));
        labels.configureAction(this, ID);
    }

    private JFrame getFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(
                getActiveView().getComponent()
                );
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        JFrame frame = getFrame();
        if (frame != null) {
            frame.setExtendedState(frame.getExtendedState() ^ Frame.ICONIFIED);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}

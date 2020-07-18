/* @(#)ArrangeWindowsAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.app.action.window;

import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.gui.Arrangeable;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

/**
 * Changes the arrangement of an {@link Arrangeable} object.
 * <p>
 * If you want this behavior in your application, you have to create it
 * and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 *
 * FIXME - Register as PropertyChangeListener on Arrangeable.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ArrangeWindowsAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    public static final String VERTICAL_ID = "window.arrangeVertical";
    public static final String HORIZONTAL_ID = "window.arrangeHorizontal";
    public static final String CASCADE_ID = "window.arrangeCascade";
    private Arrangeable arrangeable;
    private Arrangeable.Arrangement arrangement;
    
    /** Creates a new instance. */
    public ArrangeWindowsAction(Arrangeable arrangeable, Arrangeable.Arrangement arrangement) {
        this.arrangeable = arrangeable;
        this.arrangement = arrangement;
        ResourceBundleUtil labels = ApplicationLabels.getLabels();
        String labelID;
        switch (arrangement) {
            case VERTICAL : labelID = VERTICAL_ID; break;
            case HORIZONTAL : labelID = HORIZONTAL_ID; break;
            case CASCADE :
            default :
                labelID = CASCADE_ID; break;
        }
        labels.configureAction(this, labelID);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
            arrangeable.setArrangement(arrangement);
    }
}

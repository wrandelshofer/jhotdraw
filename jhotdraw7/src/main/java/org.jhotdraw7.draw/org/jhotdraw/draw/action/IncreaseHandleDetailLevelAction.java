/* @(#)SelectSameAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * SelectSameAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class IncreaseHandleDetailLevelAction extends AbstractSelectedAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "view.increaseHandleDetailLevel";
    /** Creates a new instance. */
    public IncreaseHandleDetailLevelAction(DrawingEditor editor) {
        super(editor);
        ResourceBundleUtil labels =
            DrawLabels.getLabels();
        labels.configureAction(this, ID);
        //putValue(AbstractAction.NAME, labels.getString("editSelectSame"));
        //  putValue(AbstractAction.MNEMONIC_KEY, labels.getString("editSelectSameMnem"));
        updateEnabledState();
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        increaseHandleDetaiLevel();
    }
    
    public void increaseHandleDetaiLevel() {
      DrawingView view =  getView();
        if (view != null) {
            view.setHandleDetailLevel(view.getHandleDetailLevel() + 1);
        }
    }
}

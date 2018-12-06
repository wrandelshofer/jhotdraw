/* @(#)UngroupAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

/**
 * UngroupAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class UngroupAction extends GroupAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.ungroupSelection";
    /** Creates a new instance. */
    private CompositeFigure prototype;

    /** Creates a new instance. */
    public UngroupAction(DrawingEditor editor) {
        super(editor, new GroupFigure(), false);
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.draw.Labels"));
        labels.configureAction(this, ID);
        updateEnabledState();
    }

    public UngroupAction(DrawingEditor editor, CompositeFigure prototype) {
        super(editor, prototype, false);
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.draw.Labels"));
        labels.configureAction(this, ID);
        updateEnabledState();
    }
}

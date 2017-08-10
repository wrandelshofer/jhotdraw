/* @(#)UngroupAction.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;

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
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
        updateEnabledState();
    }

    public UngroupAction(DrawingEditor editor, CompositeFigure prototype) {
        super(editor, prototype, false);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
        updateEnabledState();
    }
}

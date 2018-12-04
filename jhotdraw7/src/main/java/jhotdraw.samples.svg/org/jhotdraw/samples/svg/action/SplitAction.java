/* @(#)SplitPathsAction.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */

package org.jhotdraw.samples.svg.action;

import org.jhotdraw.draw.*;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

/**
 * SplitPathsAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class SplitAction extends CombineAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.splitPath";
    private ResourceBundleUtil labels =
            new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.samples.svg.Labels"));
    
    /** Creates a new instance. */
    public SplitAction(DrawingEditor editor) {
        super(editor, new SVGPathFigure(), false);
        labels.configureAction(this, ID);
    }
    public SplitAction(DrawingEditor editor, SVGPathFigure prototype) {
        super(editor, prototype, false);
        labels.configureAction(this, ID);
    }
}

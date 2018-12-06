/* @(#)SplitPathsAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.svg.action;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.samples.svg.SVGLabels;
import org.jhotdraw.samples.svg.figures.SVGPathFigure;
import org.jhotdraw.util.ResourceBundleUtil;

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
            SVGLabels.getLabels();
    
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

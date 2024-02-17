/* @(#)SelectSameAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import java.util.HashSet;

/**
 * SelectSameAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class SelectSameAction extends AbstractSelectedAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.selectSame";

    /** Creates a new instance. */
    public SelectSameAction(DrawingEditor editor) {
        super(editor);
        ResourceBundleUtil labels = DrawLabels.getLabels();
        labels.configureAction(this, ID);
        updateEnabledState();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        selectSame();
    }

    public void selectSame() {
        HashSet<Class<?>> selectedClasses = new HashSet<Class<?>>();
        for (Figure selected : getView().getSelectedFigures()) {
            selectedClasses.add(selected.getClass());
        }
        for (Figure f : getDrawing().getChildren()) {
            if (selectedClasses.contains(f.getClass())) {
                getView().addToSelection(f);
            }
        }
    }
}

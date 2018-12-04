/* @(#)SelectSameAction.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import java.util.*;
import org.jhotdraw.util.ResourceBundleUtil;

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
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.draw.Labels"));
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

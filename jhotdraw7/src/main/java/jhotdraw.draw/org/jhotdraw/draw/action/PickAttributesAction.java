/* @(#)PickAttributesAction.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.event.FigureSelectionEvent;
import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;
import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * PickAttributesAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PickAttributesAction extends AbstractSelectedAction {
    private static final long serialVersionUID = 1L;

    private Set<AttributeKey<?>> excludedAttributes = new HashSet<AttributeKey<?>>(
            Arrays.asList(new AttributeKey<?>[]{TRANSFORM, TEXT}));

    /** Creates a new instance. */
    public PickAttributesAction(DrawingEditor editor) {
        super(editor);
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.draw.Labels"));
        labels.configureAction(this, "edit.pickAttributes");
        updateEnabledState();
    }

    /**
     * Set of attributes that is excluded when applying default attributes.
     * By default, the TRANSFORM attribute is excluded.
     */
    public void setExcludedAttributes(Set<AttributeKey<?>> a) {
        this.excludedAttributes = a;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        pickAttributes();
    }

    @SuppressWarnings("unchecked")
    public void pickAttributes() {
        DrawingEditor editor = getEditor();
        Collection<Figure> selection = getView().getSelectedFigures();
        if (selection.size() > 0) {
            Figure figure = selection.iterator().next();
            for (Map.Entry<AttributeKey<?>, Object> entry : figure.getAttributes().entrySet()) {
                if (!excludedAttributes.contains(entry.getKey())) {
                    editor.setDefaultAttribute((AttributeKey<Object>)entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public void selectionChanged(FigureSelectionEvent evt) {
        setEnabled(getView().getSelectionCount() == 1);
    }
}

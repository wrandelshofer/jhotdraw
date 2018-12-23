/* @(#)TogglePropertiesPanelAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.odg.action;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.app.action.ActionUtil;
import org.jhotdraw.samples.odg.ODGLabels;
import org.jhotdraw.samples.odg.ODGView;
import org.jhotdraw.util.ResourceBundleUtil;

import org.jhotdraw.annotation.Nullable;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

/**
 * TogglePropertiesPanelAction.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TogglePropertiesPanelAction extends AbstractViewAction {
        private static final long serialVersionUID = 1L;

    /** Creates a new instance. */
    public TogglePropertiesPanelAction(Application app, @Nullable View view) {
        super(app, view);
        setPropertyName("propertiesPanelVisible");
        ResourceBundleUtil labels = ODGLabels.getLabels();
        putValue(AbstractAction.NAME, labels.getString("propertiesPanel"));
    }
    
    /**
     * This method is invoked, when the property changed and when
     * the view changed.
     */
    @Override
    protected void updateView() {
        putValue(ActionUtil.SELECTED_KEY,
                getActiveView() != null &&
                ! getActiveView().isPropertiesPanelVisible()
                );
    }
    
    
    @Override
    public ODGView getActiveView() {
        return (ODGView) super.getActiveView();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        getActiveView().setPropertiesPanelVisible(
                ! getActiveView().isPropertiesPanelVisible()
                );
    }
    
}

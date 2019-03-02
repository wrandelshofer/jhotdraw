/* @(#)ModelerApplication.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler;

import org.jhotdraw8.app.DocumentBasedApplication;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.RevertFileAction;
import org.jhotdraw8.collection.HierarchicalMap;

/**
 * ModelerApplication.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ModelerApplication extends DocumentBasedApplication {

    public ModelerApplication() {
        super();

        setModel(new ModelerApplicationModel());
    }

    @Override
    public HierarchicalMap<String, Action> getActionMap() {
        HierarchicalMap<String, Action> map = super.getActionMap();

        Action a;
        map.put(RevertFileAction.ID, new RevertFileAction(this, null));
        return map;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

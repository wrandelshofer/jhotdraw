/* @(#)GrapherApplication.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.grapher;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jhotdraw8.app.DocumentOrientedApplication;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.RevertFileAction;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.util.Resources;

/**
 * GrapherApplication.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GrapherApplication extends DocumentOrientedApplication {

    public GrapherApplication() {
        super();

        Resources.setVerbose(true);
        setModel(new GrapherApplicationModel());
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

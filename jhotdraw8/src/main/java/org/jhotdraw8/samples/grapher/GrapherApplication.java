/* @(#)GrapherApplication.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.grapher;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.jhotdraw8.app.DocumentOrientedApplication;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.RevertFileAction;
import org.jhotdraw8.app.action.view.ToggleViewPropertyAction;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.util.FontIconDecoder;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.DocumentProject;

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
        try {
            Resources.addDecoder(new FontIconDecoder(".*", "fontawesome:", "/fontawesome-webfont.ttf", 16.0f, GrapherApplication.class));
            Resources.addDecoder(new FontIconDecoder(".*", "materialicons:", "/MaterialIcons-Regular.ttf", 16.0f, GrapherApplication.class));
        } catch (IOException ex) {
            Logger.getLogger(GrapherApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

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

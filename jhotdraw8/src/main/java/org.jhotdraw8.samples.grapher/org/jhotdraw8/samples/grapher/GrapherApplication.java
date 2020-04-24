/*
 * @(#)GrapherApplication.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.grapher;

import org.jhotdraw8.app.DocumentBasedApplication;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.RevertFileAction;
import org.jhotdraw8.collection.HierarchicalMap;

/**
 * GrapherApplication.
 *
 * @author Werner Randelshofer
 */
public class GrapherApplication extends DocumentBasedApplication {

    public GrapherApplication() {
        super();

        GrapherApplicationModel newValue = new GrapherApplicationModel();
        newValue.getSceneStylesheets(); // sets global scene stylesheet for JavaFX
        setModel(newValue);
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

        // The following settings improve font rendering quality (less color fringes around characters).
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.lcdtext", "true");

        launch(args);
    }
}

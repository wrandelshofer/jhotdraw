/* @(#)GrapherApplication.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.samples.grapher;

import java.util.prefs.Preferences;
import org.jhotdraw.app.DocumentOrientedApplication;
import org.jhotdraw.app.SimpleApplicationModel;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.app.action.view.ToggleViewPropertyAction;
import org.jhotdraw.collection.HierarchicalMap;
import org.jhotdraw.draw.action.BringToFrontAction;
import org.jhotdraw.draw.action.SendToBackAction;
import org.jhotdraw.util.Resources;

/**
 *
 * @author Werner Randelshofer
 */
public class GrapherApplication extends DocumentOrientedApplication {

    public GrapherApplication() {
        super();
      SimpleApplicationModel model=  new SimpleApplicationModel("Grapher",()->new GrapherView(),
                                GrapherApplication.class.getResource("GrapherMenuBar.fxml"),
"XML Files","*.xml");
      
        Resources.setVerbose(true);
        
        setModel(model);
    }

    @Override
    public HierarchicalMap<String, Action> getActionMap() {
        HierarchicalMap<String, Action> map = super.getActionMap();
        map.put(SendToBackAction.ID, new SendToBackAction(this, null));
        map.put(BringToFrontAction.ID, new BringToFrontAction(this, null));
        Action a;
        map.put("view.toggleProperties", a = new ToggleViewPropertyAction(this, null, (view) -> ((GrapherView) view).getPropertiesPane(),
                "view.toggleProperties",
                Resources.getResources("org.jhotdraw.samples.grapher.Labels")));
        a.set(Action.SELECTED_KEY, Preferences.userNodeForPackage(GrapherApplication.class).getBoolean("view.propertiesPane.visible", true));
        return map;
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

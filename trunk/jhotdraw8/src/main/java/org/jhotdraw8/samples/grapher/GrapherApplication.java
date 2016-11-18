/* @(#)GrapherApplication.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.samples.grapher;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.jhotdraw8.app.DocumentOrientedApplication;
import org.jhotdraw8.app.SimpleApplicationModel;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.RevertAction;
import org.jhotdraw8.app.action.view.ToggleViewPropertyAction;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.gui.URIExtensionFilter;
import org.jhotdraw8.svg.SvgExportOutputFormat;
import org.jhotdraw8.util.FontIconDecoder;
import org.jhotdraw8.util.Resources;

/**
 * GrapherApplication.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GrapherApplication extends DocumentOrientedApplication {

    public GrapherApplication() {
        super();

        Resources.setVerbose(true);
        try {
            Resources.addDecoder(new FontIconDecoder(".*", "fontawesome:", "/fontawesome-webfont.ttf", 16.0f, GrapherApplication.class));
            Resources.addDecoder(new FontIconDecoder(".*", "materialicons:", "/MaterialIcons-Regular.ttf",16.0f, GrapherApplication.class));
        } catch (IOException ex) {
            Logger.getLogger(GrapherApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

        SimpleApplicationModel model = new SimpleApplicationModel("Grapher", GrapherProjectView::new,
                GrapherApplication.class.getResource("GrapherMenuBar.fxml"),
                "XML Files", null,"*.xml");
        model.getExportExtensionFilters().add(new URIExtensionFilter("SVG",SvgExportOutputFormat.SVG_FORMAT,"*.svg"));
        setModel(model);
    }

    @Override
    public HierarchicalMap<String, Action> getActionMap() {
        HierarchicalMap<String, Action> map = super.getActionMap();
        /*
        map.put(SendToBackAction.ID, new SendToBackAction(this, null));
        map.put(BringToFrontAction.ID, new BringToFrontAction(this, null));
        map.put(GroupAction.ID, new GroupAction(this, null,null));
        map.put(UngroupAction.ID, new UngroupAction(this, null));*/
        Action a;
        map.put(RevertAction.ID, new RevertAction(this,null));
        map.put("view.toggleProperties", a = new ToggleViewPropertyAction(this, null, (view) -> ((GrapherProjectView) view).getPropertiesPane(),
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

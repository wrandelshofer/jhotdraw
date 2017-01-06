/* @(#)GrapherApplicationModel.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.samples.grapher;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.SimpleApplicationModel;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.PrintFileAction;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.gui.URIExtensionFilter;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.svg.SvgExporter;
import org.jhotdraw8.app.DocumentProject;

/**
 * GrapherApplicationModel.
 *
 * @author Werner Randelshofer
 * @version $$Id: GrapherApplicationModel.java 1224 2016-12-18 19:10:23Z
 * rawcoder $$
 */
public class GrapherApplicationModel extends SimpleApplicationModel {

    public GrapherApplicationModel() {
        super("Grapher", GrapherProject::new,
                GrapherApplication.class.getResource("GrapherMenuBar.fxml"),
                "XML Files", null, "*.xml");
        getExportExtensionFilters().add(new URIExtensionFilter("SVG", SvgExporter.SVG_FORMAT, "*.svg"));
        getExportExtensionFilters().add(new URIExtensionFilter("PNG", BitmapExportOutputFormat.PNG_FORMAT, "*.png"));
    }

    @Override
    public HierarchicalMap<String, Action> createApplicationActionMap(Application app) {
        HierarchicalMap<String, Action> map = super.createApplicationActionMap(app);
        map.put(PrintFileAction.ID, new PrintFileAction(app, null));
        return map;
    }

}

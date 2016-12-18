/* @(#)GrapherApplicationModel.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.samples.grapher;

import org.jhotdraw8.app.SimpleApplicationModel;
import org.jhotdraw8.gui.URIExtensionFilter;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.svg.SvgExportOutputFormat;

/**
 * GrapherApplicationModel.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class GrapherApplicationModel extends SimpleApplicationModel {

    public GrapherApplicationModel() {
        super("Grapher", GrapherDocumentView::new,
                GrapherApplication.class.getResource("GrapherMenuBar.fxml"),
                "XML Files", null,"*.xml");
        getExportExtensionFilters().add(new URIExtensionFilter("SVG",SvgExportOutputFormat.SVG_FORMAT,"*.svg"));
        getExportExtensionFilters().add(new URIExtensionFilter("PNG",BitmapExportOutputFormat.PNG_FORMAT,"*.png"));
    }

}

/*
 * @(#)GrapherApplicationModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.grapher;

import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.SimpleApplicationModel;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.ExportFileAction;
import org.jhotdraw8.app.action.file.PrintFileAction;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.draw.gui.DrawingExportOptionsPane;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.io.XMLEncoderOutputFormat;
import org.jhotdraw8.gui.URIExtensionFilter;
import org.jhotdraw8.macos.MacOSPreferences;
import org.jhotdraw8.svg.io.SvgFullSceneGraphExporter;
import org.jhotdraw8.svg.io.SvgTinySceneGraphExporter;

import java.util.ResourceBundle;

import static org.jhotdraw8.io.DataFormats.registerDataFormat;

/**
 * GrapherApplicationModel.
 *
 * @author Werner Randelshofer
 */
public class GrapherApplicationModel extends SimpleApplicationModel {
    public final static DataFormat GRAPHER_FORMAT;

    static {
        DataFormat fmt = DataFormat.lookupMimeType("application/xml+grapher");
        if (fmt == null) {
            fmt = new DataFormat("application/xml+grapher");
        }
        GRAPHER_FORMAT = fmt;
    }

    public GrapherApplicationModel() {
        super("Grapher", GrapherActivity::new,
                GrapherApplication.class.getResource("GrapherMenuBar.fxml"),
                "Grapher Files", GRAPHER_FORMAT, "*.xml");
        getExportExtensionFilters().add(new URIExtensionFilter("SVG Full", registerDataFormat(SvgFullSceneGraphExporter.SVG_MIME_TYPE_WITH_VERSION), "*.svg"));
        getExportExtensionFilters().add(new URIExtensionFilter("SVG Tiny", registerDataFormat(SvgTinySceneGraphExporter.SVG_MIME_TYPE_WITH_VERSION), "*.svg"));
        getExportExtensionFilters().add(new URIExtensionFilter("PNG", registerDataFormat(BitmapExportOutputFormat.PNG_MIME_TYPE), "*.png"));
        getExportExtensionFilters().add(new URIExtensionFilter("XMLSerialized", registerDataFormat(XMLEncoderOutputFormat.XML_SERIALIZER_MIME_TYPE), "*.ser.xml"));
    }

    @Override
    public HierarchicalMap<String, Action> createApplicationActionMap(@NonNull Application app) {
        HierarchicalMap<String, Action> map = super.createApplicationActionMap(app);
        map.put(PrintFileAction.ID, new PrintFileAction(app, null));
        map.put(ExportFileAction.ID, new ExportFileAction(app, DrawingExportOptionsPane::createDialog));
        return map;
    }

    @Override
    public ResourceBundle getResources() {
        return GrapherLabels.getResources().asResourceBundle();
    }

    @NonNull
    @Override
    public ReadOnlyList<String> getSceneStylesheets() {
        final Object value = MacOSPreferences.get(MacOSPreferences.GLOBAL_PREFERENCES, "AppleInterfaceStyle");
        if ("Dark".equals(value)) {
            return ImmutableLists.of(getClass().getResource("dark-theme.css").toString());
        } else {
            return ImmutableLists.emptyList();
        }
    }
}

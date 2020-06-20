/*
 * @(#)ModelerApplication.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler;

import javafx.collections.ObservableMap;
import org.jhotdraw8.app.SimpleFileBasedApplication;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.ExportFileAction;
import org.jhotdraw8.app.action.file.PrintFileAction;
import org.jhotdraw8.app.action.file.RevertFileAction;
import org.jhotdraw8.draw.gui.DrawingExportOptionsPane;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.io.XMLEncoderOutputFormat;
import org.jhotdraw8.gui.FileURIChooser;
import org.jhotdraw8.gui.URIExtensionFilter;
import org.jhotdraw8.svg.io.SvgFullSceneGraphExporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.jhotdraw8.app.action.file.ExportFileAction.EXPORT_CHOOSER_FACTORY_KEY;

/**
 * ModelerApplication.
 *
 * @author Werner Randelshofer
 */
public class ModelerApplication extends SimpleFileBasedApplication {

    @Override
    protected void initResourceBundle() {
        setResourceBundle(ModelerLabels.getResources().asResourceBundle());
    }

    @Override
    protected void initProperties() {
        super.initProperties();
        put(NAME_KEY, "Modeler");
        put(COPYRIGHT_KEY, "Copyright © 2020 The authors and contributors of JHotDraw.");
        put(LICENSE_KEY, "MIT License.");

        List<URIExtensionFilter> exportExtensions = new ArrayList<>();
        exportExtensions.add(new URIExtensionFilter("SVG", SvgFullSceneGraphExporter.SVG_MIME_TYPE, "*.svg"));
        exportExtensions.add(new URIExtensionFilter("PNG", BitmapExportOutputFormat.PNG_MIME_TYPE, "*.png"));
        exportExtensions.add(new URIExtensionFilter("XMLSerialized", XMLEncoderOutputFormat.XML_SERIALIZER_MIME_TYPE, "*.ser.xml"));
        put(EXPORT_CHOOSER_FACTORY_KEY, () -> new FileURIChooser(FileURIChooser.Mode.OPEN, exportExtensions));
    }

    @Override
    public void initActions() {
        super.initActions();
        ObservableMap<String, Action> map = getActions();
        map.put(RevertFileAction.ID, new RevertFileAction(this, null));
        map.put(PrintFileAction.ID, new PrintFileAction(this, null));
        map.put(ExportFileAction.ID, new ExportFileAction(this, DrawingExportOptionsPane::createDialog));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        printModulesOnModulepath();
        printJarsOnClasspath();
        launch(args);
    }

    private static void printModulesOnModulepath() {
        /*
        for (Module module : ModuleLayer.boot().modules()) {
            System.out.println("module: "+module.getDescriptor().toNameAndVersion());
        }*/

    }

    private static void printJarsOnClasspath() {
        //Get the System Classloader
        ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

        //Get the URLs
        URL[] urls;
        try {
            urls = ((URLClassLoader) sysClassLoader).getURLs();
        } catch (ClassCastException e) {
            System.out.println("Error printing jars on classpath. sysClassLoader=" + sysClassLoader);
            return;
        }

        System.out.println("JARS ON CLASSPATH :");
        StringBuilder buf = new StringBuilder();
        for (URL url : urls) {
            buf.setLength(0);
            try {
                URL manifestUrl = new URL("jar", null, url.toString() + "!/META-INF/MANIFEST.MF");
                try (BufferedReader r = new BufferedReader(new InputStreamReader(manifestUrl.openStream(), StandardCharsets.UTF_8))) {
                    r.lines().filter(str -> str.startsWith("Implementation-Vendor:") || str.startsWith("Implementation-Version:")).forEach(
                            str -> buf.append(str.substring(str.indexOf(':') + 1))
                    );
                } catch (IOException e) {
                    // The jar file does not have a manifest
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("Could not construct manifest URL from url=" + url, e);
            }
            System.out.println(url + "," + (buf.length() == 0 ? " unspecified vendor unspecified version" : buf));
        }
        System.out.println("END OF JARS ON CLASSPATH");
    }

}

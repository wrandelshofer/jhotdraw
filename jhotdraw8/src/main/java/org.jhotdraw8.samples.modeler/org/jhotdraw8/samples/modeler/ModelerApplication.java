/*
 * @(#)ModelerApplication.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler;

import javafx.collections.ObservableMap;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.AbstractFileBasedApplication;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.io.XmlEncoderOutputFormat;
import org.jhotdraw8.gui.FileURIChooser;
import org.jhotdraw8.gui.URIExtensionFilter;
import org.jhotdraw8.svg.io.FXSvgFullWriter;

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
public class ModelerApplication extends AbstractFileBasedApplication {

    @Override
    protected void initResourceBundle() {
        setResources(ModelerLabels.getResources());
    }

    @Override
    protected void initProperties() {
        super.initProperties();
        set(NAME_KEY, "Modeler");
        set(COPYRIGHT_KEY, "Copyright © 2020 The authors and contributors of JHotDraw.");
        set(LICENSE_KEY, "MIT License.");

        List<URIExtensionFilter> exportExtensions = new ArrayList<>();
        exportExtensions.add(new URIExtensionFilter("SVG", FXSvgFullWriter.SVG_MIME_TYPE, "*.svg"));
        exportExtensions.add(new URIExtensionFilter("PNG", BitmapExportOutputFormat.PNG_MIME_TYPE, "*.png"));
        exportExtensions.add(new URIExtensionFilter("XMLSerialized", XmlEncoderOutputFormat.XML_SERIALIZER_MIME_TYPE, "*.ser.xml"));
        set(EXPORT_CHOOSER_FACTORY_KEY, () -> new FileURIChooser(FileURIChooser.Mode.OPEN, exportExtensions));
    }

    @Override
    public void initActions(@NonNull ObservableMap<String, Action> map) {
        super.initActions(map);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        printModulesOnModulepath();
        printJarsOnClasspath();
        launch(args);
    }

    @Override
    protected void initFactories() {
        setActivityFactory(ModelerActivity::new);
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

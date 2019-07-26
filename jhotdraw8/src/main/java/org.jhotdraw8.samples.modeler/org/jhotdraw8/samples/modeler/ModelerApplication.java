/*
 * @(#)ModelerApplication.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler;

import org.jhotdraw8.app.DocumentBasedApplication;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.RevertFileAction;
import org.jhotdraw8.collection.HierarchicalMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

/**
 * ModelerApplication.
 *
 * @author Werner Randelshofer
 */
public class ModelerApplication extends DocumentBasedApplication {

    public ModelerApplication() {
        super();

        setModel(new ModelerApplicationModel());
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

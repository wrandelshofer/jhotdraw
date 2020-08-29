/*
 * @(#)module-info.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
import org.jhotdraw8.app.spi.ApplicationResourceBundleProvider;
import org.jhotdraw8.app.spi.NodeReaderProvider;

module org.jhotdraw8.application {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires java.logging;
    requires java.desktop;
    requires transitive java.prefs;
    requires transitive javafx.fxml;
    requires transitive javafx.swing;
    requires jdk.javadoc;

    exports org.jhotdraw8.app;
    exports org.jhotdraw8.annotation;
    exports org.jhotdraw8.app.action;
    exports org.jhotdraw8.app.action.edit;
    exports org.jhotdraw8.app.action.file;
    exports org.jhotdraw8.app.action.view;
    exports org.jhotdraw8.app.spi;
    exports org.jhotdraw8.collection;
    exports org.jhotdraw8.concurrent;
    exports org.jhotdraw8.net;
    exports org.jhotdraw8.util;
    exports org.jhotdraw8.util.prefs;
    exports org.jhotdraw8.util.function;
    exports org.jhotdraw8.beans;
    exports org.jhotdraw8.io;
    exports org.jhotdraw8.binding;
    exports org.jhotdraw8.gui;
    exports org.jhotdraw8.gui.dock;
    exports org.jhotdraw8.event;
    exports org.jhotdraw8.gui.fontchooser;
    exports org.jhotdraw8.text;
    exports org.jhotdraw8.macos;
    exports org.jhotdraw8.app.action.app;
    exports org.jhotdraw8.fxml;
    exports org.jhotdraw8.tree;

    uses java.util.spi.ResourceBundleProvider;
    provides java.util.spi.ResourceBundleProvider with ApplicationResourceBundleProvider;
    opens org.jhotdraw8.gui.fontchooser to javafx.fxml;

    uses NodeReaderProvider;
    provides NodeReaderProvider with org.jhotdraw8.app.spi.FxmlNodeReaderProvider, org.jhotdraw8.app.spi.ImageNodeReaderProvider;
}
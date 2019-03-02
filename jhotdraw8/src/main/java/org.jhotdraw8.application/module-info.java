import org.jhotdraw8.app.spi.ApplicationLabelsProvider;

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
    exports org.jhotdraw8.app.action.view;
    exports org.jhotdraw8.app.spi;
    exports org.jhotdraw8.collection;
    exports org.jhotdraw8.concurrent;
    exports org.jhotdraw8.util;
    exports org.jhotdraw8.util.prefs;
    exports org.jhotdraw8.util.function;
    exports org.jhotdraw8.beans;
    exports org.jhotdraw8.io;
    exports org.jhotdraw8.binding;
    exports org.jhotdraw8.gui;
    exports org.jhotdraw8.event;
    exports org.jhotdraw8.app.action.file;
    exports org.jhotdraw8.gui.dock;
    exports org.jhotdraw8.gui.fontchooser;

    uses ResourceBundleProvider;
    provides ResourceBundleProvider with ApplicationLabelsProvider;
    opens org.jhotdraw8.gui.fontchooser to javafx.fxml;
}
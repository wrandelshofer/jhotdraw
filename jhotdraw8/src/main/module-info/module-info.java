module jhotdraw8 {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires java.logging;
    requires java.desktop;
    requires transitive java.prefs;
    requires transitive javafx.fxml;
    requires transitive javafx.swing;
    requires transitive jsr305;
    requires jdk.javadoc;

    opens org.jhotdraw8.draw.inspector
            to javafx.fxml;
    opens org.jhotdraw8.draw
            to javafx.fxml, javafx.graphics;
    opens org.jhotdraw8.samples.grapher
            to javafx.fxml, javafx.graphics;
    opens org.jhotdraw8.samples.teddy
            to javafx.fxml, javafx.graphics;

    exports org.jhotdraw8.app;
    exports org.jhotdraw8.app.action;
    exports org.jhotdraw8.app.action.view;
    exports org.jhotdraw8.collection;
    exports org.jhotdraw8.concurrent;
    exports org.jhotdraw8.css;
    exports org.jhotdraw8.css.ast;
    exports org.jhotdraw8.draw;
    exports org.jhotdraw8.draw.action;
    exports org.jhotdraw8.draw.constrain;
    exports org.jhotdraw8.draw.figure;
    exports org.jhotdraw8.draw.handle;
    exports org.jhotdraw8.draw.inspector;
    exports org.jhotdraw8.draw.input;
    exports org.jhotdraw8.draw.io;
    exports org.jhotdraw8.draw.tool;
    exports org.jhotdraw8.event;
    exports org.jhotdraw8.svg;
    exports org.jhotdraw8.util;
    exports org.jhotdraw8.util.prefs;
    exports org.jhotdraw8.util.function;
    exports org.jhotdraw8.draw.model;
    exports org.jhotdraw8.tree;
    exports org.jhotdraw8.beans;
    exports org.jhotdraw8.graph;
    exports org.jhotdraw8.draw.key;
    exports org.jhotdraw8.draw.render;
    exports org.jhotdraw8.geom;
    exports org.jhotdraw8.draw.connector;
    exports org.jhotdraw8.text;
    exports org.jhotdraw8.draw.locator;
    exports org.jhotdraw8.io;
    exports org.jhotdraw8.binding;
    exports org.jhotdraw8.gui;
    exports org.jhotdraw8.css.text;
    exports org.jhotdraw8.xml.text;
    exports org.jhotdraw8.xml;
    exports org.jhotdraw8.styleable;
}
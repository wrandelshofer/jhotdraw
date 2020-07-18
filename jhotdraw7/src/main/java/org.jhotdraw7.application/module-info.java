module org.jhotdraw7.application {
    requires transitive java.desktop;
    requires transitive java.prefs;
    requires java.logging;

    exports org.jhotdraw.app;
    opens org.jhotdraw.app;
    exports org.jhotdraw.app.action;
    exports org.jhotdraw.app.action.app;
    exports org.jhotdraw.app.action.edit;
    exports org.jhotdraw.app.action.file;
    exports org.jhotdraw.app.action.view;
    exports org.jhotdraw.app.action.window;
    exports org.jhotdraw.app.osx;
    exports org.jhotdraw.beans;
    exports org.jhotdraw.color;
    exports org.jhotdraw.gui;
    exports org.jhotdraw.gui.datatransfer;
    exports org.jhotdraw.gui.event;
    exports org.jhotdraw.gui.filechooser;
    exports org.jhotdraw.gui.fontchooser;
    exports org.jhotdraw.gui.plaf;
    exports org.jhotdraw.gui.plaf.palette;
    exports org.jhotdraw.gui.plaf.palette.colorchooser;
    exports org.jhotdraw.io;
    exports org.jhotdraw.net;
    exports org.jhotdraw.text;
    exports org.jhotdraw.undo;
    opens org.jhotdraw.undo;
    exports org.jhotdraw.util;
    exports org.jhotdraw.util.prefs;
    exports org.jhotdraw.xml;
    exports org.jhotdraw.xml.css;

    opens org.jhotdraw.app.action.images;
    opens org.jhotdraw.gui.plaf.palette.images;
    exports org.jhotdraw.annotation;
}

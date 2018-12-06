module jhotdraw.application {
    requires transitive java.desktop;
    requires transitive javax.annotation;
    requires transitive java.prefs;
    requires java.logging;

    exports org.jhotdraw.app;
    exports org.jhotdraw.beans;
    exports org.jhotdraw.color;
    exports org.jhotdraw.gui;
    exports org.jhotdraw.gui.event;
    exports org.jhotdraw.gui.fontchooser;
    exports org.jhotdraw.gui.plaf;
    exports org.jhotdraw.gui.plaf.palette;
    exports org.jhotdraw.gui.plaf.palette.colorchooser;
    exports org.jhotdraw.io;
    exports org.jhotdraw.net;
    exports org.jhotdraw.text;
    exports org.jhotdraw.undo;
    exports org.jhotdraw.util;
    exports org.jhotdraw.xml;
    exports org.jhotdraw.gui.datatransfer;
    exports org.jhotdraw.app.action;
    exports org.jhotdraw.util.prefs;
    exports org.jhotdraw.gui.filechooser;
    exports org.jhotdraw.app.action.edit;
    exports org.jhotdraw.app.action.file;
    exports org.jhotdraw.app.action.view;
    exports org.jhotdraw.xml.css;

    opens org.jhotdraw.app.action.images;
    opens org.jhotdraw.gui.plaf.palette.images;
}

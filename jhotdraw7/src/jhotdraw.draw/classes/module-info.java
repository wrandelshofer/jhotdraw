module jhotdraw.draw {
    exports org.jhotdraw.draw;
    exports org.jhotdraw.geom;
    exports org.jhotdraw.draw.io;
    exports org.jhotdraw.draw.print;
    exports org.jhotdraw.draw.action;
    exports org.jhotdraw.draw.liner;
    exports org.jhotdraw.draw.tool;
    exports org.jhotdraw.draw.handle;
    exports org.jhotdraw.draw.connector;
    exports org.jhotdraw.draw.decoration;
    exports org.jhotdraw.draw.layouter;
    exports org.jhotdraw.draw.locator;
    exports org.jhotdraw.draw.event;
    exports org.jhotdraw.draw.gui;

    requires java.desktop;
    requires javax.annotation;
    requires java.prefs;
    requires java.logging;
    requires jhotdraw.app;
    requires jhotdraw.app.nanoxml;
}
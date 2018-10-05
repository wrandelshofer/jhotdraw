module jhotdraw.samples.svg {
    exports org.jhotdraw.samples.svg;
    exports org.jhotdraw.samples.svg.action;
    exports org.jhotdraw.samples.svg.figures;
    exports org.jhotdraw.samples.svg.io;
    requires java.desktop;
    requires jhotdraw.draw;
    requires jhotdraw.app;
    requires javax.annotation;
    requires java.prefs;
    requires nanoxml;
    requires jhotdraw.app.nanoxml;
}
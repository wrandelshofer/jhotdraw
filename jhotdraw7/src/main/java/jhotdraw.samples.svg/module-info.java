module jhotdraw.samples.svg {
    exports org.jhotdraw.samples.svg;
    exports org.jhotdraw.samples.svg.action;
    exports org.jhotdraw.samples.svg.figures;
    exports org.jhotdraw.samples.svg.io;
    requires transitive java.desktop;
    requires transitive jhotdraw.draw;
    requires transitive jhotdraw.application;
    requires transitive java.prefs;
    requires transitive nanoxml;
    requires transitive jhotdraw.nanoxml;

    opens org.jhotdraw.samples.svg.action.images;
}
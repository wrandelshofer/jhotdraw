module org.jhotdraw7.samples.svg {
    exports org.jhotdraw.samples.svg;
    exports org.jhotdraw.samples.svg.action;
    exports org.jhotdraw.samples.svg.figures;
    exports org.jhotdraw.samples.svg.io;
    requires transitive java.desktop;
    requires transitive org.jhotdraw7.draw;
    requires transitive org.jhotdraw7.application;
    requires transitive java.prefs;
    requires transitive net.n3.nanoxml;
    requires transitive org.jhotdraw7.nanoxml;

    opens org.jhotdraw.samples.svg.action.images;
}
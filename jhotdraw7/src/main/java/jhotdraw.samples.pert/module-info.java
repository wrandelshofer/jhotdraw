module jhotdraw.samples.pert {
    requires jhotdraw.application;
    requires java.desktop;
    requires jhotdraw.draw;
    requires java.prefs;
    requires javax.annotation;
    requires jhotdraw.nanoxml;

    opens org.jhotdraw.samples.pert to jhotdraw.application;
    opens org.jhotdraw.samples.pert.images to jhotdraw.application;
}
module jhotdraw.samples.net {
    requires jhotdraw.application;
    requires jhotdraw.draw;
    requires java.desktop;
    requires jhotdraw.nanoxml;
    requires javax.annotation;
    requires java.prefs;

    opens org.jhotdraw.samples.net to jhotdraw.application;
    opens org.jhotdraw.samples.net.images to jhotdraw.application;
}
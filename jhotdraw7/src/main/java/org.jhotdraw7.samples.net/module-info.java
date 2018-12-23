module org.jhotdraw7.samples.net {
    requires org.jhotdraw7.application;
    requires org.jhotdraw7.draw;
    requires java.desktop;
    requires org.jhotdraw7.nanoxml;
    requires java.prefs;

    opens org.jhotdraw.samples.net to org.jhotdraw7.application;
    opens org.jhotdraw.samples.net.images to org.jhotdraw7.application;
}
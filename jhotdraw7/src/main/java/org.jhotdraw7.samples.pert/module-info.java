module org.jhotdraw7.samples.pert {
    requires org.jhotdraw7.application;
    requires java.desktop;
    requires org.jhotdraw7.draw;
    requires java.prefs;
    requires org.jhotdraw7.nanoxml;

    opens org.jhotdraw.samples.pert to org.jhotdraw7.application;
    opens org.jhotdraw.samples.pert.images to org.jhotdraw7.application;
}
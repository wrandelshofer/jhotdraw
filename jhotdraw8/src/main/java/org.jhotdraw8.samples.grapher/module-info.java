module org.jhotdraw8.samples.grapher {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires java.logging;
    requires java.desktop;
    requires transitive java.prefs;
    requires transitive javafx.fxml;
    requires transitive javafx.swing;
    requires jdk.javadoc;
    requires transitive org.jhotdraw8.draw;

    opens org.jhotdraw8.samples.grapher
            to javafx.fxml, javafx.graphics, org.jhotdraw8.application;

}
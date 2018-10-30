module jhotdraw {
    requires javafx.graphics;
    requires javafx.controls;
    requires java.logging;
    requires java.desktop;
    requires java.prefs;
    requires javafx.fxml;
    requires javafx.swing;

    opens org.jhotdraw8.draw.inspector
            to javafx.fxml;
    opens org.jhotdraw8.draw
            to javafx.fxml, javafx.graphics;
    opens org.jhotdraw8.samples.grapher
            to javafx.fxml, javafx.graphics;
    opens org.jhotdraw8.samples.teddy
            to javafx.fxml, javafx.graphics;
}
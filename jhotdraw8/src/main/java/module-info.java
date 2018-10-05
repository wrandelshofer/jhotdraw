module jhotdraw {
    requires javafx.controls;
    requires java.desktop;
    requires java.prefs;
    requires java.logging;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.graphics;

    opens org.jhotdraw8.draw.inspector
            to javafx.fxml;
    opens org.jhotdraw8.draw
            to javafx.fxml, javafx.graphics;
    opens org.jhotdraw8.samples.grapher
            to javafx.fxml, javafx.graphics;
    opens org.jhotdraw8.samples.teddy
            to javafx.fxml, javafx.graphics;
}
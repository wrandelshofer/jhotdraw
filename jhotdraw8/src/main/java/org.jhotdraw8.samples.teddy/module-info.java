module org.jhotdraw8.samples.teddy {
    requires java.desktop;
    requires java.prefs;
    requires org.jhotdraw8.application;

    opens org.jhotdraw8.samples.teddy
            to javafx.fxml, javafx.graphics;
}
import org.jhotdraw8.samples.modeler.spi.ModelerLabelsProvider;

module org.jhotdraw8.samples.modeler {
    requires java.desktop;
    requires org.jhotdraw8.draw;
    requires java.logging;
    requires java.prefs;

    opens org.jhotdraw8.samples.modeler
            to javafx.fxml, javafx.graphics;
    opens org.jhotdraw8.samples.modeler.figure
            to org.jhotdraw8.draw;

    provides ResourceBundleProvider with ModelerLabelsProvider;
}
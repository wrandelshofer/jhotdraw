/*
 * @(#)module-info.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
import org.jhotdraw8.samples.teddy.spi.TeddyResourceBundleProvider;

module org.jhotdraw8.samples.teddy {
    requires java.desktop;
    requires java.prefs;
    requires org.jhotdraw8.application;
    provides java.util.spi.ResourceBundleProvider with TeddyResourceBundleProvider;

    opens org.jhotdraw8.samples.teddy
            to javafx.fxml, javafx.graphics;
}
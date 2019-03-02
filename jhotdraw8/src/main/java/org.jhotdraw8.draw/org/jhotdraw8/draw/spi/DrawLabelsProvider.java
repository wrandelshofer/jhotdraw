package org.jhotdraw8.draw.spi;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.spi.AbstractResourceBundleProvider;

public class DrawLabelsProvider extends AbstractResourceBundleProvider {

    @Override
    public ResourceBundle getBundle(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale);
    }
}

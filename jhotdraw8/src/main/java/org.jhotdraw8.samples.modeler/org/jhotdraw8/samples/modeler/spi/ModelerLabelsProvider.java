package org.jhotdraw8.samples.modeler.spi;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.spi.AbstractResourceBundleProvider;

public class ModelerLabelsProvider extends AbstractResourceBundleProvider {

    @Override
    public ResourceBundle getBundle(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale);
    }
}

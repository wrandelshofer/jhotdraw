/*
 * @(#)GrapherResourceBundleProvider.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.grapher.spi;

import org.jhotdraw8.annotation.NonNull;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.spi.AbstractResourceBundleProvider;

public class GrapherResourceBundleProvider extends AbstractResourceBundleProvider {


    @Override
    public ResourceBundle getBundle(@NonNull String baseName, @NonNull Locale locale) {
        return ResourceBundle.getBundle(baseName, locale);
    }
}

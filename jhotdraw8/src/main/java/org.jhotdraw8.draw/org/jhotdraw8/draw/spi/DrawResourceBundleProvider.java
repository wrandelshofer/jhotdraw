/*
 * @(#)DrawResourceBundleProvider.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.spi;

import org.jhotdraw8.annotation.NonNull;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.spi.AbstractResourceBundleProvider;

public class DrawResourceBundleProvider extends AbstractResourceBundleProvider {

    @Override
    public ResourceBundle getBundle(@NonNull String baseName, @NonNull Locale locale) {
        return ResourceBundle.getBundle(baseName, locale);
    }
}

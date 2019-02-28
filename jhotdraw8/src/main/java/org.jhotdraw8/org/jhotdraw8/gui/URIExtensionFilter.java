/* @(#)URIExtensionFilter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import org.jhotdraw8.annotation.Nonnull;

import java.util.List;

/**
 * URIExtensionFilter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class URIExtensionFilter {

    private final DataFormat format;

    private final FileChooser.ExtensionFilter extensionFilter;

    public URIExtensionFilter(@Nonnull String description, DataFormat format, String... extensions) {
        extensionFilter = new FileChooser.ExtensionFilter(description, extensions);
        this.format = format;
    }

    public URIExtensionFilter(@Nonnull final String description, DataFormat format,
                              final List<String> extensions) {
        extensionFilter = new FileChooser.ExtensionFilter(description, extensions);
        this.format = format;
    }

    public FileChooser.ExtensionFilter getFileChooserExtensionFilter() {
        return extensionFilter;
    }

    public String getDescription() {
        return extensionFilter.getDescription();
    }

    public List<String> getExtensions() {
        return extensionFilter.getExtensions();
    }

    public DataFormat getDataFormat() {
        return format;
    }
}

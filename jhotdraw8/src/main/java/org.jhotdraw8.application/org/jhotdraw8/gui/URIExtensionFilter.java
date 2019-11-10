/*
 * @(#)URIExtensionFilter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.io.DataFormats;

import java.util.List;

/**
 * URIExtensionFilter.
 *
 * @author Werner Randelshofer
 */
public class URIExtensionFilter {

    private final DataFormat format;

    @NonNull
    private final FileChooser.ExtensionFilter extensionFilter;

    public URIExtensionFilter(@NonNull String description, String mimeType, String... extensions) {
        extensionFilter = new FileChooser.ExtensionFilter(description, extensions);
        this.format = DataFormats.registerDataFormat(mimeType);
    }

    public URIExtensionFilter(@NonNull String description, DataFormat format, String... extensions) {
        extensionFilter = new FileChooser.ExtensionFilter(description, extensions);
        this.format = format;
    }

    public URIExtensionFilter(@NonNull final String description, DataFormat format,
                              final List<String> extensions) {
        extensionFilter = new FileChooser.ExtensionFilter(description, extensions);
        this.format = format;
    }

    @NonNull
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

/* @(#)FontChooserModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.fontchooser;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

/**
 * FontChooserModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FontChooserModel {

    private final ListProperty<FontCollection> fontCollections = new SimpleListProperty<>();

    @Nonnull
    public ListProperty<FontCollection> fontCollectionsProperty() {
        return fontCollections;
    }

    public ObservableList<FontCollection> getFontCollections() {
        return fontCollections.get();
    }

    public void setFontCollections(ObservableList<FontCollection> value) {
        fontCollections.set(value);
    }

    @Nullable
    public FontCollection getAllFonts() {
        return fontCollections.isEmpty() ? null : fontCollections.get(0);
    }
}

/*
 * @(#)ClipboardIO.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import javafx.scene.input.Clipboard;
import org.jhotdraw8.annotation.Nullable;

import java.util.List;

/**
 * ClipboardIO.
 *
 * @author Werner Randelshofer
 */
public interface ClipboardIO<T> {

    /**
     * Writes items to the clipboard
     *
     * @param clipboard The clipboard
     * @param items     the items
     */
    void write(Clipboard clipboard, List<T> items);

    /**
     * Returns null if read failed.
     *
     * @param clipboard The clipboard
     * @return izrmd the items
     */
    @Nullable List<T> read(Clipboard clipboard);

    /**
     * Returns true if data from the clibpoard can be imported
     *
     * @param clipboard The clipboard
     * @return true if import is possible
     */
    boolean canRead(Clipboard clipboard);
}

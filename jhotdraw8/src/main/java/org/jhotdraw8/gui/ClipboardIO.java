/* @(#)ClipboardIO.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import java.util.List;
import javafx.scene.input.Clipboard;
import javax.annotation.Nonnull;

/**
 * ClipboardIO.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ClipboardIO<T> {

    /**
     * Writes items to the clipboard
     *
     * @param clipboard The clipboard
     * @param items the items
     */
    void write(@Nonnull Clipboard clipboard,@Nonnull  List<T> items);

    /**
     * Returns null if read failed.
     *
     * @param clipboard The clipboard
     * @return izrmd the items
     */@Nonnull 
    List<T> read(@Nonnull Clipboard clipboard);

    /**
     * Returns true if data from the clibpoard can be imported
     *
     * @param clipboard The clipboard
     * @return true if import is possible
     */
    boolean canRead(@Nonnull Clipboard clipboard);
}

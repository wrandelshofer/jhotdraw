/* @(#)URIChooser.java 
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import java.net.URI;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.DataFormat;
import javafx.stage.Window;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * {@code URIChooser} provides a mechanism for the user to choose an URI.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface URIChooser {

    // **************************************
    // ***** URIChooser Dialog methods *****
    // **************************************
    /**
     * Pops up an URI chooser dialog.
     *
     * @param parent the parent component of the dialog, can be {@code null} ;
     * see {@code showDialog} for details
     * @return the selected URIs if a selection has been made.
     */
    @Nullable
    public URI showDialog(@Nullable Window parent);

    /**
     * Pops up an URI chooser dialog.
     *
     * @param node the parent component of the dialog, can be {@code null} ; see
     * {@code showDialog} for details
     * @return the selected URIs or an empty list if no selection has been made.
     */
    @Nullable
    default public URI showDialog(@Nullable Node node) {
        Scene scene = node == null ? null : node.getScene();
        return showDialog(scene == null ? null : scene.getWindow());
    }

    /**
     * Gets the data format that the user selected.
     *
     * @return data format, or null if the user did not explicitly select a
     * format
     */
    @Nullable
    default DataFormat getDataFormat() {
        return null;
    }
}

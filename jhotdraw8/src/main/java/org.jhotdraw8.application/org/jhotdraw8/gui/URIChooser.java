/*
 * @(#)URIChooser.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.DataFormat;
import javafx.stage.Window;
import org.jhotdraw8.annotation.Nullable;

import java.net.URI;

/**
 * {@code URIChooser} provides a mechanism for the user to choose an URI.
 *
 * @author Werner Randelshofer
 */
public interface URIChooser {

    // **************************************
    // ***** URIChooser Dialog methods *****
    // **************************************

    /**
     * Pops up an URI chooser dialog.
     *
     * @param parent the parent component of the dialog, can be {@code null} ;
     *               see {@code showDialog} for details
     * @return the selected URIs if a selection has been made.
     */
    @Nullable URI showDialog(@Nullable Window parent);

    /**
     * Pops up an URI chooser dialog.
     *
     * @param node the parent component of the dialog, can be {@code null} ; see
     *             {@code showDialog} for details
     * @return the selected URIs or an empty list if no selection has been made.
     */
    default @Nullable URI showDialog(@Nullable Node node) {
        Scene scene = node == null ? null : node.getScene();
        return showDialog(scene == null ? null : scene.getWindow());
    }

    /**
     * Gets the data format that the user selected.
     *
     * @return data format, or null if the user did not explicitly select a
     * format
     */
    default @Nullable DataFormat getDataFormat() {
        return null;
    }
}

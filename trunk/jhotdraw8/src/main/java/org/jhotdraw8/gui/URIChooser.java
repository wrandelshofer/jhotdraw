/* @(#)URIChooser.java
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.gui;

import java.net.URI;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.DataFormat;
import javafx.stage.Window;

/**
 *{@code URIChooser} provides a mechanism for the user to choose an URI.
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
     * @param    parent  the parent component of the dialog,
     *			can be {@code null} ;
     *                  see {@code showDialog}  for details
     * @return   the selected URIs if a selection has been made.
     */
    public URI showDialog(Window parent);

    /**
     * Pops up an URI chooser dialog. 
     *
     * @param    node  the parent component of the dialog,
     *			can be {@code null} ;
     *                  see {@code showDialog}  for details
     * @return   the selected URIs or an empty list if no selection has been made.
     */
    default public URI showDialog(Node node) {
        Scene scene = node == null ? null : node.getScene();
        return showDialog(scene == null ? null : scene.getWindow());
    }
    
    /** Gets the data format that the user selected. 
     * 
     * @return data format, or null if the user did not explicitly select a format
     */
    default DataFormat getDataFormat() {
      return null;
    }
}

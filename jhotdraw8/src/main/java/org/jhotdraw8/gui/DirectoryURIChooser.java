/* @(#)FileURIChooser.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import java.io.File;
import java.net.URI;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/**
 * FileURIChooser.
 *
 * @author Werner Randelshofer
 */
public class DirectoryURIChooser implements URIChooser {

    /**
     * The associated file chooser object.
     */
    private final DirectoryChooser chooser = new DirectoryChooser();

    public DirectoryChooser getDirectoryChooser() {
        return chooser;
    }

    @Override
    public URI showDialog(Window parent) {
        File f = chooser.showDialog(parent);

        return f == null ? null : f.toURI();
    }
}

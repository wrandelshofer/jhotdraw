/* @(#)FileURIChooser.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.gui;

import java.io.File;
import java.net.URI;
import java.util.Optional;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/**
 * FileURIChooser.
 * @author Werner Randelshofer
 */
public class DirectoryURIChooser implements URIChooser {

    /** The associated file chooser object. */
    private final DirectoryChooser chooser = new DirectoryChooser();

    public DirectoryChooser getDirectoryChooser() {
        return chooser;
    }

    @Override
    public Optional<URI> showDialog(Window parent) {
        File f = chooser.showDialog(parent);
        
        return f==null?Optional.empty():Optional.of(f.toURI());
    }
}

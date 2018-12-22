/* @(#)JFileURIChooser.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.gui;

import org.jhotdraw.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import java.io.File;
import java.net.URI;

/**
 * JFileURIChooser.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class JFileURIChooser extends JFileChooser implements URIChooser {
    private static final long serialVersionUID = 1L;

    @Override
    public void setSelectedURI(@Nullable URI uri) {
        setSelectedFile(uri==null?null:new File(uri));
    }

    @Override @Nullable
    public URI getSelectedURI() {
        return getSelectedFile() == null ? null : getSelectedFile().toURI();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
}

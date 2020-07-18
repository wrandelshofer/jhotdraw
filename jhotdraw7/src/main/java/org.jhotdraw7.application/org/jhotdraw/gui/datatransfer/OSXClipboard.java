/* @(#)OSXClipboard.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.gui.datatransfer;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

/**
 * OSXClipboard.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class OSXClipboard extends AWTClipboard {

    public OSXClipboard(Clipboard target) {
        super(target);
    }

    @Override
    public Transferable getContents(Object requestor) {
        Transferable t = super.getContents(requestor);

            try {
                Class<?> c = Class.forName("ch.randelshofer.quaqua.osx.OSXClipboardTransferable");
                @SuppressWarnings("unchecked")
                boolean isAvailable = (Boolean) c.getMethod("isNativeCodeAvailable").invoke(null);
                if (isAvailable) {
                   CompositeTransferable ct = new CompositeTransferable();
                   ct.add(t);
                   ct.add((Transferable) c.getDeclaredConstructor().newInstance());
                   t = ct;
                }
            } catch (Throwable ex) {
                // silently suppress
            }

        return t;
    }
}

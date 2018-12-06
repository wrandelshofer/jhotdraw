/* @(#)SheetListener.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.gui.event;

import java.util.EventListener;

/**
 * SheetListener.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public interface SheetListener extends EventListener {
    /**
     * This method is invoked, when the user selected an option on the
     * JOptionPane or the JFileChooser pane on the JSheet.
     */
    public void optionSelected(SheetEvent evt);
}

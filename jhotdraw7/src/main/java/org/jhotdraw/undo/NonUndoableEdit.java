/* @(#)NonUndoableEdit.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */

package org.jhotdraw.undo;

import javax.swing.undo.*;
/**
 * NonUndoableEdit.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class NonUndoableEdit extends AbstractUndoableEdit {
        private static final long serialVersionUID = 1L;

    /** Creates a new instance. */
    public NonUndoableEdit() {
    }
    
    @Override
    public boolean canUndo() {
        return false;
    }
    @Override
    public boolean canRedo() {
        return false;
    }
}

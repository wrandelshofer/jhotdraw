/* @(#)EditorView.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import org.jhotdraw8.app.Project;

/**
 * An application view which can return an editor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface EditorView {
    DrawingEditor getEditor();
}

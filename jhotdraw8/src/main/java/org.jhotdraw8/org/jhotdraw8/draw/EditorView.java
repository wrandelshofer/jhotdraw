/* @(#)EditorView.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

/**
 * An application view which can return an editor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface EditorView {
    DrawingEditor getEditor();
}

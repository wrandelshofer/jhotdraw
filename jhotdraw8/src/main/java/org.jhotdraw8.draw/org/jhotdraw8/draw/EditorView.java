/*
 * @(#)EditorView.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

/**
 * An application view which can return an editor.
 *
 * @author Werner Randelshofer
 */
public interface EditorView {
    DrawingEditor getEditor();
}

/*
 * @(#)EditorActivity.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

/**
 * An application view which can return an editor.
 *
 * @author Werner Randelshofer
 */
public interface EditorActivity {
    DrawingEditor getEditor();
}

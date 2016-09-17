/* @(#)EditorView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.app.ProjectView;

/**
 * An application view which can return an editor.
 * @author werni
 */
public interface EditorView extends ProjectView{
    DrawingEditor getEditor();
}

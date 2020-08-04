/*
 * @(#)Dock.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

/**
 * A Dock is a {@link DockParent} that can also be added as a
 * {@link DockChild} to another {@link DockParent}.
 */
public interface Dock extends DockChild, DockParent {

}

/*
 * @(#)Track.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

/**
 * A Track is a {@link DockParent} that can also be added as a
 * {@link DockChild} to another {@link DockParent}.
 */
public interface Track extends DockChild, DockParent {

}

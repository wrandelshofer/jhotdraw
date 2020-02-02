package org.jhotdraw8.gui.docknew;

import javafx.collections.ObservableSet;
import javafx.scene.Parent;

public interface RootDock extends Dock {
    Parent getNode();

    /**
     * Only leafs in this set can be dropped on this Dock.
     *
     * @return the drop-able leafs.
     */
    ObservableSet<DockItem> droppableLeafs();

}

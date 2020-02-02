package org.jhotdraw8.gui.docknew;

import org.jhotdraw8.annotation.NonNull;

public class VBoxDock extends AbstractDock {
    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public @NonNull DockAxis getAxis() {
        return DockAxis.Y;
    }
}

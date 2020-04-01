package org.jhotdraw8.gui.dock;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;

public class VBoxDock extends AbstractDock {
    private ScrollPane scrollPane = new ScrollPane();
    private final VBox vbox = new VBox();

    public VBoxDock() {
        getChildren().add(scrollPane);
        scrollPane.setContent(vbox);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        CustomBinding.bindContent(vbox.getChildren(), getDockChildren(),
                DockNode::getNode);
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public @NonNull DockAxis getDockAxis() {
        return DockAxis.Y;
    }

    public boolean isResizesDockChildren() {
        return false;
    }
}

package org.jhotdraw8.gui.docknew;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;

public class HBoxDock extends AbstractDock {
    private ScrollPane scrollPane = new ScrollPane();
    private final HBox hbox = new HBox();

    public HBoxDock() {
        getChildren().add(scrollPane);
        scrollPane.setContent(hbox);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        CustomBinding.bindContent(hbox.getChildren(), getDockChildren(),
                DockNode::getNode);
    }

    @Override
    public @NonNull DockAxis getAxis() {
        return DockAxis.X;
    }

    public boolean isResizesItems() {
        return false;
    }
}

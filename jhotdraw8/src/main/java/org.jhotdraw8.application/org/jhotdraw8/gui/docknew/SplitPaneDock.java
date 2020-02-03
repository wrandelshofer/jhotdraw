package org.jhotdraw8.gui.docknew;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;


public class SplitPaneDock
        extends AbstractDock {
    private final SplitPane splitPane = new SplitPane();

    public SplitPaneDock(Orientation orientation) {
        splitPane.setOrientation(orientation);
        getChildren().add(splitPane);
        /*
        getChildren().add(scrollPane);
        switch (orientation) {
        case HORIZONTAL:
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            break;
        case VERTICAL:
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            break;
        }
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setBorder(Border.EMPTY);
        scrollPane.setStyle("-fx-background-color:transparent;-fx-border-width:0,0;-fx-padding:0;");

         */
        CustomBinding.bindContent(splitPane.getItems(), getDockChildren(),
                DockNode::getNode);
    }


    @NonNull
    @Override
    public DockAxis getAxis() {
        return splitPane.getOrientation() == Orientation.HORIZONTAL ? DockAxis.X : DockAxis.Y;
    }


}

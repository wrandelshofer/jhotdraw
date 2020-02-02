package org.jhotdraw8.gui.docknew;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Border;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.gui.CustomSkin;


public class SplitPaneDock
        extends AbstractDock {
    private final SplitPane splitPane = new SplitPane();
    @NonNull
    private ScrollPane scrollPane = new ScrollPane(splitPane);

    public SplitPaneDock(Orientation orientation) {
        setSkin(new CustomSkin<>(this));
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
        splitPane.setOrientation(orientation);
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
        CustomBinding.bindContent(splitPane.getItems(), getChildComponents(),
                k -> (Node) k);
    }


    @NonNull
    @Override
    public DockAxis getAxis() {
        return splitPane.getOrientation() == Orientation.HORIZONTAL ? DockAxis.X : DockAxis.Y;
    }


    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        scrollPane.resizeRelocate(0, 0, getWidth(), getHeight());
    }
}

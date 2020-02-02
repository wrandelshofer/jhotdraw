package org.jhotdraw8.gui.docknew;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Border;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.gui.CustomSkin;

import java.util.ArrayList;
import java.util.List;


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
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            break;
        case VERTICAL:
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            break;
        }
        scrollPane.setBorder(Border.EMPTY);
        scrollPane.setStyle("-fx-background-color:transparent;-fx-border-width:0,0;-fx-padding:0;");
        getChildComponents().addListener(this::onChildrenChanged);
    }

    @NonNull
    private void onChildrenChanged(ListChangeListener.Change<? extends DockComponent> change) {
        ObservableList<Node> dest = splitPane.getItems();
        while (change.next()) {
            int from = change.getFrom();
            int to = change.getTo();
            List<? extends DockComponent> s = change.getList();
            if (change.wasPermutated()) {
                dest.remove(from, to);
                List<Node> tabs = new ArrayList<>(to - from);
                for (int i = from; i < to; i++) {
                    tabs.add(s.get(i).getContent());
                }
                dest.addAll(from, tabs);
            }
            if (change.wasRemoved()) {
                dest.remove(from, from + change.getRemovedSize());
                for (DockComponent removed : change.getRemoved()) {
                    if (removed.getParentComponent() == this) {
                        removed.setParentComponent(null);
                    }
                }

            }
            if (change.wasAdded()) {
                List<Node> tabs = new ArrayList<>(to - from);
                for (DockComponent n : change.getAddedSubList()) {
                    n.setParentComponent(this);
                    tabs.add(n.getContent());
                }
                dest.addAll(from, tabs);

            }
        }
    }

    @Override
    public boolean isEditable() {
        return true;
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

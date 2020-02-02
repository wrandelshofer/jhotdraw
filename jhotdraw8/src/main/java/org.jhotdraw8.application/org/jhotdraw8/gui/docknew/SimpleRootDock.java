package org.jhotdraw8.gui.docknew;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.gui.CustomSkin;
import org.jhotdraw8.gui.RectangleTransition;

import java.util.ArrayDeque;
import java.util.function.Supplier;

public class SimpleRootDock
        extends AbstractDock
        implements RootDock {

    private final SetProperty<DockItem> droppableLeafs = new SimpleSetProperty<>(this, "droppableLeafs", null);
    private final ObjectProperty<DockComponent> onlyChild = new SimpleObjectProperty<>();
    private final Rectangle dropRect = new Rectangle(0, 0, 0, 0);
    @Nullable
    RectangleTransition transition;
    @NonNull
    private Insets rootDrawnDropZoneInsets = new Insets(10, 10, 10, 10);
    @NonNull
    private Insets dockDropZoneInsets = new Insets(40, 40, 40, 40);
    @NonNull
    private Insets rootDropZoneInsets = new Insets(20, 20, 20, 20);
    @NonNull
    private Insets dockDrawnDropZoneInsets = new Insets(20, 20, 20, 20);

    private final StackPane stackPane = new StackPane();
    private final BorderPane contentPane = new BorderPane();
    private Supplier<Dock> rootXSupplier = () -> new SplitPaneDock(Orientation.HORIZONTAL);
    private Supplier<Dock> rootYSupplier = () -> new SplitPaneDock(Orientation.VERTICAL);
    private Supplier<Dock> subXSupplier = () -> new SplitPaneDock(Orientation.HORIZONTAL);
    private Supplier<Dock> subYSupplier = () -> new SplitPaneDock(Orientation.VERTICAL);
    private Supplier<Dock> zSupplier = TabPaneDock::new;

    public SimpleRootDock() {
        setSkin(new CustomSkin<>(this));
        stackPane.getChildren().add(contentPane);
        getChildren().add(stackPane);
        onlyChild.addListener(this::onRootChanged);
        dropRect.setOpacity(0.4);
        dropRect.setManaged(false);
        dropRect.setMouseTransparent(true);
        dropRect.setVisible(false);
        setOnDragOver(this::onDragOver);
        setOnDragExited(this::onDragExit);
        setOnDragDropped(this::onDragDrop);
        getChildComponents().addListener(this::onChildrenChanged);
        ChangeListener<DockComponent> changeListener = (o, oldv, newv) -> {
            throw new AssertionError("root cannot be added to any other node");
        };
        parentComponent.addListener(changeListener);

    }


    private void onChildrenChanged(ListChangeListener.Change<? extends DockComponent> c) {
        if (c.getList().size() > 1) {
            throw new IllegalArgumentException("RootDock can only have one child");
        }
        onlyChild.set(c.getList().isEmpty() ? null : c.getList().get(0));
    }


    @Override
    public @NonNull DockAxis getAxis() {
        return DockAxis.Z;
    }

    @NonNull
    protected void onRootChanged(ObservableValue<? extends DockComponent> observable, DockComponent oldValue, DockComponent newValue) {
        if (oldValue != null) {
            contentPane.centerProperty().unbind();
            contentPane.centerProperty().set(null);
        }
        if (newValue != null) {
            contentPane.centerProperty().bind(newValue.contentReadOnlyProperty());
        }
    }

    @Override
    public Parent getNode() {
        return this;
    }

    @Override
    public ObservableSet<DockItem> droppableLeafs() {
        return droppableLeafs;
    }


    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        stackPane.resizeRelocate(0, 0, getWidth(), getHeight());
    }

    private void onDragDrop(@NonNull DragEvent e) {
        dropRect.setVisible(false);
        getChildren().remove(dropRect);
        if (!isAcceptable(e)) {
            return;
        }

        DockItem droppedTab = DockItem.getDraggedItem();


        DragData dragData = computeDragData(e);
        if (dragData.zone != null) {
            e.acceptTransferModes(TransferMode.MOVE);
            onDockLeafDropped(dragData.pickedDock, droppedTab, dragData.zone);

        }
        e.consume();
    }


    private void onDragExit(DragEvent e) {
        dropRect.setVisible(false);
    }

    private static class DragData {
        final Dock pickedDock;
        final DropZone zone;
        final Bounds bounds;
        final Insets insets;

        public DragData(Dock pickedDock, DropZone zone, Bounds bounds, Insets insets) {
            this.pickedDock = pickedDock;
            this.zone = zone;
            this.bounds = bounds;
            this.insets = insets;
        }
    }

    private DragData computeDragData(@NonNull DragEvent e) {
        Bounds bounds = getBoundsInLocal();

        Dock pickedDock;
        if (subtractInsets(bounds, rootDropZoneInsets).contains(e.getX(), e.getY())) {
            PickResult pick = e.getPickResult();
            Node pickedNode = pick.getIntersectedNode();
            while (pickedNode != this && pickedNode != null
                    && !(pickedNode instanceof Dock)) {
                pickedNode = pickedNode.getParent();
            }
            pickedDock = (pickedNode instanceof Dock) ? (Dock) pickedNode : null;
        } else {
            pickedDock = this;
        }

        DropZone zone = null;
        Insets insets = rootDrawnDropZoneInsets;
        if (pickedDock == this) {
            if (getChildComponents().isEmpty()) {
                zone = DropZone.CENTER;
            } else {
                zone = getZone(e.getX(), e.getY(), getBoundsInLocal(), rootDropZoneInsets);
                if (zone == DropZone.CENTER) {
                    zone = null;
                }
            }
        } else if (pickedDock != null) {
            insets = dockDrawnDropZoneInsets;
            bounds = sceneToLocal(pickedDock.getContent().localToScene(pickedDock.getContent().getBoundsInLocal()));
            zone = getZone(e.getX(), e.getY(), bounds, dockDropZoneInsets);
            if (zone == DropZone.CENTER && (!pickedDock.isEditable()
                    || pickedDock.getAxis() != DockAxis.Z)) {
                zone = null;
            }
        } else {
            insets = null;
        }
        return new DragData(pickedDock, zone, bounds, insets);
    }

    private void onDragOver(@NonNull DragEvent e) {
        if (!isAcceptable(e)) {
            return;
        }

        DragData dragData = computeDragData(e);
        updateDropRect(dragData.zone, dragData.bounds, dragData.insets);

        if (dragData.zone != null) {
            e.acceptTransferModes(TransferMode.MOVE);
            e.consume();
        }
    }

    private void updateDropRect(@Nullable final DropZone zone, @NonNull Bounds bounds, @NonNull Insets ins) {

        if (zone == null) {
            dropRect.setVisible(false);
            return;
        }
        if (dropRect.getParent() == null) {
            getChildren().add(dropRect);
        }
        BoundingBox rect;
        switch (zone) {
        case BOTTOM:
            rect = new BoundingBox(
                    bounds.getMinX(),
                    bounds.getMaxY() - ins.getBottom(),
                    bounds.getWidth(),
                    ins.getBottom());
            break;
        case LEFT:
            rect = new BoundingBox(
                    bounds.getMinX(),
                    bounds.getMinY(),
                    ins.getLeft(),
                    bounds.getHeight());
            break;
        case RIGHT:
            rect = new BoundingBox(
                    bounds.getMaxX() - ins.getRight(),
                    bounds.getMinY(),
                    ins.getRight(),
                    bounds.getHeight());
            break;
        case TOP:
            rect = new BoundingBox(
                    bounds.getMinX(),
                    bounds.getMinY(),
                    bounds.getWidth(),
                    ins.getTop());
            break;
        case CENTER:
        default:
            rect = new BoundingBox(
                    bounds.getMinX() + ins.getLeft(),
                    bounds.getMinY() + ins.getTop(),
                    bounds.getWidth() - ins.getLeft() - ins.getRight(),
                    bounds.getHeight() - ins.getTop() - ins.getBottom());
            break;
        }
        if (dropRect.isVisible() && !dropRect.getBoundsInLocal().isEmpty()) {
            if (transition == null || !transition.getToBounds().equals(rect)) {
                if (transition != null) {
                    transition.stop();
                }
                transition = new RectangleTransition(Duration.millis(200), dropRect, dropRect.getBoundsInLocal(), rect);
                transition.play();
                transition.setOnFinished(evt -> transition = null);
            }
        } else {
            dropRect.setVisible(true);
            dropRect.setX(rect.getMinX());
            dropRect.setY(rect.getMinY());
            dropRect.setWidth(rect.getWidth());
            dropRect.setHeight(rect.getHeight());
        }
    }

    private boolean isAcceptable(@NonNull DragEvent e) {
        DockItem draggedItem = DockItem.getDraggedItem();
        return e.getDragboard().getContentTypes().contains(DockItem.DRAGGED_LEAF_DATA_FORMAT)
                //    && e.getGestureSource() != null
                && draggedItem != null
                && (droppableLeafs.get() == null || droppableLeafs.contains(draggedItem));
    }

    @Nullable
    private DropZone getZone(double x, double y, @NonNull Bounds b, @NonNull Insets insets) {
        if (y - b.getMinY() < insets.getTop() && b.getHeight() > insets.getTop() + insets.getBottom()) {
            return DropZone.TOP;
        } else if (b.getMaxY() - y < insets.getBottom() && b.getHeight() > insets.getTop() + insets.getBottom()) {
            return DropZone.BOTTOM;
        } else if (x - b.getMinX() < insets.getLeft() && b.getWidth() > insets.getLeft() + insets.getRight()) {
            return DropZone.LEFT;
        } else if (b.getMaxX() - x < insets.getRight() && b.getWidth() > insets.getLeft() + insets.getRight()) {
            return DropZone.RIGHT;
        } else {
            return b.contains(x, y) ? DropZone.CENTER : null;
        }
    }

    private void onDockLeafDropped(@Nullable Dock dropTarget, @NonNull DockItem leaf, @NonNull DropZone zone) {
        Dock dragSource = leaf.getParentComponent();
        if (dragSource == null) {
            return; // can't do dnd
        }
        int index = dragSource.getChildComponents().indexOf(leaf);
        dragSource.getChildComponents().remove(index);
        System.out.println("---adding---");
        dumpTree(this, 0);
        if (!addLeafToParent(leaf, dropTarget, zone)) {
            // failed to add revert to previous state
            dragSource.getChildComponents().add(index, leaf);
        } else {
            System.out.println("---removing--- dragSource = " + dragSource);
            removeUnusedComposites(dragSource);
            dumpTree(this, 0);
        }
    }

    private void dumpTree(DockComponent node, int indent) {
        if (node != null) {
            for (int i = 0; i < indent; i++) {
                System.out.print('.');
            }
            System.out.println(node);
            for (DockComponent child : node.getChildComponentsReadOnly()) {
                dumpTree(child, indent + 1);
            }


        }
    }

    private void removeUnusedComposites(Dock node) {
        RootDock root = node.getRoot();
        if (root == null) {
            return;
        }

        ArrayDeque<Dock> todo = new ArrayDeque<>();
        todo.add(node);

        while (!todo.isEmpty()) {
            Dock composite = todo.remove();
            Dock parent = composite.getParentComponent();
            if (composite.getChildComponents().isEmpty()) {
                // Remove composite if it has zero children
                if (parent != null) {
                    parent.getChildComponents().remove(composite);
                    todo.add(parent);
                }
            } else if (composite.getAxis() != DockAxis.Z && composite.getChildComponents().size() == 1) {
                // Replace xy composite with its child if xy composite has one child
                DockComponent onlyChild = composite.getChildComponents().get(0);
                parent.getChildComponents().set(parent.getChildComponents().indexOf(composite), onlyChild);
                todo.add(parent);
            }
        }
    }

    private DockComponent getOnlyChild() {
        return getChildComponents().isEmpty() ? null : getChildComponents().get(0);
    }

    private void setOnlyChild(@Nullable DockComponent o) {
        getChildComponents().clear();
        if (o != null) {
            getChildComponents().add(o);
        }

    }

    private DockAxis getZoneAxis(DropZone zone) {
        switch (zone) {

        case TOP:
        case BOTTOM:
            return DockAxis.Y;
        case LEFT:
        case RIGHT:
            return DockAxis.X;
        case CENTER:
        default:
            return DockAxis.Z;
        }
    }

    private boolean addLeafToParent(@NonNull DockItem leaf, @NonNull Dock parent, @NonNull DropZone zone) {
        DockAxis zoneAxis = getZoneAxis(zone);

        // The parent is either the root, or a non-root
        if (parent == this) {
            if (this.getChildComponents().isEmpty()) {
                Dock newLeafDock = zSupplier.get();
                newLeafDock.getChildComponents().add(leaf);
                this.getChildComponents().add(newLeafDock);
                return true;
            }
            DockComponent oldChild = this.getOnlyChild();
            switch (zoneAxis) {
            case X:
                this.setOnlyChild(rootXSupplier.get());
                break;
            case Y:
                this.setOnlyChild(rootYSupplier.get());
                break;
            case Z:
            default:
                return false;
            }
            DockComponent movedChildDock;
            if ((oldChild instanceof DockItem) && ((DockItem) oldChild).getText() != null) {
                movedChildDock = zSupplier.get();
                ((Dock) movedChildDock).getChildComponents().add(oldChild);
            } else {
                movedChildDock = oldChild;
            }
            Dock newParent = (Dock) getOnlyChild();
            addToParentInZone(movedChildDock, newParent, zone);
            Dock newLeafDock = zSupplier.get();
            newLeafDock.getChildComponents().add(leaf);
            addToParentInZone(newLeafDock, newParent, zone);
            return true;
        } else {
            if (zoneAxis == DockAxis.Z && parent.getAxis() == DockAxis.Z) {
                parent.getChildComponents().add(leaf);
                return true;
            }
            Dock grandParent;
            switch (zoneAxis) {
            case X:
                grandParent = subXSupplier.get();
                break;
            case Y:
                grandParent = subYSupplier.get();
                break;
            case Z:
            default:
                return false;
            }
            parent.getParentComponent().getChildComponents().set(parent.getParentComponent().getChildComponents().indexOf(parent), grandParent);
            grandParent.getChildComponents().add(parent);
            Dock newLeafDock = zSupplier.get();
            newLeafDock.getChildComponents().add(leaf);
            addToParentInZone(newLeafDock, grandParent, zone);
            return true;

        }
    }

    private void addToParentInZone(DockComponent child, @NonNull Dock parent, @NonNull DropZone zone) {
        Dock oldParent = getParentComposite(child);
        if (oldParent != null) {
            oldParent.getChildComponents().remove(child);
        }

        switch (zone) {
        case TOP:
        case LEFT:
            parent.getChildComponents().add(0, child);
            break;
        case RIGHT:
        case BOTTOM:
        default:
            parent.getChildComponents().add(child);
        }
    }

    @Nullable
    private Dock getParentComposite(@NonNull DockComponent c) {
        for (Node node = c == null ? null : c.getContent().getParent(); node != null; node = node.getParent()) {
            if (node instanceof Dock) {
                return (Dock) node;
            }
        }
        return null;
    }

    @Nullable
    private RootDock getDock(@NonNull DockComponent c) {
        for (Node node = c.getContent().getParent(); node != null; node = node.getParent()) {
            if (node instanceof RootDock) {
                return (RootDock) node;
            }
        }
        return null;
    }

    @NonNull
    static Bounds subtractInsets(@NonNull Bounds b, @NonNull Insets i) {
        return new BoundingBox(
                b.getMinX() + i.getLeft(),
                b.getMinY() + i.getTop(),
                b.getWidth() - i.getLeft() - i.getRight(),
                b.getHeight() - i.getTop() - i.getBottom()
        );
    }

}

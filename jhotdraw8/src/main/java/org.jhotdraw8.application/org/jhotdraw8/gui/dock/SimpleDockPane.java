package org.jhotdraw8.gui.dock;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.gui.RectangleTransition;

import java.util.ArrayDeque;
import java.util.function.Supplier;

/**
 * A simple implementation of the {@link DockPane} interface.
 * <p>
 * This DockPane only shows the first child dock.
 * <p>
 * FIXME DockPane should allow to select which child that it shows.
 */
public class SimpleDockPane
        extends AbstractDockPane {

    @NonNull
    private final static Insets rootDrawnDropZoneInsets = new Insets(10, 10, 10, 10);
    @NonNull
    private final static Insets dockDropZoneInsets = new Insets(40, 40, 40, 40);
    @NonNull
    private final static Insets rootDropZoneInsets = new Insets(20, 20, 20, 20);
    @NonNull
    private final static Insets dockDrawnDropZoneInsets = new Insets(20, 20, 20, 20);
    @NonNull
    private final Rectangle dropRect = new Rectangle(0, 0, 0, 0);
    @NonNull
    private final StackPane stackPane = new StackPane();
    @NonNull
    private final BorderPane contentPane = new BorderPane();
    @Nullable
    private RectangleTransition transition;
    @NonNull
    private Supplier<Dock> rootXSupplier = () -> new SplitPaneDock(Orientation.HORIZONTAL);
    @NonNull
    private Supplier<Dock> rootYSupplier = () -> new SplitPaneDock(Orientation.VERTICAL);
    @NonNull
    private Supplier<Dock> subXSupplier = HBoxDock::new;
    @NonNull
    private Supplier<Dock> subYSupplier = VBoxDock::new;
    @NonNull
    private Supplier<Dock> zSupplier = TabPaneDock::new;

    public SimpleDockPane() {
        stackPane.getChildren().add(contentPane);
        getChildren().add(stackPane);
        dropRect.setOpacity(0.4);
        dropRect.setManaged(false);
        dropRect.setMouseTransparent(true);
        dropRect.setVisible(false);
        setOnDragOver(this::onDragOver);
        setOnDragExited(this::onDragExit);
        setOnDragDropped(this::onDragDrop);
        dockChildren.addListener(this::onRootChanged);
    }

    @NonNull
    private static Bounds subtractInsets(@NonNull Bounds b, @NonNull Insets i) {
        return new BoundingBox(
                b.getMinX() + i.getLeft(),
                b.getMinY() + i.getTop(),
                b.getWidth() - i.getLeft() - i.getRight(),
                b.getHeight() - i.getTop() - i.getBottom()
        );
    }

    private Dock createDock(@NonNull DockAxis zoneAxis, @Nullable Dock parent) {
        Supplier<Dock> supplier;
        switch (zoneAxis) {
        case X:
            supplier = rootXSupplier;
            break;
        case Y:
            supplier = rootYSupplier;
            break;
        case Z:
            supplier = zSupplier;
            break;
        default:
            throw new IllegalStateException("Unexpected value: " + zoneAxis);
        }
        return supplier.get();
    }


    private boolean addToParent(@NonNull Dockable dockable, @NonNull Dock parent, @NonNull DropZone zone) {
        DockNode child;
        DockAxis zoneAxis = getZoneAxis(zone);

        // Make sure that the parent of the dockable is a z-axis dock
        if ((parent instanceof DockPane) || zoneAxis != DockAxis.Z) {
            child = createDock(DockAxis.Z, parent);
            ((Dock) child).getDockChildren().add(dockable);
        } else {
            child = dockable;
        }

        // Add to parent if axis matches
        if (parent.getDockAxis() == zoneAxis) {
            addToZoneInParent(child, parent, zone, -1);
            return true;
        }

        // Add to grand parent if grand parent's axis match
        Dock grandParent = parent.getDockParent();
        if (grandParent != null && grandParent.getDockAxis() == zoneAxis) {
            addToZoneInParent(child, grandParent, zone, grandParent.getDockChildren().indexOf(parent));
            return true;
        }
        // Add to new grand parent
        Dock newGrandParent = createDock(zoneAxis, grandParent);
        if (grandParent == null) {
            DockNode removed = getDockChildren().set(0, newGrandParent);
            if (removed != null) {
                addToZoneInParent(removed, newGrandParent, zone, -1);
            }
        } else {
            grandParent.getDockChildren().set(grandParent.getDockChildren().indexOf(parent), newGrandParent);
            newGrandParent.getDockChildren().add(parent);
        }
        addToZoneInParent(child, newGrandParent, zone, -1);
        return true;
    }

    private void addToZoneInParent(DockNode child, @NonNull Dock parent, @NonNull DropZone zone, int insertionIndex) {
        Dock oldParent = child.getDockParent();
        if (oldParent != null) {
            oldParent.getDockChildren().remove(child);
        }

        ObservableList<DockNode> children = parent.getDockChildren();
        switch (zone) {
        case TOP:
        case LEFT:
            children.add(insertionIndex == -1 ? 0 : insertionIndex, child);
            break;
        case RIGHT:
        case BOTTOM:
        default:
            children.add(insertionIndex == -1 ? children.size() : insertionIndex + 1, child);
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
            if (getDockChildrenReadOnly().isEmpty()) {
                zone = DropZone.CENTER;
            } else {
                zone = getZone(e.getX(), e.getY(), getBoundsInLocal(), rootDropZoneInsets);
                if (zone == DropZone.CENTER) {
                    zone = null;
                }
            }
        } else if (pickedDock != null) {
            insets = dockDrawnDropZoneInsets;
            bounds = sceneToLocal(pickedDock.getNode().localToScene(pickedDock.getNode().getBoundsInLocal()));
            zone = getZone(e.getX(), e.getY(), bounds, dockDropZoneInsets);
            if (zone == DropZone.CENTER && (!pickedDock.isEditable()
                    || pickedDock.getDockAxis() != DockAxis.Z)) {
                zone = null;
            }
        } else {
            insets = null;
        }
        return new DragData(pickedDock, zone, bounds, insets);
    }

    private void dumpTree(DockNode node, int indent) {
        if (node != null) {
            for (int i = 0; i < indent; i++) {
                System.out.print('.');
            }
            System.out.println(node);
            for (DockNode child : node.getDockChildrenReadOnly()) {
                dumpTree(child, indent + 1);
            }


        }
    }

    @Override
    public @NonNull DockAxis getDockAxis() {
        return DockAxis.Z;
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

    private boolean isAcceptable(@NonNull DragEvent e) {
        Dockable draggedItem = DockPane.getDraggedDockable();
        return e.getDragboard().getContentTypes().contains(DockPane.DOCKABLE_DATA_FORMAT)
                //    && e.getGestureSource() != null
                && draggedItem != null
                && (getDockablePredicate().test(draggedItem));
    }

    @Override
    public boolean isResizesDockChildren() {
        return true;
    }

    private void onDockableDropped(@Nullable Dock dropTarget, @NonNull Dockable dockable, @NonNull DropZone zone) {
        DockPane leafRoot = dockable.getDockPane();
        Dock dragSource = dockable.getDockParent();
        if (dragSource == null
                || dropTarget == null
                || (dropTarget instanceof DockPane) && (dropTarget != this)) {
            return; // can't do dnd
        }
        int index = dragSource.getDockChildren().indexOf(dockable);
        dragSource.getDockChildren().remove(index);
        if (!addToParent(dockable, dropTarget, zone)) {
            // failed to add revert to previous state
            dragSource.getDockChildren().add(index, dockable);
        } else {
            System.out.println("-----add-----");
            dumpTree(this, 0);
            System.out.println("-----cleanup-----");
            removeUnusedDocks(dragSource);
            dumpTree(this, 0);
            if (leafRoot != this) {
                dumpTree(leafRoot, 0);
            }
        }
    }

    private void onDragDrop(@NonNull DragEvent e) {
        dropRect.setVisible(false);
        getChildren().remove(dropRect);
        if (!isAcceptable(e)) {
            return;
        }

        Dockable droppedTab = DockPane.getDraggedDockable();


        DragData dragData = computeDragData(e);
        if (dragData.zone != null) {
            e.acceptTransferModes(TransferMode.MOVE);
            onDockableDropped(dragData.pickedDock, droppedTab, dragData.zone);

        }
        e.consume();
    }

    private void onDragExit(DragEvent e) {
        dropRect.setVisible(false);
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

    protected void onRootChanged(ListChangeListener.Change<? extends DockNode> c) {
        contentPane.centerProperty().unbind();
        if (c.getList().isEmpty()) {
            contentPane.centerProperty().set(null);
        } else {
            contentPane.centerProperty().bind(c.getList().get(0).nodeProperty());
        }

    }

    private void removeUnusedDocks(Dock node) {
        DockPane root = node.getDockPane();
        if (root == null) {
            return;
        }

        ArrayDeque<Dock> todo = new ArrayDeque<>();
        todo.add(node);

        while (!todo.isEmpty()) {
            Dock dock = todo.remove();
            Dock parent = dock.getDockParent();
            if (parent != null) {
                if (dock.getDockChildrenReadOnly().isEmpty()) {
                    // Remove composite if it has zero children
                    parent.getDockChildren().remove(dock);
                    todo.add(parent);
                } else if (dock.getDockAxis() != DockAxis.Z && dock.getDockChildren().size() == 1) {
                    // Replace xy composite with its child if xy composite has one child
                    DockNode onlyChild = dock.getDockChildren().remove(0);
                    parent.getDockChildren().set(parent.getDockChildren().indexOf(dock), onlyChild);
                    todo.add(parent);
                }
            }
        }
    }

    private void updateDropRect(@Nullable final DropZone zone, @NonNull Bounds bounds, @NonNull Insets ins) {
        if (zone == null) {
            dropRect.setVisible(false);
            return;
        }
        if (dropRect.getParent() == null) {
            stackPane.getChildren().add(dropRect);
        }
        double x = bounds.getMinX(),
                y = bounds.getMinY(),
                w = bounds.getWidth(),
                h = bounds.getHeight();
        double btm = ins.getBottom(),
                lft = ins.getLeft(),
                rgt = ins.getRight(),
                top = ins.getTop();
        BoundingBox rect;
        switch (zone) {
        case BOTTOM:
            rect = new BoundingBox(x, y + h - btm, w, btm);
            break;
        case LEFT:
            rect = new BoundingBox(x, y, lft, h);
            break;
        case RIGHT:
            rect = new BoundingBox(x + w - rgt, y, rgt, h);
            break;
        case TOP:
            rect = new BoundingBox(x, y, w, top);
            break;
        case CENTER:
        default:
            rect = new BoundingBox(x + lft, y + top, w - lft - rgt, h - top - btm);
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

    public Supplier<Dock> getZSupplier() {
        return zSupplier;
    }

    public void setZSupplier(Supplier<Dock> zSupplier) {
        this.zSupplier = zSupplier;
    }
}

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
    private final ObjectProperty<DockComponent> root = new SimpleObjectProperty<>();
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
        root.addListener(this::onRootChanged);
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

    @NonNull
    private void onChildrenChanged(ListChangeListener.Change<? extends DockComponent> c) {
        if (c.getList().size() > 1) {
            throw new IllegalArgumentException("RootDock can only have one child");
        }
        root.set(c.getList().isEmpty() ? null : c.getList().get(0));

        while (c.next()) {
            if (c.wasRemoved()) {
                for (DockComponent removed : c.getRemoved()) {
                    if (removed.getParentComponent() == this) {
                        removed.setParentComponent(null);
                    }
                }

            }
            if (c.wasAdded()) {
                for (DockComponent n : c.getAddedSubList()) {
                    n.setParentComponent(this);
                }
            }
        }
    }

    @Override
    public boolean isEditable() {
        return true;
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
            if (oldValue.parentComponentProperty().get() == this) {
                oldValue.parentComponentProperty().set(null);
            }
        }
        if (newValue != null) {
            newValue.parentComponentProperty().set(null);
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

        if (isAcceptable(e)) {
            DockItem droppedTab = DockItem.getDraggedItem();

            DropZone zone = null;
            Dock pickedDock = getPickedDock(e);
            if (pickedDock == this && getChildComponents().isEmpty()) {
                zone = DropZone.CENTER;
            } else if (pickedDock != null) {
                Insets insets = dockDropZoneInsets;
                Bounds bounds = sceneToLocal(pickedDock.getContent().localToScene(pickedDock.getContent().getBoundsInLocal()));
                zone = getZone(e.getX(), e.getY(), bounds, insets);
                if (!pickedDock.isEditable() ||
                        zone == DropZone.CENTER && pickedDock.getAxis() != DockAxis.Z) {
                    zone = null;
                }
            }
            if (zone != null) {
                e.acceptTransferModes(TransferMode.MOVE);
                onDockLeafDropped(pickedDock, droppedTab, zone);

            }
            e.consume();
        }
    }


    private void onDragExit(DragEvent e) {
        dropRect.setVisible(false);
    }

    private void onDragOver(@NonNull DragEvent e) {
        if (!isAcceptable(e)) {
            return;
        }

        DropZone zone = null;
        Bounds bounds = getBoundsInLocal();
        Insets insets = rootDrawnDropZoneInsets;
        Dock pickedDock = getPickedDock(e);
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
        }
        /*else if (zone != null && getChildren().size() == 1) {
            zone = DropZone.CENTER;
        }*/
        updateDropRect(zone, bounds, insets);

        if (zone != null) {
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

    @Nullable
    private Dock getPickedDock(@NonNull DragEvent e) {
        PickResult pick = e.getPickResult();
        Node pickedNode = pick.getIntersectedNode();
        while (pickedNode != this && pickedNode != null
                && !(pickedNode instanceof Dock)) {
            pickedNode = pickedNode.getParent();
        }
        return (pickedNode instanceof Dock) ? (Dock) pickedNode : null;
    }

    private void onDockLeafDropped(@Nullable Dock dropTarget, @NonNull DockItem leaf, @NonNull DropZone zone) {
        Dock dragSource = leaf.getParentComponent();
        if (dragSource == null) {
            return; // can't do dnd
        }
        dragSource.getChildComponents().remove(leaf);

        addLeafToParent(leaf, dropTarget, zone);
        removeUnusedComposites(dragSource);
        dumpTree(this, 0);
        RootDock sourceRoot = dragSource.getRoot();
        if (sourceRoot != null) {
            dumpTree(sourceRoot, 0);
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

    private void addLeafToParent(@NonNull DockItem leaf, @NonNull Dock parent, @NonNull DropZone zone) {
        DockAxis zoneAxis = getZoneAxis(zone);

        // case Z: => add item to existing dock or make new first child of root
        if (zoneAxis == DockAxis.Z) {
            if (parent != this) {
                parent.getChildComponents().add(leaf);
            } else {
                Dock newComposite = zSupplier.get();
                newComposite.getChildComponents().add(leaf);
                setOnlyChild(newComposite);
            }
            return;
        }

        int insertionIndex = -1;


        // otherwise => add item to new zComposite
        Dock zComposite = zSupplier.get();
        zComposite.getChildComponents().add(leaf);

        // new zComposite must be added to a parent with proper orientation
        if (parent.getAxis() == zoneAxis) {
            // okay
        } else {
            Dock grandParent = parent.getParentComponent();
            if (grandParent != null && grandParent.getAxis() == zoneAxis) {
                insertionIndex = grandParent.getChildComponents().indexOf(parent);
                parent = grandParent;

            } else {
                Dock newParent;
                if (grandParent == null || grandParent == this) {
                    switch (zoneAxis) {
                    case X:
                        newParent = rootXSupplier.get();
                        break;
                    case Y:
                    default:
                        newParent = rootYSupplier.get();
                        break;
                    }
                    if (grandParent != null) {
                        grandParent.getChildComponents().remove(parent);
                        addToParentInZone(newParent, grandParent, zone, -1);
                    } else {
                        DockComponent onlyChild = getOnlyChild();
                        if (onlyChild != null) {
                            getChildComponents().remove(onlyChild);
                            getChildComponents().add(newParent);
                            newParent.getChildComponents().add(onlyChild);
                        } else {
                            getChildComponents().add(newParent);
                        }
                    }
                    parent = newParent;
                } else if (grandParent.getAxis() != zoneAxis) {
                    switch (zoneAxis) {
                    case X:
                        newParent = subXSupplier.get();
                        break;
                    case Y:
                    default:
                        newParent = subYSupplier.get();
                        break;
                    }
                    addToParentInZone(newParent, grandParent, zone, -1);
                    grandParent.getChildComponents().remove(parent);
                    newParent.getChildComponents().add(parent);
                    parent = newParent;
                } else {
                    parent = grandParent;
                }
            }
        }
        addToParentInZone(zComposite, parent, zone, insertionIndex);
    }

    private void addToParentInZone(Dock child, @NonNull Dock parent, @NonNull DropZone zone, int insertionIndex) {
        Dock oldParent = getParentComposite(child);
        if (oldParent != null) {
            oldParent.getChildComponents().remove(child);
        }

        switch (zone) {
        case TOP:
        case LEFT:
            parent.getChildComponents().add(
                    insertionIndex == -1
                            ? 0
                            : insertionIndex, child);
            break;
        case RIGHT:
        case BOTTOM:
        default:
            parent.getChildComponents().add(
                    insertionIndex == -1 || insertionIndex >= parent.getChildComponents().size()
                            ? parent.getChildComponents().size()
                            : insertionIndex + 1, child);
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

}

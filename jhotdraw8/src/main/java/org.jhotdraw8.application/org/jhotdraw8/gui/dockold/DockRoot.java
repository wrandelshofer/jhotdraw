/*
 * @(#)DockRoot.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dockold;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.input.DragEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.gui.CustomSkin;
import org.jhotdraw8.gui.RectangleTransition;

import java.util.function.Supplier;

import static javafx.geometry.Orientation.VERTICAL;

/**
 * A {@code DockRoot} is the root of a containment hierarchy for
 * {@link DockItem}s.
 * <p>
 * A dock item can not be directly added to the dock root. The containment
 * hierarchy consists of the following nodes:
 * <ul>
 * <li>DockRoot: this is the root of the containment hierarchy. Can hold zero or
 * one Track.</li>
 * <li>Track: provides a vertically or horizontally tiled pane. Can hold zero or
 * more Tracks or Docks.</li>
 * <li>Dock: provides space for one or more dock items. Can hold zero or more
 * DockItems.</li>
 * <li>DockItem: represents an item of a Dock.</li>
 * </ul>
 * <p>
 * <b>Drag and drop operations:</b>
 * <p>
 * Dock items are draggable, The dock root serves as the drop target.
 * <p>
 * When a dock item is dragged over the dock root, the dock root shows drop
 * zones onto which the dock item can be dropped. The drop zones are: the
 * "center" of a dock, the "top", "right", "bottom" or "left" edge of a dock.
 * <p>
 * When a dock item is dropped on the center of a dock, the dock root adds it to
 * that dock. When a dock item is dropped on the edge of a dock, the dock root
 * creates a track if needed, adds a new dock to the track, and then adds the
 * dock item to the dock. Finally, the dock root removes all empty docks and all
 * tracks which only have one child from its hierarchy.
 *
 * @author Werner Randelshofer
 */
public class DockRoot extends Control {

    @NonNull
    private Insets dockDrawnDropZoneInsets = new Insets(20, 20, 20, 20);
    @NonNull
    private Insets dockDropZoneInsets = new Insets(40, 40, 40, 40);
    private final ObjectProperty<Supplier<Dock>> dockFactory = new SimpleObjectProperty<>(this, "dockFactory", TabPaneDock::new);
    @NonNull
    private SetProperty<DockItem> dockableItems = new SimpleSetProperty<>(this, "dockableItems", null);
    private Rectangle dropRect;
    private final ObjectProperty<Supplier<Track>> horizontalTrackFactory = new SimpleObjectProperty<>(this, "htrackFactory", () -> new SplitPaneTrack(Orientation.HORIZONTAL));
    @NonNull
    private Insets rootDrawnDropZoneInsets = new Insets(10, 10, 10, 10);
    @NonNull
    private Insets rootDropZoneInsets = new Insets(20, 20, 20, 20);
    private StackPane stackPane;
    @Nullable
    RectangleTransition transition;
    private final ObjectProperty<Supplier<Track>> verticalTrackFactory = new SimpleObjectProperty<>(this, "vtrackFactory", () -> new SplitPaneTrack(Orientation.VERTICAL));
    private final ObjectProperty<Supplier<Track>> verticalRootTrackFactory = new SimpleObjectProperty<>(this, "vRootTrackFactory", () -> new SplitPaneTrack(Orientation.VERTICAL));
    private final ObservableMap<Class<?>, Supplier<Track>> verticalTrackFactoryMap = FXCollections.observableHashMap();

    public DockRoot() {
        init();

    }

    private void addTab(@NonNull DockItem tab, @Nullable Dock parent, @NonNull DropZone zone) {
        // remove item from old dock, and maybe collapse parents
        Dock oldDock = tab.getDock();
        DockRoot root;
        if (oldDock != null) {
            oldDock.getItems().remove(tab);
            root = getDockRoot(oldDock.getNode());
        } else {
            root = null;
        }

        if (parent != null) {
            addTabToDock(tab, parent, zone);
        } else {
            addTabToThis(tab, zone);
        }

        if (oldDock != null) {
            if (oldDock.getItems().isEmpty()) {
                Track track = getParentTrack(oldDock.getNode());
                if (track != null) {
                    track.getItems().remove(oldDock);
                } else {
                    root.stackPane.getChildren().remove(oldDock);
                }
                while (track != null && track.getItems().size() <= 1) {
                    Track parentTrack = getParentTrack(track.getNode());
                    if (track.getItems().isEmpty()) {
                        if (parentTrack != null) {
                            parentTrack.getItems().remove(track);
                        } else {
                            root.stackPane.getChildren().remove(track);
                        }
                    } else {
                        Node reparentedNode = track.getItems().get(0);
                        track.getItems().clear();
                        if (parentTrack != null) {
                            int i = parentTrack.getItems().indexOf(track);
                            parentTrack.getItems().remove(i);
                            parentTrack.getItems().add(i, reparentedNode);
                        } else {
                            root.stackPane.getChildren().remove(track);
                            root.stackPane.getChildren().add(0, reparentedNode);
                        }
                    }
                    track = parentTrack;
                }
            }
        }
    }

    private void addTabToDock(DockItem tab, @NonNull Dock parent, @NonNull DropZone zone) {
        // case CENTER: => add item to existing dock
        switch (zone) {
            case CENTER:
                parent.getItems().add(tab);
                return;
        }

        // case TOP,LEFT,RIGHT,BOTTOM: => add item to new dock
        Dock newDock = createDock();
        newDock.getItems().add(tab);

        // add newDock to parentTrack, if necessary create a suitable parent Track
        Track parentTrack = getParentTrack(parent.getNode());
        Orientation neededOrientation;
        neededOrientation = getNeededOrientation(zone);

        if (parentTrack == null) {
            parentTrack = createTrack(null, parent, neededOrientation);
            addTrack(parentTrack);
        } else if (parentTrack.getOrientation() != neededOrientation) {
            Track oldParentTrack = parentTrack;
            parentTrack = createTrack(parentTrack, parent, neededOrientation);
            int i = oldParentTrack.getItems().indexOf(parent.getNode());
            oldParentTrack.getItems().remove(i);
            oldParentTrack.getItems().add(i, parentTrack.getNode());
            parentTrack.getItems().add(parent.getNode());

        }
        int oldIndex = parentTrack.getItems().indexOf(parent.getNode());
        switch (zone) {
            case LEFT:
            case TOP:
                parentTrack.getItems().add(oldIndex, newDock.getNode());
                break;
            case RIGHT:
            case BOTTOM:
            default:
                parentTrack.getItems().add(oldIndex + 1, newDock.getNode());
                break;
        }
    }

    private void addTabToThis(DockItem tab, DropZone zone) {
        if (zone == DropZone.CENTER) {
            zone = DropZone.LEFT;
        }

        Dock newDock = createDock();
        newDock.getItems().add(tab);

        Orientation neededOrientation = getNeededOrientation(zone);
        Track topTrack = getRootTrack();
        if (topTrack == null || topTrack.getOrientation() != neededOrientation) {
            topTrack = createTrack(null, newDock, neededOrientation);
            addTrack(topTrack);
        }

        switch (zone) {
            case LEFT:
            case TOP:
                topTrack.getItems().add(0, newDock.getNode());
                break;
            case RIGHT:
            case BOTTOM:
            default:
                topTrack.getItems().add(newDock.getNode());
                break;
        }
    }

    private Dock createDock() {
        return getDockFactory().get();
    }

    private Track createTrack(@Nullable Track parentTrack, @Nullable Dock dock, @NonNull Orientation orientation) {
        Supplier<Track> supplier = null;
        switch (orientation) {
            case VERTICAL:
                if (parentTrack == null) {
                    supplier = verticalRootTrackFactory.get();
                }
                if (supplier == null && dock != null) {
                    supplier = getVerticalTrackFactoryMap().get(dock.getClass());
                }
                if (supplier == null) {
                    supplier = getVerticalTrackFactory();
                }
                break;
            case HORIZONTAL:
            default:
                supplier = getHorizontalDockTrackFactory();
                break;
        }
        return supplier.get();
    }

    @NonNull
    public ObjectProperty<Supplier<Dock>> dockFactoryProperty() {
        return dockFactory;
    }

    @Nullable
    public SetProperty<DockItem> dockableItemsProperty() {
        return dockableItems;
    }

    public Supplier<Dock> getDockFactory() {
        return dockFactory.get();
    }

    public void setDockFactory(Supplier<Dock> value) {
        dockFactory.set(value);
    }

    public ObservableSet<DockItem> getDockableItems() {
        return dockableItems.get();
    }

    public void setDockableItems(ObservableSet<DockItem> dockableItems) {
        this.dockableItems.set(dockableItems);
    }

    public Supplier<Track> getHorizontalDockTrackFactory() {
        return horizontalTrackFactory.get();
    }

    public void setHorizontalTrackFactory(Supplier<Track> value) {
        horizontalTrackFactory.set(value);
    }

    private Orientation getNeededOrientation(@NonNull DropZone zone) {
        Orientation neededOrientation;
        switch (zone) {
            case LEFT:
            case RIGHT:
                neededOrientation = Orientation.HORIZONTAL;
                break;
            case TOP:
            case BOTTOM:
            default:
                neededOrientation = Orientation.VERTICAL;
                break;
        }
        return neededOrientation;
    }

    @NonNull
    private Track getParentTrack(@NonNull Node n) {
        Node node = n.getParent();
        while (node != null && !(node instanceof Track)) {
            node = node.getParent();
        }
        return (Track) node;
    }

    @Nullable
    private Dock getPickedDock(@NonNull DragEvent e) {
        PickResult pick = e.getPickResult();
        Node pickedNode = pick.getIntersectedNode();
        while (pickedNode != this && pickedNode != null && !(pickedNode instanceof Dock)) {
            pickedNode = pickedNode.getParent();
        }
        return pickedNode == this ? null : (Dock) pickedNode;
    }

    @NonNull
    private DockRoot getDockRoot(@NonNull Node n) {
        Node node = n.getParent();
        while (node != null && !(node instanceof DockRoot)) {
            node = node.getParent();
        }
        return (DockRoot) node;
    }

    @Nullable
    public Track getRootTrack() {
        if (stackPane.getChildren().size() > 0) {
            Node topNode = stackPane.getChildren().get(0);
            return (topNode instanceof Track) ? (Track) topNode : null;
        }
        return null;
    }

    public Supplier<Track> getVerticalTrackFactory() {
        return verticalTrackFactory.get();
    }

    public void setVerticalInnerTrackFactory(Supplier<Track> value) {
        verticalTrackFactory.set(value);
    }

    public void setVerticalRootTrackFactory(Supplier<Track> value) {
        verticalRootTrackFactory.set(value);
    }

    @NonNull
    public ObservableMap<Class<?>, Supplier<Track>> getVerticalTrackFactoryMap() {
        return verticalTrackFactoryMap;
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

    private void onDragDrop(@NonNull DragEvent e) {
        dropRect.setVisible(false);
        getChildren().remove(dropRect);

        if (isAcceptable(e)) {
            DockItem droppedTab = DockItem.draggedTab;

            DropZone zone = getZone(e.getX(), e.getY(), getBoundsInLocal(), rootDropZoneInsets);
            Dock parent = null;
            if (zone == null || zone == DropZone.CENTER) {
                Dock pickedNode = getPickedDock(e);
                if (pickedNode != null) {
                    Insets insets = dockDropZoneInsets;
                    Bounds bounds = sceneToLocal(pickedNode.getNode().localToScene(pickedNode.getNode().getBoundsInLocal()));
                    zone = getZone(e.getX(), e.getY(), bounds, insets);
                    parent = pickedNode;
                } else if (e.getPickResult().getIntersectedNode() != this) {
                    parent = null;
                }
            }
            if (zone == DropZone.CENTER && parent == null) {
                //zone = null;
            }
            if (zone != null) {
                e.acceptTransferModes(TransferMode.MOVE);
                addTab(droppedTab, parent, zone);

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

        DropZone zone = getZone(e.getX(), e.getY(), getBoundsInLocal(), rootDropZoneInsets);
        Bounds bounds = getBoundsInLocal();
        Insets insets = rootDrawnDropZoneInsets;
        if (zone == DropZone.CENTER) {
            Dock pickedNode = getPickedDock(e);
            if (pickedNode != null) {
                insets = dockDrawnDropZoneInsets;
                bounds = sceneToLocal(pickedNode.getNode().localToScene(pickedNode.getNode().getBoundsInLocal()));
                zone = getZone(e.getX(), e.getY(), bounds, dockDropZoneInsets);
                if (zone == DropZone.CENTER && !pickedNode.isEditable()) {
                    zone = null;
                }
            } else if (e.getPickResult().getIntersectedNode() != stackPane) {
                zone = null;
            }
        } else if (e.getPickResult().getIntersectedNode() == stackPane) {
            zone = DropZone.CENTER;
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

    @NonNull
    public ObjectProperty<Supplier<Track>> horizontalTrackFactoryProperty() {
        return horizontalTrackFactory;
    }

    private void init() {
        setSkin(new CustomSkin<>(this));
        dropRect = new Rectangle();
        dropRect.setOpacity(0.4);
        dropRect.setManaged(false);
        dropRect.setMouseTransparent(true);
        dropRect.setVisible(false);
        setOnDragOver(this::onDragOver);
        setOnDragExited(this::onDragExit);
        setOnDragDropped(this::onDragDrop);
        stackPane = new StackPane();
        getChildren().add(stackPane);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        stackPane.resizeRelocate(0, 0, getWidth(), getHeight());
    }

    /**
     * Clears the containment hierarchy of this DockRoot.
     */
    public void clear() {
        stackPane.getChildren().clear();
    }

    /**
     * Inserts the specified Dock at the highest possible point into the
     * containment hierarchy.
     * <p>
     * If the containment hierarchy is empty, the Dock becomes the root of the
     * hierarchy.
     * <p>
     * If the root of the containment hierarchy is not a Track, a vertical track
     * is pushed, and the existing root node and the specified Dock are added as
     * children to the vertical track.
     * <p>
     * If the root of the containment hierarchy is a track, the specified dock
     * are added as a child to the vertical track.
     *
     * @param dock a dock
     */
    public void addDock(@NonNull Dock dock) {
        final ObservableList<Node> children = stackPane.getChildren();
        if (children.isEmpty()) {
            children.add(dock.getNode());
        } else {
            Node oldRoot = children.get(0);
            if (oldRoot instanceof Track) {
                Track track = (Track) oldRoot;
                track.getItems().add(dock.getNode());
            } else {
                children.remove(0);
                Track track = createTrack(null, (oldRoot instanceof Dock) ? (Dock) oldRoot : null, VERTICAL);
                track.getItems().addAll(oldRoot, dock.getNode());
                children.add(track.getNode());
            }
        }
    }

    /**
     * Inserts the specified DockItem at the highest possible point into the
     * containment hierarchy.
     * <p>
     * Creates a Dock for the DockItem, and then calls method {@link #addDock}.
     *
     * @param dockItem a dock item
     */
    public void addDockItem(DockItem dockItem) {
        Dock dock = createDock();
        dock.getItems().add(dockItem);
        addDock(dock);
    }

    /**
     * Pushes the specified track on the top of the containment hierarchy. The
     * current top level node (typically a Track or a Dock) is added as the
     * first child to the specified track.
     *
     * @param track a track
     */
    public void addTrack(@NonNull Track track) {
        if (stackPane.getChildren().isEmpty()) {

        } else {
            Node topNode = stackPane.getChildren().remove(0);
            track.getItems().add(0, topNode);
        }
        stackPane.getChildren().add(0, track.getNode());
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

    @NonNull
    public ObjectProperty<Supplier<Track>> verticalDockTrackFactoryProperty() {
        return verticalTrackFactory;
    }

    private boolean isAcceptable(@NonNull DragEvent e) {
        return e.getDragboard().getContentTypes().contains(DockItem.DOCKABLE_TAB_FORMAT)
                //    && e.getGestureSource() != null
                && DockItem.draggedTab != null
                && (dockableItems.get() == null || dockableItems.get().contains(DockItem.draggedTab));
    }

}

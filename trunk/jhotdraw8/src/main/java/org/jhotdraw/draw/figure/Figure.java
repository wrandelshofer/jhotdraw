/* @(#)Figure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import org.jhotdraw.draw.handle.HandleType;
import org.jhotdraw.draw.key.DirtyMask;
import static java.lang.Math.*;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.BoundsInLocalOutlineHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import javafx.css.Styleable;
import javafx.geometry.BoundingBox;
import javafx.scene.transform.Translate;
import org.jhotdraw.collection.IterableTree;
import org.jhotdraw.collection.IndexedSet;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.handle.ResizeHandleKit;
import org.jhotdraw.draw.handle.RotateHandle;
import static java.lang.Math.min;
import static java.lang.Math.max;
import org.jhotdraw.draw.locator.RelativeLocator;
import static java.lang.Math.min;
import static java.lang.Math.max;
import javafx.scene.transform.NonInvertibleTransformException;
import static org.jhotdraw.draw.figure.TransformableFigure.TRANSFORM;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * A <em>figure</em> is a graphical (figurative) element of a {@link Drawing}.
 * <p>
 * <b>State.</b> The state of a figure is described by the values of its
 * property map.</p>
 * <p>
 * <b>Tree Structure.</b> A figure can be composed of other figures in a tree
 * structure. The composition is implemented with the {@code getChildren} and
 * the {@code parent} getProperties. The composition can be restricted to a
 * specific parent type. By convention all getChildren of a {@code Drawing} must
 * be {@link Layer}s, and the parent of a {@code Layer} must be a
 * {@code Drawing}.</p>
 * <p>
 * <b>Coordinate System and Transformations.</b> A figure introduces a local
 * coordinate system which applies to its own geometry and all descendant
 * figures in its subtree. The Figure interface provides methods which allow to
 * transform between the local coordinate system of a figure, the coordinate
 * system of its parent, and the world coordinate system.</p>
 * <p>
 * <b>Connections.</b> A figure can be connected to other figures. The
 * connection are directed. By convention, when a figure "A" is connected to an
 * other figure "B", then "A" adds itself in the {@code connectedFigures}
 * property of "B". When "A" is disconnected from "B", then "A" removes itself
 * from the {@code connectedFigures} property of "B".</p>
 * <p>
 * <b>Rendering.</b> A figure can render its graphical representation into a
 * JavaFX {@code Node} with the help of a {@link RenderContext}.</p>
 * <p>
 * <b>Handles.</b> A figure can produce {@code Handle}s which allow to
 * graphically change the state of the figure in a {@link DrawingView}.</p>
 * <p>
 * <b>Layout.</b> The updateLayout of a figure may depend on the updateLayout of other
 figures, typically on its descendents. However the updateLayout of a figure may
 also depend on the updateLayout of connected figures. These dependencies can be
 cyclic. A figure does not automatically update the updateLayout of other figures:
 Method {@link #updateLayout} must be invoked to incrementally update the updateLayout of
 a figure and its descendants based on the current updateLayout of all other figures
 in the tree structure. Method {@link #getConnectedFigures} must be used to
 find figures which compute their updateLayout based on this figure.</p>
 * <p>
 * <b>Layout hints and node update hints.</b> Essentially each time when the
 * state of a figure is changed, method {@code updateLayout()} needs to be invoked on
 * the root of the tree hierarchy to incrementally update the state of all
 * dependent figures and then all figures need to be rendered again. This is
 * time consuming. The interface {@link org.jhotdraw.draw.key.FigureMapAccessor}
 * provides hints about which figures need to be laid out and rendered again.
 * The hints are given by a {@link DirtyMask}.
 * </p>
 * <p>
 * <b>Styling.</b> Some property values of a figure can be styled using CSS. The
 * corresponding property key must implement the interface
 * {@link org.jhotdraw.styleable.StyleableMapAccessor}. The style information is
 * cached in the figure getProperties. When the position of a figure in the tree
 * structure is changed, method {@code updateCss()} must be called to update the
 * style information of the figure and its descendants.</p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Figure extends StyleablePropertyBean, IterableTree<Figure> {

    // ----
    // various declarations
    // ----
    /**
     * To avoid name clashes in the stylesheet, all styleable JHotDraw
     * getProperties use the prefix {@code "-jhotdraw-"}.
     *
     * XXX mapping of css attribute names to keys should be done elsewhere!
     */
    public final static String JHOTDRAW_CSS_PREFIX = "";
    // ----
    // key declarations
    // ----

    // ----
    // property names
    // ----
    /**
     * The name of the parent property.
     */
    public final static String PARENT_PROPERTY = "parent";

    // ----
    // property fields
    // ----
    /**
     * Removes the specified connection target.
     *
     * @param targetFigure a Figure which is a connection target.
     */
    void removeConnectionTarget(Figure targetFigure);

    /**
     * Requests to remove all connection targets.
     */
    void removeAllConnectionTargets();

    /**
     * This method is invoked on the connection targets by
     * {@link org.jhotdraw.draw.model.DrawingModel} when it determines that the
     * connection targets of the figure have changed.
     * <p>
     * The default implementation of this method is empty.
     */
    default void connectNotify() {
    }

    /**
     * This method is invoked on a figure and all its descendants by
     * {@link org.jhotdraw.draw.model.DrawingModel} when it determines that the
     * transformation of the figure has changed.
     * <p>
     * The default implementation of this method calls
     * {@link #invalidateTransforms}.
     */
    default void transformNotify() {
        invalidateTransforms();
    }

    /**
     * This method is invoked on a figure by
     * {@link org.jhotdraw.draw.model.DrawingModel} when it determines that the
     * figure needs to be laid out again.
     * <p>
     * The default implementation of this method calls {@link #updateLayout}.
     */
    default void layoutNotify() {
        updateLayout();
    }

    /**
     * This method is invoked on a figure by
     * {@link org.jhotdraw.draw.model.DrawingModel} when it determines that the
     * figure needs to apply its stylesheet agin.
     * <p>
     * The default implementation of this method calls {@link #updateCss} and
     * then {@code #updateLayout}.
     */
    default void stylesheetNotify() {
        updateCss();
        updateLayout();
    }

    /**
     * The parent figure.
     * <p>
     * If this figure has not been added as a child to another figure, then this
     * variable will be null.
     * </p>
     * By convention the parent is set exclusively by a composite figure on its
     * child figures. The composite figure sets parent to itself on a child
     * immediately after the child figure has been added to the composite
     * figure. The composite figure sets parent to {@code null} on a child
     * immediately after the child figure has been removed from the composite
     * figure.
     *
     * @return the parent property, with {@code getBean()} returning this
     * figure, and {@code getName()} returning {@code PARENT_PROPERTY}.
     */
    ObjectProperty<Figure> parentProperty();

    // ----
    // behavior methods
    // ----
    /**
     * The bounds that should be used for transformations of this figure.
     * <p>
     * The bounds are given in the untransformed local coordinate space of the
     * figure.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale. Invoke {@link #updateLayout} if you are not sure that the cache is
     * valid.
     *
     * @return the updateLayout bounds
     */
    public Bounds getBoundsInLocal();

    /**
     * The bounds that should be used for updateLayout calculations for this figure.
     * <p>
     * The bounds are given in the coordinate space of the parent figure.
     * <p>
     * This method may use caching and return incorrect results if the caches
     * are stale. Invoke {@link #invalidateTransforms} and {@link #updateLayout} if
     * you are not sure that the cache is valid.
     *
     * @return the updateLayout bounds
     */
    default public Bounds getBoundsInParent() {
        Bounds b = getBoundsInLocal();
        double[] points = new double[8];
        points[0] = b.getMinX();
        points[1] = b.getMinY();
        points[2] = b.getMaxX();
        points[3] = b.getMinY();
        points[4] = b.getMaxX();
        points[5] = b.getMaxY();
        points[6] = b.getMinX();
        points[7] = b.getMaxY();

        Transform t = getLocalToParent();
        t.transform2DPoints(points, 0, points, 0, 4);

        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < points.length; i += 2) {
            minX = min(minX, points[i]);
            maxX = max(maxX, points[i]);
            minY = min(minY, points[i + 1]);
            maxY = max(maxY, points[i + 1]);
        }
        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Attempts to change the updateLayout bounds of the figure.
     * <p>
 The figure may choose to only partially change its updateLayout bounds.
 <p>
     * Reshape typically changes property values in this figure. The way how
     * this is performed is implementation specific.
     *
     * @param transform the desired transformation in parent coordinates
     */
    void reshape(Transform transform);

    /**
     * Attempts to change the updateLayout bounds of the figure.
     * <p>
     * Width and height are ignored, if the figure is not resizable.
     * <p>
 If the updateLayout bounds of the figure changes, it fires an invalidation
 event.
     *
     * @param x desired x-position in parent coordinates
     * @param y desired y-position in parent coordinates
     * @param width desired width in parent coordinates, may be negative
     * @param height desired height in parent coordinates, may be negative
     */
    default void reshape(double x, double y, double width, double height) {
        if (width == 0 || height == 0) {
            return;
        }

        Bounds oldBounds = getBoundsInLocal();

        double sx = width / oldBounds.getWidth();
        double sy = height / oldBounds.getHeight();

        Affine tx = new Affine();
        if (!Double.isNaN(sx) && !Double.isNaN(sy)
                && !Double.isInfinite(sx) && !Double.isInfinite(sy)
                && (sx != 1d || sy != 1d)) {
            tx.appendScale(sx, sy, oldBounds.getMinX(), oldBounds.getMinY());
        }
        tx.appendTranslation(x - oldBounds.getMinX(), y - oldBounds.getMinY());

        reshape(tx);
    }

    /**
     * This method is invoked by a {@code RenderContext}, when it needs a node
     * to create a JavaFX scene graph for a figure.
     * <p>
     * A typical implementation should look like this:
     * <pre>{@code
     * public Node createNode(RenderContext v) {
     * return new ...desired subclass of Node...();
     * }
     * }</pre>
     * <p>
     * A figure may be rendered with multiple {@code RenderContext}s
     * simultaneously. Each {@code RenderContext} uses this method to
     * instantiate a JavaFX node for the figure and associate it to the figure.
     * <p>
     * This method must create a new instance because returning an already
     * existing instance may cause undesired side effects on other
     * {@code RenderContext}s.
     * <p>
     * Note that by convention this method <b>may only</b> be invoked by a
     * {@code RenderContext} object.
     *
     * @param ctx the renderer which will use the node
     * @return the newly created node
     */
    Node createNode(RenderContext ctx);

    /**
     * This method is invoked by a {@code RenderContext}, when it needs to
     * update the node which represents the scene graph in the figure.
     * <p>
     * A figure which is composed from child figures, must add the nodes of its
     * getChildren to its node. This ensures that coordinate space
     * transformations of the composed figure are properly propagated to its
     * getChildren.
     * </p>
     * <pre>
     * public void updateNode(RenderContext rc, Node n) {
     *     ObservableList&lt;Node&gt; group = ((Group) n).getChildren();
     * group.clear();
     * for (Figure child : children()) {
     * group.add(rc.getNode(child));
     * }
     * </pre>
     * <p>
     * A figure may be shown in multiple {@code RenderContext}s. Each
     * {@code RenderContext} uses this method to update the a JavaFX node for
     * the figure.
     * <p>
     * Note that the figure <b>must</b> retrieve the JavaFX node from other
     * figures from the render context by invoking {@code rc.getNode(child)}
     * rather than creating new nodes using {@code child.createNode(rc)}. This
     * convention allows to implement a cache in the render context for the Java
     * FX node. Also, render contexts like {@code DrawingView} need to associate
     * input events on Java FX nodes to the corresponding figure.
     *
     * @param ctx the drawing view
     * @param node the node which was created with {@link #createNode}
     */
    void updateNode(RenderContext ctx, Node node);

    /**
     * Whether children may be added to this figure.
     *
     * @return true if getChildren are allowed
     */
    boolean isAllowsChildren();

    /**
     * This method whether the provided figure is a suitable parent for this
     * figure.
     *
     * @param newParent The new parent figure.
     * @return true if {@code newParent} is an acceptable parent
     */
    public boolean isSuitableParent(Figure newParent);

    /**
     * Whether the {@code updateLayout} method of this figure does anything.
     *
     * @return true if the {@code updateLayout} method is not empty.
     */
    boolean isLayoutable();

    /**
     * Whether the figure is selectable by the user.
     *
     * @return true if the user may select the figure
     */
    boolean isSelectable();

    /**
     * Whether the figure is deletable by the user.
     *
     * @return true if the user may delete the figure
     */
    boolean isDeletable();

    /**
     * Whether the figure can be reshaped as a group together with other
     * figures.
     * <p>
 If this figure uses one of the other figures for computing its position
 or its updateLayout, then it will return false.
 <p>
     * The default implementation always returns true.
     *
     * @param others A set of figures.
     * @return true if the user may reshape this figure together with those in
     * the set.
     */
    default boolean isGroupReshapeableWith(Set<Figure> others) {
        return true;
    }

    /**
     * Whether the figure is editable by the user.
     *
     * @return true if the user may edit the figure.
     */
    boolean isEditable();

    /**
     * Whether the figure and all its ancestors are visible.
     *
     * @return true if the user can see the figure
     */
    default boolean isVisible() {
        Figure node = this;
        while (node != null) {
            if (!node.get(HideableFigure.VISIBLE)) {
                return false;
            }
            node = node.getParent();
        }
        return true;
    }

    /**
     * Whether the figure is decomposable.
     *
     * @return true if the figure is decomposable
     */
    default boolean isDecomposable() {
        return true;
    }

    /**
     * Creates handles of the specified level and for the specified drawing view
     * and adds them to the provided list.
     *
     * @param handleType The desired handle type
     * @param dv The drawing view which will display the handles
     * @param list The handles.
     */
    default void createHandles(HandleType handleType, DrawingView dv, List<Handle> list) {
        if (handleType == HandleType.SELECT) {
            list.add(new BoundsInLocalOutlineHandle(this));
        } else if (handleType == HandleType.MOVE) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            list.add(new MoveHandle(this, RelativeLocator.northEast()));
            list.add(new MoveHandle(this, RelativeLocator.northWest()));
            list.add(new MoveHandle(this, RelativeLocator.southEast()));
            list.add(new MoveHandle(this, RelativeLocator.southWest()));
        } else if (handleType == HandleType.RESIZE) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_RESIZE_OUTLINE));
            if (this instanceof TransformableFigure) {
                list.add(new RotateHandle((TransformableFigure) this));
            }
            ResizeHandleKit.addCornerResizeHandles(this, list);
            ResizeHandleKit.addEdgeResizeHandles(this, list);
        } else if (handleType == HandleType.TRANSFORM) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_TRANSFORM_OUTLINE));
            if (this instanceof TransformableFigure) {
                list.add(new RotateHandle((TransformableFigure) this));
            }
        }
    }

    /**
     * Gets a connector for this figure at the given location.
     *
     * @param pointInLocal the location of the connector in local coordinates.
     * @param prototype The prototype used to create a connection or null if
     * unknown. This allows for specific connectors for different connection
     * figures.
     * @return Returns the connector. Returns null if there is no connector at
     * the given location.
     */
    Connector findConnector(Point2D pointInLocal, Figure prototype);

    /**
     * Updates the updateLayout of this figure and of its descendant figures. Does not
     * update connection figures.
     * <p>
     * This figure may cache its updateLayout. However, this figure does not 
     * keep track when its cache becomes invalid. Use a 
     * {@link org.jhotdraw.draw.model.DrawingModel} to manage this cache, or
     * invoke this method after you performed one of the following operations:
     * <ul>
     * <li>Changing a property of this figure or of one of its descendants with
     * dirty bits {@link org.jhotdraw.draw.key.DirtyBits#LAYOUT}.</li>
     * <li>Changing a property of one of the connection targets of this figure
     * with dirty bits
     * {@link org.jhotdraw.draw.key.DirtyBits#CONNECTION_LAYOUT}.</li>
     * <li>Invoking {@code updateLayout} on one of the connection targets of this
     * figure.</li>
     * </ul>
     */
    void updateLayout();

    /**
     * Applies the drawing stylesheet on this figure and on its descendant
     * figures.
     * <p>
     * This figure may cache stylesheet values. However, this figure does not
     * keep track when its stylesheet values becomes invalid. Use a
     * {@link org.jhotdraw.draw.model.DrawingModel} to manage this cache, or 
     * invoke this method after you performed one of the following operations:
     * <ul>
     * <li>Changing a property of one of the ancestors this figure with dirty
     * bits {@link org.jhotdraw.draw.key.DirtyBits#STYLE}.</li>
     * </ul>
     */
    void updateCss();

    /**
     * Invoked by {@code DrawingModel} when the figure is added to a drawing.
     *
     * @param drawing the drawing to which this figure has been added
     */
    void addNotify(Drawing drawing);

    /**
     * Invoked by {@code DrawingModel} when the figure is removed from a
     * drawing.
     *
     * @param drawing the drawing from which this figure has been removed
     */
    void removeNotify(Drawing drawing);

    // ----
    // convenience methods
    // ----
    /**
     * Adds a new child to the figure.
     *
     * @param newChild the new child
     */
    default void add(Figure newChild) {
        getChildren().add(newChild);
    }

    /**
     * Removes a child from the figure.
     *
     * @param child a child of the figure
     */
    default void remove(Figure child) {
        getChildren().remove(child);
    }

    /**
     * Gets the child with the specified index from the figure.
     *
     * @param index the index
     * @return the child
     */
    default Figure getChild(int index) {
        return getChildren().get(index);
    }

    /**
     * Gets the last child.
     *
     * @return The last child. Returns null if the figure has no getChildren.
     */
    default Figure getLastChild() {
        return getChildren().isEmpty() ? null : getChildren().get(0);
    }

    /**
     * Gets the first child.
     *
     * @return The first child. Returns null if the figure has no getChildren.
     */
    default Figure getFirstChild() {
        return getChildren().isEmpty() //
                ? null//
                : getChildren().get(getChildren().size() - 1);
    }

    /**
     * The child figures.
     * <p>
     * All changes on this list causes this figure to fire an invalidation
     * event.
     * <p>
     * If a child is added to this figure, the child must be removed from its
     * former parent. This figure must set itself set as the parent of the child
     * immediately after the figure has been added.</p>
     * <p>
     * If a child is removed from this figure, this figure must set parent to
     * null immediately before the child is removed.</p>
     * <p>
     * Note that this method returns a {@code ReadOnlyListProperty} which holds
     * an instance of {@link IndexedSet}. {@code ListChangeListener}s can get
     * the associated {@code Figure} using the following code:</p>
     * <pre>{@code
     * (ListChangeListener.Change change) -> Figure figure =
     *      (Figure) ((ReadOnlyProperty) change.getList()).getBean();
     * }</pre>
     *
     *
     * @return the children
     */
    @Override
    ObservableList<Figure> getChildren();

    /**
     * Returns the parent figure.
     * <p>
     * Note that there is no convenience method named {@code setParent}.
     *
     * @return parent figure or null, if the figure has no parent.
     */
    @Override
    default Figure getParent() {
        return parentProperty().get();
    }

    /**
     * Returns the root.
     *
     * @return the root
     */
    default Figure getRoot() {
        Figure parent = this;
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        return parent;
    }

    /**
     * Returns the ancestor Drawing.
     *
     * @return the drawing or null if no ancestor is a drawing. Returns this, if
     * this figure is a drawing.
     */
    default Drawing getDrawing() {
        return getAncestor(Drawing.class);
    }

    /**
     * Returns the ancestor Layer.
     *
     * @return the drawing or null if no ancestor is a layer. Returns this, if
     * this figure is a layer.
     */
    default Layer getLayer() {
        return getAncestor(Layer.class);
    }

    /**
     * Returns all figures which are connected to this figure.
     * <p>
 Returns all figures which compute their updateLayout based on this figure.
 <pre><code>
     * +-----------------+                    +-------------------------+
     * | ConnectedFigure |-----connection----&gt;| ConnectionTarget (this) |
     * +-----------------+                    +-------------------------+
     * </code></pre>
     *
     * @return a list of connected figures
     */
    ObservableSet<Figure> getConnectedFigures();

    /**
     * Returns all figures which are connection targets of this figure.
     * <p>
 Returns all figures on which the updateLayout of this figure is based.
 <pre><code>
     * +------------------------+                    +------------------+
     * | ConnectedFigure (this) |-----connection----&gt;| ConnectionTarget |
     * +------------------------+                    +------------------+
     * </code></pre>
     *
     * @return a list of connection target figures
     */
    default Set<Figure> getConnectionTargets() {
        return Collections.emptySet();
    }

    /**
     * Removes all connected figures and all connection targets.
     * <pre><code>
     * +-----------------+                    +------+                    +------------------+
     * | ConnectedFigure |-----connection----&gt;| this |-----connection----&gt;| ConnectionTarget |
     * +-----------------+                    +------+                    +------------------+
     * </code></pre>
     *
     */
    default void disconnect() {
        for (Figure connectedFigure : new ArrayList<Figure>(getConnectedFigures())) {
            connectedFigure.removeConnectionTarget(this);
        }
        removeAllConnectionTargets();
    }

    // ---
    // static methods
    // ---
    /**
     * Returns all supported keys of the figure.
     * <p>
     * The default implementation returns all declared and inherited keys.
     *
     * @return the keys
     */
    default Set<MapAccessor<?>> getSupportedKeys() {
        return Figure.getDeclaredAndInheritedKeys(this.getClass());
    }

    /**
     * Returns all keys declared in this class and inherited from parent
     * classes.
     *
     * @param clazz A figure class.
     * @return the keys
     */
    public static Set<MapAccessor<?>> getDeclaredAndInheritedKeys(Class<?> clazz) {
        try {
            HashSet<MapAccessor<?>> keys = new HashSet<>();
            LinkedList<Class<?>> todo = new LinkedList<>();
            HashSet<Class<?>> done = new HashSet<>();
            todo.add(clazz);
            while (!todo.isEmpty()) {
                Class<?> c = todo.removeFirst();
                for (Field f : c.getDeclaredFields()) {
                    if (MapAccessor.class.isAssignableFrom(f.getType())) {
                        MapAccessor<?> k = (MapAccessor<?>) f.get(null);
                        keys.add(k);
                    }
                }
                if (c.getSuperclass() != null) {
                    todo.add(c.getSuperclass());
                }
                for (Class<?> i : c.getInterfaces()) {
                    if (done.add(i)) {
                        todo.add(i);
                    }
                }

            }
            return keys;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new InternalError("class can not read its own keys");

        }
    }

    /**
     * Returns the preferred aspect ratio of the figure. The aspect ratio is
     * defined as the height divided by the width of the figure. If a figure
     * does not have a preference it should return its current aspect ratio.
     *
     * @return the preferred aspect ratio of the figure.
     */
    default double getPreferredAspectRatio() {
        Bounds bounds = getBoundsInLocal();
        return (bounds.getHeight() == 0 || bounds.getWidth() == 0) ? 1 : bounds.getHeight() / bounds.getWidth();
    }

    /**
     * Returns the center of the figure in the local coordinates of the figure.
     *
     * @return The center of the figure
     */
    default Point2D getCenterInLocal() {
        Bounds b = getBoundsInLocal();
        return new Point2D((b.getMinX() + b.getMaxX()) * 0.5, (b.getMinY()
                + b.getMaxY()) * 0.5);
    }

    /**
     * Returns the center of the figure in the local coordinates of the figure.
     *
     * @return The center of the figure
     */
    default Point2D getCenterInParent() {
        Bounds b = getBoundsInParent();
        return new Point2D((b.getMinX() + b.getMaxX()) * 0.5, (b.getMinY()
                + b.getMaxY()) * 0.5);
    }

    /**
     * Returns the transformation from parent coordinates into local
     * coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @return the transformation
     */
    Transform getParentToLocal();

    /**
     * Returns the transformation from local coordinates into parent
     * coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @return the transformation
     */
    Transform getLocalToParent();

    /**
     * Returns the transformation from world coordinates into local coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @return the transformation
     */
    Transform getWorldToLocal();

    /**
     * Returns the transformation from world coordinates into parent
     * coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @return the transformation
     */
    Transform getWorldToParent();

    /**
     * Returns the transformation from local coordinates into world coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @return the transformation
     */
    Transform getLocalToWorld();

    /**
     * Returns the transformation from world coordinates into drawing
     * coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @return the transformation
     */
    Transform getParentToWorld();

    /**
     * Transforms the specified point from world coordinates into local
     * coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @param pointInWorld point in drawing coordinates
     * @return point in local coordinates
     */
    default Point2D worldToLocal(Point2D pointInWorld) {
        return getWorldToLocal().transform(pointInWorld);
    }

    /**
     * Transforms the specified point from world coordinates into parent
     * coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @param pointInWorld point in drawing coordinates
     * @return point in local coordinates
     */
    default Point2D worldToParent(Point2D pointInWorld) {
        return getWorldToParent().transform(pointInWorld);
    }

    /**
     * Transforms the specified point from local coordinates into drawing
     * coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @param p point in drawing coordinates
     * @return point in local coordinates
     */
    default Point2D localToWorld(Point2D p) {
        return getLocalToWorld().transform(p);
    }

    @Override
    default Styleable getStyleableParent() {
        return getParent();
    }

    /**
     * Clears all cached values.
     *
     * The default implementation calls {@link #invalidateTransforms}.
     */
    default void invalidate() {
        invalidateTransforms();
    }

    /**
     * Clears all cached transformation matrices.
     * <p>
     * This figure may cache transformation matrices. However, this figure does
     * not keep track when its cache becomes invalid. Use a 
     * {@link org.jhotdraw.draw.model.DrawingModel} to manage this cache, or
     * invoke this method after you performed one of the following operations:
     * <ul>
     * <li>Addition or removal of this figure (or of any of its ancestors) from
     * its parent figure. (That is, performing a structural change in the
     * ancestor line of this figure).</li>
     * <li>Changing a property of this figure or of one of its ancestors with
     * dirty bits {@link org.jhotdraw.draw.key.DirtyBits#TRANSFORM}</li>
     * </ul>
     */
    void invalidateTransforms();
}

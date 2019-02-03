/* @(#)Figure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.Styleable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableSet;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.ReadOnlySet;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.handle.AnchorOutlineHandle;
import org.jhotdraw8.draw.handle.BoundsInLocalOutlineHandle;
import org.jhotdraw8.draw.handle.BoundsInTransformOutlineHandle;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.handle.MoveHandle;
import org.jhotdraw8.draw.handle.ResizeHandleKit;
import org.jhotdraw8.draw.handle.RotateHandle;
import org.jhotdraw8.draw.handle.TransformHandleKit;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.tree.TreeNode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A <em>figure</em> is a graphical (figurative) element of a {@link Drawing}.
 * <p>
 * <b>Rendering.</b> A figure can render a JavaFX scene graph (see {@link Node})
 * with the help of a {@link RenderContext}. The contents of the scene graph
 * depends on the class of the figure, the state of the figure, and the state of
 * the render context.
 * <p>
 * <b>State.</b> The state of a figure is defined by its property values. The
 * state consists of genuine property values and of computed property
 * values.<br>
 * Genuine property values typically describe the shape and the style of a
 * figure.<br>
 * Computed property values typically describe the layout of the figure or
 * cached values. Such as cached CSS properties and cached transformation
 * matrices. Computed property values often depend on the state of other
 * figures.
 * <p>
 * <b>Tree Structure.</b> A figure can be composed of other figures in a tree
 * structure. The composition is implemented with the {@code children} property
 * and the {@code parent} property.<br>
 * The composition can be restricted. Typically the parent of {@code Layer}
 * objects is restricted to instances of {@code Drawing}, and the parent of all
 * other figures is restricted to non-instances of {@code Drawing}.
 * <p>
 * <b>Local Coordinate Systems.</b> A figure may introduce a local coordinate
 * system which affects the graphical representation of itself and of its
 * descendants.<br>
 * The Figure interface provides methods which allow to transform between the
 * local coordinate system of a figure, the coordinate system of its parent, and
 * the world coordinate system.</p>
 * <p>
 * <b>Dependent Figures.</b> The state of a figure may depend on the state of
 * other figures. These dependencies are made explicit by parent/child
 * relationships and provider/dependant relationships.<br>
 * The parent/child relationships are strictly hierarchical, the
 * provider/dependant relationships may include cycles.<br>
 * The parent/child relationships are typically used for grouping figures into
 * {@code Layer}s, {@code Group}s and into layout hierarchies.<br>
 * The provider/dependant relationships are typically used for the creation of
 * line connections between figures, such as with
 * {@link SimpleLineConnectionFigure}. The strategy for updating the state of
 * dependent figures is implement in {@link DrawingModel}.
 * <p>
 * <b>Handles.</b> A figure can produce {@code Handle}s which allow to
 * graphically change the state of the figure in a drawing view.</p>
 * <p>
 * <b>Map Accessors.</b> A figure has an open ended set of property values. The
 * property values are accessed using {@code FigureMapAccessor}s.
 * <p>
 * <b>Styling.</b> Some property values of a figure can be styled using CSS. The
 * corresponding property key must implement the interface
 * {@link org.jhotdraw8.styleable.WriteableStyleableMapAccessor}.</p>
 * <p>
 * <b>Update Strategy.</b> A figure does not automatically update its computed
 * property values. The update strategy is factored out into
 * {@link org.jhotdraw8.draw.model.DrawingModel}. Drawing model uses {@link
 * org.jhotdraw8.draw.key.DirtyBits} in the {@code FigureMapAccessor} to
 * determine which dependent figures need to be updated.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern Drawing Framework, KeyAbstraction.
 * @design.pattern org.jhotdraw8.draw.model.DrawingModel Facade, Subsystem.
 * @design.pattern org.jhotdraw8.draw.model.DrawingModel Strategy, Context.
 * @design.pattern RenderContext Builder, Builder.
 * @design.pattern Handle Adapter, Adaptee.
 * @design.pattern org.jhotdraw8.draw.tool.CreationTool AbstractFactory,
 * AbstractProduct.
 * @design.pattern org.jhotdraw8.draw.locator.Locator Strategy, Context.
 * @design.pattern org.jhotdraw8.draw.connector.Connector Strategy, Context.
 * @design.pattern Figure Mixin, Mixin. The Mixin pattern is used to extend the
 * functionality of a class that implements the {@link Figure} interface. The
 * functionality is provided by interfaces with default methods (traits).
 * @design.pattern Figure Composite, Component. {@link Figure} uses the
 * composite pattern to provide uniform access to composite nodes and leaf nodes
 * of a tree structure.
 */
public interface Figure extends StyleablePropertyBean, TreeNode<Figure> {

    // ----
    // various declarations
    // ----
    /**
     * To avoid name clashes in the stylesheet, all styleable JHotDraw
     * getProperties use the prefix {@code "-jhotdraw-"}.
     * <p>
     * XXX mapping of css attribute names to keys should be done elsewhere!
     */
    String JHOTDRAW_CSS_PREFIX = "";
    // ----
    // key declarations
    // ----

    // ----
    // property names
    // ----
    /**
     * The name of the parent property.
     */
    String PARENT_PROPERTY = "parent";

    /**
     * Computes the union of the bounds of the provided figures in world
     * coordinates.
     *
     * @param selection a set of figures
     * @return bounds
     */
    static Bounds bounds(Collection<? extends Figure> selection) {
        double minx = Double.POSITIVE_INFINITY;
        double miny = Double.POSITIVE_INFINITY;
        double maxx = Double.NEGATIVE_INFINITY;
        double maxy = Double.NEGATIVE_INFINITY;

        for (Figure f : selection) {
            Bounds fb = Transforms.transform(f.getLocalToWorld(), f.getBoundsInLocal());
            double v = fb.getMaxX();
            if (v > maxx) {
                maxx = v;
            }
            v = fb.getMaxY();
            if (v > maxy) {
                maxy = v;
            }
            v = fb.getMinX();
            if (v < minx) {
                minx = v;
            }
            v = fb.getMinY();
            if (v < miny) {
                miny = v;
            }
        }
        return new BoundingBox(minx, miny, maxx - minx, maxy - miny);
    }

    /**
     * FIXME should be private!
     */
    Map<Class<?>, Set<MapAccessor<?>>> declaredAndInheritedKeys = new HashMap<>();

    static void getDeclaredMapAccessors(Class<?> clazz, @Nonnull Collection<MapAccessor<?>> keys) {
        try {
            for (Field f : clazz.getDeclaredFields()) {
                if (MapAccessor.class.isAssignableFrom(f.getType())) {
                    MapAccessor<?> k = (MapAccessor<?>) f.get(null);
                    keys.add(k);
                }
            }
        } catch (@Nonnull IllegalArgumentException | IllegalAccessException ex) {
            throw new InternalError("class can not read its own keys", ex);
        }
    }

    static void getDeclaredKeys(Class<?> clazz, @Nonnull Collection<Key<?>> keys) {
        try {
            for (Field f : clazz.getDeclaredFields()) {
                if (Key.class.isAssignableFrom(f.getType())) {
                    Key<?> k = (Key<?>) f.get(null);
                    keys.add(k);
                }
            }
        } catch (@Nonnull IllegalArgumentException | IllegalAccessException ex) {
            throw new InternalError("class can not read its own keys");
        }
    }

    /**
     * Returns all keys declared in this class and inherited from parent
     * classes.
     *
     * @param clazz A figure class.
     * @return an unmodifiable set of the keys
     */
    static Set<MapAccessor<?>> getDeclaredAndInheritedMapAccessors(Class<?> clazz) {
        Set<MapAccessor<?>> keys = declaredAndInheritedKeys.get(clazz);
        if (keys == null) {
            keys = new HashSet<>();
            LinkedList<Class<?>> todo = new LinkedList<>();
            Set<Class<?>> done = new HashSet<>();
            todo.add(clazz);
            while (!todo.isEmpty()) {
                Class<?> c = todo.removeFirst();
                getDeclaredMapAccessors(c, keys);
                if (c.getSuperclass() != null) {
                    todo.add(c.getSuperclass());
                }
                for (Class<?> i : c.getInterfaces()) {
                    if (done.add(i)) {
                        todo.add(i);
                    }
                }

            }
            keys = Collections.unmodifiableSet(keys);
            declaredAndInheritedKeys.put(clazz, keys);
        }
        return keys;
    }

    /**
     * Computes the union of the visual bounds of the provided figures in world
     * coordinates.
     *
     * @param selection a set of figures
     * @return bounds
     */
    @Nullable
    static Bounds visualBounds(Collection<Figure> selection) {
        Bounds b = null;

        for (Figure f : selection) {
            Bounds fb;
            if (f instanceof Drawing) {
                fb = (f.getLocalToWorld() == null) ? f.getBoundsInLocal() : f.getLocalToWorld().transform(f.getBoundsInLocal());
                if (b == null) {
                    b = fb;
                } else {
                    b = Geom.union(b, fb);
                }
            } else {
                for (Figure ff : f.preorderIterable()) {
                    fb = ff.getBoundsInLocal();
                    double grow = 0.0;
                    if (ff.get(StrokableFigure.STROKE) != null) {
                        switch (ff.getNonnull(StrokableFigure.STROKE_TYPE)) {
                            case CENTERED:
                                grow += ff.getNonnull(StrokableFigure.STROKE_WIDTH).getConvertedValue() * 0.5;
                                break;
                            case INSIDE:
                                break;
                            case OUTSIDE:
                                grow += ff.getNonnull(StrokableFigure.STROKE_WIDTH).getConvertedValue();
                                break;
                        }
                    }
                    if (ff.get(CompositableFigure.EFFECT) != null) {
                        grow += 10.0;
                    }
                    fb = Geom.grow(fb, grow, grow);
                    fb = f.localToWorld(fb);
                    if (b == null) {
                        b = fb;
                    } else {
                        b = Geom.union(b, fb);
                    }
                }
            }
        }
        return b;
    }

    // ----
    // convenience methods
    // ----

    /**
     * Adds a new child to the figure.
     *
     * @param newChild the new child
     */
    default void addChild(Figure newChild) {
        getChildren().add(newChild);
    }

    /**
     * Invoked by {@code DrawingModel} when the figure is added to a drawing.
     *
     * @param drawing the drawing to which this figure has been added
     */
    default void addNotify(Drawing drawing) {
    }

    /**
     * Creates handles of the specified level and adds them to the provided
     * list.
     *
     * @param handleType The desired handle type
     * @param list       The handles.
     */
    default void createHandles(HandleType handleType, @Nonnull List<Handle> list) {
        if (handleType == HandleType.SELECT) {
            list.add(new BoundsInLocalOutlineHandle(this));
        } else if (handleType == HandleType.ANCHOR) {
            list.add(new AnchorOutlineHandle(this));
        } else if (handleType == HandleType.LEAD) {
            list.add(new AnchorOutlineHandle(this, Handle.STYLECLASS_HANDLE_LEAD_OUTLINE));
        } else if (handleType == HandleType.MOVE) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            list.add(new MoveHandle(this, RelativeLocator.NORTH_EAST));
            list.add(new MoveHandle(this, RelativeLocator.NORTH_WEST));
            list.add(new MoveHandle(this, RelativeLocator.SOUTH_EAST));
            list.add(new MoveHandle(this, RelativeLocator.SOUTH_WEST));
        } else if (handleType == HandleType.POINT) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_POINT_OUTLINE));
            ResizeHandleKit.addCornerResizeHandles(this, list, Handle.STYLECLASS_HANDLE_POINT);
        } else if (handleType == HandleType.RESIZE) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_RESIZE_OUTLINE));
            if (this instanceof ResizableFigure) {
                ResizeHandleKit.addCornerResizeHandles(this, list, Handle.STYLECLASS_HANDLE_RESIZE);
                ResizeHandleKit.addEdgeResizeHandles(this, list, Handle.STYLECLASS_HANDLE_RESIZE);
            } else {
                list.add(new MoveHandle(this, RelativeLocator.NORTH_EAST, Handle.STYLECLASS_HANDLE_RESIZE));
                list.add(new MoveHandle(this, RelativeLocator.NORTH_WEST, Handle.STYLECLASS_HANDLE_RESIZE));
                list.add(new MoveHandle(this, RelativeLocator.SOUTH_EAST, Handle.STYLECLASS_HANDLE_RESIZE));
                list.add(new MoveHandle(this, RelativeLocator.SOUTH_WEST, Handle.STYLECLASS_HANDLE_RESIZE));

            }
        } else if (handleType == HandleType.TRANSFORM) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_TRANSFORM_OUTLINE));
            list.add(new BoundsInTransformOutlineHandle(this, Handle.STYLECLASS_HANDLE_TRANSFORM_OUTLINE));
            if (this instanceof TransformableFigure) {
                TransformableFigure tf = (TransformableFigure) this;
                list.add(new RotateHandle(tf));
                TransformHandleKit.addTransformHandles(tf, list);
            }
        }
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
     * This method is invoked on a figure by
     * {@link org.jhotdraw8.draw.model.DrawingModel} when it determines that one
     * or more layout subjects have been added or removed.
     * <p>
     * The default implementation of this method is empty.
     */
    default void layoutSubjectChangedNotify() {
    }

    /**
     * This method is invoked on a figure by
     * {@link org.jhotdraw8.draw.model.DrawingModel} when it determines that one
     * or more layout observers have been added or removed.
     * <p>
     * The default implementation of this method is empty.
     */
    default void layoutObserverChangedNotify() {
    }

    /**
     * Disconnects all layout subjects and layout observers from this figure.
     * <p>
     * This method is called, when the figure is about to be removed from a
     * drawing.
     */
    default void disconnect() {
        for (Figure connectedFigure : new ArrayList<>(getLayoutObservers())) {
            connectedFigure.removeLayoutSubject(this);
        }
        removeAllLayoutSubjects();
    }

    /**
     * Fires a property change event.
     *
     * @param <T>      the value type
     * @param source   the event source
     * @param key      the property key
     * @param oldValue the old property value
     * @param newValue the new property value
     */
    default <T> void firePropertyChangeEvent(Figure source, Key<T> key, @Nullable T oldValue, @Nullable T newValue) {
        if (hasPropertyChangeListeners()) {
            firePropertyChangeEvent(new FigurePropertyChangeEvent(source, key, oldValue, newValue));
        } else {
            Figure parent = getParent();
            if (parent != null) {
                parent.firePropertyChangeEvent(source, key, oldValue, newValue);
            }
        }
    }

    /**
     * Fires a property change event.
     *
     * @param event the event
     */
    default void firePropertyChangeEvent(FigurePropertyChangeEvent event) {
        if (hasPropertyChangeListeners()) {
            for (Listener<FigurePropertyChangeEvent> l : getPropertyChangeListeners()) {
                l.handle(event);
            }
        }
        Figure parent = getParent();
        if (parent != null) {
            parent.firePropertyChangeEvent(event);
        }
    }

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
     * stale. Invoke {@link #layout} if you are not sure that the cache is
     * valid.
     *
     * @return the local bounds
     */
    default Bounds getBoundsInLocal() {
        return getCssBoundsInLocal().getConvertedBoundsValue();
    }

    CssRectangle2D getCssBoundsInLocal();

    /**
     * The bounds that should be used for layout calculations for this figure.
     * <p>
     * The bounds are given in the coordinate space of the parent figure.
     * <p>
     * This method may use caching and return incorrect results if the caches
     * are stale. Invoke {@link #invalidateTransforms} and {@link #layout} if
     * you are not sure that the cache is valid.
     *
     * @return the local bounds
     */
    default Bounds getBoundsInParent() {
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
        if (t != null) {
            t.transform2DPoints(points, 0, points, 0, 4);
        }

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
     * Returns the bounds of the figure in world coordinates.
     *
     * @return the bounds in world coordinates
     */
    default Bounds getBoundsInWorld() {
        return Transforms.transform(getLocalToWorld(), getBoundsInLocal());
    }


    /**
     * Returns the center of the figure in the local coordinates of the figure.
     *
     * @return The center of the figure
     */
    @Nonnull
    default Point2D getCenterInLocal() {
        Bounds b = getBoundsInLocal();
        return Geom.center(b);
    }

    /**
     * Returns the center of the figure in the local coordinates of the figure.
     *
     * @return The center of the figure
     */
    @Nonnull
    default Point2D getCenterInParent() {
        Bounds b = getBoundsInParent();
        return Geom.center(b);
    }

    /**
     * The child figures.
     * <p>
     * All changes on this list causes this figure to fire an invalidation
     * event.
     * <p>
     * If a child is added to this list, then this figure removes the child from
     * its former parent, and then sets itself as the parent of the child.</p>
     * <p>
     * If a child is removed from this list, then this figure sets the parent of
     * the child to null.</p>
     *
     * @return the children
     */
    @Override
    ObservableList<Figure> getChildren();

    /**
     * Returns all figures which observe the layout of this figure.
     * <p>
     * When the layout of this figure changes, then the layout of the observers
     * figures must be updated.
     * <p>
     * The update strategy is implemented in {@link DrawingModel}.
     * {@code DrawingMode} observes state changes in figures and updates
     * dependent figures. {@code DrawingModel} can coalesce multiple state
     * changes of an observed figure into a smaller number of layout calls on
     * the observers. {@code DrawingModel} can also detect cyclic layout
     * dependencies and prevent endless update loops.
     *
     * @return a list of dependent figures
     */
    ObservableSet<Figure> getLayoutObservers();

    /**
     * Returns the ancestor Drawing.
     *
     * @return the drawing or null if no ancestor is a drawing. Returns this, if
     * this figure is a drawing.
     */
    @Nullable
    default Drawing getDrawing() {
        return getAncestor(Drawing.class);
    }

    /**
     * Returns the ancestor Layer.
     *
     * @return the drawing or null if no ancestor is a layer. Returns this, if
     * this figure is a layer.
     */
    @Nullable
    default Layer getLayer() {
        return getAncestor(Layer.class);
    }

    /**
     * Returns the transformation from local coordinates into parent
     * coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @return the transformation
     */
    @Nullable
    Transform getLocalToParent();

    /**
     * Returns the transformation from local coordinates into world coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @return the transformation
     */
    @Nullable
    Transform getLocalToWorld();

    @Override
    @Nullable
    default Figure getParent() {
        return parentProperty().get();
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
    @Nullable
    Transform getParentToLocal();

    /**
     * Returns the transformation from world coordinates into drawing
     * coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @return the transformation
     */
    @Nullable
    Transform getParentToWorld();

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
     * List of property change listeners.
     *
     * @return a list of property change listeners
     */
    CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> getPropertyChangeListeners();

    /**
     * Returns all figures which are subject to the layout of this figure.
     * <p>
     * When the layout of a layout subject changes, then the layout of this
     * figure needs to be updated.
     * <p>
     * See {@link #getLayoutObservers} for a description of the update strategy.
     * <p>
     * This default implementation returns an unmodifiable empty set.
     *
     * @return a list of layout subjects
     */
    default ReadOnlySet<Figure> getLayoutSubjects() {
        return ImmutableSet.emptySet();
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

    @Override
    default Styleable getStyleableParent() {
        return getParent();
    }

    // ---
    // static methods
    // ---

    /**
     * Returns all supported map accessors of the figure.
     * <p>
     * The default implementation returns all declared and inherited map
     * accessors.
     *
     * @return an unmodifiable set of keys
     */
    default Set<MapAccessor<?>> getSupportedKeys() {
        return Figure.getDeclaredAndInheritedMapAccessors(this.getClass());
    }

    /**
     * Returns the transformation from world coordinates into local coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @return the transformation
     */
    @Nullable
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
    @Nullable
    Transform getWorldToParent();

    /**
     * Whether this figure has property change listeners.
     *
     * @return true if this figure has property change listeners
     */
    boolean hasPropertyChangeListeners();

    /**
     * Invalidates the transformation matrices of this figure.
     * <p>
     * This figure does not keep track of changes that cause the invalidation of
     * its transformation matrices. Use a
     * {@link org.jhotdraw8.draw.model.DrawingModel} to manage the
     * transformation matrices of the figures in a drawing. Or call this method
     * on a figure and all its descendants, after you have performed a change
     * which invalidated the transform matrices of the figure.
     *
     * @return true if the transformation matrices of the child figures must be
     * invalidated as well
     */
    boolean invalidateTransforms();

    /**
     * Whether children may be added to this figure.
     *
     * @return true if getChildren are allowed
     */
    boolean isAllowsChildren();

    /**
     * Whether the figure is decomposable by the user.
     *
     * @return true if the figure is decomposable
     */
    default boolean isDecomposable() {
        return true;
    }

    /**
     * Whether the figure is deletable by the user.
     *
     * @return true if the user may delete the figure
     */
    boolean isDeletable();

    /**
     * Whether the figure is editable by the user.
     *
     * @return true if the user may edit the figure.
     */
    boolean isEditable();

    /**
     * Whether the figure can be reshaped as a group together with other
     * figures.
     * <p>
     * If this figure uses one of the other figures for computing its position
     * or its layout, then it will return false.
     * <p>
     * The default implementation always returns true.
     *
     * @param others A set of figures.
     * @return true if the user may reshape this figure together with
     * those in the set.
     */
    default boolean isGroupReshapeableWith(Set<Figure> others) {
        return true;
    }

    /**
     * Whether the {@code layout} method of this figure does anything.
     * <p>
     * The default implementation returns false.
     *
     * @return true if the {@code layout} method is not empty.
     */
    default boolean isLayoutable() {
        return false;
    }

    /**
     * Whether the figure is selectable by the user.
     *
     * @return true if the user may select the figure
     */
    boolean isSelectable();

    /**
     * This method whether the provided figure is a suitable parent for this
     * figure.
     *
     * @param newParent The new parent figure.
     * @return true if {@code newParent} is an acceptable parent
     */
    boolean isSuitableParent(Figure newParent);

    /**
     * Returns true if the specified key is supported by this figure.
     * <p>
     * The default implementation returns all declared and inherited map
     * accessors.
     *
     * @param key a key
     * @return whether the key is supported
     */
    default boolean isSupportedKey(MapAccessor<?> key) {
        return getSupportedKeys().contains(key);
    }

    /**
     * Whether the figure and all its ancestors are visible.
     *
     * @return true if the user can see the figure
     */
    default boolean isShowing() {
        Figure node = this;
        while (node != null) {
            if (!node.getStyledNonnull(HideableFigure.VISIBLE)) {
                return false;
            }
            node = node.getParent();
        }
        return true;
    }

    /**
     * Whether the figure is visible.
     *
     * @return true if the user can see the figure
     */
    default boolean isVisible() {
        Figure node = this;
        return node.getStyledNonnull(HideableFigure.VISIBLE);
    }

    /**
     * Updates the layout of this figure, based on the layout of its children
     * and the layout of observed layout subjects.
     * <p>
     * This figure does not keep track of changes that require layout updates.
     * {@link org.jhotdraw8.draw.model.DrawingModel} to manage layout updates.
     * <p>
     * The default implementation is empty.
     *
     * @param ctx the render context (optional)
     */
    default void layout(@Nonnull RenderContext ctx) {

    }

    /**
     * This method is invoked on a figure by
     * {@link org.jhotdraw8.draw.model.DrawingModel} when it determines that the
     * figure needs to be laid out.
     * <p>
     * The default implementation of this method calls {@link #layout}.
     *
     * @param ctx the render context (optional)
     */
    default void layoutNotify(@Nonnull RenderContext ctx) {
        layout(ctx);
    }

    /**
     * Transforms the specified point from local coordinates into world
     * coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @param p point in local coordinates
     * @return point in world coordinates
     */
    @Nonnull
    default Point2D localToWorld(@Nonnull Point2D p) {
        final Transform ltw = getLocalToWorld();
        return ltw == null ? p : ltw.transform(p);
    }

    /**
     * Transforms the specified bounds from local coordinates into world
     * coordinates.
     * <p>
     * This method may use caching and return incorrect results if the cache is
     * stale.
     *
     * @param p bounds in local coordinates
     * @return bounds in world coordinates
     */
    @Nonnull
    default Bounds localToWorld(@Nonnull Bounds p) {
        final Transform ltw = getLocalToWorld();
        return ltw == null ? p : ltw.transform(p);
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

    /**
     * Removes a child from the figure.
     *
     * @param child a child of the figure
     */
    default void removeChild(Figure child) {
        getChildren().remove(child);
    }

    /**
     * Requests to removeChild all connection targets.
     */
    void removeAllLayoutSubjects();

    // ----
    // property fields
    // ----

    /**
     * Removes the specified connection target.
     *
     * @param targetFigure a Figure which is a connection target.
     */
    void removeLayoutSubject(Figure targetFigure);

    /**
     * Invoked by {@code DrawingModel} when the figure is removed from a
     * drawing.
     *
     * @param drawing the drawing from which this figure has been removed
     */
    default void removeNotify(Drawing drawing) {
    }

    /**
     * Attempts to change the local bounds of the figure.
     * <p>
     * The figure may choose to only partially change its local bounds.
     *
     * @param transform the desired transformation in local coordinates
     */
    void reshapeInLocal(Transform transform);

    /**
     * Attempts to change the local bounds of the figure.
     * <p>
     * See {@link #reshapeInLocal(CssSize, CssSize, CssSize, CssSize)} for a description of this method.
     *
     * @param bounds the desired bounds
     */
    default void reshapeInLocal(@Nonnull Bounds bounds) {
        reshapeInLocal(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    }

    /**
     * Attempts to change the local bounds of the figure.
     * <p>
     * See {#link #reshapeInLocal(Transform)} for a description of this method.
     * <p>
     * This is a convenience method which takes all parameters in pixel units.
     *
     * @param x      desired x-position in parent coordinates
     * @param y      desired y-position in parent coordinates
     * @param width  desired width in parent coordinates, may be negative
     * @param height desired height in parent coordinates, may be negative
     */
    default void reshapeInLocal(double x, double y, double width, double height) {
        reshapeInLocal(new CssSize(x), new CssSize(y), new CssSize(width), new CssSize(height));
    }

    /**
     * Attempts to change the local bounds of the figure.
     * <p>
     * See {#link #reshapeInLocal(Transform)} for a description of this method.
     * <p>
     * This method takes parameters as {@code CssSize}s. This can be used to avoid rounding
     * errors when the figure is reshaped in non-pixel units.
     * <p>
     * This method can forward a call to {@link #reshapeInLocal(Transform)}
     * using the following code:
     * <pre><code>
     * void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
     *   Transform tx = Transforms.createReshapeTransform(getCssBoundsInLocal(), x, y, width, height);
     *   reshapeInLocal(tx);
     * }
     * </code></pre>
     *
     * @param x      desired x-position in parent coordinates
     * @param y      desired y-position in parent coordinates
     * @param width  desired width in parent coordinates, may be negative
     * @param height desired height in parent coordinates, may be negative
     */
    void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height);

    /**
     * Attempts to change the parent bounds of the figure.
     * <p>
     * The figure may choose to only partially change its parent bounds.
     * <p>
     * This method typically changes property values in this figure with
     * {@link org.jhotdraw8.draw.key.DirtyBits#NODE},
     * {@link org.jhotdraw8.draw.key.DirtyBits#LAYOUT},
     * {@link org.jhotdraw8.draw.key.DirtyBits#TRANSFORM} in the
     * {@link org.jhotdraw8.draw.key.FigureKey}. This method may also call
     * {@code reshapeInLocal} on child figures.
     *
     * @param transform the desired transformation in parent coordinates
     */
    void reshapeInParent(Transform transform);

    /**
     * Attempts to translate the parent bounds of the figure.
     *
     * @param t the translation in x and in y direction
     */
    default void translateInParent(CssPoint2D t) {
        if (Transforms.isIdentityOrNull(getParentToLocal())) {
            translateInLocal(t);
        } else {
            Point2D p = t.getConvertedValue();
            reshapeInParent(new Translate(p.getX(), p.getY()));
        }
    }

    /**
     * Attempts to translate the local bounds of the figure.
     *
     * @param t the translation in x and in y direction
     */
    default void translateInLocal(CssPoint2D t) {
        CssRectangle2D b = getCssBoundsInLocal();
        reshapeInLocal(b.getMinX().add(t.getX()),
                b.getMinY().add(t.getY()),
                b.getWidth(), b.getHeight());
    }

    /**
     * This method is invoked on a figure by
     * {@link org.jhotdraw8.draw.model.DrawingModel} when it determines that the
     * figure needs to apply its stylesheet again.
     * <p>
     * The default implementation of this method calls {@link #updateCss} and
     * then {@code #layout}.
     *
     * @param ctx the render context (optional)
     */
    default void stylesheetNotify(@Nonnull RenderContext ctx) {
        updateCss();
        layout(ctx);
    }

    /**
     * Attempts to transform the figure.
     * <p>
     * The figure may choose to only partially change its transformation.
     *
     * @param transform the desired transformation in local coordinates
     */
    void transformInLocal(Transform transform);

    /**
     * Attempts to transform the figure.
     * <p>
     * The figure may choose to only partially change its transformation.
     *
     * @param transform the desired transformation in parent coordinates
     */
    void transformInParent(Transform transform);

    /**
     * This method is invoked on a figure and all its descendants by
     * {@link org.jhotdraw8.draw.model.DrawingModel} when it determines that the
     * transformation of the figure has changed.
     * <p>
     * The default implementation of this method calls
     * {@link #invalidateTransforms}.
     *
     * @return true if the transforms were valid
     */
    default boolean transformNotify() {
        return invalidateTransforms();
    }

    /**
     * Updates the stylesheet cache of this figure depending on its property
     * values and on the and the property values of its ancestors.
     * <p>
     * This figure does not keep track of changes that require CSS updates. Use
     * a {@link org.jhotdraw8.draw.model.DrawingModel} to manage CSS updates.
     */
    void updateCss();

    /**
     * This method is invoked by a {@code RenderContext}, when it needs to
     * update the node which represents the scene graph in the figure.
     * <p>
     * A figure which is composed from child figures, must addChild the nodes of
     * its getChildren to its node. This ensures that coordinate space
     * transformations of the composed figure are properly propagated to its
     * getChildren.
     * </p>
     * <pre>
     * public void updateNode(RenderContext rc, Node n) {
     *     ObservableList&lt;Node&gt; group = ((Group) n).getChildren();
     * group.clear();
     * for (Figure child : children()) {
     * group.addChild(rc.getNode(child));
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
     * FX node. Also, render contexts like a drawing view need to associate
     * input events on Java FX nodes to the corresponding figure.
     * <p>
     * This figure does not keep track of changes that require node updates.
     * {@link org.jhotdraw8.draw.model.DrawingModel} to manage node updates.
     *
     * @param ctx  the render context
     * @param node the node which was created with {@link #createNode}
     */
    void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node);

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
    @Nonnull
    default Point2D worldToLocal(@Nonnull Point2D pointInWorld) {
        final Transform wtl = getWorldToLocal();
        return Transforms.isIdentityOrNull(wtl) ? pointInWorld : wtl.transform(pointInWorld);
    }

    @Nonnull
    default CssPoint2D worldToLocal(@Nonnull CssPoint2D pointInWorld) {
        final Transform wtl = getWorldToLocal();
        return Transforms.isIdentityOrNull(wtl) ? pointInWorld : new CssPoint2D(wtl.transform(pointInWorld.getConvertedValue()));
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
    @Nonnull
    default Point2D worldToParent(@Nonnull Point2D pointInWorld) {
        final Transform wtp = getWorldToParent();
        return Transforms.isIdentityOrNull(wtp) ? pointInWorld : wtp.transform(pointInWorld);
    }
}

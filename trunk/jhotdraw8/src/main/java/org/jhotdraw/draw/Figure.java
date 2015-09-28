/* @(#)Figure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.SimpleFigureKey;
import java.io.IOException;
import static java.lang.Math.*;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.BoundsInLocalHandle;
import static java.lang.Math.min;
import static java.lang.Math.max;
import javafx.css.Styleable;
import javafx.geometry.BoundingBox;
import javafx.scene.transform.Translate;
import org.jhotdraw.collection.BooleanKey;
import org.jhotdraw.draw.css.StyleablePropertyBean;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.key.BlendModeStyleableFigureKey;
import org.jhotdraw.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw.draw.key.EffectStyleableFigureKey;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;

/**
 * A <em>figure</em> is a graphical (figurative) element of a {@link Drawing}.
 * <p>
 * <b>State.</b> The state of a figure is described by the values of its
 * property map.</p>
 * <p>
 * <b>Tree Structure.</b> A figure can be composed of other figures in a tree
 * structure. The composition is implemented with the {@code children} and the
 * {@code parent} properties. The composition can be restricted to a specific
 * parent type. By convention all children of a {@code Drawing} must be
 * {@link Layer}s, and the parent of a {@code Layer} must be a
 * {@code Drawing}.</p>
 * <p>
 * <b>Connections.</b> A figure can be connected to other figures. The
 * connections are directed. By convention, when a figure "A" is connected to an
 * other figure "B", then "A" adds itself in the {@code connections} property of
 * "B". When "A" is disconnected from "B", then "A" removes itself from the
 * {@code connections} property of "B".</p>
 * <p>
 * <b>Rendering.</b> A figure can render its graphical representation into a
 * JavaFX {@code Node} with the help of a {@link RenderContext}.</p>
 * <p>
 * <b>Handles.</b> A figure can produce {@code Handle}s which allow to
 * graphically change the state of the figure in a {@link DrawingView}.</p>
 * <p>
 * <b>Layout.</b> The state of a figure may depend on the state of other
 * figures. The dependencies can be cyclic due to connections. A figure does not
 * automatically update its dependent state. Method {@code layout()} must be
 * invoked to incrementally update the state of a figure and its descendants
 * based on the current state of all other figures in the tree structure.</p>
 * <p>
 * <b>Layout hints and node update hints.</b> Essentially each time when the
 * state of a figure is changed, method {@code layout()} needs to be invoked on
 * the root of the tree hierarchy to incrementally update the state of all
 * dependent figures and then all figures need to be rendered again. This is
 * time consuming. The interface {@link org.jhotdraw.draw.key.FigureKey}
 * provides hints about which figures need to be laid out and rendered again.
 * The hints are given by a {@link DirtyMask}.
 * </p>
 * <p>
 * <b>Styling.</b> Some property values of a figure can be styled using CSS. The
 * corresponding property key must implement the interface
 * {@link org.jhotdraw.draw.css.StyleableKey}. The style information is cached
 * in the figure properties. When the position of a figure in the tree structure
 * is changed, method {@code applyCss()} must be called to update the style
 * information of the figure and its descendants.</p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Figure extends StyleablePropertyBean {

    // ----
    // various declarations
    // ----
    /**
     * To avoid name clashes in the stylesheet, all styleable JHotDraw
     * properties use the prefix {@code "-jhotdraw-"}.
     */
    public final static String JHOTDRAW_CSS_PREFIX = "-jhotdraw-";
    // ----
    // key declarations
    // ----

    /**
     * Specifies a blend mode applied to the figure. The {@code null} value is
     * interpreted as {@code SRC_OVER}.
     * <p>
     * Default value: {@code SRC_OVER}.
     */
    public static BlendModeStyleableFigureKey BLEND_MODE = new BlendModeStyleableFigureKey("blendMode", BlendMode.SRC_OVER);
    /**
     * Specifies an effect applied to the figure. The {@code null} value means
     * that no effect is applied.
     * <p>
     * Default value: {@code null}.
     */
    public static EffectStyleableFigureKey EFFECT = new EffectStyleableFigureKey("effect", null);
    /**
     * Specifies the opacity of the figure. A figure with {@code 0} opacity is
     * completely translucent. A figure with {@code 1} opacity is completely
     * opaque.
     * <p>
     * Values smaller than {@code 0} are treated as {@code 0}. Values larger
     * than {@code 1} are treated as {@code 1}.
     * <p>
     * Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey OPACITY = new DoubleStyleableFigureKey("opacity", 1.0);
    /**
     * Defines the angle of rotation around the center of the figure in degrees.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey ROTATE = new DoubleStyleableFigureKey("rotate", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    /**
     * Defines the rotation axis used. Default value: {@code Rotate.Z_AXIS}.
     */
    public static SimpleFigureKey<Point3D> ROTATION_AXIS = new SimpleFigureKey<>("rotationAxis", Point3D.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), Rotate.Z_AXIS);
    /**
     * Defines the scale factor by which coordinates are scaled on the x axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_X = new DoubleStyleableFigureKey("scaleX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the y axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_Y = new DoubleStyleableFigureKey("scaleY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the z axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_Z = new DoubleStyleableFigureKey("scaleZ", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    /**
     * Defines the translation on the x axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_X = new DoubleStyleableFigureKey("translateX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    /**
     * Defines the translation on the y axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_Y = new DoubleStyleableFigureKey("translateY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    /**
     * Defines the translation on the z axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_Z = new DoubleStyleableFigureKey("translateZ", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    /**
     * Defines the visibility of the figure. Default value: {@code true}.
     */
    public static BooleanStyleableFigureKey VISIBLE = new BooleanStyleableFigureKey("visible", DirtyMask.of(DirtyBits.NODE), true);
    /**
     * Whether the figure is disabled. Default value: {@code false}.
     */
    public static BooleanKey DISABLED = new BooleanKey("disabled", false);
    /**
     * Defines the id of the figure. The id is used for styling the figure with
     * CSS.
     *
     * Default value: {@code null}.
     */
    public static SimpleFigureKey<String> ID = new SimpleFigureKey<>("id", String.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), null);
    /**
     * Defines the style class of the figure. The style class is used for
     * styling the figure with CSS.
     *
     * Default value: {@code null}.
     */
    public static SimpleFigureKey<ObservableList<String>> STYLE_CLASS = new SimpleFigureKey<>("styleClass", ObservableList.class, "<String>", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), null);
    /**
     * Defines the style of the figure. The style is used for styling the figure
     * with CSS.
     *
     * Default value: {@code null}.
     */
    public static SimpleFigureKey<String> STYLE = new SimpleFigureKey<>("style", List.class, "<String>", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), null);

    // ----
    // property names
    // ----
    /**
     * The name of the children property.
     */
    public final static String CHILDREN_PROPERTY = "children";
    /**
     * The name of the parent property.
     */
    public final static String PARENT_PROPERTY = "parent";
    /**
     * The name of the connections property.
     */
    public final static String CONNECTIONS_PROPERTY = "connections";

    // ----
    // property fields
    // ----
    /**
     * The child figures.
     * <p>
     * All changes on this property causes this figure to fire an invalidation
     * event.
     * <p>
     * If a child is added to this figure, the child must be removed from its
     * former parent. This figure must set itself set as the parent of the child
     * immediately after the figure has been added.</p>
     * <p>
     * If a child is removed from this figure, this figure must set parent to
     * null immediately before the child is removed.</p>
     * <p>
     * Note that this method returns a {@code ReadOnlyListProperty} and not just
     * an {@code ObservableList}. {@code ListChangeListener}s can get the
     * associated {@code Figure} using the following code:</p>
     * <pre>{@code
     * (ListChangeListener.Change change) -> Figure figure =
     *      (Figure) ((ReadOnlyProperty) change.getList()).getBean();
     * }</pre>
     *
     *
     * @return the children property, with {@code getBean()} returning this
     * figure, and {@code getName()} returning {@code CHILDREN_PROPERTY}.
     */
    ReadOnlyListProperty<Figure> childrenProperty();

    /**
     * The connection figures.
     * <p>
     * By convention this set is maintained by the connected figures.
     * <p>
     * For example, to remove a {@code ConnectionFigure} from this set set its
     * corresponding {@code START_FIGURE} or {@code END_FIGURE} property to
     * null.
     *
     * @return the connections property, with {@code getBean()} returning this
     * figure, and {@code getName()} returning {@code CONNECTIONS_PROPERTY}.
     */
    ReadOnlySetProperty<Figure> connectionsProperty();

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
     * The bounds that should be used for layout calculations for this figure.
     * <p>
     * The bounds are given in the untransformed local coordinate space of the
     * figure.
     *
     * @return the layout bounds
     */
    public Bounds getBoundsInLocal();

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

    default public Bounds getBoundsInDrawing() {
        // FIXME apply parent transforms until root
        // also implementations have to transform their
        // geometry so that we get tighter bounds
        return getBoundsInLocal();
    }

    /**
     * Attempts to change the layout bounds of the figure.
     * <p>
     * The figure may choose to only partially change its layout bounds.
     * <p>
     * Reshape typically changes property values in this figure. The way how
     * this is performed is implementation specific.
     *
     * @param transform the desired transformation
     */
    void reshape(Transform transform);

    /**
     * Attempts to change the layout bounds of the figure.
     * <p>
     * Width and height are ignored, if the figure is not resizable.
     * <p>
     * If the layout bounds of the figure changes, it fires an invalidation
     * event.
     *
     * @param x desired x-position
     * @param y desired y-position
     * @param width desired width, may be negative
     * @param height desired height, may be negative
     */
    default void reshape(double x, double y, double width, double height) {
        Bounds oldBounds = getBoundsInLocal();
        Rectangle2D newBounds = new Rectangle2D(x - min(width, 0), y
                - min(height, 0), abs(width), abs(height));

        double sx = newBounds.getWidth() / oldBounds.getWidth();
        double sy = newBounds.getHeight() / oldBounds.getHeight();

        Affine reshape = new Affine();
        Affine tx = new Affine();
        tx.appendTranslation(-oldBounds.getMinX(), -oldBounds.getMinY());
        if (!Double.isNaN(sx) && !Double.isNaN(sy)
                && !Double.isInfinite(sx) && !Double.isInfinite(sy)
                && (sx != 1d || sy != 1d)
                && !(sx < 0.0001) && !(sy < 0.0001)) {
            reshape.append(tx);
            tx.setToIdentity();
            tx.scale(sx, sy);
            reshape.append(tx);
            tx.setToIdentity();
        }
        tx.translate(newBounds.getMinX(), newBounds.getMinY());
        reshape.append(tx);
        reshape(reshape);
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
     * @param renderer the drawing view which will use the node
     * @return the newly created node
     */
    Node createNode(RenderContext renderer);

    /**
     * This method is invoked by a {@code RenderContext}, when it needs to
     * update the node which represents the scene graph in the figure.
     * <p>
     * A figure which is composed from child figures, must add the nodes of its
     * children to its node. This ensures that coordinate space transformations
     * of the composed figure are properly propagated to its children.
     * </p>
     * <pre>
     * public void updateNode(RenderContext rc, Node n) {
     *     ObservableList&lt;Node&gt; group = ((Group) n).getChildren();
     *     group.clear();
     *     for (Figure child : childrenProperty()) {
     *         group.add(rc.getNode(child));
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
     * @param renderer the drawing view
     * @param node the node which was created with {@link #createNode}
     */
    void updateNode(RenderContext renderer, Node node);

    /**
     * Whether children may be added to this figure.
     *
     * @return true if children are allowed
     */
    boolean allowsChildren();

    /**
     * Whether the {@code layout} method of this figure does anything.
     *
     * @return true if the {@code layout} method is not empty.
     */
    boolean isLayoutable();

    /**
     * Whether the figure is selectable.
     *
     * @return true if the user may select the figure
     */
    boolean isSelectable();

    /**
     * Whether the figure or one if its ancestors is disabled.
     *
     * @return true if the user may select the figure
     */
    default boolean isDisabled() {
        Figure node = this;
        while (node != null) {
            if (node.get(DISABLED)) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    /**
     * Whether the figure and all its ancestors are visible.
     *
     * @return true if the user can see the figure
     */
    default boolean isVisible() {
        Figure node = this;
        while (node != null) {
            if (!node.get(VISIBLE)) {
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
     * Creates handles of the specified level and for the specified drawing
     * view.
     *
     * @param detailLevel The desired detail level
     * @param dv The drawing view which will display the handles
     * @return The handles. Returns an empty list if the figure does not provide
     * handles at the desired detail level.
     */
    default List<Handle> createHandles(int detailLevel, DrawingView dv) {
        if (detailLevel > 0) {
            return Collections.emptyList();
        } else {
            List<Handle> list = new LinkedList<>();
            list.add(new BoundsInLocalHandle(this, dv, Handle.STYLECLASS_HANDLE_OUTLINE));
            list.add(new MoveHandle(this, dv, Handle.STYLECLASS_HANDLE_MOVE, 0, 0));
            list.add(new MoveHandle(this, dv, Handle.STYLECLASS_HANDLE_MOVE, 1, 0));
            list.add(new MoveHandle(this, dv, Handle.STYLECLASS_HANDLE_MOVE, 0, 1));
            list.add(new MoveHandle(this, dv, Handle.STYLECLASS_HANDLE_MOVE, 1, 1));
            return list;
        }
    }

    /**
     * Gets a connector for this figure at the given location.
     *
     * @param p the location of the connector.
     * @param prototype The prototype used to create a connection or null if
     * unknown. This allows for specific connectors for different connection
     * figures.
     * @return Returns the connector. Returns null if there is no connector at
     * the given location.
     */
    Connector findConnector(Point2D p, Figure prototype);

    /**
     * Updates the layout of this figure and of its descendant figures. Does not
     * update connection figures.
     */
    void layout();

    /**
     * Applies the stylesheet on this figure and on its descendant figures.
     */
    void applyCss();
    // ----
    // convenience methods
    // ----

    /**
     * Adds a new child to the figure.
     *
     * @param newChild the new child
     */
    default void add(Figure newChild) {
        childrenProperty().add(newChild);
    }

    /**
     * Removes a child from the figure.
     *
     * @param child a child of the figure
     */
    default void remove(Figure child) {
        childrenProperty().remove(child);
    }

    /**
     * Gets the child with the specified index from the figure.
     *
     * @param index the index
     * @return the child
     */
    default Figure getChild(int index) {
        return childrenProperty().get(index);
    }

    /**
     * Gets the last child.
     *
     * @return The last child. Returns null if the figure has no children.
     */
    default Figure getLastChild() {
        return childrenProperty().isEmpty() ? null : childrenProperty().get(0);
    }

    /**
     * Gets the first child.
     *
     * @return The first child. Returns null if the figure has no children.
     */
    default Figure getFirstChild() {
        return childrenProperty().isEmpty() //
                ? null//
                : childrenProperty().get(childrenProperty().getSize() - 1);
    }

    /**
     * Returns all children of the figure.
     *
     * @return a list of the children
     */
    default ObservableList<Figure> children() {
        return childrenProperty().get();
    }

    /**
     * Returns the parent figure.
     * <p>
     * Note that there is no convenience method named {@code setParent}.
     *
     * @return parent figure or null, if the figure has no parent.
     */
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
     * Returns the nearest parent Drawing.
     *
     * @return the drawing or null if no ancestor is a drawing.
     */
    default Drawing getDrawing() {
        Figure parent = this;
        while (parent != null && !(parent instanceof Drawing)) {
            parent = parent.getParent();
        }
        return (Drawing) parent;
    }

    /**
     * Returns an iterable which can iterate through this figure and all its
     * descendants in preorder sequence.
     *
     * @return the iterable
     */
    default public Iterable<Figure> preorderIterable() {

        return new Iterable<Figure>() {

            @Override
            public Iterator<Figure> iterator() {
                return new PreorderIterator(Figure.this);
            }
        };
    }

    /**
     * Returns all connections of the figure.
     *
     * @return a list of the children
     */
    default ObservableSet<Figure> connections() {
        return connectionsProperty().get();
    }

    /**
     * Updates a figure node with all applicable {@code SimpleFigureKey}s
     * defined in this interface.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applyFigureProperties(Node node) {
        node.setId(get(ID));
        node.setVisible(getStyled(VISIBLE));
        node.setBlendMode(getStyled(BLEND_MODE));
        node.setEffect(getStyled(EFFECT));
        node.setOpacity(getStyled(OPACITY));
        node.setRotate(getStyled(ROTATE));
        node.setRotationAxis(getStyled(ROTATION_AXIS));
        node.setScaleX(getStyled(SCALE_X));
        node.setScaleY(getStyled(SCALE_Y));
        node.setScaleZ(getStyled(SCALE_Z));
        node.setTranslateX(getStyled(TRANSLATE_X));
        node.setTranslateY(getStyled(TRANSLATE_Y));
        node.setTranslateZ(getStyled(TRANSLATE_Z));
    }

    // ---
    // static methods
    // ---
    /**
     * Returns all keys declared in the figure class and inherited from parent
     * classes.
     *
     * @param f A figure.
     * @return the keys
     */
    public static Set<Key<?>> getSupportedKeys(Figure f) {
        return getDeclaredAndInheritedKeys(f.getClass());
    }

    /**
     * Returns all keys declared in this class and inherited from parent
     * classes.
     *
     * @param c A figure class.
     * @return the keys
     */
    public static Set<Key<?>> getDeclaredAndInheritedKeys(Class<?> c) {
        try {
            HashSet<Key<?>> keys = new HashSet<>();
            for (Field f : c.getFields()) {
                if (Key.class.isAssignableFrom(f.getType())) {
                    Key<?> k = (Key<?>) f.get(null);
                    keys.add(k);
                }
            }
            return keys;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new InternalError("class can not read its own keys");
        }
    }

    static class PreorderIterator implements Iterator<Figure> {

        private final LinkedList<Iterator<Figure>> stack = new LinkedList<>();

        private PreorderIterator(Figure root) {
            LinkedList<Figure> v = new LinkedList<>();
            v.add(root);
            stack.push(v.iterator());
        }

        @Override
        public boolean hasNext() {
            return (!stack.isEmpty() && stack.peek().hasNext());
        }

        @Override
        public Figure next() {
            Iterator<Figure> iter = stack.peek();
            Figure node = iter.next();
            Iterator<Figure> children = node.children().iterator();

            if (!iter.hasNext()) {
                stack.pop();
            }
            if (children.hasNext()) {
                stack.push(children);
            }
            return node;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
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
     * Returns the transformation from parent coordinates into local
     * coordinates.
     *
     * @return the transformation
     */
    default Transform getParentToLocal() {
        Point2D center = getCenterInLocal();

        Transform translate = Transform.translate(-get(TRANSLATE_X), -get(TRANSLATE_Y));
        Transform scale = Transform.scale(1.0 / get(SCALE_X), 1.0 / get(SCALE_Y), center.getX(), center.getY());
        Transform rotate = Transform.rotate(-get(ROTATE), center.getX(), center.getY());

        Transform t = scale.createConcatenation(rotate).createConcatenation(translate);
        return t;
    }

    /**
     * Returns the transformation from local coordinates into parent
     * coordinates.
     *
     * @return the transformation
     */
    default Transform getLocalToParent() {
        Point2D center = getCenterInLocal();
        Transform translate = Transform.translate(get(TRANSLATE_X), get(TRANSLATE_Y));
        Transform scale = Transform.scale(get(SCALE_X), get(SCALE_Y), center.getX(), center.getY());
        Transform rotate = Transform.rotate(get(ROTATE), center.getX(), center.getY());

        Transform t = translate.createConcatenation(rotate).createConcatenation(scale);
        return t;
    }

    /**
     * Returns the transformation from drawing coordinates into local
     * coordinates.
     *
     * @return the transformation
     */
    default Transform getDrawingToLocal() {
        Transform t = getParentToLocal();
        return getParent() == null ? t : t.createConcatenation(getParent().getDrawingToLocal());
    }

    /**
     * Returns the transformation from local coordinates into drawing
     * coordinates.
     *
     * @return the transformation
     */
    default Transform getLocalToDrawing() {
        Transform t = getLocalToParent();
        return getParent() == null ? t : getParent().getLocalToDrawing().createConcatenation(t);
    }

    /**
     * Returns the transformation from parent coordinates into drawing
     * coordinates.
     *
     * @return the transformation
     */
    default Transform getParentToDrawing() {
        Transform t = new Translate();
        return getParent() == null ? t : getParent().getLocalToDrawing().createConcatenation(t);
    }

    /**
     * Transforms the specified point from drawing coordinates into local
     * coordinates.
     *
     * @param p point in drawing coordinates
     * @return point in local coordinates
     */
    default Point2D drawingToLocal(Point2D p) {
        return getDrawingToLocal().transform(p);
    }

    /**
     * Transforms the specified point from local coordinates into drawing
     * coordinates.
     *
     * @param p point in drawing coordinates
     * @return point in local coordinates
     */
    default Point2D localToDrawing(Point2D p) {
        return getLocalToDrawing().transform(p);
    }

    @Override
    default Styleable getStyleableParent() {
        return getParent();
    }

    @Override
    default String getStyle() {
        return get(STYLE);
    }

    @Override
    default ObservableList<String> getStyleClass() {
        return get(STYLE_CLASS);
    }

    @Override
    default String getId() {
        return get(ID);
    }

    /**
     * Dumps the figure and its descendants to system.out.
     */
    default void dumpTree() {
        try {
            dumpTree(System.out, 0);
        } catch (IOException e) {
            throw new InternalError(e);
        }
    }

    /**
     * Dumps the figure and its descendants.
     *
     * @param out an output stream
     * @param depth the indentation depth
     * @throws java.io.IOException from appendable
     */
    default void dumpTree(Appendable out, int depth) throws IOException {
        for (int i = 0; i < depth; i++) {
            out.append('.');
        }
        out.append(toString());
        out.append('\n');
        for (Figure child : children()) {
            child.dumpTree(out, depth + 1);
        }
    }
}

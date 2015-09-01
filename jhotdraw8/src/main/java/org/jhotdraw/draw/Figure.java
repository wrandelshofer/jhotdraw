/* @(#)Figure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import static java.lang.Math.*;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.jhotdraw.beans.OptionalProperty;
import org.jhotdraw.beans.PropertyBean;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.SimpleHighlightHandle;

/**
 * A {@code Figure} is an editable element of a {@link Drawing}.
 * <p>
 * A figure typically has a visual representation, such as a rectangle or a line.
 * <p>
 * A figure fires the following events:
 * <ul>
 * <li>An invalidation event to {@code InvalidationListener}s registered on the 
 * figure, when its JavaFX {@code Node} needs to be updated.</li>
 * <li>A change event to {@code MapChangeListenerss registered on the figures
 * {@code properties} when a property value has been changed.</li>
 * <li>A change event to {@code ListChangeListener}s registered on the figures
 * {@code childrenProperty} when a child figure has been added or removed or
 * reordered.</li>
 * </ul>
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Figure extends PropertyBean, Observable {
    // ----
    // keys
    // ----

    /**
     * Specifies a blend mode applied to the figure.
     * The {@code null} value is interpreted as {@code SRC_OVER}.
     * <p>
     * Default value: {@code SRC_OVER}.
     */
    public static Key<BlendMode> BLEND_MODE = new Key<>("blendMode", BlendMode.class, BlendMode.SRC_OVER);
    /**
     * Specifies an effect applied to the figure.
     * The {@code null} means that no effect is applied.
     * <p>
     * Default value: {@code null}.
     */
    public static Key<Effect> EFFECT = new Key<>("effect", Effect.class, null);
    /**
     * Specifies the opacity of the figure.
     * A figure with {@code 0} opacity is completely translucent.
     * A figure with {@code 1} opacity is completely opaque.
     * <p>
     * Values smaller than {@code 0} are treated as {@code 0}. 
     * Values larger than {@code 1} are treated as {@code 1}. 
     * <p>
     * Default value: {@code 1}.
     */
    public static Key<Double> OPACITY = new Key<>("opacity", Double.class, 1.0);
    /**
     * Defines the angle of rotation around the center of the figure in degrees.
     * Default value: {@code 0}.
     */
    public static Key<Double> ROTATE = new Key<>("rotate", Double.class, 0.0);
    /**
     * Defines the rotation axis used.
     * Default value: {@code Rotate.Z_AXIS}.
     */
    public static Key<Point3D> ROTATION_AXIS = new Key<>("rotationAxis", Point3D.class, Rotate.Z_AXIS);
    /**
     * Defines the scale factor by which coordinates are scaled on the x axis
     * about the center of the figure.
     * Default value: {@code 1}.
     */
    public static Key<Double> SCALE_X = new Key<>("scaleX", Double.class, 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the y axis
     * about the center of the figure.
     * Default value: {@code 1}.
     */
    public static Key<Double> SCALE_Y = new Key<>("scaleY", Double.class, 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the z axis
     * about the center of the figure.
     * Default value: {@code 1}.
     */
    public static Key<Double> SCALE_Z = new Key<>("scaleZ", Double.class, 1.0);

    // ----
    // property names
    // ----
    /** The name of the children property. */
    public final static String CHILDREN_PROPERTY = "children";
    /** The name of the parent property. */
    public final static String PARENT_PROPERTY = "parent";

    // ----
    // properties
    // ----
    /** The child figures. 
     * <p>
     * If a child is added to a figure, figure must set itself set as the parent
     * of the child immediately after the child has been added.
     * <p>
     * If a child is removed from a figure, figure must set parent to null
     * immediately before the child is removed.
     * <p>
     * Note that this method returns a {@code ReadOnlyListProperty} and not
     * just an {@code ObservableList}. {@code ListChangeListener}s can get
     * the associated {@code Figure} using the following code:
     * <pre>{@code
     * (ListChangeListener.Change change) -> Figure figure = (Figure) ((ReadOnlyProperty) change.getList()).getBean();
     * }</pre>
     * <p>
     *
     * @return the children property, with {@code getBean()} returning this figure,
     * and {@code getName()} returning {@code CHILDREN_PROPERTY}.
     */
    ReadOnlyListProperty<Figure> childrenProperty();

    /** The parent figure.
     * <p>
     * @return the children property, with {@code getBean()} returning this figure,
     * and {@code getName()} returning {@code PARENT_PROPERTY}.
     * @see #childrenProperty
     */
    OptionalProperty<Figure> parentProperty();

    // ----
    // Behaviors
    // ----
    /** The rectangular bounds that should be used for layout calculations
     * for this figure.
     * @return the layout bounds
     */
    public Bounds getLayoutBounds();

    /** Attempts to change the layout bounds of the figure.
     * <p>
     * The figure may choose to only partially perform the transformation.
     * <p>
     * If the layout bounds of the figure changes, it fires an invalidation event.
     *
     * @param transform the desired transformation
     */
    void reshape(Transform transform);

    /** Attempts to change the layout bounds of the figure.
     * <p>
     * Width and height are ignored, if the figure is not resizable.
     * <p>
     * If the layout bounds of the figure changes, it fires an invalidation event.
     * @param x desired x-position
     * @param y desired y-position
     * @param width desired width, may be negative
     * @param height desired height, may be negative
     */
    default void reshape(double x, double y, double width, double height) {
        Bounds oldBounds = getLayoutBounds();
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

    /** This method is invoked by {@code DrawingView}, when it needs a node
     * to create a scene graph for a figure.
     * <p>
     * A typical implementation should look like this:
     * <pre>{@code
     * public void putNode(DrawingView v) {
     *    v.putNode(this, new ...desired subclass of Node...());
     * }
     * }</pre>
     * <p>
     * A figure may be shown in multiple {@code DrawingView}s. 
     * Each {@code DrawingView} view uses this method to instantiate a JavaFX
     * node for the figure.
     *
     * @param drawingView 
     */
    void putNode(DrawingView drawingView);

    /** This method is invoked by {@code DrawingView}, when it needs to update
     * the node which represents the scene graph in the figure.
     * <p>
     * A figure which is composed from other figures, can add the nodes of
     * of these figures to its node.
     * <pre>{@code
     * public void updateNode(DrawingView v, Node n) {
     * ObservableList<Node> group = ((Group) n).getChildren();
     * group.clear();
     * for (Figure child : childrenProperty()) {
     * group.add(v.getNode(child));
     * }
     * }
     * }</pre>
     * <p>
     * A figure may be shown in multiple {@code DrawingView}s. 
     * Each {@code DrawingView} view uses this method to update the a JavaFX
     * node for the figure.
     *
     *
     * @param drawingView 
     */
    void updateNode(DrawingView drawingView, Node node);

    /** Whether children may be added to this figure. */
    boolean allowsChildren();

    /** Whether the figure is selectable by the user. */
    boolean isSelectable();

    /** Creates handles of the specified level and for the specified drawing view. 
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
            list.add(new SimpleHighlightHandle(this, dv));
            return list;
        }
    }
    // ----
    // Convenience Methods
    // ----

    /** Adds a new child to the figure. */
    default void add(Figure newChild) {
        childrenProperty().add(newChild);
    }

    /** Removes a child from the figure. */
    default void remove(Figure child) {
        childrenProperty().remove(child);
    }

    default ObservableList<Figure> children() {
        return childrenProperty().get();
    }

    /** Returns the parent figure.
     * @return parent figure or empty, if the figure is the root.  */
    default Optional<Figure> getParent() {
        return parentProperty().get();
    }

    /** Updates a figure node. 
     * This method can be used from {@code updateNode}.
     */
    default void updateFigureProperties(Node node) {
        node.setBlendMode(get(BLEND_MODE));
        node.setEffect(get(EFFECT));
        node.setOpacity(get(OPACITY));
        node.setRotate(get(ROTATE));
        node.setRotationAxis(get(ROTATION_AXIS));
        node.setScaleX(get(SCALE_X));
        node.setScaleY(get(SCALE_Y));
        node.setScaleZ(get(SCALE_Z));
    }

    // ---
    // static methods
    // ---
    /** Returns all keys declared in this class. */
    public static HashMap<String, Key<?>> getFigureKeys() {
        try {
            HashMap<String, Key<?>> keys = new HashMap<>();
            for (Field f : Figure.class.getDeclaredFields()) {
                if (Key.class.isAssignableFrom(f.getType())) {
                    Key<?> value = (Key<?>) f.get(null);
                    keys.put(value.getName(), value);
                }
            }
            return keys;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new InternalError("class can not read its own keys");
        }
    }

}

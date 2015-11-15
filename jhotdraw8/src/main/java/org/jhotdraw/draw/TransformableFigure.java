/* @(#)Figure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.draw.handle.HandleType;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.SimpleFigureKey;
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
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.BoundsInLocalOutlineHandle;
import static java.lang.Math.min;
import static java.lang.Math.max;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.geometry.BoundingBox;
import javafx.scene.transform.Translate;
import org.jhotdraw.collection.BooleanKey;
import org.jhotdraw.collection.IterableTree;
import org.jhotdraw.collection.IndexedSet;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.handle.MoveHandleKit;
import org.jhotdraw.draw.handle.ResizeHandleKit;
import org.jhotdraw.draw.handle.RotateHandle;
import org.jhotdraw.draw.key.BlendModeStyleableFigureKey;
import org.jhotdraw.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw.draw.key.EffectStyleableFigureKey;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.FigureKey;
import org.jhotdraw.draw.key.ObservableWordListFigureKey;
import org.jhotdraw.draw.key.ObservableWordListStyleableFigureKey;
import org.jhotdraw.draw.key.StringStyleableFigureKey;

/**
 * Transformable figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface TransformableFigure extends Figure {
    /**
     * Defines the angle of rotation around the center of the figure in degrees.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey ROTATE = new DoubleStyleableFigureKey("rotate", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.TRANSFORM), 0.0);
    /**
     * Defines the rotation axis used. Default value: {@code Rotate.Z_AXIS}.
     */
    public static SimpleFigureKey<Point3D> ROTATION_AXIS = new SimpleFigureKey<>("rotationAxis", Point3D.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.TRANSFORM), Rotate.Z_AXIS);
    /**
     * Defines the scale factor by which coordinates are scaled on the x axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_X = new DoubleStyleableFigureKey("scaleX", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.TRANSFORM), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the y axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_Y = new DoubleStyleableFigureKey("scaleY", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.TRANSFORM), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the z axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_Z = new DoubleStyleableFigureKey("scaleZ", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.TRANSFORM), 1.0);
    /**
     * Defines the translation on the x axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_X = new DoubleStyleableFigureKey("translateX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT, DirtyBits.TRANSFORM), 0.0);
    /**
     * Defines the translation on the y axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_Y = new DoubleStyleableFigureKey("translateY", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.TRANSFORM), 0.0);
    /**
     * Defines the translation on the z axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_Z = new DoubleStyleableFigureKey("translateZ", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.TRANSFORM), 0.0);
    
    /**
     * Updates a figure node with all transformation properties defined in this
     * interface.
     * <p>
     * Applies the following properties: {@code ROTATE}, {@code ROTATION_AXIS},
     * {@code SCALE_X}, {@code SCALE_Y}, {@code SCALE_Z}, {@code TRANSLATE_X},
     * {@code TRANSLATE_Y}, {@code TRANSLATE_Z}.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applyTransformProperties(Node node) {
        node.setRotate(getStyled(ROTATE));
        node.setRotationAxis(getStyled(ROTATION_AXIS));
        node.setScaleX(getStyled(SCALE_X));
        node.setScaleY(getStyled(SCALE_Y));
        node.setScaleZ(getStyled(SCALE_Z));
        node.setTranslateX(getStyled(TRANSLATE_X));
        node.setTranslateY(getStyled(TRANSLATE_Y));
        node.setTranslateZ(getStyled(TRANSLATE_Z));
    }
    /**
     * Updates a figure node with all applicable {@code Key}s
     * defined in this interface and the Figure interface.
     * <p>
     * Invokes the methods {@link #applyStyleProperties(javafx.scene.Node) },
     * {@link #applyEffectProperties(javafx.scene.Node) },
     * {@link #applyTransformProperties(javafx.scene.Node) }.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applyTransformableFigureProperties(Node node) {
        applyFigureProperties(node);
        applyTransformProperties(node);
    }
}

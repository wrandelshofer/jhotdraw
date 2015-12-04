/* @(#)Figure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.SimpleFigureKey;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.Point3DStyleableMapAccessor;

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
     * Defines the scale factor by which coordinates are scaled on the axes
     * about the center of the figure. 
     */
    public static Point3DStyleableMapAccessor SCALE = new Point3DStyleableMapAccessor("scale", SCALE_X,SCALE_Y,SCALE_Z);
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
     * Defines the translation on the axes about the center of the figure.
     */
    public static Point3DStyleableMapAccessor TRAMSLATE = new Point3DStyleableMapAccessor("translate", TRANSLATE_X,TRANSLATE_Y,TRANSLATE_Z);
    
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
    default void applyTransformableFigureProperties(Node node) {
        node.setRotate(getStyled(ROTATE));
        node.setRotationAxis(getStyled(ROTATION_AXIS));
        node.setScaleX(getStyled(SCALE_X));
        node.setScaleY(getStyled(SCALE_Y));
        node.setScaleZ(getStyled(SCALE_Z));
        node.setTranslateX(getStyled(TRANSLATE_X));
        node.setTranslateY(getStyled(TRANSLATE_Y));
        node.setTranslateZ(getStyled(TRANSLATE_Z));
    }
}

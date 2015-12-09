/* @(#)Figure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import java.util.ArrayList;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.SimpleFigureKey;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
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
    public static Point3DStyleableMapAccessor TRANSLATE = new Point3DStyleableMapAccessor("translate", TRANSLATE_X,TRANSLATE_Y,TRANSLATE_Z);
    
    public static SimpleFigureKey<ArrayList<Transform>> TRANSFORMS = new SimpleFigureKey<>("tranforms",ArrayList.class,new Class<?>[]{Transform.class},DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.TRANSFORM),new ArrayList<>());
    
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
        node.getTransforms().setAll(get(TRANSFORMS));
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
     * Computes the transformation from local coordinates into parent
     * coordinates.
     *
     * @return the transformation
     */
    @Override
    default Transform computeLocalToParent() {
        Point2D center = getCenterInLocal();
        Transform translate = Transform.translate(getStyled(TransformableFigure.TRANSLATE_X), get(TransformableFigure.TRANSLATE_Y));
        Transform scale = Transform.scale(getStyled(TransformableFigure.SCALE_X), get(TransformableFigure.SCALE_Y), center.getX(), center.getY());
        Transform rotate = Transform.rotate(getStyled(TransformableFigure.ROTATE), center.getX(), center.getY());

        Transform t = translate.createConcatenation(rotate).createConcatenation(scale).createConcatenation(getTransform());
        return t;
    }

    /**
     * Computes the transformation from parent coordinates into local
     * coordinates.
     *
     * @return the transformation
     */
    @Override
    default Transform computeParentToLocal() {
        Point2D center = getCenterInLocal();

        Transform translate = Transform.translate(-getStyled(TransformableFigure.TRANSLATE_X), -get(TransformableFigure.TRANSLATE_Y));
        Transform scale = Transform.scale(1.0 / getStyled(TransformableFigure.SCALE_X), 1.0 / get(TransformableFigure.SCALE_Y), center.getX(), center.getY());
        Transform rotate = Transform.rotate(-getStyled(TransformableFigure.ROTATE), center.getX(), center.getY());

        Transform t = getInverseTransform().createConcatenation(scale).createConcatenation(rotate).createConcatenation(translate);
        return t;
    }
    
    default Transform getTransform() {
        ArrayList<Transform> list = get(TRANSFORMS);
        Transform t;
        if (list.isEmpty()) {
            t = new Translate(0, 0);
        } else {
            t = list.get(0);
            for (int i = 1, n = list.size(); i < n; i++) {
                t.createConcatenation(list.get(i));
            }
        }
        return t;
    }

    default Transform getInverseTransform() {
        ArrayList<Transform> list = get(TRANSFORMS);
        Transform t;
        if (list.isEmpty()) {
            t = new Translate(0, 0);
        } else {
            try {
                t = list.get(list.size() - 1).createInverse();
                for (int i = list.size() - 2; i >= 0; i--) {
                    t.createConcatenation(list.get(i).createInverse());
                }
            } catch (NonInvertibleTransformException e) {
                throw new InternalError(e);
            }
        }
        return t;
    }


}
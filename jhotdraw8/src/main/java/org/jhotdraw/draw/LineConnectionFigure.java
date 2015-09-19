/* @(#)LineConnectionFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.shape.AbstractShapeFigure;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.text.Text;
import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * LineConnectionFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineConnectionFigure extends AbstractShapeFigure implements ConnectionFigure {

    /** Holds a strong reference to the property. */
    private Property<ConnectableFigure> startFigureProperty;
    /** Holds a strong reference to the property. */
    private Property<ConnectableFigure> endFigureProperty;
    /** Holds a strong reference to the property. */
    private Property<Connector> startConnectorProperty;
    /** Holds a strong reference to the property. */
    private Property<Connector> endConnectorProperty;

    /** Whether the value of the start property is valid.
     * XXX should use a bitmask instead of a field for each invalidable value.
     */
    private boolean isStartValid = false;
    /** Whether the value of the end property is valid.
     * XXX should use a bitmask instead of a field for each invalidable value.
     */
    private boolean isEndValid = false;

    public LineConnectionFigure() {
        this(0, 0, 1, 1);
    }

    public LineConnectionFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public LineConnectionFigure(double startX, double startY, double endX, double endY) {
        set(START, new Point2D(startX, startY));
        set(END, new Point2D(endX, endY));

        // We must update the start and end point when ever one of
        // the connected figures or one of the connectors changes
        InvalidationListener ilStart = observable -> {
            invalidateStart();
        };
        ChangeListener<ConnectableFigure> clStart = (observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.connections().remove(LineConnectionFigure.this);
                oldValue.properties().removeListener(ilStart);
            }
            if (newValue != null) {
                newValue.connections().add(LineConnectionFigure.this);
                newValue.properties().addListener(ilStart);
                invalidateStart();
            }
        };
        ChangeListener<Observable> oclStart = (observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeListener(ilStart);
            }
            if (newValue != null) {
                newValue.addListener(ilStart);
                invalidateStart();
            }
        };
        InvalidationListener ilEnd = observable -> {
            invalidateEnd();
        };
        ChangeListener<ConnectableFigure> clEnd = (observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.connections().remove(LineConnectionFigure.this);
                oldValue.properties().removeListener(ilEnd);
            }
            if (newValue != null) {
                newValue.connections().add(LineConnectionFigure.this);
                newValue.properties().addListener(ilEnd);
                invalidateEnd();
            }
        };
        ChangeListener<Observable> oclEnd = (observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeListener(ilEnd);
            }
            if (newValue != null) {
                newValue.addListener(ilEnd);
                invalidateEnd();
            }
        };

        startFigureProperty = START_FIGURE.propertyAt(properties());
        startFigureProperty.addListener(clStart);
        endFigureProperty = END_FIGURE.propertyAt(properties());
        endFigureProperty.addListener(clEnd);
        startConnectorProperty = START_CONNECTOR.propertyAt(properties());
        startConnectorProperty.addListener(oclStart);
        endConnectorProperty = END_CONNECTOR.propertyAt(properties());
        endConnectorProperty.addListener(oclEnd);
    }

    @Override
    public Bounds getBoundsInLocal() {
        validate();
        Point2D start = get(START);
        Point2D end = get(END);
        return new BoundingBox(//
                min(start.getX(), end.getX()),//
                min(start.getY(), end.getY()),//
                abs(start.getX() - end.getX()), //
                abs(start.getY() - end.getY()));
    }

    @Override
    public void reshape(Transform transform) {
        if (get(START_FIGURE) == null) {
            set(START, transform.transform(get(START)));
        }
        if (get(END_FIGURE) == null) {
            set(END, transform.transform(get(END)));
        }
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        if (get(START_FIGURE) == null) {
            set(START, new Point2D(x, y));
        }
        if (get(END_FIGURE) == null) {
            set(END, new Point2D(x + width, y + height));
        }
    }

    @Override
    public Node createNode(DrawingRenderer drawingView) {
        return new Line();
    }

    @Override
    public void updateNode(DrawingRenderer drawingView, Node node) {
        validate();
        Line lineNode = (Line) node;
        applyFigureProperties(lineNode);
        updateShapeProperties(lineNode);
        Point2D start = get(START);
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        Point2D end = get(END);
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());
    }

    /** Some property values of this figure depend on property values of
     * other figures. Invoke this method to ensure that you get the correct
     * property values.
     */
    public void validate() {
        validateStart();
        validateEnd();
    }

    private void validateStart() {
        if (!isStartValid) {
            updateStart();
            isStartValid = true;
        }
    }

    private void validateEnd() {
        if (!isEndValid) {
            updateEnd();
            isEndValid = true;
        }
    }

    private void updateStart() {
        get(START_CONNECTOR).updateStartPosition(this);
    }

    private void updateEnd() {
        get(END_CONNECTOR).updateEndPosition(this);
    }

    private void invalidateStart() {
        isStartValid = false;
        // we also have to invalidate the end because 
        // some connectors require this
        isEndValid = false;
    }

    private void invalidateEnd() {
        isEndValid = false;
        // we also have to invalidate the start because 
        // some connectors require this
        isStartValid = false;
    }
}

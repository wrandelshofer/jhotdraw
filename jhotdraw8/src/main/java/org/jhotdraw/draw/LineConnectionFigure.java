/* @(#)LineConnectionFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.draw.handle.HandleType;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.SimpleFigureKey;
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
import static java.lang.Math.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jhotdraw.draw.handle.ConnectionPointHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.LineOutlineHandle;
import org.jhotdraw.draw.shape.LineFigure;

/**
 * LineConnectionFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineConnectionFigure extends AbstractShapeFigure {

    /**
     * The CSS type selector for this object is {@code "LineConnection"}.
     */
    public final static String TYPE_SELECTOR = "LineConnection";

    /**
     * Holds a strong reference to the property.
     */
    private Property<Connector> startConnectorProperty;
    /**
     * Holds a strong reference to the property.
     */
    private Property<Connector> endConnectorProperty;
    /**
     * The start position of the line.
     */
    public static SimpleFigureKey<Point2D> START = LineFigure.START;
    /**
     * The end position of the line.
     */
    public static SimpleFigureKey<Point2D> END = LineFigure.END;
    /**
     * The start connector.
     */
    public static SimpleFigureKey<Connector> START_CONNECTOR = new SimpleFigureKey<>("startConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.CONNECTION, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT, DirtyBits.TRANSFORM), null);
    /**
     * The end connector.
     */
    public static SimpleFigureKey<Connector> END_CONNECTOR = new SimpleFigureKey<>("endConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.CONNECTION, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT, DirtyBits.TRANSFORM), null);

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
        // the connected figures on one of the connectors changes
        ChangeListener<Connector> clStart = (observable, oldValue, newValue) -> {
            if (oldValue != null && get(END_CONNECTOR) != null && get(END_CONNECTOR).getTarget() != oldValue.getTarget()) {
                oldValue.getTarget().getConnectedFigures().remove(LineConnectionFigure.this);
            }
            if (newValue != null) {
                newValue.getTarget().getConnectedFigures().add(LineConnectionFigure.this);
            }
        };
        ChangeListener<Connector> clEnd = (observable, oldValue, newValue) -> {
            if (oldValue != null && get(START_CONNECTOR) != null && get(START_CONNECTOR).getTarget() != oldValue.getTarget()) {
                oldValue.getTarget().getConnectedFigures().remove(LineConnectionFigure.this);
            }
            if (newValue != null) {
                newValue.getTarget().getConnectedFigures().add(LineConnectionFigure.this);
            }
        };

        startConnectorProperty = START_CONNECTOR.propertyAt(propertiesProperty());
        startConnectorProperty.addListener(clStart);
        endConnectorProperty = END_CONNECTOR.propertyAt(propertiesProperty());
        endConnectorProperty.addListener(clEnd);
    }

    @Override
    public Bounds getBoundsInLocal() {
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
        if (get(START_CONNECTOR) == null) {
            set(START, transform.transform(get(START)));
        }
        if (get(END_CONNECTOR) == null) {
            set(END, transform.transform(get(END)));
        }
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        if (get(START_CONNECTOR) == null) {
            set(START, new Point2D(x, y));
        }
        if (get(END_CONNECTOR) == null) {
            set(END, new Point2D(x + width, y + height));
        }
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        return new Line();
    }

    @Override
    public void updateNode(RenderContext drawingView, Node node) {
        Line lineNode = (Line) node;
        applyFigureProperties(lineNode);
        applyShapeProperties(lineNode);
        Point2D start = get(START);
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        Point2D end = get(END);
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());
    }

    @Override
    public void layout() {
        Point2D start = get(START);
        Point2D end = get(END);
        Connector startConnector = get(START_CONNECTOR);
        Connector endConnector = get(END_CONNECTOR);
        if (startConnector != null) {
            start = startConnector.getPositionInDrawing(this);
        }
        if (endConnector != null) {
            end = endConnector.getPositionInDrawing(this);
        }

        // We must switch off rotations for the following computations
        // because
        if (startConnector != null) {
            set(START, drawingToParent(startConnector.chopStart(this, start, end)));
        }
        if (endConnector != null) {
            set(END, drawingToParent(endConnector.chopEnd(this, start, end)));
        }
    }

    @Override
    public boolean isLayoutable() {
        return true;
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return null;
    }

    @Override
    public void createHandles(HandleType handleType, DrawingView dv, List<Handle> list) {
        if (handleType == HandleType.SELECT) {
            list.add(new LineOutlineHandle(this));
        } else if (handleType == HandleType.MOVE || handleType == HandleType.RESIZE) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE));
            list.add(new ConnectionPointHandle(this, START, START_CONNECTOR));
            list.add(new ConnectionPointHandle(this, END, END_CONNECTOR));
        } else {
            super.createHandles(handleType, dv, list);
        }
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    /**
     * Returns true if this figure can connect to the specified figure with the
     * specified connector.
     *
     * @param figure The figure to which we want connect
     * @param connector The connector that we want to use
     * @return true if the connection is supported
     */
    public boolean canConnect(Figure figure, Connector connector) {
        return true;
    }

    @Override
    public void removeConnectionTarget(Figure connectedFigure) {
        if (connectedFigure != null) {
            if (get(START_CONNECTOR) != null && connectedFigure == get(START_CONNECTOR).getTarget()) {
                set(START_CONNECTOR, null);
            }
            if (get(END_CONNECTOR) != null && connectedFigure == get(END_CONNECTOR).getTarget()) {
                set(END_CONNECTOR, null);
            }
        }
    }

    /**
     * Returns all figures which are connected by this figure.
     *
     * @return a list of connected figures
     */
    @Override
    public Set<Figure> getConnectionTargets() {
        HashSet<Figure> ctf = new HashSet<>();
        if (get(START_CONNECTOR) != null) {
            ctf.add(get(START_CONNECTOR).getTarget());
        }
        if (get(END_CONNECTOR) != null) {
            ctf.add(get(END_CONNECTOR).getTarget());
        }
        return ctf;
    }

    @Override
    public void removeAllConnectionTargets() {
        set(START_CONNECTOR, null);
        set(START_CONNECTOR, null);
        set(END_CONNECTOR, null);
        set(END_CONNECTOR, null);
    }

    @Override
    public boolean isGroupReshapeableWith(Set<Figure> others) {
        for (Figure f : getConnectionTargets()) {
            if (others.contains(f)) {
                return false;
            }
        }
        return true;
    }

}

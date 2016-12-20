/* @(#)LineConnectionFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SimpleFigureKey;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.connector.Connector;
import static java.lang.Math.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.connector.ChopRectangleConnector;
import org.jhotdraw8.draw.handle.BoundsInLocalOutlineHandle;
import org.jhotdraw8.draw.handle.BoundsInTransformOutlineHandle;
import org.jhotdraw8.draw.handle.ConnectionPointHandle;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.LineOutlineHandle;
import org.jhotdraw8.draw.handle.MoveHandle;
import org.jhotdraw8.draw.handle.RotateHandle;
import org.jhotdraw8.draw.handle.TransformHandleKit;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Point2DStyleableMapAccessor;
import org.jhotdraw8.draw.locator.PointLocator;

/**
 * A figure which draws a line connection between two figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineConnectionFigure extends AbstractLeafFigure implements StrokeableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        NonTransformableFigure {

    /**
     * The CSS type selector for this object is {@code "LineConnection"}.
     */
    public final static String TYPE_SELECTOR = "LineConnection";

    /**
     * Holds a strong reference to the property.
     */
    private Property<Figure> startTargetProperty;
    /**
     * Holds a strong reference to the property.
     */
    private Property<Figure> endTargetProperty;
    /**
     * The start position of the line.
     */
    public static Point2DStyleableMapAccessor START = LineFigure.START;
    /**
     * The end position of the line.
     */
    public static Point2DStyleableMapAccessor END = LineFigure.END;
    /**
     * The start position of the line.
     */
    public static DoubleStyleableFigureKey START_X = LineFigure.START_X;
    /**
     * The end position of the line.
     */
    public static DoubleStyleableFigureKey END_X = LineFigure.END_X;
    /**
     * The start position of the line.
     */
    public static DoubleStyleableFigureKey START_Y = LineFigure.START_Y;
    /**
     * The end position of the line.
     */
    public static DoubleStyleableFigureKey END_Y = LineFigure.END_Y;
    /**
     * The start connector.
     */
    public static SimpleFigureKey<Connector> START_CONNECTOR = new SimpleFigureKey<>("startConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.DEPENDENCY, DirtyBits.LAYOUT, DirtyBits.DEPENDENT_LAYOUT,DirtyBits.TRANSFORM), null);
    /**
     * The end connector.
     */
    public static SimpleFigureKey<Connector> END_CONNECTOR = new SimpleFigureKey<>("endConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.DEPENDENCY, DirtyBits.LAYOUT,DirtyBits.DEPENDENT_LAYOUT, DirtyBits.TRANSFORM), null);
    /**
     * The start target.
     */
    public static SimpleFigureKey<Figure> START_TARGET = new SimpleFigureKey<>("startTarget", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.DEPENDENCY, DirtyBits.LAYOUT, DirtyBits.DEPENDENT_LAYOUT,DirtyBits.TRANSFORM), null);
    /**
     * The end target.
     */
    public static SimpleFigureKey<Figure> END_TARGET = new SimpleFigureKey<>("endTarget", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.DEPENDENCY, DirtyBits.LAYOUT, DirtyBits.DEPENDENT_LAYOUT,DirtyBits.TRANSFORM), null);

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
        // the connection targets changes
        ChangeListener<Figure> clStart = (observable, oldValue, newValue) -> {
            if (oldValue != null&& get(END_TARGET)!=oldValue) {
                oldValue.getDependentFigures().remove(LineConnectionFigure.this);
            }
            if (newValue != null) {
                newValue.getDependentFigures().add(LineConnectionFigure.this);
            }
        };
        ChangeListener<Figure> clEnd = (observable, oldValue, newValue) -> {
            if (oldValue != null && get(START_TARGET) != oldValue) {
                oldValue.getDependentFigures().remove(LineConnectionFigure.this);
            }
            if (newValue != null) {
                newValue.getDependentFigures().add(LineConnectionFigure.this);
            }
        };

        startTargetProperty = START_TARGET.propertyAt(getProperties());
        startTargetProperty.addListener(clStart);
        endTargetProperty = END_TARGET.propertyAt(getProperties());
        endTargetProperty.addListener(clEnd);
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
    public void reshapeInLocal(Transform transform) {
        if (get(START_TARGET) == null) {
            set(START, transform.transform(get(START)));
        }
        if (get(END_TARGET) == null) {
            set(END, transform.transform(get(END)));
        }
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        if (get(START_TARGET) == null) {
            set(START, new Point2D(x, y));
        }
        if (get(END_TARGET) == null) {
            set(END, new Point2D(x + width, y + height));
        }
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        return new Line();
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Line lineNode = (Line) node;
        applyHideableFigureProperties(lineNode);
        applyStrokeableFigureProperties(lineNode);
        applyCompositableFigureProperties(node);
        applyStyleableFigureProperties(ctx, node);
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
       Figure startTarget = get(START_TARGET);
       Figure endTarget = get(END_TARGET);
        if (startConnector != null&&startTarget!=null) {
            start = startConnector.getPositionInWorld(this,startTarget);
        }
        if (endConnector != null&&endTarget!=null) {
            end = endConnector.getPositionInWorld(this,endTarget);
        }

        // We must switch off rotations for the following computations
        // because
        if (startConnector != null&&startTarget!=null) {
            set(START, worldToParent(startConnector.chopStart(this, startTarget,start, end)));
        }
        if (endConnector != null&&endTarget!=null) {
            set(END, worldToParent(endConnector.chopEnd(this, endTarget,start, end)));
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
    public void createHandles(HandleType handleType, List<Handle> list) {
        if (handleType == HandleType.SELECT) {
            list.add(new LineOutlineHandle(this));
        } else if (handleType == HandleType.MOVE) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            if (get(START_CONNECTOR) == null) {
                list.add(new MoveHandle(this, Handle.STYLECLASS_HANDLE_MOVE, new PointLocator(START)));
            }
            if (get(END_CONNECTOR) == null) {
                list.add(new MoveHandle(this, Handle.STYLECLASS_HANDLE_MOVE, new PointLocator(END)));
            }
        } else if (handleType == HandleType.RESIZE) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_RESIZE_OUTLINE));
            list.add(new ConnectionPointHandle(this, START, START_CONNECTOR, START_TARGET));
            list.add(new ConnectionPointHandle(this, END, END_CONNECTOR, END_TARGET));
        } else if (handleType == HandleType.POINT) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_POINT_OUTLINE));
            list.add(new ConnectionPointHandle(this, Handle.STYLECLASS_HANDLE_POINT, Handle.STYLECLASS_HANDLE_POINT_CONNECTED, START, START_CONNECTOR, START_TARGET));
            list.add(new ConnectionPointHandle(this, Handle.STYLECLASS_HANDLE_POINT, Handle.STYLECLASS_HANDLE_POINT_CONNECTED, END, END_CONNECTOR, END_TARGET));
        } else if (handleType == HandleType.TRANSFORM) {
            list.add(new LineOutlineHandle(this,  Handle.STYLECLASS_HANDLE_TRANSFORM_OUTLINE));
        }else{
            super.createHandles(handleType, list);
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
            if (get(START_TARGET) != null && connectedFigure == get(START_TARGET)) {
                set(START_TARGET, null);
            }
            if (get(END_TARGET) != null && connectedFigure == get(END_TARGET)) {
                set(END_TARGET, null);
            }
        }
    }

    /**
     * Returns all figures which are connected by this figure - they provide to the
     * layout of this figure.
     *
     * @return a list of connected figures
     */
    @Override
    public Set<Figure> getProvidingFigures() {
        HashSet<Figure> ctf = new HashSet<>();
        if (get(START_TARGET) != null) {
            ctf.add(get(START_TARGET));
        }
        if (get(END_TARGET) != null) {
            ctf.add(get(END_TARGET));
        }
        return ctf;
    }

    @Override
    public void removeAllConnectionTargets() {
        set(START_CONNECTOR, null);
        set(END_CONNECTOR, null);
    }

    @Override
    public boolean isGroupReshapeableWith(Set<Figure> others) {
        for (Figure f : getProvidingFigures()) {
            if (others.contains(f)) {
                return false;
            }
        }
        return true;
    }

    public void setStartConnection(Figure target, Connector connector) {
              set(START_CONNECTOR, connector);
              set(START_TARGET, target);
              }
    public void setEndConnection(Figure target, Connector connector) {
              set(END_CONNECTOR, connector);
              set(END_TARGET, target);
              }
}

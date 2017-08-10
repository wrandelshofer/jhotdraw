/* @(#)AbstractLineConnectionFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.handle.LineConnectionOutlineHandle;
import org.jhotdraw8.draw.handle.LineConnectorHandle;
import org.jhotdraw8.draw.handle.LineOutlineHandle;
import org.jhotdraw8.draw.handle.MoveHandle;
import org.jhotdraw8.draw.handle.SelectionHandle;
import org.jhotdraw8.draw.locator.PointLocator;

/**
 * Base class for line connection figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractLineConnectionFigure extends AbstractLeafFigure
        implements NonTransformableFigure, LineConnectingFigure {

    private final ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper();

    /**
     * Holds a strong reference to the property.
     */
    private Property<Connector> endConnectorProperty;
    /**
     * Holds a strong reference to the property.
     */
    private Property<Figure> endTargetProperty;
    /**
     * Holds a strong reference to the property.
     */
    private Property<Connector> startConnectorProperty;
    /**
     * Holds a strong reference to the property.
     */
    private Property<Figure> startTargetProperty;

    public AbstractLineConnectionFigure() {
        this(0, 0, 1, 1);
    }

    public AbstractLineConnectionFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public AbstractLineConnectionFigure(double startX, double startY, double endX, double endY) {
        set(START, new Point2D(startX, startY));
        set(END, new Point2D(endX, endY));

        // We must update the start and end point when ever one of
        // the connection targets changes
        ChangeListener<Figure> clStart = (observable, oldValue, newValue) -> {
            if (oldValue != null && get(END_TARGET) != oldValue) {
                oldValue.getLayoutObservers().remove(AbstractLineConnectionFigure.this);
            }
            if (newValue != null) {
                newValue.getLayoutObservers().add(AbstractLineConnectionFigure.this);
            }
            updateConnectedProperty();
        };
        ChangeListener<Figure> clEnd = (observable, oldValue, newValue) -> {
            if (oldValue != null && get(START_TARGET) != oldValue) {
                oldValue.getLayoutObservers().remove(AbstractLineConnectionFigure.this);
            }
            if (newValue != null) {
                newValue.getLayoutObservers().add(AbstractLineConnectionFigure.this);
            }
            updateConnectedProperty();
        };
        ChangeListener<Connector> clConnector = (observable, oldValue, newValue) -> {
            updateConnectedProperty();
        };

        startTargetProperty = START_TARGET.propertyAt(getProperties());
        startTargetProperty.addListener(clStart);
        endTargetProperty = END_TARGET.propertyAt(getProperties());
        endTargetProperty.addListener(clEnd);
        startConnectorProperty = START_CONNECTOR.propertyAt(getProperties());
        startConnectorProperty.addListener(clConnector);
        endConnectorProperty = END_CONNECTOR.propertyAt(getProperties());
        endConnectorProperty.addListener(clConnector);
    }


    @Override
    public void createHandles(HandleType handleType, List<Handle> list) {
        if (handleType == HandleType.SELECT) {
            list.add(new LineOutlineHandle(this));
        } else if (handleType == HandleType.MOVE) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            if (get(START_CONNECTOR) == null) {
                list.add(new MoveHandle(this, new PointLocator(START), Handle.STYLECLASS_HANDLE_MOVE));
            }else
                list.add(new SelectionHandle(this, new PointLocator(START), Handle.STYLECLASS_HANDLE_MOVE_LOCKED));
            if (get(END_CONNECTOR) == null) {
                list.add(new MoveHandle(this, new PointLocator(END), Handle.STYLECLASS_HANDLE_MOVE));
            }else
                list.add(new SelectionHandle(this, new PointLocator(END), Handle.STYLECLASS_HANDLE_MOVE_LOCKED));
        } else if (handleType == HandleType.RESIZE) {
            list.add(new LineConnectionOutlineHandle(this, Handle.STYLECLASS_HANDLE_RESIZE_OUTLINE));
            list.add(new LineConnectorHandle(this, START, START_CONNECTOR, START_TARGET));
            list.add(new LineConnectorHandle(this, END, END_CONNECTOR, END_TARGET));
        } else if (handleType == HandleType.POINT) {
            list.add(new LineConnectionOutlineHandle(this, Handle.STYLECLASS_HANDLE_POINT_OUTLINE));
            list.add(new LineConnectorHandle(this, Handle.STYLECLASS_HANDLE_POINT, Handle.STYLECLASS_HANDLE_POINT_CONNECTED, START, START_CONNECTOR, START_TARGET));
            list.add(new LineConnectorHandle(this, Handle.STYLECLASS_HANDLE_POINT, Handle.STYLECLASS_HANDLE_POINT_CONNECTED, END, END_CONNECTOR, END_TARGET));
        } else if (handleType == HandleType.TRANSFORM) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_TRANSFORM_OUTLINE));
        } else {
            super.createHandles(handleType, list);
        }
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

    /**
     * Returns all figures which are connected by this figure - they provide to
     * the layout of this figure.
     *
     * @return an unmodifiable set of connected figures
     */
    @Override
    public Set<Figure> getLayoutSubjects() {
        Set<Figure> ctf = new HashSet<>();
        if (get(START_TARGET) != null) {
            ctf.add(get(START_TARGET));
        }
        if (get(END_TARGET) != null) {
            ctf.add(get(END_TARGET));
        }
        return ctf;
    }

    public boolean isConnected() {
        return connected.get();
    }

    @Override
    public boolean isGroupReshapeableWith(Set<Figure> others) {
        for (Figure f : getLayoutSubjects()) {
            if (others.contains(f)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isLayoutable() {
        return true;
    }

    @Override
    public void layout() {
        Point2D start = get(START);
        Point2D end = get(END);
        Connector startConnector = get(START_CONNECTOR);
        Connector endConnector = get(END_CONNECTOR);
        Figure startTarget = get(START_TARGET);
        Figure endTarget = get(END_TARGET);
        if (startConnector != null && startTarget != null) {
            start = startConnector.getPositionInWorld(this, startTarget);
        }
        if (endConnector != null && endTarget != null) {
            end = endConnector.getPositionInWorld(this, endTarget);
        }

        // We must switch off rotations for the following computations
        // because
        if (startConnector != null && startTarget != null) {
            final Point2D p = worldToParent(startConnector.chopStart(this, startTarget, start, end));
            if (p != null) {
                set(START, p);
            }
        }
        if (endConnector != null && endTarget != null) {
            final Point2D p = worldToParent(endConnector.chopEnd(this, endTarget, start, end));
            if (p != null) {
                set(END, p);
            }
        }
    }

    @Override
    public void removeAllLayoutSubjects() {
        set(START_TARGET, null);
        set(END_TARGET, null);
    }

    @Override
    public void removeLayoutSubject(Figure subject) {

        if (subject == get(START_TARGET)) {
            set(START_TARGET, null);
        }
        if (subject == get(END_TARGET)) {
            set(END_TARGET, null);
        }

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
    public void reshapeInLocal(double x, double y, double width, double height) {
        if (get(START_TARGET) == null) {
            set(START, new Point2D(x, y));
        }
        if (get(END_TARGET) == null) {
            set(END, new Point2D(x + width, y + height));
        }
    }

    public void setEndConnection(Figure target, Connector connector) {
        set(END_CONNECTOR, connector);
        set(END_TARGET, target);
    }

    public void setStartConnection(Figure target, Connector connector) {
        set(START_CONNECTOR, connector);
        set(START_TARGET, target);
    }

    private void updateConnectedProperty() {
        connected.set(get(START_CONNECTOR) != null
                && get(START_TARGET) != null && get(END_CONNECTOR) != null && get(END_TARGET) != null);
    }

}

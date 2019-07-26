/*
 * @(#)AbstractLineConnectionFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Point2D;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableSets;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ReadOnlySet;
import org.jhotdraw8.collection.ReadOnlySetWrapper;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for line connection figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractLineConnectionFigure extends AbstractLeafFigure
        implements NonTransformableFigure, LineConnectingFigure {

    private final ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper();

    public AbstractLineConnectionFigure() {
        this(0, 0, 1, 1);
    }

    public AbstractLineConnectionFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public AbstractLineConnectionFigure(double startX, double startY, double endX, double endY) {
        set(START, new CssPoint2D(startX, startY));
        set(END, new CssPoint2D(endX, endY));
    }

    @Override
    protected <T> void changed(Key<T> key, @Nullable T oldValue, @Nullable T newValue) {
        if (key == START_TARGET) {
            if (oldValue != null && get(END_TARGET) != oldValue) {
                ((Figure) oldValue).getLayoutObservers().remove(AbstractLineConnectionFigure.this);
            }
            if (newValue != null) {
                ((Figure) newValue).getLayoutObservers().add(AbstractLineConnectionFigure.this);
            }
            updateConnectedProperty();
        } else if (key == END_TARGET) {
            if (oldValue != null && get(START_TARGET) != oldValue) {
                ((Figure) oldValue).getLayoutObservers().remove(AbstractLineConnectionFigure.this);
            }
            if (newValue != null) {
                ((Figure) newValue).getLayoutObservers().add(AbstractLineConnectionFigure.this);
            }
            updateConnectedProperty();
        } else if (key == START_CONNECTOR) {
            updateConnectedProperty();
        } else if (key == END_CONNECTOR) {
            updateConnectedProperty();
        }
    }

    @Nonnull
    @Override
    public CssRectangle2D getCssBoundsInLocal() {
        CssPoint2D start = getNonnull(START);
        CssPoint2D end = getNonnull(END);
        return new CssRectangle2D(//
                CssSize.min(start.getX(), end.getX()),//
                CssSize.min(start.getY(), end.getY()),//
                start.getX().subtract(end.getX()).abs(), //
                start.getY().subtract(end.getY()).abs()
        );
    }

    /**
     * Returns all figures which are connected by this figure - they provide to
     * the layout of this figure.
     *
     * @return an unmodifiable set of connected figures
     */
    @Nonnull
    @Override
    public ReadOnlySet<Figure> getLayoutSubjects() {
        final Figure startTarget = get(START_TARGET);
        final Figure endTarget = get(END_TARGET);
        if (startTarget == null && endTarget == null) {
            return ImmutableSets.emptySet();
        }
        Set<Figure> ctf = new HashSet<>();
        if (startTarget != null) {
            ctf.add(startTarget);
        }
        if (endTarget != null) {
            ctf.add(endTarget);
        }
        return new ReadOnlySetWrapper<>(ctf);
    }

    public boolean isConnected() {
        return connected.get();
    }

    @Override
    public boolean isGroupReshapeableWith(@Nonnull Set<Figure> others) {
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
    public void reshapeInLocal(@Nonnull Transform transform) {
        if (get(START_TARGET) == null) {
            set(START, new CssPoint2D(transform.transform(getNonnull(START).getConvertedValue())));
        }
        if (get(END_TARGET) == null) {
            set(END, new CssPoint2D(transform.transform(getNonnull(END).getConvertedValue())));
        }
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        if (get(START_TARGET) == null) {
            set(START, new CssPoint2D(x, y));
        }
        if (get(END_TARGET) == null) {
            set(END, new CssPoint2D(x.add(width), y.add(height)));
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

    protected void updateConnectedProperty() {
        connected.set(get(START_CONNECTOR) != null
                && get(START_TARGET) != null && get(END_CONNECTOR) != null && get(END_TARGET) != null);
    }

    public ReadOnlyBooleanWrapper connectedProperty() {
        return connected;
    }
}

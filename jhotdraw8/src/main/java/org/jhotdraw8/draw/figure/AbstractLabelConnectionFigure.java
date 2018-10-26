/* @(#)AbstractLabelConnectionFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.css.text.CssDimension;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.handle.BoundsInLocalOutlineHandle;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.handle.LabelConnectorHandle;
import org.jhotdraw8.draw.handle.MoveHandle;
import org.jhotdraw8.draw.key.CssDimensionStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.key.Point2DStyleableFigureKey;
import org.jhotdraw8.draw.key.Point2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.SimpleFigureKey;
import org.jhotdraw8.draw.key.Dimension2DStyleableMapAccessor;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Geom;

/**
 * A Label that can be attached to another Figure by setting LABEL_CONNECTOR and
 * LABEL_TARGET.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractLabelConnectionFigure extends AbstractLabelFigure
        implements ConnectingFigure, TransformableFigure {

    /**
     * The horizontal position of the text. Default value: {@code baseline}
     */
    public final static EnumStyleableFigureKey<HPos> TEXT_HPOS = new EnumStyleableFigureKey<>("textHPos", HPos.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), false, HPos.LEFT);

    /**
     * The label target.
     */
    @Nullable
    public final static SimpleFigureKey<Figure> LABEL_TARGET = new SimpleFigureKey<>("labelTarget", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The connector.
     */
    @Nullable
    public final static SimpleFigureKey<Connector> LABEL_CONNECTOR = new SimpleFigureKey<>("labelConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    public final static DoubleStyleableFigureKey LABELED_LOCATION_X = new DoubleStyleableFigureKey("labeledLocationX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), 0.0);
    public final static DoubleStyleableFigureKey LABELED_LOCATION_Y = new DoubleStyleableFigureKey("labeledLocationY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), 0.0);
    public final static Point2DStyleableMapAccessor LABELED_LOCATION = new Point2DStyleableMapAccessor("labeledLocation", LABELED_LOCATION_X, LABELED_LOCATION_Y);

    /**
     * The perpendicular offset of the label.
     * <p>
     * The offset is perpendicular to the tangent line of the figure.
     */
    public final static CssDimensionStyleableFigureKey LABEL_OFFSET_Y = new CssDimensionStyleableFigureKey("labelOffsetY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), CssDimension.ZERO);
    /**
     * The tangential offset of the label.
     * <p>
     * The offset is on tangent line of the figure.
     */
    public final static CssDimensionStyleableFigureKey LABEL_OFFSET_X = new CssDimensionStyleableFigureKey("labelOffsetX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), CssDimension.ZERO);
    public final static Dimension2DStyleableMapAccessor LABELED_OFFSET = new Dimension2DStyleableMapAccessor("labelOffset", LABEL_OFFSET_X, LABEL_OFFSET_Y);
    /**
     * Whether the label should be rotated with the target.
     */
    public final static EnumStyleableFigureKey<LabelAutorotate> LABEL_AUTOROTATE = new EnumStyleableFigureKey<>("labelAutorotate", LabelAutorotate.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), false, LabelAutorotate.OFF);
    /**
     * The position relative to the parent (respectively the offset).
     */
    public static final Point2DStyleableFigureKey LABEL_TRANSLATE = new Point2DStyleableFigureKey(
            "labelTranslation", DirtyMask
                    .of(DirtyBits.NODE, DirtyBits.LAYOUT), new Point2D(0, 0));
    private final ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper();

    public AbstractLabelConnectionFigure() {
    }

    @Override
    protected <T> void changed(Key<T> key, @Nullable T oldValue, @Nullable T newValue) {
        if (key == LABEL_TARGET) {
            if (oldValue != null) {
                ((Figure) oldValue).getLayoutObservers().remove(this);
            }
            if (newValue != null) {
                ((Figure) newValue).getLayoutObservers().add(this);
            }
            updateConnectedProperty();
        } else if (key == LABEL_CONNECTOR) {
            updateConnectedProperty();
        }
    }

    private void updateConnectedProperty() {
        connected.set(get(LABEL_CONNECTOR) != null
                && get(LABEL_TARGET) != null);
    }

    /**
     * This property is true when the figure is connected.
     *
     * @return the connected property
     */
    public ReadOnlyBooleanProperty connectedProperty() {
        return connected.getReadOnlyProperty();
    }

    @Override
    public void createHandles(HandleType handleType, @Nonnull List<Handle> list) {
        if (handleType == HandleType.MOVE) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            if (get(LABEL_CONNECTOR) == null) {
                list.add(new MoveHandle(this, RelativeLocator.NORTH_EAST));
                list.add(new MoveHandle(this, RelativeLocator.NORTH_WEST));
                list.add(new MoveHandle(this, RelativeLocator.SOUTH_EAST));
                list.add(new MoveHandle(this, RelativeLocator.SOUTH_WEST));
            }
        } else if (handleType == HandleType.RESIZE) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            list.add(new LabelConnectorHandle(this, ORIGIN, LABELED_LOCATION, LABEL_CONNECTOR, LABEL_TARGET));
        } else if (handleType == HandleType.POINT) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            list.add(new LabelConnectorHandle(this, ORIGIN, LABELED_LOCATION, LABEL_CONNECTOR, LABEL_TARGET));
        } else {
            super.createHandles(handleType, list);
        }
    }

    /**
     * Returns all figures which are connected by this figure - they provide to
     * the layout of this figure.
     *
     * @return a list of connected figures
     */
    @Nonnull
    @Override
    public Set<Figure> getLayoutSubjects() {
        final Figure labelTarget = get(LABEL_TARGET);
        return labelTarget == null ? Collections.emptySet() : Collections.singleton(labelTarget);
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
    public void layout() {

        Figure labelTarget = get(LABEL_TARGET);
        final Point2D labeledLoc;
        Connector labelConnector = get(LABEL_CONNECTOR);
        final Point2D perp;
        final Point2D tangent;
        if (labelConnector != null && labelTarget != null) {
            labeledLoc = labelConnector.getPositionInWorld(this, labelTarget);
            tangent = labelConnector.getTangentInWorld(this, labelTarget);
            perp = Geom.perp(tangent);
        } else {
            labeledLoc = get(LABELED_LOCATION);
            tangent = new Point2D(1, 0);
            perp = new Point2D(0, -1);
        }

        set(LABELED_LOCATION, labeledLoc);
        Bounds b = getLayoutBounds();
        double hposTranslate = 0;
        switch (getStyled(TEXT_HPOS)) {
            case CENTER:
                hposTranslate = b.getWidth() * -0.5;
                break;
            case LEFT:
                break;
            case RIGHT:
                hposTranslate = -b.getWidth();
                break;
        }

        // FIXME must convert with current font size of label!!
        final double labelOffsetX = getStyled(LABEL_OFFSET_X).getConvertedValue();
        final double labelOffsetY = getStyled(LABEL_OFFSET_Y).getConvertedValue();
        Point2D origin = labeledLoc
                .add(perp.multiply(-labelOffsetY))
                .add(tangent.multiply(labelOffsetX));

        Rotate rotate = null;
        final boolean layoutTransforms;
        switch (getStyled(LABEL_AUTOROTATE)) {
            case FULL: {// the label follows the rotation of its target figure in the full circle: 0..360°
                final double theta = (Math.atan2(tangent.getY(), tangent.getX()) * 180.0 / Math.PI + 360.0) % 360.0;
                rotate = new Rotate(theta, origin.getX(), origin.getY());
                layoutTransforms=true;
                // set(ROTATE, theta);
            }
            break;
            case HALF: {// the label follows the rotation of its target figure in the half circle: -90..90°
                final double theta = (Math.atan2(tangent.getY(), tangent.getX()) * 180.0 / Math.PI + 360.0) % 360.0;
                final double halfTheta = theta <= 90.0 || theta > 270.0 ? theta : (theta + 180.0) % 360.0;
                rotate = new Rotate(halfTheta, origin.getX(), origin.getY());
                layoutTransforms=true;
                // set(ROTATE, halfTheta);
            }
            break;
            case OFF:
            default:
                layoutTransforms=false;
                break;
        }        // FIXME add tx in angle of rotated label!
//        origin=origin.add(tangent.multiply(hposTranslate));
        origin = origin.add(hposTranslate, 0);

        Point2D labelTranslation = getStyled(LABEL_TRANSLATE);
        origin = origin.add(labelTranslation);
        set(ORIGIN, origin);
        List<Transform> transforms = new ArrayList<>();
        if (rotate != null) {
            transforms.add(rotate);
        }
       if (layoutTransforms) {
            setTransforms(transforms.toArray(new Transform[transforms.size()]));
       }

        Bounds bconnected = getLayoutBounds();
        setCachedValue(BOUNDS_IN_LOCAL_CACHE_KEY, bconnected);
        invalidateTransforms();
    }

    @Override
    public void removeAllLayoutSubjects() {
        set(LABEL_TARGET, null);
    }

    @Override
    public void removeLayoutSubject(Figure subject) {
        if (subject == get(LABEL_TARGET)) {
            set(LABEL_TARGET, null);
        }

    }

    @Override
    public void updateGroupNode(RenderContext ctx, @Nonnull Group node) {
        super.updateGroupNode(ctx, node);
        applyTransformableFigureProperties(node);
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        if (get(LABEL_TARGET) == null) {
            super.reshapeInLocal(x, y, width, height);
            set(LABELED_LOCATION, get(ORIGIN));
            set(LABEL_TRANSLATE, new Point2D(0, 0));
        } else {
            Bounds bounds = getBoundsInLocal();
            double newX, newY;
            newX = x + Math.min(0, width);
            newY = y + Math.min(0, height);
            Point2D oldValue = get(LABEL_TRANSLATE);
            set(LABEL_TRANSLATE,
                    new Point2D(oldValue.getX() + newX - bounds.getMinX(), oldValue.getY() + newY - bounds.getMinY()));
        }
    }

    public void setLabelConnection(Figure target, Connector connector) {
        set(LABEL_CONNECTOR, connector);
        set(LABEL_TARGET, target);
    }
}

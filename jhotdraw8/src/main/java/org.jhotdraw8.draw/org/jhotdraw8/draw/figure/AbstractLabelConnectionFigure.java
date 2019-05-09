/* @(#)AbstractLabelConnectionFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableSets;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ReadOnlySet;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.handle.BoundsInLocalOutlineHandle;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.handle.LabelConnectorHandle;
import org.jhotdraw8.draw.handle.MoveHandle;
import org.jhotdraw8.draw.key.CssPoint2DStyleableKey;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.EnumStyleableKey;
import org.jhotdraw8.draw.key.NullableObjectKey;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Geom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public final static EnumStyleableKey<HPos> TEXT_HPOS = new EnumStyleableKey<>("textHPos", HPos.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), HPos.LEFT);

    /**
     * The label target.
     */
    @Nonnull
    public final static NullableObjectKey<Figure> LABEL_TARGET = new NullableObjectKey<>("labelTarget", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The connector.
     */
    @Nonnull
    public final static NullableObjectKey<Connector> LABEL_CONNECTOR = new NullableObjectKey<>("labelConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    public final static CssSizeStyleableKey LABELED_LOCATION_X = new CssSizeStyleableKey("labeledLocationX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), CssSize.ZERO);
    public final static CssSizeStyleableKey LABELED_LOCATION_Y = new CssSizeStyleableKey("labeledLocationY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), CssSize.ZERO);
    public final static CssPoint2DStyleableMapAccessor LABELED_LOCATION = new CssPoint2DStyleableMapAccessor("labeledLocation", LABELED_LOCATION_X, LABELED_LOCATION_Y);

    /**
     * The perpendicular offset of the label.
     * <p>
     * The offset is perpendicular to the tangent line of the figure.
     */
    public final static CssSizeStyleableKey LABEL_OFFSET_Y = new CssSizeStyleableKey("labelOffsetY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), CssSize.ZERO);
    /**
     * The tangential offset of the label.
     * <p>
     * The offset is on tangent line of the figure.
     */
    public final static CssSizeStyleableKey LABEL_OFFSET_X = new CssSizeStyleableKey("labelOffsetX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), CssSize.ZERO);
    public final static CssPoint2DStyleableMapAccessor LABEL_OFFSET = new CssPoint2DStyleableMapAccessor("labelOffset", LABEL_OFFSET_X, LABEL_OFFSET_Y);
    /**
     * Whether the label should be rotated with the target.
     */
    public final static EnumStyleableKey<LabelAutorotate> LABEL_AUTOROTATE = new EnumStyleableKey<>("labelAutorotate", LabelAutorotate.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), LabelAutorotate.OFF);
    /**
     * The position relative to the parent (respectively the offset).
     */
    public static final CssPoint2DStyleableKey LABEL_TRANSLATE = new CssPoint2DStyleableKey(
            "labelTranslation", DirtyMask
            .of(DirtyBits.NODE, DirtyBits.LAYOUT), new CssPoint2D(0, 0));
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
                list.add(new MoveHandle(this, BoundsLocator.NORTH_EAST));
                list.add(new MoveHandle(this, BoundsLocator.NORTH_WEST));
                list.add(new MoveHandle(this, BoundsLocator.SOUTH_EAST));
                list.add(new MoveHandle(this, BoundsLocator.SOUTH_WEST));
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
    public ReadOnlySet<Figure> getLayoutSubjects() {
        final Figure labelTarget = get(LABEL_TARGET);
        return labelTarget == null ? ImmutableSets.emptySet() : ImmutableSets.of(labelTarget);
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
    public void layout(@Nonnull RenderContext ctx) {

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
            labeledLoc = getNonnull(LABELED_LOCATION).getConvertedValue();
            tangent = new Point2D(1, 0);
            perp = new Point2D(0, -1);
        }

        set(LABELED_LOCATION, new CssPoint2D(labeledLoc));
        Bounds b = getTextBounds(ctx);
        double hposTranslate = 0;
        switch (getStyledNonnull(TEXT_HPOS)) {
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
        final double labelOffsetX = getStyledNonnull(LABEL_OFFSET_X).getConvertedValue();
        final double labelOffsetY = getStyledNonnull(LABEL_OFFSET_Y).getConvertedValue();
        Point2D origin = labeledLoc
                .add(perp.multiply(-labelOffsetY))
                .add(tangent.multiply(labelOffsetX));

        Rotate rotate = null;
        final boolean layoutTransforms;
        switch (getStyledNonnull(LABEL_AUTOROTATE)) {
            case FULL: {// the label follows the rotation of its target figure in the full circle: 0..360°
                final double theta = (Math.atan2(tangent.getY(), tangent.getX()) * 180.0 / Math.PI + 360.0) % 360.0;
                rotate = new Rotate(theta, origin.getX(), origin.getY());
                layoutTransforms = true;
                // set(ROTATE, theta);
            }
            break;
            case HALF: {// the label follows the rotation of its target figure in the half circle: -90..90°
                final double theta = (Math.atan2(tangent.getY(), tangent.getX()) * 180.0 / Math.PI + 360.0) % 360.0;
                final double halfTheta = theta <= 90.0 || theta > 270.0 ? theta : (theta + 180.0) % 360.0;
                rotate = new Rotate(halfTheta, origin.getX(), origin.getY());
                layoutTransforms = true;
                // set(ROTATE, halfTheta);
            }
            break;
            case OFF:
            default:
                layoutTransforms = false;
                break;
        }        // FIXME add tx in angle of rotated label!
//        origin=origin.add(tangent.multiply(hposTranslate));
        origin = origin.add(hposTranslate, 0);

        Point2D labelTranslation = getStyledNonnull(LABEL_TRANSLATE).getConvertedValue();
        origin = origin.add(labelTranslation);
        set(ORIGIN, new CssPoint2D(origin));
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
        applyTransformableFigureProperties(ctx, node);
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        if (get(LABEL_TARGET) == null) {
            super.reshapeInLocal(x, y, width, height);
            set(LABELED_LOCATION, getNonnull(ORIGIN));
            set(LABEL_TRANSLATE, new CssPoint2D(0, 0));
        } else {
            CssRectangle2D bounds = getCssBoundsInLocal();
            CssSize newX, newY;
            newX = width.getValue() > 0 ? x.add(width) : x;
            newY = height.getValue() > 0 ? y.add(height) : y;
            CssPoint2D oldValue = getNonnull(LABEL_TRANSLATE);
            set(LABEL_TRANSLATE,
                    new CssPoint2D(x.subtract(bounds.getMinX()).add(oldValue.getX()),
                            y.subtract(bounds.getMinY()).add(oldValue.getY())));
        }
    }

    @Override
    public void translateInLocal(@Nonnull CssPoint2D delta) {
        if (get(LABEL_TARGET) == null) {
            super.translateInLocal(delta);
            set(LABELED_LOCATION, getNonnull(ORIGIN));
            set(LABEL_TRANSLATE, new CssPoint2D(0, 0));
        } else {
            CssPoint2D oldValue = getNonnull(LABEL_TRANSLATE);
            set(LABEL_TRANSLATE, oldValue.add(delta));
        }
    }

    public void setLabelConnection(Figure target, Connector connector) {
        set(LABEL_CONNECTOR, connector);
        set(LABEL_TARGET, target);
    }
}

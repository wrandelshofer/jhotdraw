/*
 * @(#)AbstractLabelConnectionFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
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
import org.jhotdraw8.draw.key.EnumStyleableKey;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXGeom;
import org.jhotdraw8.geom.Geom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A Label that can be attached to another Figure by setting LABEL_CONNECTOR and
 * LABEL_TARGET.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractLabelConnectionFigure extends AbstractLabelFigure
        implements ConnectingFigure, TransformableFigure, LabelConnectionFigure {

    /**
     * The horizontal position of the text. Default value: {@code baseline}
     */
    @NonNull
    public final static EnumStyleableKey<HPos> TEXT_HPOS = new EnumStyleableKey<>("textHPos", HPos.class, HPos.LEFT);

    @NonNull
    public final static CssSizeStyleableKey LABELED_LOCATION_X = new CssSizeStyleableKey("labeledLocationX", CssSize.ZERO);
    @NonNull
    public final static CssSizeStyleableKey LABELED_LOCATION_Y = new CssSizeStyleableKey("labeledLocationY", CssSize.ZERO);
    @NonNull
    public final static CssPoint2DStyleableMapAccessor LABELED_LOCATION = new CssPoint2DStyleableMapAccessor("labeledLocation", LABELED_LOCATION_X, LABELED_LOCATION_Y);

    /**
     * The perpendicular offset of the label.
     * <p>
     * The offset is perpendicular to the tangent line of the figure.
     */
    @NonNull
    public final static CssSizeStyleableKey LABEL_OFFSET_Y = new CssSizeStyleableKey("labelOffsetY", CssSize.ZERO);
    /**
     * The tangential offset of the label.
     * <p>
     * The offset is on tangent line of the figure.
     */
    @NonNull
    public final static CssSizeStyleableKey LABEL_OFFSET_X = new CssSizeStyleableKey("labelOffsetX", CssSize.ZERO);
    @NonNull
    public final static CssPoint2DStyleableMapAccessor LABEL_OFFSET = new CssPoint2DStyleableMapAccessor("labelOffset", LABEL_OFFSET_X, LABEL_OFFSET_Y);
    /**
     * Whether the label should be rotated with the target.
     */
    @NonNull
    public final static EnumStyleableKey<LabelAutorotate> LABEL_AUTOROTATE = new EnumStyleableKey<>("labelAutorotate", LabelAutorotate.class, LabelAutorotate.OFF);
    /**
     * The position relative to the parent (respectively the offset).
     */
    @NonNull
    public static final CssPoint2DStyleableKey LABEL_TRANSLATE = new CssPoint2DStyleableKey(
            "labelTranslation", new CssPoint2D(0, 0));
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
    public void createHandles(HandleType handleType, @NonNull List<Handle> list) {
        if (handleType == HandleType.MOVE) {
            list.add(new BoundsInLocalOutlineHandle(this));
            if (get(LABEL_CONNECTOR) == null) {
                list.add(new MoveHandle(this, BoundsLocator.NORTH_EAST));
                list.add(new MoveHandle(this, BoundsLocator.NORTH_WEST));
                list.add(new MoveHandle(this, BoundsLocator.SOUTH_EAST));
                list.add(new MoveHandle(this, BoundsLocator.SOUTH_WEST));
            }
        } else if (handleType == HandleType.RESIZE) {
            list.add(new BoundsInLocalOutlineHandle(this));
            list.add(new LabelConnectorHandle(this, ORIGIN, LABELED_LOCATION, LABEL_CONNECTOR, LABEL_TARGET));
        } else if (handleType == HandleType.POINT) {
            list.add(new BoundsInLocalOutlineHandle(this));
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
    @NonNull
    @Override
    public ReadOnlySet<Figure> getLayoutSubjects() {
        final Figure labelTarget = get(LABEL_TARGET);
        return labelTarget == null ? ImmutableSets.emptySet() : ImmutableSets.of(labelTarget);
    }

    public boolean isConnected() {
        return connected.get();
    }

    @Override
    public boolean isGroupReshapeableWith(@NonNull Set<Figure> others) {
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
    public void layout(@NonNull RenderContext ctx) {

        Figure labelTarget = get(LABEL_TARGET);
        final Point2D labeledLoc;
        Connector labelConnector = get(LABEL_CONNECTOR);
        final Point2D perp;
        final Point2D tangent;

        Text textNode = new Text();

        updateTextNode(ctx, textNode);
        Bounds textNodeLayoutBounds = textNode.getLayoutBounds();

        if (labelConnector != null && labelTarget != null) {
            labeledLoc = labelConnector.getPositionInWorld(this, labelTarget);
            tangent = labelConnector.getTangentInWorld(this, labelTarget).normalize();
            perp = FXGeom.perp(tangent);

            set(LABELED_LOCATION, new CssPoint2D(labeledLoc));
            double hposTranslate = 0;
            switch (getStyledNonNull(TEXT_HPOS)) {
            case CENTER: {
                hposTranslate = textNodeLayoutBounds.getWidth() * -0.5;
                break;
            }
            case LEFT:
                break;
            case RIGHT: {
                hposTranslate = -textNodeLayoutBounds.getWidth();
                break;
            }
            }

            // FIXME must convert with current font size of label!!
            final double labelOffsetX = getStyledNonNull(LABEL_OFFSET_X).getConvertedValue();
            final double labelOffsetY = getStyledNonNull(LABEL_OFFSET_Y).getConvertedValue();
            Point2D origin = labeledLoc
                    .add(perp.multiply(-labelOffsetY))
                    .add(tangent.multiply(labelOffsetX));

            Rotate rotate = null;
            final boolean layoutTransforms;
            switch (getStyledNonNull(LABEL_AUTOROTATE)) {
            case FULL: {// the label follows the rotation of its target figure in the full circle: 0..360°
                final double theta = (Geom.atan2(tangent.getY(), tangent.getX()) * 180.0 / Math.PI + 360.0) % 360.0;
                rotate = new Rotate(theta, origin.getX(), origin.getY());
                layoutTransforms = true;
                // set(ROTATE, theta);
            }
            break;
            case HALF: {// the label follows the rotation of its target figure in the half circle: -90..90°
                final double theta = (Geom.atan2(tangent.getY(), tangent.getX()) * 180.0 / Math.PI + 360.0) % 360.0;
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

            Point2D labelTranslation = getStyledNonNull(LABEL_TRANSLATE).getConvertedValue();
            origin = origin.add(labelTranslation);
            set(ORIGIN, new CssPoint2D(origin));
            List<Transform> transforms = new ArrayList<>();
            if (rotate != null) {
                transforms.add(rotate);
            }
            if (layoutTransforms) {
                setTransforms(transforms.toArray(new Transform[0]));
            }
        }

        textNode.setX(getStyledNonNull(ORIGIN_X).getConvertedValue());
        textNode.setY(getStyledNonNull(ORIGIN_Y).getConvertedValue());
        Bounds b = textNode.getLayoutBounds();
        Insets i = getStyledNonNull(PADDING).getConvertedValue();
        Bounds bconnected = new BoundingBox(
                b.getMinX() - i.getLeft(),
                b.getMinY() - i.getTop(),
                b.getWidth() + i.getLeft() + i.getRight(),
                textNode.getBaselineOffset() + i.getTop() + i.getBottom());
        setCachedLayoutBounds(bconnected);
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
    public void updateGroupNode(@NonNull RenderContext ctx, @NonNull Group node) {
        super.updateGroupNode(ctx, node);
        applyTransformableFigureProperties(ctx, node);
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        if (get(LABEL_TARGET) == null) {
            super.reshapeInLocal(x, y, width, height);
            set(LABELED_LOCATION, getNonNull(ORIGIN));
            set(LABEL_TRANSLATE, new CssPoint2D(0, 0));
        } else {
            CssRectangle2D bounds = getCssLayoutBounds();
            CssSize newX, newY;
            newX = width.getValue() > 0 ? x.add(width) : x;
            newY = height.getValue() > 0 ? y.add(height) : y;
            CssPoint2D oldValue = getNonNull(LABEL_TRANSLATE);
            set(LABEL_TRANSLATE,
                    new CssPoint2D(x.subtract(bounds.getMinX()).add(oldValue.getX()),
                            y.subtract(bounds.getMinY()).add(oldValue.getY())));
        }
    }

    @Override
    public void translateInLocal(@NonNull CssPoint2D delta) {
        if (get(LABEL_TARGET) == null) {
            super.translateInLocal(delta);
            set(LABELED_LOCATION, getNonNull(ORIGIN));
            set(LABEL_TRANSLATE, new CssPoint2D(0, 0));
        } else {
            CssPoint2D oldValue = getNonNull(LABEL_TRANSLATE);
            set(LABEL_TRANSLATE, oldValue.add(delta));
        }
    }

    public void setLabelConnection(Figure target, Connector connector) {
        set(LABEL_CONNECTOR, connector);
        set(LABEL_TARGET, target);
    }
}

/* @(#)TextAreaFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.PathConnector;
import org.jhotdraw8.draw.figure.AbstractLeafFigure;
import org.jhotdraw8.draw.figure.CompositableFigure;
import org.jhotdraw8.draw.figure.ConnectableFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.PaddableFigure;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.draw.figure.RectangularFigure;
import org.jhotdraw8.draw.figure.ResizableFigure;
import org.jhotdraw8.draw.figure.ShapeableFigure;
import org.jhotdraw8.draw.figure.StrokableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.figure.TextFillableFigure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.StringStyleableFigureKey;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.Arrays;
import java.util.List;

/**
 * Renders a "SysMLDiagram" element.
 * <p>
 * A SysMLDiagram is drawn as a rectangle with a heading in its top left corner.<br>
 * The heading is contained in a rectangle with a cutoff corner.
 * <p>
 * The heading consists of the following fields:
 * <ul>
 * <li>kind (bold)</li>
 * <li>model element type (in square brackets)</li>
 * <li>model element name</li>
 * <li>diagram name (in square brackets)</li>
 * </ul>
 */
public class MLDiagramFigure extends AbstractLeafFigure
        implements StrokableFigure, FillableFigure, TransformableFigure,
        ResizableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        ConnectableFigure, PathIterableFigure, RectangularFigure, ShapeableFigure,
        NameFontableFigure, KindFontableFigure, TextFillableFigure, PaddableFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "MLDiagram";

    public final static StringStyleableFigureKey DIAGRAM_KIND = new StringStyleableFigureKey(MLConstants.ML_NAMESPACE_PREFIX, "diagramKind", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), true, null, null);
    public final static StringStyleableFigureKey MODEL_ELEMENT_TYPE = new StringStyleableFigureKey(MLConstants.ML_NAMESPACE_PREFIX, "modelElementType", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), true, null, null);
    public final static StringStyleableFigureKey MODEL_ELEMENT_NAME = new StringStyleableFigureKey(MLConstants.ML_NAMESPACE_PREFIX, "modelElementName", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), true, null, null);
    public final static StringStyleableFigureKey DIAGRAM_NAME = new StringStyleableFigureKey(MLConstants.ML_NAMESPACE_PREFIX, "diagramName", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), true, null, null);

    private Path path;

    public MLDiagramFigure() {
        /*
        setStyled(StyleOrigin.USER_AGENT, KIND_FONT_FAMILY, "Arial Bold");
        setStyled(StyleOrigin.USER_AGENT, PADDING, new CssInsets(4, 14, 4, 6, UnitConverter.DEFAULT));
        setStyled(StyleOrigin.USER_AGENT, SHAPE,  "M0,0 20,0 20,10 10,20 0,20Z");
        setStyled(StyleOrigin.USER_AGENT, SHAPE_SLICE, new CssInsets(0,10,10,0, UnitConverter.DEFAULT));
*/
    }

    @Override
    public Node createNode(RenderContext ctx) {
        Group g = new Group();
        g.setAutoSizeChildren(false);
        Path p = new Path();
        Text diagramKindText = new Text();
        Text diagramNameText = new Text();
        g.getChildren().addAll(p, diagramKindText, diagramNameText);
        return g;
    }

    private String getDiagramTitle() {
        StringBuilder buf = new StringBuilder();
        String modelElementType = getStyled(MODEL_ELEMENT_TYPE);
        String modelElementName = getStyled(MODEL_ELEMENT_NAME);
        String diagramName = getStyled(DIAGRAM_NAME);
        if (modelElementType != null) {
            buf.append('[').append(modelElementType).append(']');
        }
        if (modelElementName != null) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append(modelElementName);
        }
        if (diagramName != null) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append('[').append(diagramName).append(']');
        }
        return buf.toString();
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Group g = (Group) node;
        Path p = (Path) g.getChildren().get(0);
        Text diagramKindText = (Text) g.getChildren().get(1);
        Text diagramNameText = (Text) g.getChildren().get(2);
        applyStrokableFigureProperties(ctx, p);
        applyFillableFigureProperties(ctx, p);
        String diagramKind = getStyled(DIAGRAM_KIND);
        diagramKindText.setText(diagramKind);
        diagramNameText.setText(getDiagramTitle());
        applyKindTextFontableFigureProperties(ctx, diagramKindText);
        applyNameTextFontableFigureProperties(ctx, diagramNameText);
        for (Text textNode : Arrays.asList(diagramKindText, diagramNameText)) {
            applyTextFillableFigureProperties(ctx, textNode);
            textNode.setTextOrigin(VPos.TOP);
        }
        applyTransformableFigureProperties(ctx, node);

        UnitConverter converter = ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        Insets padding = getStyledNonnull(PADDING).getConvertedValue(converter);
        double size = diagramKindText.getFont().getSize();
        Bounds bounds = getBoundsInLocal();

        double y = bounds.getMinY() + padding.getTop();

        diagramKindText.setX(bounds.getMinX() + padding.getLeft());
        diagramKindText.setY(y);
        diagramNameText.setX(diagramKindText.getLayoutBounds().getMaxX() + (diagramKind == null ? 0 : size / 2));
        diagramNameText.setY(y);

        Bounds textBounds = Geom.union(
                diagramKindText.getLayoutBounds(),
                diagramNameText.getLayoutBounds());

        Bounds titleFrameBounds = new BoundingBox(bounds.getMinX(), bounds.getMinY(),
                textBounds.getMaxX() - bounds.getMinX() + padding.getRight(),
                textBounds.getMaxY() - bounds.getMinY() + padding.getBottom());

        applyShapeableProperties(ctx, p, titleFrameBounds);
        addFramePath(p.getElements());
    }

    private void addFramePath(List<PathElement> pathElements) {
        Bounds b = getBoundsInLocal();
        pathElements.add(new MoveTo(b.getMinX(), b.getMinY()));
        pathElements.add(new LineTo(b.getMaxX(), b.getMinY()));
        pathElements.add(new LineTo(b.getMaxX(), b.getMaxY()));
        pathElements.add(new LineTo(b.getMinX(), b.getMaxY()));
        pathElements.add(new ClosePath());
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }


    @Override
    public void layout(@Nonnull RenderContext ctx) {
        if (path == null) {
            path = new Path();
        }
        Bounds boundsInLocal = getBoundsInLocal();
        path.getElements().clear();
        addFramePath(path.getElements());
    }

    @Override
    public @Nullable Connector findConnector(Point2D pointInLocal, Figure connectingFigure) {
        return new PathConnector(new RelativeLocator(getBoundsInLocal(), pointInLocal));
    }

    @Override
    public PathIterator getPathIterator(@Nullable AffineTransform tx) {
        if (path == null) {
            layout(new SimpleRenderContext());
        }
        return path == null ? Shapes.emptyPathIterator() : Shapes.awtShapeFromFX(path).getPathIterator(tx);
    }
}

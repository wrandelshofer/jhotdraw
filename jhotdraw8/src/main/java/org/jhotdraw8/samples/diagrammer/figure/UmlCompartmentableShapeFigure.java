package org.jhotdraw8.samples.diagrammer.figure;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
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
import org.jhotdraw8.draw.figure.TextFontableFigure;
import org.jhotdraw8.draw.figure.TextLayoutableFigure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.io.UnitConverter;
import org.jhotdraw8.samples.diagrammer.model.UmlCompartmentalizedData;
import org.jhotdraw8.samples.diagrammer.model.UmlCompartmentedDataStyleableFigureKey;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a "UmlCompartmentableShapeFigure".
 * <p>
 * This figure is drawn as a rectangle that contains a single column of compartments.
 * <p>
 * The compartments are separated by a solid line.
 * <p>
 * A compartment contains a keyword and a list of textual items.
 * <p>
 * The keyword is typically centered.
 * The textual items are typically left aligned.
 * The first textual item in the first compartment is often printed in bold face.
 * <p>
 * Examples of UMLCompartmentableShapes:
 * <pre>
 *      ┌─────────────┐           ┌─────────────┐
 *      │  keyword I  │           │  keyword X  │
 *      │ item I.1    │           │ item X.1    │
 *      │ item I.2    │           └─────────────┘
 *      │ item I.3    │
 *      ├─────────────┤
 *      │  keyword II │
 *      │ item II.1   │
 *      │ item II.2   │
 *      │ item II.3   │
 *      └─────────────┘
 * </pre>
 */
public class UmlCompartmentableShapeFigure extends AbstractLeafFigure
        implements StrokableFigure, FillableFigure, TransformableFigure,
        ResizableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        ConnectableFigure, PathIterableFigure, RectangularFigure, ShapeableFigure,
        TextFontableFigure, TextFillableFigure, PaddableFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "UMLCompartmentableShape";
    /**
     * The labeled item lists. Default value: {@code ""}.
     */
    public final static UmlCompartmentedDataStyleableFigureKey DATA = new UmlCompartmentedDataStyleableFigureKey("data", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), new UmlCompartmentalizedData());

    /**
     * The line spacing. Default value: {@code 0.0}
     */
    public final static CssSizeStyleableFigureKey LINE_SPACING = TextLayoutableFigure.LINE_SPACING;

    private Path path;

    @Override
    public Node createNode(RenderContext ctx) {
        Group g = new Group();
        g.setAutoSizeChildren(false);
        Path p = new Path();
        Text text = new Text();
        g.getChildren().addAll(p, text);
        return g;
    }

    @Override
    public CssRectangle2D getCssBoundsInLocal() {
        return getNonnull(BOUNDS);
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        set(X, width.getValue() < 0 ? x.add(width) : x);
        set(Y, height.getValue() < 0 ? y.add(height) : y);
        set(WIDTH, width.abs());
        set(HEIGHT, height.abs());
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Group g = (Group) node;
        Path p = (Path) g.getChildren().get(0);
        //Text text = (Text) g.getChildren().get(1);
        applyShapeableProperties(ctx, p);
        applyStrokableFigureProperties(ctx, p);
        applyFillableFigureProperties(ctx, p);
        UmlCompartmentalizedData cpData = getStyled(DATA);
        applyTransformableFigureProperties(ctx, node);

        List<Text> textNodes = new ArrayList<Text>();
        for (int i = 1, n = g.getChildren().size(); i < n; i++) {
            textNodes.add((Text) g.getChildren().get(i));
        }
        UnitConverter converter = ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        double lineSpacing = converter.convert(getNonnull(LINE_SPACING), UnitConverter.DEFAULT);
        Insets padding = getStyledNonnull(PADDING).getConvertedValue(converter);
        Bounds bounds = getBoundsInLocal();
        updateTextNodes(ctx, textNodes, cpData,
                bounds.getMinX() + padding.getLeft(), bounds.getMinY() + padding.getTop(),
                bounds.getWidth() - padding.getLeft() - padding.getRight(), lineSpacing, padding);
        if (g.getChildren().size() != textNodes.size() + 1) {
            g.getChildren().clear();
            g.getChildren().add(p);
            g.getChildren().addAll(textNodes);
        }
    }

    private void updateTextNodes(@Nonnull RenderContext ctx, List<Text> list, UmlCompartmentalizedData cpData, double x, double y,
                                 double wrappingWidth,
                                 double lineSpacing, Insets padding) {
        if (cpData == null) {
            list.clear();
            return;
        }

        StringBuilder buf = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, ImmutableList<String>> entry : cpData.getMap().entrySet()) {
            if (list.size() <= i) {
                list.add(new Text());
            }
            list.get(i++).setText(entry.getKey());
            buf.setLength(0);
            for (String item : entry.getValue()) {
                if (buf.length() != 0) {
                    buf.append('\n');
                }
                buf.append(item);
            }
            if (list.size() <= i) {
                list.add(new Text());
            }
            list.get(i++).setText(buf.toString());
        }
        while (list.size() > i) {
            list.remove(list.size() - 1);
        }
        for (Text text : list) {
            applyTextFontableFigureProperties(ctx, text);
            text.setWrappingWidth(wrappingWidth);
            text.setTextAlignment(TextAlignment.CENTER);
            text.setX(x);
            text.setY(y);
            text.setTextOrigin(VPos.TOP);
            text.setLineSpacing(lineSpacing);
            y += text.prefHeight(wrappingWidth) + lineSpacing;
        }
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
        applyShapeableProperties(ctx, path);
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


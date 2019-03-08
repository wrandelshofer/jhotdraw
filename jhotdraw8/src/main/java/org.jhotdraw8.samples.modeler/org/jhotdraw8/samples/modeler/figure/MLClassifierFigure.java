package org.jhotdraw8.samples.modeler.figure;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
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
import org.jhotdraw8.draw.figure.TextLayoutableFigure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.StringStyleableFigureKey;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.samples.modeler.model.MLCompartmentalizedData;
import org.jhotdraw8.samples.modeler.model.MLCompartmentedDataStyleableFigureKey;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Renders a "UMLClassifierShape" element.
 * <p>
 * A UMLClassifierShape is drawn as a rectangle that contains a single column of compartments.<br>
 * A compartment shows a portion of information.<br>
 * The compartments are separated by a solid line.
 * <p>
 * The first compartment shows the following information:
 * <ul>
 * <li>Metaclass (optional)</li>
 * <li>Name</li>
 * </ul>
 * <p>
 * All other compartments show the following information:
 * <ul>
 * <li>Compartment Name (hideable)</li>
 * <li>a list of textual Items</li>
 * </ul>
 * <p>
 * Metaclass, Name and CompartmentName are centered.
 * The Items are left aligned.
 * <p>
 * Example of a UMLClassifierShape:
 * <pre>
 *      ┌────────────────┐
 *      │    «class»     │
 *      │     Point      │
 *      ├────────────────┤
 *      │   attributes   │
 *      │ x:double       │
 *      │ y:double       │
 *      ├────────────────┤
 *      │   operations   │
 *      │ add(Point)     │
 *      │ subtract(Point)│
 *      └────────────────┘
 * </pre>
 */
public class MLClassifierFigure extends AbstractLeafFigure
        implements StrokableFigure, FillableFigure, TransformableFigure,
        ResizableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        ConnectableFigure, PathIterableFigure, RectangularFigure, ShapeableFigure,
        BodyFontableFigure, TextFillableFigure, PaddableFigure,
        NameFontableFigure, LabelFontableFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "MLClassifier";

    public final static MLCompartmentedDataStyleableFigureKey COMPARTMENTS = new MLCompartmentedDataStyleableFigureKey(MLConstants.ML_NAMESPACE_PREFIX, "compartments", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), new MLCompartmentalizedData());
    public final static StringStyleableFigureKey KEYWORD = MLConstants.KEYWORD;
    public final static StringStyleableFigureKey NAME = new StringStyleableFigureKey(MLConstants.ML_NAMESPACE_PREFIX, "name", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), false, "unnamed", null);
    public final static BooleanStyleableFigureKey LABELS_VISIBLE = new BooleanStyleableFigureKey("labelsVisible", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), false);
    public final static BooleanStyleableFigureKey KEYWORD_VISIBLE = MLConstants.KEYWORD_VISIBLE;
    /**
     * The line spacing. Default value: {@code 0.0}
     */
    public final static CssSizeStyleableFigureKey LINE_SPACING = TextLayoutableFigure.LINE_SPACING;

    private Path path;

    public MLClassifierFigure() {
    }

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
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Group g = (Group) node;
        Path p = (Path) g.getChildren().get(0);

        applyShapeableProperties(ctx, p);
        applyStrokableFigureProperties(ctx, p);
        applyFillableFigureProperties(ctx, p);
        MLCompartmentalizedData cpData = getStyled(COMPARTMENTS);
        applyTransformableFigureProperties(ctx, node);

        List<Text> textNodes = new ArrayList<Text>();
        for (int i = 1, n = g.getChildren().size(); i < n; i++) {
            textNodes.add((Text) g.getChildren().get(i));
        }
        UnitConverter converter = ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        double lineSpacing = converter.convert(getNonnull(LINE_SPACING), UnitConverter.DEFAULT);
        Insets padding = getStyledNonnull(PADDING).getConvertedValue(converter);
        Bounds bounds = getBoundsInLocal();
        updateTextNodes(ctx, textNodes,
                p.getElements(),
                get(KEYWORD),
                getNonnull(NAME),
                cpData,
                bounds,
                lineSpacing, padding);
        if (g.getChildren().size() != textNodes.size() + 1) {
            g.getChildren().clear();
            g.getChildren().add(p);
            g.getChildren().addAll(textNodes);
        }
    }

    /**
     * @param ctx
     * @param textNodes    Text nodes for rendering the text contents. Text nodes already contained
     *                     in this list will be reused.
     * @param pathElements Path elements for rendering the separator between compartments.
     *                     Path elements already containd in this list will not be altered.
     * @param metaclass
     * @param name
     * @param cpData
     * @param lineSpacing
     * @param padding
     */
    private void updateTextNodes(@Nonnull RenderContext ctx,
                                 List<Text> textNodes,
                                 List<PathElement> pathElements,
                                 @Nullable String metaclass, @Nonnull String name, MLCompartmentalizedData cpData,
                                 Bounds bounds,
                                 double lineSpacing, Insets padding) {
        if (cpData == null) {
            textNodes.clear();
            return;
        }

        // Ensure that we have enough text nodes.
        boolean compartmentNamesVisible = getStyledNonnull(LABELS_VISIBLE);
        ensureEnoughTextNodes(textNodes, metaclass, cpData, compartmentNamesVisible);

        // Compute geometry
        double width = bounds.getWidth();
        double left = padding.getLeft();
        double right = padding.getRight();
        double top = padding.getTop();
        double bottom = padding.getBottom();
        double wrappingWidth = width - left - right;
        double x = bounds.getMinX();
        double y = bounds.getMinY() + top;

        UnitConverter converter = ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        CssSize cssStrokeWidthSize = get(STROKE_WIDTH);
        double strokeWidth = cssStrokeWidthSize == null ? 0 : converter.convert(cssStrokeWidthSize, UnitConverter.DEFAULT);

        {
            StringBuilder buf = new StringBuilder();
            int i = 0;
            Text node;

            // add metaclass and name
            if (metaclass != null && getStyledNonnull(KEYWORD_VISIBLE)) {
                node = textNodes.get(i++);
                applyLabelStyle(ctx, node);
                node.setText("«" + metaclass + "»");
                node.setWrappingWidth(wrappingWidth);
                node.setY(y);
                y += node.getLayoutBounds().getHeight() + lineSpacing;
            }
            node = textNodes.get(i++);
            applyNameStyle(ctx, node);
            node.setText(name);
            node.setWrappingWidth(wrappingWidth);
            node.setY(y);
            y += node.getLayoutBounds().getHeight() + lineSpacing;

            // add compartments
            for (Map.Entry<String, ImmutableList<String>> entry : cpData.getMap().entrySet()) {
                // add separator line
                pathElements.add(new MoveTo(x, y + bottom + strokeWidth * 0.5));
                pathElements.add(new LineTo(x + width, y + bottom + strokeWidth * 0.5));
                y += bottom + strokeWidth + top;

                if (compartmentNamesVisible) {
                    // add compartment name
                    node = textNodes.get(i++);
                    node.setText(entry.getKey());
                    node.setY(y);
                    applyLabelStyle(ctx, node);
                    y += node.getLayoutBounds().getHeight() + lineSpacing;
                }

                // add compartment items
                node = textNodes.get(i++);
                node.setY(y);
                buf.setLength(0);
                for (String item : entry.getValue()) {
                    if (buf.length() != 0) {
                        buf.append('\n');
                    }
                    buf.append(item);
                }
                node.setText(buf.toString());
                applyItemStyle(ctx, node);
                y += node.getLayoutBounds().getHeight() + lineSpacing;
            }
        }

        // apply common styles
        for (Text node : textNodes) {
            node.setWrappingWidth(wrappingWidth);
            node.setX(x + left);
            node.setTextOrigin(VPos.TOP);
            node.setLineSpacing(lineSpacing);
        }
    }

    private void applyLabelStyle(RenderContext ctx, Text node) {
        applyLabelTextFontableFigureProperties(ctx, node);
        node.setTextAlignment(TextAlignment.CENTER);
    }

    private void applyNameStyle(RenderContext ctx, Text node) {
        applyNameTextFontableFigureProperties(ctx, node);
        node.setTextAlignment(TextAlignment.CENTER);
    }

    private void applyItemStyle(RenderContext ctx, Text node) {
        applyBodyTextFontableFigureProperties(ctx, node);
        node.setTextAlignment(TextAlignment.LEFT);
    }

    private void ensureEnoughTextNodes(List<Text> list, @Nullable String metaclass, MLCompartmentalizedData cpData, boolean compartmentLabelsVisible) {
        // We need a text node for the metaclass if present and for the name
        int n = (metaclass == null ? 0 : 1) + 1;

        // we need a text node for the compartment name
        if (compartmentLabelsVisible) {
            n += cpData.getMap().size();
        }
        // we need a text node for the compartment items
        n += cpData.getMap().size();

        // adjust list
        while (list.size() < n) {
            list.add(new Text());
        }
        while (list.size() > n) {
            list.remove(list.size() - 1);
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


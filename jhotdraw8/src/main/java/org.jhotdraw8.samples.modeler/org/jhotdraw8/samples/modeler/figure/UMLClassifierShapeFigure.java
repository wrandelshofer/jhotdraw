/*
 * @(#)UMLClassifierShapeFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.figure;

import javafx.collections.ObservableList;
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
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.Key;
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
import org.jhotdraw8.draw.figure.TextEditableFigure;
import org.jhotdraw8.draw.figure.TextFillableFigure;
import org.jhotdraw8.draw.figure.TextLayoutableFigure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.key.BooleanStyleableKey;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.NullableStringStyleableKey;
import org.jhotdraw8.draw.key.StringStyleableKey;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.samples.modeler.model.MLCompartmentalizedData;
import org.jhotdraw8.samples.modeler.model.MLCompartmentedDataStyleableFigureKey;
import org.jhotdraw8.samples.modeler.model.MLModifier;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.EnumSet;
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
 * <li>Keyword (optional)</li>
 * <li>Name</li>
 * </ul>
 * <p>
 * All other compartments show the following information:
 * <ul>
 * <li>Compartment Name (hideable)</li>
 * <li>List of textual items</li>
 * </ul>
 * <p>
 * Metaclass, Name and CompartmentName are centered.
 * The textual items are left aligned.
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
 * Classifiers with the following keywords are treated as follows:
 * <dl>
 * <dt>«interface»</dt><dd>Items are shown with abstract item font
 * by default.</dd>
 * <dt>«abstract class»</dt><dd>Items are shown with abstract item font
 * by default.</dd>
 * </dl>
 * </pre>
 * Classifiers with the following modifiers in their name are treated as follows:
 * <dl>
 * <dt>{abstract}</dt><dd>Items are shown with abstract item font
 * by default.</dd>
 * </dl>
 * Items that contain an annotation in curly braces are treated as follows:
 * <dl>
 * <dt>{abstract}</dt><dd>Item is shown with abstract item font.</dd>
 * <dt>{default}</dt><dd>Item is shown with item font.</dd>
 * <dt>{static}</dt><dd>Item is shown with static item font.</dd>
 * </dl>
 */
public class UMLClassifierShapeFigure extends AbstractLeafFigure
        implements StrokableFigure, FillableFigure, TransformableFigure,
        ResizableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        ConnectableFigure, PathIterableFigure, RectangularFigure, ShapeableFigure,
        ItemFontableFigure, TextFillableFigure, PaddableFigure,
        NameFontableFigure, CompartmentLabelFontableFigure, KeywordLabelFontableFigure,
        TextEditableFigure, AbstractItemFontableFigure, StaticItemFontableFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "MLClassifier";

    public final static MLCompartmentedDataStyleableFigureKey COMPARTMENTS = MLConstants.COMPARTMENTS;
    @Nullable
    public final static NullableStringStyleableKey KEYWORD = MLConstants.KEYWORD;
    @Nullable
    public final static StringStyleableKey NAME = MLConstants.NAME;
    public final static BooleanStyleableKey COMPARTMENT_LABELS_VISIBLE = new BooleanStyleableKey("compartmentLabelsVisible", false);
    public final static BooleanStyleableKey KEYWORD_LABEL_VISIBLE = MLConstants.KEYWORD_LABEL_VISIBLE;
    /**
     * The line spacing. Default value: {@code 0.0}
     */
    @Nullable
    public final static CssSizeStyleableKey LINE_SPACING = TextLayoutableFigure.LINE_SPACING;
    /**
     * This key is used to tag editable nodes.
     */
    private static final String TEXT_NODE_TEXT_KEY = "text";

    private Path path;

    public UMLClassifierShapeFigure() {
    }

    @NonNull
    @Override
    public Node createNode(RenderContext ctx) {
        Group g = new Group();
        g.setAutoSizeChildren(false);
        Path p = new Path();
        Text text = new Text();
        g.getChildren().addAll(p, text);
        return g;
    }

    @Nullable
    @Override
    public TextEditorData getTextEditorDataFor(@Nullable Point2D pointInLocal, @Nullable Node node) {
        if (node == null) {
            return null;
        }
        Key<?> key = (Key<?>) node.getProperties().get(TEXT_NODE_TEXT_KEY);
        if (key != null && key.getValueType() == String.class) {
            @SuppressWarnings("unchecked") Key<String> stringKey = (Key<String>) key;
            return new TextEditorData(this, node.getBoundsInLocal(), stringKey);
        }
        return null;
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Group g = (Group) node;
        ObservableList<Node> children = g.getChildren();
        Path p = (Path) children.get(0);

        applyCompositableFigureProperties(ctx, node);
        applyHideableFigureProperties(ctx, node);
        applyShapeableProperties(ctx, p);
        applyStrokableFigureProperties(ctx, p);
        applyFillableFigureProperties(ctx, p);
        MLCompartmentalizedData cpData = getStyledNonNull(COMPARTMENTS);
        applyTransformableFigureProperties(ctx, node);

        List<Text> textNodes = new ArrayList<Text>();
        for (int i = 1, n = children.size(); i < n; i++) {
            textNodes.add((Text) children.get(i));
        }
        UnitConverter converter = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        double lineSpacing = converter.convert(getStyledNonNull(LINE_SPACING), UnitConverter.DEFAULT);
        Insets padding = getStyledNonNull(PADDING).getConvertedValue(converter);
        Bounds bounds = getLayoutBounds();
        String name = get(NAME);
        updateTextNodes(ctx, textNodes,
                p.getElements(),
                get(KEYWORD),
                name == null ? "" : name,
                cpData,
                bounds,
                lineSpacing, padding);
        if (children.size() != textNodes.size() + 1) {
            children.clear();
            children.add(p);
            children.addAll(textNodes);
        }
    }

    /**
     * @param ctx          the render context
     * @param textNodes    Text nodes for rendering the text contents. Text nodes already contained
     *                     in this list will be reused.
     * @param pathElements Path elements for rendering the separator between compartments.
     *                     Path elements already containd in this list will not be altered.
     * @param keyword      the keyword
     * @param name         the name
     * @param cpData       the compartment data
     * @param lineSpacing  the line spacing
     * @param padding      the padding
     */
    private void updateTextNodes(@NonNull RenderContext ctx,
                                 @NonNull List<Text> textNodes,
                                 @NonNull List<PathElement> pathElements,
                                 @Nullable String keyword, @NonNull String name,
                                 @NonNull MLCompartmentalizedData cpData,
                                 @NonNull Bounds bounds,
                                 double lineSpacing, @NonNull Insets padding) {

        // Ensure that we have enough text nodes.
        boolean keywordLabelVisible = getStyledNonNull(KEYWORD_LABEL_VISIBLE);
        boolean compartmentNamesVisible = getStyledNonNull(COMPARTMENT_LABELS_VISIBLE);
        ensureEnoughTextNodes(textNodes, keyword, cpData,
                keywordLabelVisible,
                compartmentNamesVisible);

        // Compute geometry
        double width = bounds.getWidth();
        double left = padding.getLeft();
        double right = padding.getRight();
        double top = padding.getTop();
        double bottom = padding.getBottom();
        double wrappingWidth = width - left - right;
        double x = bounds.getMinX();
        double y = bounds.getMinY() + top;

        UnitConverter converter = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        CssSize cssStrokeWidthSize = get(STROKE_WIDTH);
        double strokeWidth = cssStrokeWidthSize == null ? 0 : converter.convert(cssStrokeWidthSize, UnitConverter.DEFAULT);

        EnumSet<MLModifier> classifierModifiers = computeClassifierModifiers(keyword);

        // Apply common styles to text nodes
        for (Text node : textNodes) {
            node.setWrappingWidth(wrappingWidth);
            node.setX(x + left);
            node.setTextOrigin(VPos.TOP);
            node.setLineSpacing(lineSpacing);
        }


        {
            StringBuilder buf = new StringBuilder();
            int i = 0;
            Text node;

            // add metaclass and name
            if (keyword != null && getStyledNonNull(KEYWORD_LABEL_VISIBLE)) {
                node = textNodes.get(i++);
                applyKeywordLabelStyle(ctx, node);
                node.setText("«" + keyword + "»");
                node.getProperties().put(TEXT_NODE_TEXT_KEY, KEYWORD);
                node.setY(y);
                y += node.getLayoutBounds().getHeight() + lineSpacing;
            }
            node = textNodes.get(i++);
            applyNameStyle(ctx, node);
            node.setText(name);
            node.getProperties().put(TEXT_NODE_TEXT_KEY, NAME);
            node.setY(y);
            y += node.getLayoutBounds().getHeight() + lineSpacing;

            // add compartments
            for (Map.Entry<String, ImmutableList<String>> entry : cpData.getMap().entrySet()) {
                // add separator line
                pathElements.add(new MoveTo(x, y + bottom + strokeWidth * 0.5));
                pathElements.add(new LineTo(x + width, y + bottom + strokeWidth * 0.5));
                y += bottom + strokeWidth + top;

                if (compartmentNamesVisible) {
                    // add compartment name labels
                    node = textNodes.get(i++);
                    node.getProperties().put(TEXT_NODE_TEXT_KEY, COMPARTMENTS);
                    node.setWrappingWidth(wrappingWidth);
                    node.setText(entry.getKey());
                    node.setY(y);
                    applyCompartmentLabelStyle(ctx, node);
                    y += node.getLayoutBounds().getHeight() + lineSpacing;
                }

                // add compartment items
                for (String item : entry.getValue()) {
                    node = textNodes.get(i++);
                    node.setY(y);
                    buf.setLength(0);
                    buf.append(item);
                    node.getProperties().put(TEXT_NODE_TEXT_KEY, COMPARTMENTS);
                    node.setText(buf.toString());
                    applyItemStyle(ctx, classifierModifiers, buf.toString(), node);
                    y += node.getLayoutBounds().getHeight() + lineSpacing;
                }
            }
        }

    }

    @NonNull
    private EnumSet<MLModifier> computeClassifierModifiers(@Nullable String keyword) {
        EnumSet<MLModifier> classifierModifiers = EnumSet.noneOf(MLModifier.class);
        if (keyword != null) {
            switch (keyword) {
                case "interface":
                case "abstract class":
                    classifierModifiers.add(MLModifier.ABSTRACT);
                    break;
            }
        }
        return classifierModifiers;
    }

    @NonNull
    private EnumSet<MLModifier> computeOperationModifiers(@Nullable String operation) {
        EnumSet<MLModifier> operationModifiers = EnumSet.noneOf(MLModifier.class);
        if (operation != null) {
            if (operation.contains("{abstract}")) {
                operationModifiers.add(MLModifier.ABSTRACT);
            }
            if (operation.contains("{default}")) {
                operationModifiers.add(MLModifier.DEFAULT);
            }
            if (operation.contains("{static}")) {
                operationModifiers.add(MLModifier.STATIC);
            }
        }
        return operationModifiers;
    }

    private void applyCompartmentLabelStyle(RenderContext ctx, @NonNull Text node) {
        applyCompartmentLabelTextFontableFigureProperties(ctx, node);
        node.setTextAlignment(TextAlignment.CENTER);
    }

    private void applyKeywordLabelStyle(RenderContext ctx, @NonNull Text node) {
        applyKeywordLabelTextFontableFigureProperties(ctx, node);
        node.setTextAlignment(TextAlignment.CENTER);
    }

    private void applyNameStyle(RenderContext ctx, @NonNull Text node) {
        applyNameTextFontableFigureProperties(ctx, node);
        node.setTextAlignment(TextAlignment.CENTER);
    }

    private void applyItemStyle(RenderContext ctx, @NonNull EnumSet<MLModifier> classifierModifiers, String text, @NonNull Text node) {
        EnumSet<MLModifier> opModifiers = computeOperationModifiers(text);
        if (opModifiers.contains(MLModifier.STATIC)) {
            applyStaticItemTextFontableFigureProperties(ctx, node);
        } else if (opModifiers.contains(MLModifier.DEFAULT)) {
            applyItemTextFontableFigureProperties(ctx, node);
        } else if (opModifiers.contains(MLModifier.ABSTRACT)) {
            applyAbstractItemTextFontableFigureProperties(ctx, node);
        } else if (classifierModifiers.contains(MLModifier.ABSTRACT)) {
            applyAbstractItemTextFontableFigureProperties(ctx, node);
        } else {
            applyItemTextFontableFigureProperties(ctx, node);
        }


        node.setTextAlignment(TextAlignment.LEFT);
    }

    private void ensureEnoughTextNodes(@NonNull List<Text> list, @Nullable String keyword,
                                       @NonNull MLCompartmentalizedData cpData,
                                       boolean keywordLabelVisible,
                                       boolean compartmentLabelsVisible) {
        // We need a text node for the keyword if present and for the name
        int n = (keywordLabelVisible ? 1 : 0) + 1;

        // we need a text node for the compartment name
        if (compartmentLabelsVisible) {
            n += cpData.getMap().size();
        }

        // we need a text node for each compartment item
        for (Map.Entry<String, ImmutableList<String>> entry : cpData.getMap().entrySet()) {
            n += entry.getValue().size();
        }

        // adjust list
        while (list.size() < n) {
            list.add(new Text());
        }
        while (list.size() > n) {
            list.remove(list.size() - 1);
        }
    }

    @NonNull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }


    @Override
    public void layout(@NonNull RenderContext ctx) {
        if (path == null) {
            path = new Path();
        }
        applyShapeableProperties(ctx, path);
    }

    @Override
    public @Nullable Connector findConnector(@NonNull Point2D pointInLocal, Figure connectingFigure) {
        return new PathConnector(new BoundsLocator(getLayoutBounds(), pointInLocal));
    }

    @NonNull
    @Override
    public PathIterator getPathIterator(@Nullable AffineTransform tx) {
        if (path == null) {
            layout(new SimpleRenderContext());
        }
        return path == null ? Shapes.emptyPathIterator() : Shapes.awtShapeFromFX(path).getPathIterator(tx);
    }
}


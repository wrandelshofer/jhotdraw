/*
 * @(#)SvgLineFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.figure.AbstractLeafFigure;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.figure.TextableFigure;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.svg.text.SvgFontSize;
import org.jhotdraw8.svg.text.SvgTextAnchor;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.Objects;

/**
 * Represents an SVG 'text' element.
 *
 * @author Werner Randelshofer
 */
public class SvgTextFigure extends AbstractLeafFigure
        implements StyleableFigure, LockableFigure, SvgTransformableFigure,
        PathIterableFigure, HideableFigure, TextableFigure,
        SvgDefaultableFigure,
        SvgElementFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final @NonNull String TYPE_SELECTOR = "text";
    public static final @NonNull CssSizeStyleableKey X = new CssSizeStyleableKey("x", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey Y = new CssSizeStyleableKey("y", CssSize.ZERO);
    @Nullable
    private Bounds cachedLayoutBounds;

    public SvgTextFigure() {
        this(0, 0);
    }

    public SvgTextFigure(@NonNull Point2D position) {
        this(position.getX(), position.getY());
    }

    public SvgTextFigure(double x, double y) {
        // Performance: Only set properties if they differ from the default value.
        if (x != 0) {
            set(X, new CssSize(x));
        }
        if (y != 0) {
            set(Y, new CssSize(y));
        }
    }

    @NonNull
    @Override
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        g.setManaged(false);
        g.setAutoSizeChildren(false);
        Text n0 = new Text();
        n0.setManaged(false);
        Text n1 = new Text();
        n1.setManaged(false);
        g.getProperties().put("fillNode",n0);
        g.getProperties().put("strokeNode",n1);
        return g;
    }


    @Nullable
    protected Bounds getCachedLayoutBounds() {
        return cachedLayoutBounds;
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Nullable
    protected Bounds setCachedLayoutBounds(Bounds newValue) {
        Bounds oldValue = cachedLayoutBounds;
        cachedLayoutBounds = newValue;
        return oldValue;
    }

    @NonNull
    @Override
    public Bounds getLayoutBounds() {
        Bounds boundsInLocal = getCachedLayoutBounds();
        return boundsInLocal == null ? computeLayoutBounds() : boundsInLocal;
    }

    @Override
    public @NonNull Bounds getBoundsInLocal() {
        return getCssLayoutBounds().getConvertedBoundsValue();
    }

    @NonNull
    @Override
    public CssRectangle2D getCssLayoutBounds() {
        return new CssRectangle2D(getLayoutBounds());
    }

    /**
     * Computes the bounds of the node for layout calculations. These bounds
     * include the text of the node and the padding.
     *
     * @return the layout bounds
     */
    @NonNull
    public Bounds computeLayoutBounds() {
        return computeLayoutBounds(new SimpleRenderContext(), new Text());
    }

    @NonNull
    protected Bounds computeLayoutBounds(RenderContext ctx, Text textNode) {
        updateTextNode(ctx, textNode);
        Bounds b = textNode.getLayoutBounds();
        Insets i = new Insets(0,0,0,0);

        return new BoundingBox(
                b.getMinX() - i.getLeft(),
                b.getMinY() - i.getTop(),
                b.getWidth() + i.getLeft() + i.getRight(),
                textNode.getBaselineOffset() + i.getTop() + i.getBottom());
    }

    /**
     * Returns the bounds of the text node for layout calculations. These bounds
     * only includes the text - without padding.
     *
     * @param ctx the render context
     * @return the layout bounds of the text
     */
    @NonNull
    protected Bounds getTextBounds(@Nullable RenderContext ctx) {
        Text textNode = new Text();
        updateTextNode(ctx, textNode);
        Bounds b = textNode.getLayoutBounds();
        return b;
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        Text tn = new Text();
        tn.setX(getStyledNonNull(X).getConvertedValue());
        tn.setY(getStyledNonNull(Y).getConvertedValue());
        tn.setBoundsType(TextBoundsType.VISUAL);
        //applyTextFontableFigureProperties(null, tn);
        //applyTextLayoutableFigureProperties(null, tn);

        // We must set the font before we set the text, so that JavaFx does not need to retrieve
        // the system default font, which on Windows requires that the JavaFx Toolkit is launched.
        tn.setText(getText(null));

        return Shapes.awtShapeFromFX(tn).getPathIterator(tx);
    }

    @Nullable
    protected String getText(RenderContext ctx) {
        return get(TEXT);
    }


    @Override
    public void layout(@NonNull RenderContext ctx) {
        Bounds b = computeLayoutBounds(ctx, new Text());
        setCachedLayoutBounds(b);
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        Bounds lb = computeLayoutBounds();
        Insets i = new Insets(0,0,0,0);
        set(X,new CssSize(x.getConvertedValue() + i.getLeft()));
        set(Y, new CssSize(y.getConvertedValue() + lb.getHeight() - i.getBottom()));
    }

    @Override
    public void translateInLocal(@NonNull CssPoint2D delta) {
        CssSize x = get(X);
        CssSize y = get(Y);
        set(X, x.add(delta.getX()));
        set(Y, y.add(delta.getY()));
    }

    protected void updateGroupNode(RenderContext ctx, Group node) {
        applySvgDefaultableCompositingProperties(ctx,node);

    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Group g = (Group) node;
        Text n0 = (Text) g.getProperties().get("fillNode");
        Text n1 = (Text) g.getProperties().get("strokeNode");

        updateGroupNode(ctx, g);
        updateTextNode(ctx, n0);
        updateTextNode(ctx, n1);
        applySvgShapeProperties(ctx,n0,n1);

        g.getChildren().clear();
        if (n0.getFill() != null) {
            g.getChildren().add(n0);
        }
        if (n1.getStroke() != null) {
            g.getChildren().add(n1);
        }
    }

    protected void updateTextNode(@NonNull RenderContext ctx, @NonNull Text tn) {
        applySvgDefaultableFillProperties(ctx,tn);
        SvgTextAnchor textAnchor = getDefaultableStyledNonNull(TEXT_ANCHOR);
        switch (textAnchor) {
        case START:
            tn.setTextAlignment(TextAlignment.LEFT);
            break;
        case MIDDLE:
            tn.setTextAlignment(TextAlignment.CENTER);
            break;
        case END:
            tn.setTextAlignment(TextAlignment.RIGHT);
            break;
        }

        SvgFontSize fontSize = getDefaultableStyledNonNull(FONT_SIZE_KEY);
        UnitConverter unitConverter = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        double convertedFontSize = fontSize.getConvertedValue(this, unitConverter);
        Font font = tn.getFont();
        if (font!=null&&font.getSize()!=convertedFontSize) {
            font=Font.font(font.getFamily(),convertedFontSize);
            tn.setFont(font);
        }


        // We must set the font before we set the text, so that JavaFx does not
        // need to retrieve the system default font, which on Windows requires
        // that the JavaFx Toolkit is launched.
        final String text = getText(ctx) ;
        if (!Objects.equals(text, tn.getText())) {
            tn.setText(text);
        }
        Bounds boundsInLocal = tn.getBoundsInLocal();
        double x=getStyledNonNull(X).getConvertedValue(unitConverter);
        double y=getStyledNonNull(Y).getConvertedValue(unitConverter);
        switch (textAnchor) {
        case START:
            break;
        case MIDDLE:
            x-=boundsInLocal.getWidth()*0.5;
            break;
        case END:
            x-=boundsInLocal.getWidth();
            break;
        }
        tn.setX(x);
        tn.setY(y);
    }
}

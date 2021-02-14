/*
 * @(#)PageLabelFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.key.StringStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * PageLabelFigure.
 *
 * @author Werner Randelshofer
 */
public class PageLabelFigure extends AbstractLabelFigure
        implements HideableFigure, TextFontableFigure, TextLayoutableFigure, StyleableFigure, LockableFigure, TransformableFigure, CompositableFigure {
    public static final String TYPE_SELECTOR = "PageLabel";
    public static final String NUM_PAGES_PLACEHOLDER = "${numPages}";
    public static final String PAGE_PLACEHOLDER = "${page}";
    public static final String DATE_PLACEHOLDER = "${date}";
    /**
     * The text. Default value: {@code ""}.
     */
    public static final StringStyleableKey TEXT_WITH_PLACEHOLDERS = new StringStyleableKey("text", "",
            "Supported placeholders:  " + PAGE_PLACEHOLDER + ", " + NUM_PAGES_PLACEHOLDER + ", " + DATE_PLACEHOLDER);

    public PageLabelFigure() {
        this(0, 0, "");
    }

    public PageLabelFigure(@NonNull Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public PageLabelFigure(double x, double y, String text, @NonNull Object... keyValues) {
        set(TEXT_WITH_PLACEHOLDERS, text);
        set(ORIGIN, new CssPoint2D(x, y));
        for (int i = 0; i < keyValues.length; i += 2) {
            @SuppressWarnings("unchecked") // the set() method will perform the check for us
                    Key<Object> key = (Key<Object>) keyValues[i];
            set(key, keyValues[i + 1]);
        }
    }

    @Override
    protected String getText(@Nullable RenderContext ctx) {
        String text = get(TEXT_WITH_PLACEHOLDERS);
        final Integer pageNumber = ctx == null ? 0 : ctx.get(RenderContext.RENDER_PAGE_NUMBER);
        final Integer numPages = ctx == null ? 0 : ctx.get(RenderContext.RENDER_NUMBER_OF_PAGES);
        final Instant timestamp = ctx == null ? Instant.now() : ctx.get(RenderContext.RENDER_TIMESTAMP);
        final String date = timestamp == null ? null : DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(timestamp.atZone(ZoneId.systemDefault()).toLocalDate());

        if (pageNumber != null) {
            text = replaceAll(text, PAGE_PLACEHOLDER, "" + (pageNumber + 1));
        }
        if (numPages != null) {
            text = replaceAll(text, NUM_PAGES_PLACEHOLDER, "" + numPages);
        }
        if (date != null) {
            text = replaceAll(text, DATE_PLACEHOLDER, date);
        }

        return text;
    }

    private @NonNull String replaceAll(@NonNull String text, @NonNull String placeholder, String replace) {
        for (int p = text.indexOf(placeholder); p != -1; p = text.indexOf(placeholder)) {
            text = text.substring(0, p) + replace + text.substring(p + placeholder.length());
        }
        return text;
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        super.updateNode(ctx, node);
        applyTransformableFigureProperties(ctx, node);
        applyCompositableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyHideableFigureProperties(ctx, node);
    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}

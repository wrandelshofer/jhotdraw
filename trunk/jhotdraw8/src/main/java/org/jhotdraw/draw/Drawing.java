/* @(#)Drawing.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import java.io.File;
import java.net.URI;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.FigureKey;
import org.jhotdraw.draw.key.SimpleFigureKey;
import java.net.URL;
import java.util.List;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import org.jhotdraw.collection.Key;
import org.jhotdraw.css.StyleManager;
import org.jhotdraw.css.ast.Stylesheet;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.styleable.StyleableStyleManager;
import org.jhotdraw.draw.key.PaintStyleableFigureKey;

/**
 * A <em>drawing</em> is an image composed of graphical (figurative) elements.
 * <p>
 * <b>Tree Structure.</b> The graphical elements are represented by
 * {@link Figure} objects. The figure objects are organized in a tree structure
 * of which the drawing object is typically the root.
 * <p>
 * <b>Nested Drawings.</b> Since {@code Drawing} implements the {@code Figure}
 * interface, a drawing may be contained in another drawing.</p>
 * <p>
 * <b>Styling.</b> A drawing can have a style sheet which may affect the state
 * of its descendant figures. Since figures cache style sheet data, method
 * {@code applyCss} must be invoked on the drawing, when its style sheet is
 * changed, and on a figure when its location in the descendant tree structure
 * changes.</p>
 * <p>
 * <b>Layers.</b> By convention all children of a {@code Drawing} must be
 * {@link Layer}s.</p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Drawing extends Figure {

    /**
     * Specifies the home address of all relative URLs used in a drawing.
     * <p>
     * This property is not styleable.</p>
     */
    public final static Key<URI> DOCUMENT_HOME = new SimpleFigureKey<>("documentHome", URI.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT),new File(System.getProperty("user.home")).toURI());
    /**
     * Holds a list of author stylesheets. If the value is null, then no
     * stylesheets are used.
     * <p>
     * Supports the following data types for list entries:
     * <ul>
     * <li>URI. The URI points to a CSS file. If the URI is relative, then it is
     * relative to {@code DOCUMENT_HOME}.</li>
     * <li>String. The String contains a CSS as a literal.</li>
     * </ul>
     * <p>
     * This property is not styleable.</p>
     */
    public final static Key<List<URI>> AUTHOR_STYLESHEETS = new SimpleFigureKey<>("authorStylesheets", List.class, new Class<?>[]{URI.class}, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.TRANSFORM, DirtyBits.CONNECTION_LAYOUT, DirtyBits.STYLE), null);
    /**
     * Holds a list of user agent stylesheets. If the value is null, then no
     * stylesheets are used.
     * <ul>
     * <li>URI. The URI points to a CSS file. If the URI is relative, then it is
     * relative to {@code DOCUMENT_HOME}.</li>
     * <li>String. The String contains a CSS as a literal.</li>
     * </ul>
     * <p>
     * This property is not styleable.</p>
     */
    public final static Key<List<URI>> USER_AGENT_STYLESHEETS = new SimpleFigureKey<>("userAgentStylesheets", List.class, new Class<?>[]{URI.class}, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.TRANSFORM, DirtyBits.CONNECTION_LAYOUT, DirtyBits.STYLE), null);
    /**
     * Holds a list of inline stylesheets. If the value is null, then no
     * stylesheets are used.
     * <p>
     * This property is not styleable.</p>
     */
    public final static Key<List<String>> INLINE_STYLESHEETS = new SimpleFigureKey<>("inlineStylesheets", List.class, new Class<?>[]{String.class}, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.TRANSFORM, DirtyBits.CONNECTION_LAYOUT, DirtyBits.STYLE), null);
    /**
     * Defines the canvas width.
     * <p>
     * Canvas width and height are used to determine the bounds of the drawing
     * when it is printed or exported. {@code DrawingView} typically ignores
     * this value so that the user can still edit figures which are outside of
     * the bounds of the drawing.
     * </p>
     * <p>
     * This property is not styleable.</p>
     */
    public final static DoubleStyleableFigureKey WIDTH = new DoubleStyleableFigureKey("width", DirtyMask.of(DirtyBits.NODE), 640.0);
    /**
     * Defines the canvas height.
     * <p>
     * See {@link #WIDTH} for a description.
     * </p>
     * <p>
     * This property is not styleable.</p>
     */
    public final static DoubleStyleableFigureKey HEIGHT = new DoubleStyleableFigureKey("height", DirtyMask.of(DirtyBits.NODE), 480.0);
    /**
     * Defines the canvas color.
     * <p>
     * A drawing typically renders a rectangle with the dimensions given by
     * {@code WIDTH} and {@code HEIGHT} and fills it with the 
     * {@code BACKGROUND} paint.
     * </p>
     * <p>
     * This property is styleable with the key
     * {@code Figure.JHOTDRAW_CSS_PREFIX+"background"}.</p>
     */
    public final static PaintStyleableFigureKey BACKGROUND = new PaintStyleableFigureKey("background", Color.WHITE);

    /**
     * The CSS type selector for a drawing figure is {@code "Drawing"}.
     */
    public final static String TYPE_SELECTOR = "Drawing";

    @Override
    default String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    /**
     * Gets the style manager of the drawing.
     *
     * @return the style manager
     */
    StyleManager<Figure> getStyleManager();
}

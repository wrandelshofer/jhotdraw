/*
 * @(#)Drawing.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ListKey;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.NullableCssColorStyleableKey;
import org.jhotdraw8.draw.key.NullableObjectKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.graph.DirectedGraphBuilder;
import org.jhotdraw8.graph.GraphSearch;

import java.net.URI;
import java.nio.file.Paths;

/**
 * A <em>drawing</em> is an image composed of graphical (figurative) elements.
 * <p>
 * <b>Styling.</b> A drawing can have a style sheet which affects the style of
 * the figures.
 * <p>
 * <b>Layers.</b> By convention the children of a {@code Drawing} must be
 * {@link Layer}s. To addChild figures to a drawing, first addChild a layer, and then addChild the figures to the layer.</p>
 *
 * @author Werner Randelshofer
 * @design.pattern Drawing Framework, KeyAbstraction. The drawing framework
 * supports the creation of editors for structured drawings. The key
 * abstractions of the framework are: null {@link Drawing}, {@link Figure}, {@link org.jhotdraw8.draw.handle.Handle},
 * {@link org.jhotdraw8.draw.tool.Tool}, {@link org.jhotdraw8.draw.DrawingView},
 * {@link org.jhotdraw8.draw.DrawingEditor}, {@link org.jhotdraw8.draw.model.DrawingModel}.
 * @design.pattern org.jhotdraw8.draw.model.DrawingModel Facade, Subsystem.
 * @design.pattern Drawing Strategy, Context.
 * {@link org.jhotdraw8.draw.io.InputFormat} and
 * {@link org.jhotdraw8.draw.io.OutputFormat} encapsulate the algorithms for
 * loading and saving a {@link Drawing}.
 */
public interface Drawing extends Figure {

    /**
     * Specifies the home address of all relative URLs used in a drawing.
     * <p>
     * XXX internally we should only use absolute URLs.
     * <p>
     * This property is not styleable.</p>
     */
    Key<URI> DOCUMENT_HOME = new NullableObjectKey<>("documentHome", URI.class,
            Paths.get(System.getProperty("user.home")).toUri());
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
    Key<ImmutableList<URI>> AUTHOR_STYLESHEETS = new ListKey<URI>("authorStylesheets", URI.class);
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
    Key<ImmutableList<URI>> USER_AGENT_STYLESHEETS = new ListKey<URI>("userAgentStylesheets", URI.class);
    /**
     * Holds a list of inline stylesheets. If the value is null, then no
     * stylesheets are used.
     * <p>
     * This property is not styleable.</p>
     */
    Key<ImmutableList<String>> INLINE_STYLESHEETS = new ListKey<String>("inlineStylesheets", String.class);
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
    CssSizeStyleableKey WIDTH = new CssSizeStyleableKey("width", new CssSize(640.0));
    /**
     * Defines the canvas height.
     * <p>
     * See {@link #WIDTH} for a description.
     * </p>
     * <p>
     * This property is not styleable.</p>
     */
    CssSizeStyleableKey HEIGHT = new CssSizeStyleableKey("height", new CssSize(480.0));
    /**
     * Defines the canvas color.
     * <p>
     * A drawing typically renders a rectangle with the dimensions given by
     * {@code WIDTH} and {@code HEIGHT} and fills it with the {@code BACKGROUND}
     * paint.
     * </p>
     * <p>
     * This property is styleable with the key
     * {@code Figure.JHOTDRAW_CSS_PREFIX+"background"}.</p>
     */
    NullableCssColorStyleableKey BACKGROUND = new NullableCssColorStyleableKey("background", new CssColor("white", Color.WHITE));

    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    String TYPE_SELECTOR = "Drawing";

    @NonNull
    @Override
    default String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    /**
     * Gets the style manager of the drawing.
     *
     * @return the style manager
     */
    @Nullable StylesheetsManager<Figure> getStyleManager();

    /**
     * Updates the stylesheets in the style manager.
     */
    void updateStyleManager();

    /**
     * Performs one layout pass over the entire drawing.
     *
     * @param ctx the render context
     */
    default void layoutAll(@NonNull RenderContext ctx) {
        for (Figure f : layoutDependenciesIterable()) {
            f.layout(ctx);
        }
    }

    default void updateAllStylesheets(@NonNull RenderContext ctx) {
        StylesheetsManager<Figure> styleManager = getStyleManager();
        styleManager.applyStylesheetsTo(preorderIterable());
        for (Figure f : preorderIterable()) {
            f.invalidateTransforms();
        }

    }

    /**
     * Returns all figures in topological order according to their layout dependencies.
     * Independent figures come first.
     *
     * @return figures in topological order according to layout dependencies
     */
    @NonNull
    default Iterable<Figure> layoutDependenciesIterable() {
        // build a graph which includes all figures that must be laid out and all their observers
        // transitively
        DirectedGraphBuilder<Figure, Figure> graphBuilder = new DirectedGraphBuilder<>();
        for (Figure f : postorderIterable()) {
            graphBuilder.addVertex(f);
            for (Figure obs : f.getLayoutObservers()) {
                graphBuilder.addVertex(obs);
                graphBuilder.addArrow(f, obs, f);
            }
        }
        return GraphSearch.sortTopologically(graphBuilder);
    }
}

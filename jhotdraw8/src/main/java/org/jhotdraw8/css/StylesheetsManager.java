/* @(#)StylesheetsManager.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import java.net.URI;
import java.util.List;
import javafx.css.StyleOrigin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Stylesheet;

/**
 * StylesheetsManager.
 *
 * @author Werner Randelshofer
 * @param <E> the element type that can be styled by this style manager
 */
public interface StylesheetsManager<E> {

    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin the style origin
     * @param url the stylesheet url
     */
    default void addStylesheet(StyleOrigin origin, URI url) {
        addStylesheet(origin, null, url);
    }

    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin the style origin
     * @param documentHome the document Home url
     * @param url the stylesheet url
     */
    void addStylesheet(StyleOrigin origin, URI documentHome, URI url);

    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin the style origin
     * @param stylesheet the stylesheet given as a literal string
     */
    void addStylesheet(StyleOrigin origin, String stylesheet);

    /**
     * Removes all stylesheets with the specified origin.
     *
     * @param origin the style origin
     */
    void clearStylesheets(StyleOrigin origin);

    /**
     * Sets a list of stylesheets with the specified origin.
     *
     * @param <T> type of the list elements
     * @param origin the origin
     * @param stylesheets list elements can be Strings or URIs.
     */
    default <T> void setStylesheets(StyleOrigin origin, List<T> stylesheets) {
        setStylesheets(origin, null, stylesheets);
    }

    /**
     * Sets a list of stylesheets with the specified origin.
     *
     * @param <T> type of the list elements
     * @param origin the origin
     * @param documentHome the document home
     * @param stylesheets list elements can be Strings or URIs.
     */
    <T> void setStylesheets(StyleOrigin origin, URI documentHome, List<T> stylesheets);

    /**
     * Applies all managaed stylesheets to the specified element.
     *
     * @param e The element
     */
    public void applyStylesheetsTo(E e);

    /**
     * Returns the selector model of the style manager.
     *
     * @return the selector model
     */
    public SelectorModel<E> getSelectorModel();

    /**
     * Applies the provided stylesheet.
     *
     * @param styleOrigin the style origin to be used when setting attribute
     * values
     * @param s the stylesheet
     * @param element the element
     * @return true if an element was selected
     */
    public boolean applyStylesheetTo(StyleOrigin styleOrigin, Stylesheet s, E element);

    /**
     * Returns true if the provided stylesheet has selectors which match the
     * specified element.
     *
     * @param s the stylesheet
     * @param elem the element
     * @return true the element was selected
     */
    default boolean matchesElement(@NonNull Stylesheet s, E elem) {
        SelectorModel<E> selectorModel = getSelectorModel();
        for (StyleRule r : s.getStyleRules()) {
            if (null != r.getSelectorGroup().match(selectorModel, elem)) {
                return true;
            }
        }

        return false;
    }
}

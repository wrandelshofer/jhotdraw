/*
 * @(#)StylesheetsManager.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.css.StyleOrigin;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Stylesheet;

import java.net.URI;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * StylesheetsManager.
 *
 * @param <E> the element type that can be styled by this style manager
 * @author Werner Randelshofer
 */
public interface StylesheetsManager<E> {

    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin the style origin
     * @param url    the stylesheet url
     */
    default void addStylesheet(StyleOrigin origin, URI url) {
        addStylesheet(origin, null, url);
    }

    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin       the style origin
     * @param documentHome the document Home url
     * @param url          the stylesheet url
     */
    void addStylesheet(StyleOrigin origin, URI documentHome, URI url);

    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin     the style origin
     * @param stylesheet the stylesheet
     */
    void addStylesheet(StyleOrigin origin, Stylesheet stylesheet);

    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin     the style origin
     * @param stylesheet the stylesheet given as a literal string
     */
    void addStylesheet(StyleOrigin origin, String stylesheet);

    default void applyStylesheetsTo(@NonNull Iterable<E> iterable) {
        StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList())
                .stream()
                .parallel()
                .forEach(this::applyStylesheetsTo);
    }

    /**
     * Removes all stylesheets with the specified origin.
     *
     * @param origin the style origin
     */
    void clearStylesheets(StyleOrigin origin);

    /**
     * Sets a list of stylesheets with the specified origin.
     *
     * @param <T>         type of the list elements
     * @param origin      the origin
     * @param stylesheets list elements can be Strings or URIs.
     */
    default <T> void setStylesheets(StyleOrigin origin, List<T> stylesheets) {
        setStylesheets(origin, null, stylesheets);
    }

    /**
     * Sets a list of stylesheets with the specified origin.
     *
     * @param <T>          type of the list elements
     * @param origin       the origin
     * @param documentHome the document home
     * @param stylesheets  list elements can be Strings or URIs.
     */
    <T> void setStylesheets(StyleOrigin origin, URI documentHome, List<T> stylesheets);

    /**
     * Applies all managed stylesheets to the specified element.
     *
     * @param e The element
     */
    void applyStylesheetsTo(E e);

    /**
     * Returns the selector model of the style manager.
     *
     * @return the selector model
     */
    @NonNull
    SelectorModel<E> getSelectorModel();

    /**
     * Applies the provided stylesheet.
     *
     * @param styleOrigin            the style origin to be used when setting attribute
     *                               values
     * @param s                      the stylesheet
     * @param element                the element
     * @param suppressParseException if parse exceptions should be suppressed
     * @return true if an element was selected
     * @throws ParseException on parse exception
     */
    boolean applyStylesheetTo(StyleOrigin styleOrigin, Stylesheet s, E element, boolean suppressParseException) throws ParseException;

    /**
     * Returns true if the provided stylesheet has selectors which match the
     * specified element.
     *
     * @param s    the stylesheet
     * @param elem the element
     * @return true the element was selected
     */
    default boolean matchesElement(@NonNull Stylesheet s, E elem) {
        SelectorModel<E> selectorModel = getSelectorModel();
        for (StyleRule r : s.getStyleRules()) {
            if (r.getSelectorGroup().matches(selectorModel, elem)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a localized help text.
     *
     * @return the help text
     */
    String getHelpText();
}

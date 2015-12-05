/* @(#)StyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.css.StyleOrigin;
import org.jhotdraw.css.ast.Stylesheet;
import org.jhotdraw.draw.Figure;

/**
 * StyleManager.
 *
 * @author Werner Randelshofer
 * @param <E> the element type that can be styled by this style manager
 */
public interface StyleManager<E> {

    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin the style origin
     * @param url the stylesheet url
     */
    default void addStylesheet(StyleOrigin origin, URI url) {
        addStylesheet(origin,null,url);
    }
    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin the style origin
     * @param documentHome the document Home url
     * @param url the stylesheet url
     */
    void addStylesheet(StyleOrigin origin, URI documentHome, URI url) ;

    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin the style origin
     * @param stylesheet the stylesheet given as a literal string
     */
    void addStylesheet(StyleOrigin origin, String stylesheet) ;

    /**
     * Removes all stylesheets with the specified origin.
     *
     * @param origin the style origin
     */
    void clearStylesheets(StyleOrigin origin);
  
    /** Sets a list of stylesheets with the specified origin.
     * 
     * @param <T> type of the list elements
     * @param origin the origin
     * @param stylesheets list elements can be Strings or URIs.
     */
    default <T> void setStylesheets(StyleOrigin origin, List<T> stylesheets) {
        setStylesheets(origin,null,stylesheets);
    }
    /** Sets a list of stylesheets with the specified origin.
     * 
     * @param <T> type of the list elements
     * @param origin the origin
     * @param documentHome the document home
     * @param stylesheets list elements can be Strings or URIs.
     */
    <T> void setStylesheets(StyleOrigin origin, URI documentHome, List<T> stylesheets);

    /** Applies styles to the specified element. 
     * @param e The element
     */
    default void applyStylesTo(E e) {
        applyStylesTo(e, Collections.emptyMap());
    }
    
    /** Applies styles to the specified element and adds the given
     * pseudo class states to the element during evaluation.
     * 
     * @param e The element
     * @param pseudoClassStates pseudo class states given to the specified elements
     */
    public void applyStylesTo(E e, Map<String,Set<E>> pseudoClassStates);
    
    /** Returns the selector model of the style manager.
     * @return the selector model
     */
    public SelectorModel<E> getSelectorModel();

    public void applyStylesheetTo(StyleOrigin styleOrigin, Stylesheet s, Figure f, HashMap<String, Set<Figure>> pseudoStyles);
}

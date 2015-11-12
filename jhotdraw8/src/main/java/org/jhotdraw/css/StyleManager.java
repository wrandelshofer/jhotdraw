/* @(#)StyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import javafx.css.StyleOrigin;
import org.jhotdraw.draw.AbstractFigure;

/**
 * StyleManager.
 *
 * @author Werner Randelshofer
 */
public interface StyleManager<E> {

    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin the style origin
     * @param url the stylesheet url
     * @throws java.io.IOException if the stylesheet can not be loaded and
     * parsed properly
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
     * @throws java.io.IOException if the stylesheet can not be loaded and
     * parsed properly
     */
    void addStylesheet(StyleOrigin origin, URI documentHome, URI url) ;

    /**
     * Adds a stylesheet with the specified origin.
     *
     * @param origin the style origin
     * @param stylesheet the stylesheet given as a literal string
     * @throws java.io.IOException if the stylesheet can not be parsed properly
     */
    void addStylesheet(StyleOrigin origin, String stylesheet) ;

    /**
     * Removes all stylesheets with the specified origin.
     *
     * @param origin the style origin
     */
    void clearStylesheets(StyleOrigin origin);
  
    /** Returns a list of stylesheets with the specified origin.
     * 
     * @param origin
     * @param list list entries can be Strings or URIs.
     */
    default <T> void setStylesheets(StyleOrigin origin, List<T> stylesheets) {
        setStylesheets(origin,null,stylesheets);
    }
    /** Returns a list of stylesheets with the specified origin.
     * 
     * @param origin
     * @param list list entries can be Strings or URIs.
     */
    <T> void setStylesheets(StyleOrigin origin, URI documentHome, List<T> stylesheets);

    /** Applies styles to the specified element. */
    public void applyStylesTo(E e);
}

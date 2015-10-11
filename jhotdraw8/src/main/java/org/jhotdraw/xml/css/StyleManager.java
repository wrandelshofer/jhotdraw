/* @(#)StyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.xml.css;

import java.io.IOException;
import java.net.URL;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;
import javafx.css.StyleOrigin;
import org.jhotdraw.xml.css.ast.Stylesheet;

/**
 * StyleManager.
 *
 * @author Werner Randelshofer
 */
public interface StyleManager {

    // ----
    // property names
    // ----
    /**
     * The name of the user agents stylesheet property.
     */
    public final static String USER_AGENT_STYLESHEETS_PROPERTY = "userAgentStylesheets";
    /**
     * The name of the author stylesheets property.
     */
    public final static String AUTHOR_STYLESHEETS_PROPERTY = "authorStylesheets";
    // ---
    // properties
    // ---

    ReadOnlyListProperty<Stylesheet> userAgentStylesheetsProperty();

    ReadOnlyListProperty<Stylesheet> authorStylesheetsProperty();

    // ---
    // convenience methods
    // ---
    default ObservableList<Stylesheet> getUserAgentStylesheets() {
        return userAgentStylesheetsProperty().get();
    }

    default ObservableList<Stylesheet> getAuthorStylesheets() {
        return authorStylesheetsProperty().get();
    }

    /**
     * Adds a stylesheet to the specified origin.
     *
     * @param origin the style origin
     * @param stylesheetUrl the stylesheet url
     * @throws java.io.IOException if the stylesheet can not be loaded and
     * parsed properly
     */
    default void addStylesheet(StyleOrigin origin, URL stylesheetUrl) throws IOException {
        Stylesheet sh = new CssParser().parseStylesheet(stylesheetUrl);
        switch (origin) {
        case USER_AGENT:
            getUserAgentStylesheets().add(sh);
            break;
        case AUTHOR:
            getAuthorStylesheets().add(sh);
            break;
        default:
            throw new IllegalArgumentException("Illegal origin:" + origin);
        }
    }

    /**
     * Adds a stylesheet to the specified origin.
     *
     * @param origin the style origin
     * @param str the stylesheet as a literal string
     * @throws java.io.IOException if the stylesheet can not be parsed properly
     */
    default void addStylesheet(StyleOrigin origin, String str) throws IOException {
        Stylesheet sh = new CssParser().parseStylesheet(str);
        switch (origin) {
        case USER_AGENT:
            getUserAgentStylesheets().add(sh);
            break;
        case AUTHOR:
            getAuthorStylesheets().add(sh);
            break;
        default:
            throw new IllegalArgumentException("Illegal origin:" + origin);
        }
    }
}

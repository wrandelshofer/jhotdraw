/* @(#)StyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;
import javafx.css.StyleOrigin;
import org.jhotdraw.css.ast.Stylesheet;

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
    public final static String USER_AGENT_STYLESHEET_URL_OR_STRING_PROPERTY = "userAgentStylesheetUrlOrString";
    /**
     * The name of the author stylesheets property.
     */
    public final static String AUTHOR_STYLESHEET_URL_OR_STRING_PROPERTY = "authorStylesheetUrlOrString";
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
    ReadOnlyListProperty<Object> userAgentStylesheetUrlOrStringProperty();

    ReadOnlyListProperty<Object> authorStylesheetUrlOrStringProperty();

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

    default ObservableList<Object> getUserAgentStylesheetUrlOrString() {
        return userAgentStylesheetUrlOrStringProperty().get();
    }

    default ObservableList<Object> getAuthorStylesheetUrlOrString() {
        return authorStylesheetUrlOrStringProperty().get();
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
                getUserAgentStylesheetUrlOrString().add(stylesheetUrl);
                getUserAgentStylesheets().add(sh);
                break;
            case AUTHOR:
                getAuthorStylesheetUrlOrString().add(stylesheetUrl);
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
                getUserAgentStylesheetUrlOrString().add(str);
                getUserAgentStylesheets().add(sh);
                break;
            case AUTHOR:
                getAuthorStylesheetUrlOrString().add(str);
                getAuthorStylesheets().add(sh);
                break;
            default:
                throw new IllegalArgumentException("Illegal origin:" + origin);
        }
    }

    /**
     * Removes all stylesheets of the specified origin.
     *
     * @param origin the style origin
     */
    default void clearStylesheets(StyleOrigin origin) {
        switch (origin) {
            case USER_AGENT:
                getUserAgentStylesheetUrlOrString().clear();
                getUserAgentStylesheets().clear();
                break;
            case AUTHOR:
                getAuthorStylesheetUrlOrString().clear();
                getAuthorStylesheets().clear();
                break;
            default:
                throw new IllegalArgumentException("Illegal origin:" + origin);
        }
    }

    default void updateStylesheets(StyleOrigin origin, URI documentHome, List<URI> urlOrString) throws IOException {
        if (documentHome == null) {
            documentHome = new File(System.getProperty("user.home")).toURI();
        }

        ObservableList<Object> myUrlOrString;
        ObservableList<Stylesheet> myStylesheet;
        switch (origin) {
            case USER_AGENT:
                myUrlOrString = getUserAgentStylesheetUrlOrString();
                myStylesheet = getUserAgentStylesheets();
                break;
            case AUTHOR:
                myUrlOrString = getAuthorStylesheetUrlOrString();
                myStylesheet = getAuthorStylesheets();
                break;
            default:
                throw new IllegalArgumentException("Illegal origin:" + origin);
        }

        myStylesheet.clear();
        if (urlOrString == null) {
            return;
        }

        // update stylesheets
        for (int i = 0, n = urlOrString.size(); i < n; i++) {
            Object item = urlOrString.get(i);
            Stylesheet sh;
            try {
                // FIXME don't perform IO in user event thread!
                if (item instanceof URI) {
                    sh = new CssParser().parseStylesheet(documentHome.resolve((URI) item));
                } else {
                    sh = new CssParser().parseStylesheet((String) item);
                }
            } catch (IOException e) {
                System.err.println("Warning: Could not load stylesheet \"" + item+"\". "+e.getMessage());
                sh = null;
            }
            if (sh != null) {
                myUrlOrString.add(i, item);
                myStylesheet.add(i, sh);
            }
        }
    }
}

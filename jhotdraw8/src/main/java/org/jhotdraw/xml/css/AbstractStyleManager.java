/*
 * @(#)AbstractStyleManager.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */

package org.jhotdraw.xml.css;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import org.jhotdraw.xml.css.ast.Stylesheet;

/**
 * AbstractStyleManager.
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class AbstractStyleManager implements StyleManager {

    /**
     * Holds the user agent stylesheets.
     */
    protected final ReadOnlyListProperty<Stylesheet>userAgentStylesheets//
            = new ReadOnlyListWrapper<Stylesheet>(//
                    this, USER_AGENT_STYLESHEETS_PROPERTY, //
                    FXCollections.observableArrayList()).getReadOnlyProperty();
    /**
     * Holds the author stylesheets.
     */
    protected final ReadOnlyListProperty<Stylesheet> authorStylesheets//
            = new ReadOnlyListWrapper<Stylesheet>(//
                    this, AUTHOR_STYLESHEETS_PROPERTY, //
                    FXCollections.observableArrayList()).getReadOnlyProperty();

    @Override
    public ReadOnlyListProperty<Stylesheet> userAgentStylesheetsProperty() {
        return userAgentStylesheets;
    }

    @Override
    public ReadOnlyListProperty<Stylesheet> authorStylesheetsProperty() {
        return authorStylesheets;
    }

   
}

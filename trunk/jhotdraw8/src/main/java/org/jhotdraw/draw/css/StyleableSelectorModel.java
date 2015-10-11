/*
 * @(#)StyleableSelectorModel.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.draw.css;

import javafx.css.PseudoClass;
import javafx.css.Styleable;
import org.jhotdraw.xml.css.SelectorModel;

/**
 * StyleableSelectorModel.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class StyleableSelectorModel implements SelectorModel<Styleable> {

    @Override
    public boolean hasStyleId(Styleable element, String id) {
        return id.equals(element.getId());
    }

    @Override
    public boolean hasStyleType(Styleable element, String type) {
        return type.equals(element.getTypeSelector());
    }

    @Override
    public boolean hasStyleClass(Styleable element, String clazz) {
        return element.getStyleClass().contains(clazz);
    }

    @Override
    public boolean hasStylePseudoClass(Styleable element, String pseudoClass) {
        return element.getPseudoClassStates().contains(PseudoClass.getPseudoClass(pseudoClass));
    }

    @Override
    public Styleable getParent(Styleable element) {
        return element.getStyleableParent();
    }

    @Override
    public Styleable getPreviousSibling(Styleable element) {
        return null;
    }

}

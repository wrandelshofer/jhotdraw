/*
 * @(#)TypeSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */

package org.jhotdraw.xml.css.ast;

import org.jhotdraw.xml.css.SelectorModel;

/**
 * A "class selector" matches an element based on its type.
 * 
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class TypeSelector extends  SimpleSelector {

        private final String type;

        public TypeSelector(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Type:" + type;
        }

        @Override
        public <T> T match(SelectorModel<T> model, T element) {
            return (element != null && model.hasStyleType(element, type)) //
                    ? element : null;
        }
    }

  

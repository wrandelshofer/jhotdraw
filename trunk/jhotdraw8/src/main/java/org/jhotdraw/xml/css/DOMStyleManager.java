/* @(#)DOMStyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.xml.css;

import java.util.*;
import org.jhotdraw.util.ReversedList;
import org.w3c.dom.Element;

/**
 * DOMStyleManager applies styling Rules to an XML DOM. This class
 * supportsorg.w3c.dom.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DOMStyleManager implements StyleManager {

    private java.util.List<DOMRule> rules;

    public DOMStyleManager() {
        rules = new ArrayList<DOMRule>();
    }

    public void addRule(String selector, Map<String, String> properties) {
        DOMRule rule = new DOMRule(selector, properties);
        rules.add(rule);
    }

    public void applyStylesTo(Element elem) {
        for (DOMRule rule : rules) {
            if (rule.matches(elem)) {
                rule.apply(elem);
            }
        }
    }

    public void clear() {
        rules.clear();
    }
}

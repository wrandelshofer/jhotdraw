/* @(#)StyleableStyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.css;

import com.sun.javafx.css.parser.CSSParser;
import java.util.ArrayList;
import java.util.Map;
import javafx.css.Styleable;
import org.jhotdraw.xml.css.DOMRule;
import org.jhotdraw.xml.css.DOMStyleManager;
import org.jhotdraw.xml.css.StyleManager;
import org.w3c.dom.Element;

/**
 * StyleableStyleManager.
 * @author Werner Randelshofer
 */
public class StyleableStyleManager implements StyleManager {
    private java.util.List<StyleableRule> rules;
CSSParser parser;
    public StyleableStyleManager() {
        rules = new ArrayList<StyleableRule>();
    }

    public void addRule(String selector, Map<String, String> properties) {
        StyleableRule rule = new StyleableRule(selector, properties);
        rules.add(rule);
    }

    public void applyStylesTo(Styleable elem) {
        for (StyleableRule rule : rules) {
            if (rule.matches(elem)) {
                rule.apply(elem);
            }
        }
    }

    public void clear() {
        rules.clear();
    }

}

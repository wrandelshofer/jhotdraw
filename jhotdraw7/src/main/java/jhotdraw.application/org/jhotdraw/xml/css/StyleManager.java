/* @(#)StyleManager.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 *
 * Original code taken from article "Swing and CSS" by Joshua Marinacci 10/14/2003
 * http://today.java.net/pub/a/today/2003/10/14/swingcss.html
 */

package org.jhotdraw.xml.css;

import org.w3c.dom.Element;

import java.util.ArrayList;
/**
 * StyleManager applies styling Rules to an XML DOM.
 * This class supports net.n3.nanoxml as well as org.w3c.dom.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StyleManager {
    protected java.util.List<CSSRule> rules;
    
    public StyleManager() {
        rules = new ArrayList<CSSRule>();
    }
    
    public void add(CSSRule rule) {
        rules.add(rule);
    }
    
    public void applyStylesTo(Element elem) {
        for (CSSRule rule : rules) {
            if(rule.matches(elem)) {
                rule.apply(elem);
            }
        }
    }


    public void clear() {
        rules.clear();
    }
}

package org.jhotdraw.nanoxml.css;

import net.n3.nanoxml.IXMLElement;
import org.jhotdraw.util.ReversedList;
import org.jhotdraw.xml.css.CSSRule;
import org.jhotdraw.xml.css.StyleManager;

public class NanoXMLStyleManager extends StyleManager {

    @Override
    public void add(CSSRule rule) {
        super.add(new NanoXMLCSSRule(rule));
    }

    public void applyStylesTo(IXMLElement elem) {
        for (CSSRule rule : new ReversedList<CSSRule>(rules)) {
            NanoXMLCSSRule r=(NanoXMLCSSRule)rule;
            if(r.matches(elem)) {
                //System.out.println("StyleManager applying "+rule+" to "+elem);
                r.apply(elem);
            }
        }
    }
}

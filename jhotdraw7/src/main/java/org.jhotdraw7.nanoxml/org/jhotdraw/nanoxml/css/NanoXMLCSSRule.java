package org.jhotdraw.nanoxml.css;

import net.n3.nanoxml.IXMLElement;
import org.jhotdraw.xml.css.CSSRule;

import java.util.Map;

public class NanoXMLCSSRule extends CSSRule {

    public NanoXMLCSSRule(CSSRule that) {
        super(that);
    }

    public NanoXMLCSSRule(String name, String value) {
        super(name, value);
    }

    public NanoXMLCSSRule(String selector, String propertyName, String propertyValue) {
        super(selector, propertyName, propertyValue);
    }

    public NanoXMLCSSRule(String selector, Map<String, String> properties) {
        super(selector, properties);
    }

    public boolean matches(IXMLElement elem) {
        boolean isMatch = false;
        switch (type) {
            case ALL :
                isMatch = true;
                break;
            case ELEMENT_NAME : {
                String name = elem.getName();
                isMatch = name != null && name.equals(selector);
                break;
            }
            case CLASS_ATTRIBUTE : {
                String value = elem.getAttribute("class",null);
                if (value != null) {
                    String[] clazzes = value.split(" ");
                    for (String clazz : clazzes) {
                        if (clazz.equals(selector)) {
                            isMatch = true;
                            break;
                        }
                    }
                }
                break;
            }
            case ID_ATTRIBUTE : {
                String name = elem.getAttribute("id",null);
                isMatch = name != null && name.equals(selector);
                break;
            }
        }
        return isMatch;
    }
    public void apply(IXMLElement elem) {
        for (Map.Entry<String,String> property : properties.entrySet()) {
            if (! elem.hasAttribute(property.getKey())) {
                elem.setAttribute(property.getKey(), property.getValue());
            }
        }
    }
}

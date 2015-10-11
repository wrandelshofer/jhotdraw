/*@(#)DocumentSelectorModel.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.xml.css;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * {@code DocumentSelectorModel} provides an API for CSS
 * {@link org.jhotdraw.xml.css.ast.SelectorGroup}'s.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DocumentSelectorModel implements SelectorModel<Element> {

    @Override
    public boolean hasStyleId(Element elem, String id) {
        String value = elem.getAttribute("id");
        return value != null && value.equals(id);
    }

    @Override
    public boolean hasStyleType(Element elem, String type) {
        String value = elem.getNodeName();
        return value != null && value.equals(type);
    }

    @Override
    public boolean hasStyleClass(Element elem, String clazz) {
        String value = elem.getAttribute("class");
        if (value == null) {
            return false;
        }
        String[] clazzes = value.split(" +");
        for (String c : clazzes) {
            if (c.equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasStylePseudoClass(Element element, String pseudoClass) {
        return false;
    }

    @Override
    public Element getParent(Element elem) {
        Node n = elem.getParentNode();
        while (n != null && !(n instanceof Element)) {
            n = n.getParentNode();
        }
        return (Element) n;
    }

    @Override
    public Element getPreviousSibling(Element element) {
        Node n = element.getPreviousSibling();
        while (n != null && !(n instanceof Element)) {
            n = n.getPreviousSibling();
        }
        return (Element) n;
    }

}

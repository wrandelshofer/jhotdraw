/*@(#)DocumentSelectorModel.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * {@code DocumentSelectorModel} provides an API for CSS
 * {@link org.jhotdraw.css.ast.SelectorGroup}'s.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DocumentSelectorModel implements SelectorModel<Element> {

    @Override
    public boolean hasId(Element elem, String id) {
        String value = elem.getAttribute("id");
        return value != null && value.equals(id);
    }

    @Override
    public boolean hasType(Element elem, String type) {
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
    public boolean hasPseudoClass(Element element, String pseudoClass) {
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

    @Override
    public boolean hasAttribute(Element element, String attributeName) {
        return element.hasAttribute(attributeName);
    }

    @Override
    public boolean attributeValueEquals(Element element, String attributeName, String attributeValue) {
        String actualValue = element.getAttribute(attributeName);
        return actualValue != null && actualValue.equals(attributeValue);
    }

    @Override
    public boolean attributeValueStartsWith(Element element, String attributeName, String substring) {
        String actualValue = element.getAttribute(attributeName);
        return actualValue != null && (actualValue.startsWith(substring));
    }

    @Override
    public boolean attributeValueEndsWith(Element element, String attributeName, String substring) {
        String actualValue = element.getAttribute(attributeName);
        return actualValue != null && (actualValue.endsWith(substring));
    }

    @Override
    public boolean attributeValueContains(Element element, String attributeName, String substring) {
        String actualValue = element.getAttribute(attributeName);
        return actualValue != null && (actualValue.contains(substring));
    }

    @Override
    public boolean attributeValueContainsWord(Element element, String attributeName, String word) {
        String value = element.getAttribute(attributeName);
        if (value != null) {
            String[] words = value.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                if (word.equals(words[i])) {
                    return true;
                }
            }
        }
        return false;
    }

}

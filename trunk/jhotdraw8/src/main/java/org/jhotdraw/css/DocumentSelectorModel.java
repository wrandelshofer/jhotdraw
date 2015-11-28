/*@(#)DocumentSelectorModel.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * {@code DocumentSelectorModel} provides an API for CSS
 * {@link org.jhotdraw.css.ast.SelectorGroup}'s.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DocumentSelectorModel implements SelectorModel<Element> {

    private final MapProperty<String, Set<Element>> additionalPseudoClassStates = new SimpleMapProperty<>();

    public MapProperty<String, Set<Element>> additionalPseudoClassStatesProperty() {
        return additionalPseudoClassStates;
    }

    @Override
    public boolean hasId(Element elem, String id) {
        String value = elem.getAttribute("id");
        return value != null && value.equals(id);
    }
    @Override
    public String getId(Element elem) {
        return elem.getAttribute("id");
    }

    @Override
    public boolean hasType(Element elem, String type) {
        String value = elem.getNodeName();
        return value != null && value.equals(type);
    }
    @Override
    public String getType(Element elem) {
        return elem.getNodeName();
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
    public Set<String> getStyleClasses(Element elem) {
        String value = elem.getAttribute("class");
        if (value == null) {
            return Collections.emptySet();
        }
        String[] clazzes = value.split(" +");
        return new HashSet<>(Arrays.asList(clazzes));
    }

    /**
     * Supports the following pseudo classes:
     * <ul>
     * <li>root</li>
     * <li>nth-child(odd)</li>
     * <li>nth-child(even)</li>
     * <li>first-child</li>
     * <li>last-child</li>
     * </ul>
     * Does not support the following pseudo classes:
     * <ul>
     * <li>nth-child(2n+1)</li>
     * <li>nth-last-child(2n+1)</li>
     * <li>nth-last-child(odd)</li>
     * <li>nth-last-child(even)</li>
     * <li>nth-of-type(2n+1)</li>
     * <li>nth-of-type(even)</li>
     * <li>nth-of-type(odd)</li>
     * <li>nth-last-of-type(2n+1)</li>
     * <li>nth-last-of-type(even)</li>
     * <li>nth-last-of-type(odd)</li>
     * <li>first-of-type()</li>
     * <li>last-of-type()</li>
     * <li>only-child()</li>
     * <li>only-of-type()</li>
     * <li>empty</li>
     * <li>not(...)</li>
     * </ul>
     *
     * @param element the element
     * @param pseudoClass the desired pseudo clas
     * @return true if the element has the pseudo class
     */
    @Override
    public boolean hasPseudoClass(Element element, String pseudoClass) {
        switch (pseudoClass) {
            case "root":
                return element.getOwnerDocument() != null
                        && element.getOwnerDocument().getDocumentElement() == element;
            case "nth-child(even)": {
                int i = getChildIndex(element);
                return i != -1 && i % 2 == 0;
            }
            case "nth-child(odd)": {
                int i = getChildIndex(element);
                return i != -1 && i % 2 == 1;
            }
            case "first-child": {
                return isFirstChild(element);
            }
            case "last-child": {
                return isLastChild(element);
            }
            default:
                return false;
        }
    }

    private int getChildIndex(Element element) {
        if (element.getParentNode() != null) {
            NodeList list = element.getParentNode().getChildNodes();

            for (int i = 0, j = 0, n = list.getLength(); i < n; i++) {
                if (list.item(i) == element) {
                    return j;
                }
                if (list.item(i) instanceof Element) {
                    j++;
                }
            }
        }
        return -1;
    }

    private boolean isFirstChild(Element element) {
        if (element.getParentNode() != null) {
            NodeList list = element.getParentNode().getChildNodes();

            for (int i = 0, n = list.getLength(); i < n; i++) {
                if (list.item(i) == element) {
                    return true;
                }
                if (list.item(i) instanceof Element) {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean isLastChild(Element element) {
        if (element.getParentNode() != null) {
            NodeList list = element.getParentNode().getChildNodes();

            for (int i = list.getLength() - 1; i >= 0; i--) {
                if (list.item(i) == element) {
                    return true;
                }
                if (list.item(i) instanceof Element) {
                    return false;
                }
            }
        }
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
        // FIXME we need the XML schema to return the correct result
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
    public String getAttributeValue(Element element, String attributeName) {
        return element.getAttribute(attributeName);
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

    @Override
    public Set<String> getAttributeNames(Element element) {
        // FIXME we need the XML schema to return the correct result
        Set<String> attr = new HashSet<String>();
        NamedNodeMap nnm = element.getAttributes();
        for (int i = 0, n = nnm.getLength(); i < n; i++) {
            Node node = nnm.item(i);
            attr.add(node.getLocalName());
        }
        return attr;
    }

}

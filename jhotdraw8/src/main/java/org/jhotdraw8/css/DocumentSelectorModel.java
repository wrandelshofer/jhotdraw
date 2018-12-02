/*@(#)DocumentSelectorModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.css.StyleOrigin;
import org.jhotdraw8.collection.ReadOnlyList;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * {@code DocumentSelectorModel} provides an API for CSS
 * {@link org.jhotdraw8.css.ast.SelectorGroup}'s.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DocumentSelectorModel implements SelectorModel<Element> {

    private final MapProperty<String, Set<Element>> additionalPseudoClassStates = new SimpleMapProperty<>();

    @Nonnull
    public MapProperty<String, Set<Element>> additionalPseudoClassStatesProperty() {
        return additionalPseudoClassStates;
    }

    @Override
    public String getAttributeAsString(@Nonnull Element elem, StyleOrigin origin, @Nullable String namespace, @Nonnull String name) {
        return getAttributeAsString(elem, namespace,name);
    }

    @Nullable
    @Override
    public List<CssToken> getAttribute(@Nonnull Element element, @Nullable StyleOrigin origin, @Nullable String namespace, @Nonnull String name) {
        String str=getAttributeAsString(element,origin,namespace,name);
        if (str==null)return null;
        try {
            return new StreamCssTokenizer(str).toTokenList();
        } catch (IOException e) {
            throw new RuntimeException("unexpected exception",e);
        }
    }

    @Override
    public boolean hasId(@Nonnull Element elem, @Nonnull String id) {
        String value = elem.getAttribute("id");
        return value != null && value.equals(id);
    }

    @Override
    public String getId(@Nonnull Element elem) {
        return elem.getAttribute("id");
    }

    @Override
    public boolean hasType(@Nonnull Element elem, @Nullable String namespace, @Nonnull String type) {
        String value = elem.getNodeName();
        return value != null && value.equals(type);
    }

    @Override
    public String getType(@Nonnull Element elem) {
        return elem.getNodeName();
    }

    @Override
    public boolean hasStyleClass(@Nonnull Element elem, @Nonnull String clazz) {
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

    @Nonnull
    @Override
    public Set<String> getStyleClasses(@Nonnull Element elem) {
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
     * @param element     the element
     * @param pseudoClass the desired pseudo clas
     * @return true if the element has the pseudo class
     */
    @Override
    public boolean hasPseudoClass(@Nonnull Element element, @Nonnull String pseudoClass) {
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

    @Nonnull
    @Override
    public Element getParent(@Nonnull Element elem) {
        Node n = elem.getParentNode();
        while (n != null && !(n instanceof Element)) {
            n = n.getParentNode();
        }
        return (Element) n;
    }

    @Nonnull
    @Override
    public Element getPreviousSibling(@Nonnull Element element) {
        Node n = element.getPreviousSibling();
        while (n != null && !(n instanceof Element)) {
            n = n.getPreviousSibling();
        }
        return (Element) n;
    }

    @Override
    public boolean hasAttribute(@Nonnull Element element, @Nullable String namespace, @Nonnull String attributeName) {
        // FIXME we need the XML schema to return the correct result
        return element.hasAttribute(attributeName);
    }

    @Override
    public boolean attributeValueStartsWith(@Nonnull Element element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        String actualValue = element.getAttribute(attributeName);
        return actualValue != null && (actualValue.startsWith(substring));
    }

    @Override
    public String getAttributeAsString(@Nonnull Element element,@Nullable String namespace,  @Nonnull String attributeName) {
        return element.getAttribute(attributeName);
    }

    @Nonnull
    @Override
    public Set<QualifiedName> getAttributeNames(@Nonnull Element element) {
        Set<QualifiedName> attr = new LinkedHashSet<>();
        NamedNodeMap nnm = element.getAttributes();
        for (int i = 0, n = nnm.getLength(); i < n; i++) {
            Node node = nnm.item(i);
            attr.add(new QualifiedName(node.getNamespaceURI(),node.getLocalName()));
        }
        return attr;
    }

    @Nonnull
    @Override
    public Set<QualifiedName> getComposedAttributeNames(@Nonnull Element element) {
        return getAttributeNames(element);
    }

    @Nonnull
    @Override
    public Set<QualifiedName> getDecomposedAttributeNames(@Nonnull Element element) {
        return getAttributeNames(element);
    }

    @Override
    public void setAttribute(@Nonnull Element element, @Nonnull StyleOrigin origin, @Nullable String namespace, @Nonnull String name, @Nullable ReadOnlyList<CssToken> value) {
        StringBuilder buf = new StringBuilder();
        for (CssToken t : value) buf.append(t.fromToken());
        String value1 = buf.toString();
        switch (origin) {
            case USER:
            case USER_AGENT:
            case INLINE:
            case AUTHOR:
                if (value1 == null) {
                    element.removeAttribute(name);
                } else {
                    element.setAttribute(name, value1);
                }
                break;
            default:
                throw new UnsupportedOperationException("unsupported origin:" + origin);
        }
    }
}

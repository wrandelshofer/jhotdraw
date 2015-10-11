/* @(#)DOMRule.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;

/**
 * Rule matches on a CSS selector.
 * <p>
 * Supported selectors:
 * <ul>
 * <li><code>*</code> matches all objects.</li>
 * <li><code>name</code> matches an element name.</li>
 * <li><code>.name</code> matches the value of the attribute "class".</li>
 * <li><code>#name</code> matches the value of the attribute "id".</li>
 * </ul>
 * XXX should be an inner class of StyleableStyleManager.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
class StyleableRule {

    private String selector;

    private static enum SelectorType {

        ALL, ELEMENT_NAME, CLASS_ATTRIBUTE, ID_ATTRIBUTE
    }
    private SelectorType type;

    private static class PropertyValue {

        String unparsedValue;
        Object convertedValue;

        public PropertyValue(String unparsedValue) {
            this.unparsedValue = unparsedValue;
        }

    }

    protected Map<String, PropertyValue> properties;

    public StyleableRule(String name, String unparsedValue) {
        properties = new HashMap<>();
        properties.put(name, new PropertyValue(unparsedValue));
    }

    public StyleableRule(String selector, String propertyName, String unparsedValue) {
        setSelector(selector);
        properties = new HashMap<>();
        properties.put(propertyName, new PropertyValue(unparsedValue));
    }

    public StyleableRule(String selector, Map<String, String> unparsedProperties) {
        setSelector(selector);
        properties = new HashMap<>();
        for (Map.Entry<String, String> entry : unparsedProperties.entrySet()) {
            properties.put(entry.getKey(), new PropertyValue(entry.getValue()));
        }
    }

    public void setSelector(String selector) {
        switch (selector.charAt(0)) {
            case '*':
                type = SelectorType.ALL;
                break;
            case '.':
                type = SelectorType.CLASS_ATTRIBUTE;
                break;
            case '#':
                type = SelectorType.ID_ATTRIBUTE;
                break;
            default:
                type = SelectorType.ELEMENT_NAME;
                break;
        }
        this.selector = (type == SelectorType.ELEMENT_NAME) ? selector : selector.substring(1);
    }

    public boolean matches(Styleable elem) {
        boolean isMatch = false;
        switch (type) {
            case ALL:
                isMatch = true;
                break;
            case ELEMENT_NAME: {
                String name = elem.getTypeSelector();
                isMatch = name.equals(selector);
                break;
            }
            case CLASS_ATTRIBUTE: {
                List<String> clazzes = elem.getStyleClass();
                for (String clazz : clazzes) {
                    if (clazz.equals(selector)) {
                        isMatch = true;
                        break;
                    }
                }
                break;
            }
            case ID_ATTRIBUTE: {
                String name = elem.getId();
                isMatch = name != null && name.equals(selector);
                break;
            }
        }
        return isMatch;
    }

    public void apply(Styleable elem) {
        List<CssMetaData<? extends Styleable, ?>> list = elem.getCssMetaData();
        HashMap<String, CssMetaData<? extends Styleable, ?>> map = new HashMap<>();
        for (CssMetaData<? extends Styleable, ?> meta : list) {
            map.put(meta.getProperty(), meta);
        }

        for (Map.Entry<String, PropertyValue> entry : properties.entrySet()) {
            @SuppressWarnings("unchecked")
            CssMetaData<Styleable, Object> meta = (CssMetaData<Styleable, Object>) map.get(entry.getKey());

            if (meta != null && meta.isSettable(elem)) {
                StyleableProperty<Object> styleableProperty = meta.getStyleableProperty(elem);
                PropertyValue pv = entry.getValue();
                if (pv.convertedValue == null) {
                    // only convert once and then foolishly assume no other object will request a different conversion
                    @SuppressWarnings("unchecked")
                    StyleConverter<String, Object> converter = (StyleConverter<String, Object>) meta.getConverter();
                    ParsedValueImpl<String, Object> parsedValue = new ParsedValueImpl<>(pv.unparsedValue, null);
                    pv.convertedValue = converter.convert(parsedValue, null);
                }
//                System.out.println("StyleableRule. implement " + entry.getKey() + ":" + pv.unparsedValue + " c:" + pv.convertedValue);
                styleableProperty.applyStyle(StyleOrigin.AUTHOR, pv.convertedValue);

            }
        }
    }

    @Override
    public String toString() {
        return "CSSRule[" + selector + properties + "]";
    }

    private static class ParsedValueImpl<V, T> extends ParsedValue<V, T> {

        public ParsedValueImpl(V value, StyleConverter<V, T> converter) {
            super(value, converter);
        }

    }
}

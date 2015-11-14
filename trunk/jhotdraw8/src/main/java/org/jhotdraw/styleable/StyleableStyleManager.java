/* @(#)StyleableStyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.styleable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw.css.AbstractStyleManager;
import org.jhotdraw.css.AbstractStyleManager;
import org.jhotdraw.css.CssParser;
import org.jhotdraw.css.CssParser;
import org.jhotdraw.css.SelectorModel;
import org.jhotdraw.css.ast.Declaration;
import org.jhotdraw.css.ast.StyleRule;
import org.jhotdraw.css.ast.Stylesheet;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.styleable.StyleableSelectorModel;

/**
 * StyleableStyleManager.
 *
 * @author Werner Randelshofer
 */
public class StyleableStyleManager extends AbstractStyleManager<Styleable> {

    private final StyleableSelectorModel selectorModel = new StyleableSelectorModel();

    private final CssParser parser = new CssParser();

    private final WeakHashMap<Declaration, Object> convertedValues = new WeakHashMap<>();

    public StyleableStyleManager() {
    }

    /**
     * Applies the stylesheets to the specified element.
     *
     * @param elem The element
     */
    @Override
    public void applyStylesTo(Styleable elem, Map<String, Set<Styleable>> pseudoClassStates) {
        applyStylesTo(null, elem);
    }

    /**
     * Applies the stylesheets to the specified element.
     *
     * @param origin The style origin. Specify null to apply all origins.
     * @param elem The element
     */
    public void applyStylesTo(StyleOrigin origin, Styleable elem) {
        List<CssMetaData<? extends Styleable, ?>> metaList = elem.getCssMetaData();
        HashMap<String, CssMetaData<? extends Styleable, ?>> metaMap = new HashMap<>();
        for (CssMetaData<? extends Styleable, ?> m : metaList) {
            metaMap.put(m.getProperty(), m);
        }

        // user agent stylesheets can not override element attributes
        if (origin == null || origin == StyleOrigin.USER_AGENT) {
            for (Entry e : getUserAgentStylesheets()) {
                Stylesheet s = e.getStylesheet();
                if (s != null) {
                    applyStylesTo(StyleOrigin.USER_AGENT, s, metaMap, elem);
                }
            }
        }

        // author stylesheet override user agent stylesheets and element attributes
        if (origin == null || origin == StyleOrigin.AUTHOR) {
            for (Entry e : getAuthorStylesheets()) {
                Stylesheet s = e.getStylesheet();
                if (s != null) {
                    applyStylesTo(StyleOrigin.AUTHOR, s, metaMap, elem);
                }
            }
        }
        // inline stylesheets override user agent stylesheets, element attributes and author stylesheets
        if (origin == null || origin == StyleOrigin.INLINE) {
            for (Entry e : getAuthorStylesheets()) {
                Stylesheet s = e.getStylesheet();
                if (s != null) {
                    applyStylesTo(StyleOrigin.INLINE, s, metaMap, elem);
                }
            }
        }

        // inline styles can override all other values
        if (origin == null || origin == StyleOrigin.INLINE) {
            applyInlineStylesTo(metaMap, elem);
        }

        //
        metaMap.clear();
    }

    private void applyStylesTo(StyleOrigin origin, Stylesheet s, HashMap<String, CssMetaData<? extends Styleable, ?>> metaMap, Styleable elem) {
        for (StyleRule r : s.getRulesets()) {
            if (r.getSelectorGroup().matches(selectorModel, elem)) {
                for (Declaration d : r.getDeclarations()) {
                    @SuppressWarnings("unchecked")
                    CssMetaData<Styleable, ?> m = (CssMetaData<Styleable, ?>) metaMap.get(d.getProperty());
                    if (m != null && m.isSettable(elem)) {
                        if (!convertedValues.containsKey(d)) {
                            @SuppressWarnings("unchecked")
                            StyleConverter<String, Object> converter = (StyleConverter<String, Object>) m.getConverter();
                            ParsedValueImpl<String, Object> parsedValue = new ParsedValueImpl<>(d.getTermsAsString(), null);
                            convertedValues.put(d, converter.convert(parsedValue, null));
                        }
                        Object convertedValue = convertedValues.get(d);
                        @SuppressWarnings("unchecked")
                        StyleableProperty<Object> styleableProperty = (StyleableProperty<Object>) m.getStyleableProperty(elem);
                        styleableProperty.applyStyle(origin, convertedValue);
                    }
                }
            }
        }
    }

    private void applyInlineStylesTo(HashMap<String, CssMetaData<? extends Styleable, ?>> metaMap, Styleable elem) {
        // inline styles can override all other values
        String style = elem.getStyle();
        if (style != null) {
            try {
                for (Declaration d : parser.parseDeclarationList(style)) {
                    @SuppressWarnings("unchecked")
                    CssMetaData<Styleable, ?> m = (CssMetaData<Styleable, ?>) metaMap.get(d.getProperty());
                    if (m != null && m.isSettable(elem)) {
                        if (!convertedValues.containsKey(d)) {
                            @SuppressWarnings("unchecked")
                            StyleConverter<String, Object> converter = (StyleConverter<String, Object>) m.getConverter();
                            ParsedValueImpl<String, Object> parsedValue = new ParsedValueImpl<>(d.getTermsAsString(), null);
                            convertedValues.put(d, converter.convert(parsedValue, null));
                        }
                        Object convertedValue = convertedValues.get(d);
                        @SuppressWarnings("unchecked")
                        StyleableProperty<Object> styleableProperty = (StyleableProperty<Object>) m.getStyleableProperty(elem);
                        styleableProperty.applyStyle(StyleOrigin.USER_AGENT, convertedValue);
                    }
                }
            } catch (IOException ex) {
                System.err.println("DOMStyleManager: Invalid style attribute on element. style=" + style);
                ex.printStackTrace();
            }
        }
    }

    @Override
    public SelectorModel<Styleable> getSelectorModel() {
        return selectorModel;
    }

    @Override
    public void applyStylesheetTo(StyleOrigin styleOrigin, Stylesheet s, Figure f, HashMap<String, Set<Figure>> pseudoStyles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static class ParsedValueImpl<V, T> extends ParsedValue<V, T> {

        public ParsedValueImpl(V value, StyleConverter<V, T> converter) {
            super(value, converter);
        }

    }
}

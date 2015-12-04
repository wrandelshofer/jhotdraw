/* @(#)FigureStyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javafx.css.StyleOrigin;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.css.AbstractStyleManager;
import org.jhotdraw.css.CssParser;
import org.jhotdraw.css.SelectorModel;
import org.jhotdraw.css.ast.Declaration;
import org.jhotdraw.css.ast.StyleRule;
import org.jhotdraw.css.ast.Stylesheet;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.text.Converter;
import org.jhotdraw.styleable.StyleableMapAccessor;

/**
 * FigureStyleManager.
 *
 * @author Werner Randelshofer
 */
public class FigureStyleManager extends AbstractStyleManager<Figure> {

    private final FigureSelectorModel selectorModel = new FigureSelectorModel();

    private final CssParser parser = new CssParser();

    private final WeakHashMap<Declaration, Object> convertedValues = new WeakHashMap<>();

    public FigureStyleManager() {
    }

    /**
     * Applies the stylesheets to the specified element.
     *
     * @param elem The element
     */
    @Override
    public void applyStylesTo(Figure elem, Map<String, Set<Figure>> pseudoClassStates) {
        applyStylesTo(null, elem);
    }

    /**
     * Applies the stylesheets to the specified element.
     *
     * @param origin The style origin. Specify null to apply all origins.
     * @param elem The element
     */
    public void applyStylesTo(StyleOrigin origin, Figure elem) {
        Set<MapAccessor<?>> metaList = elem.getSupportedKeys();
        HashMap<String, MapAccessor<?>> metaMap = new HashMap<>();
        for (MapAccessor<?> k : metaList) {
            if (k instanceof StyleableMapAccessor) {
                StyleableMapAccessor<?> sk = (StyleableMapAccessor<?>) k;
                metaMap.put(sk.getCssName(), k);
            }
        }

        // user agent stylesheets can not override element attributes
        if (origin == null || origin == StyleOrigin.USER_AGENT) {
            elem.removeAll(StyleOrigin.USER_AGENT);
            for (MyEntry e : getUserAgentStylesheets()) {
                Stylesheet s = e.getStylesheet();
                if (s != null) {
                    applyStylesTo(StyleOrigin.USER_AGENT, s, metaMap, elem);
                }
            }
        }

        // author stylesheet override user agent stylesheets and element attributes
        if (origin == null || origin == StyleOrigin.AUTHOR) {
            elem.removeAll(StyleOrigin.AUTHOR);
            for (MyEntry e : getAuthorStylesheets()) {
                Stylesheet s = e.getStylesheet();
                if (s != null) {
                    applyStylesTo(StyleOrigin.AUTHOR, s, metaMap, elem);
                }
            }
        }

        // inline stylesheets override user agent stylesheets, element attributes and author stylesheets
        if (origin == null || origin == StyleOrigin.INLINE) {
            elem.removeAll(StyleOrigin.INLINE);
            for (MyEntry e : getInlineStylesheets()) {
                Stylesheet s = e.getStylesheet();
                if (s != null) {
                    applyStylesTo(StyleOrigin.INLINE, s, metaMap, elem);
                }
            }

            // inline styles can override all other values
            applyInlineStylesTo(metaMap, elem);
        }

        //
        metaMap.clear();
    }

    private void applyStylesTo(StyleOrigin origin, Stylesheet s, HashMap<String, MapAccessor<?>> metaMap, Figure elem) {
        for (StyleRule r : s.getRulesets()) {
            if (r.getSelectorGroup().matches(selectorModel, elem)) {
                for (Declaration d : r.getDeclarations()) {
                    @SuppressWarnings("unchecked")
                    StyleableMapAccessor<Object> k = (StyleableMapAccessor<Object>) metaMap.get(d.getProperty());
                    if (k != null) {
                        if (!convertedValues.containsKey(d)) {
                            @SuppressWarnings("unchecked")
                            Converter<Object> converter = k.getConverter();
                            try {
                                convertedValues.put(d, converter.fromString(d.getTermsAsString()));
                            } catch (ParseException | IOException ex) {
                                System.err.println("Warning FigureStyleManager can not convert CSS term to string. " + d.getProperty() + ":" + d.getTermsAsString());
                                ex.printStackTrace();
                            }
                        }
                        if (convertedValues.containsKey(d)) {
                            Object convertedValue = convertedValues.get(d);
                            elem.setStyled(origin, k, convertedValue);
                        }
                    }
                }
            }
        }
    }

    private void applyInlineStylesTo(HashMap<String, MapAccessor<?>> metaMap, Figure elem) {
        // inline styles can override all other values
        String style = elem.getStyle();
        if (style != null) {
            try {
                for (Declaration d : parser.parseDeclarationList(style)) {
                    @SuppressWarnings("unchecked")
                    StyleableMapAccessor<Object> k = (StyleableMapAccessor<Object>) metaMap.get(d.getProperty());
                    if (k != null) {
                        if (!convertedValues.containsKey(d)) {
                            @SuppressWarnings("unchecked")
                            Converter<Object> converter = k.getConverter();
                            try {
                                convertedValues.put(d, converter.fromString(d.getTermsAsString()));
                            } catch (ParseException | IOException ex) {
                                System.err.println("Warning FigureStyleManager can not convert CSS term to string. " + d.getProperty() + ":" + d.getTermsAsString());
                                //ex.printStackTrace();
                            }
                        }
                        Object convertedValue = convertedValues.get(d);
                        elem.setStyled(StyleOrigin.INLINE, k, convertedValue);
                    }
                }
            } catch (IOException ex) {
                System.err.println("DOMStyleManager: Invalid style attribute on element. style=" + style);
                ex.printStackTrace();
            }
        }
    }

    @Override
    public SelectorModel<Figure> getSelectorModel() {
        return selectorModel;
    }

    @Override
    public void applyStylesheetTo(StyleOrigin styleOrigin, Stylesheet s, Figure elem, HashMap<String, Set<Figure>> pseudoStyles) {
        // FIXME this is very inefficient for a single element
        selectorModel.additionalPseudoClassStatesProperty().putAll(pseudoStyles);
        Set<MapAccessor<?>> metaList = elem.getSupportedKeys();
        HashMap<String, MapAccessor<?>> metaMap = new HashMap<>();
        for (MapAccessor<?> k : metaList) {
            if (k instanceof StyleableMapAccessor) {
                StyleableMapAccessor<?> sk = (StyleableMapAccessor<?>) k;
                metaMap.put(sk.getCssName(), k);
            }
        }

        applyStylesTo(styleOrigin, s, metaMap, elem);

        // FIXME this is very inefficient for a single element
        metaMap.clear();
        selectorModel.additionalPseudoClassStatesProperty().clear();
    }

}

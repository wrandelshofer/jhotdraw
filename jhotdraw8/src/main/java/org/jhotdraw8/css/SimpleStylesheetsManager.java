/* @(#)SimpleStylesheetsManager.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.css.StyleOrigin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.collection.ReadableList;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.Selector;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Stylesheet;

/**
 * SimpleStylesheetsManager.
 *
 * @param <E> the element type that can be styled by this style manager
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleStylesheetsManager<E> implements StylesheetsManager<E> {

    private final CssParser parser = new CssParser();
    private SelectorModel<E> selectorModel;
    /**
     * Cache for parsed user agent stylesheets.
     * <p>
     * The key is either an URI or a literal CSS String for which we cache the
     * data. The value contains the parsed stylesheet entry.
     */
    private LinkedHashMap<Object, ParsedStylesheetEntry> userAgentList = new LinkedHashMap<>();
    /**
     * @see #userAgentList
     */
    private LinkedHashMap<Object, ParsedStylesheetEntry> authorList = new LinkedHashMap<>();
    /**
     * @see #userAgentList
     */
    private LinkedHashMap<Object, ParsedStylesheetEntry> inlineList = new LinkedHashMap<>();
    @Nonnull
    private Executor executor = Executors.newCachedThreadPool();
    private Map<String, ReadableList<CssToken>> cachedAuthorCustomProperties;
    private Map<String, ReadableList<CssToken>> cachedInlineCustomProperties;
    private Map<String, ReadableList<CssToken>> cachedUserAgentCustomProperties;
    private final static Logger LOGGER = Logger.getLogger(SimpleStylesheetsManager.class.getName());
    private final BiFunction<SelectorModel<E>, Map<String, ReadableList<CssToken>>, CssFunctionProcessor<E>> functionProcessorFactory;

    public SimpleStylesheetsManager(SelectorModel<E> selectorModel) {
        this(selectorModel, SimpleCssFunctionProcessor::new);
    }

    public SimpleStylesheetsManager(SelectorModel<E> selectorModel, BiFunction<SelectorModel<E>, Map<String, ReadableList<CssToken>>, CssFunctionProcessor<E>> functionProcessorFactory) {
        this.selectorModel = selectorModel;
        this.functionProcessorFactory = functionProcessorFactory;
    }

    private void doSetAttribute(SelectorModel<E> selectorModel1, E elem, StyleOrigin styleOrigin, @Nullable String namespace, @Nonnull String name, ReadableList<CssToken> value,
                                Map<String, ReadableList<CssToken>> customProperties) {
        if (value == null) {
            selectorModel1.setAttribute(elem, styleOrigin, namespace, name, null);
        } else {
            CssFunctionProcessor<E> processor = functionProcessorFactory.apply(selectorModel1, customProperties);
            ReadableList<CssToken> processed = preprocessTerms(elem, processor, value);
            selectorModel1.setAttribute(elem, styleOrigin, namespace, name, processed);
        }
    }

    public void getSelectorModel(SelectorModel<E> newValue) {
        selectorModel = newValue;
    }

    @Override
    public SelectorModel<E> getSelectorModel() {
        return selectorModel;
    }

    @Override
    public void addStylesheet(@Nonnull StyleOrigin origin, @Nullable URI documentHome, @Nonnull URI uri) {
        URI resolvedUri = documentHome == null ? uri : documentHome.resolve(uri);
        invalidate();
        getMap(origin).put(resolvedUri, new ParsedStylesheetEntry(origin, resolvedUri));
    }

    @Override
    public void addStylesheet(@Nonnull StyleOrigin origin, @Nonnull String str) {
        invalidate();
        getMap(origin).put(str, new ParsedStylesheetEntry(origin, str));
    }

    private void invalidate() {
        cachedAuthorCustomProperties = null;
        cachedInlineCustomProperties = null;
        cachedUserAgentCustomProperties = null;
    }

    @Override
    public void clearStylesheets(@Nullable StyleOrigin origin) {
        if (origin == null) {
            authorList.clear();
            userAgentList.clear();
            inlineList.clear();
            invalidate();
        } else {
            getMap(origin).clear();
        }
    }

    private LinkedHashMap<Object, ParsedStylesheetEntry> getMap(StyleOrigin origin) {
        switch (origin) {
            case AUTHOR:
                return authorList;
            case USER_AGENT:
                return userAgentList;
            case INLINE:
                return inlineList;
            default:
                throw new IllegalArgumentException("illegal origin:" + origin);
        }
    }

    private void setMap(StyleOrigin origin, LinkedHashMap<Object, ParsedStylesheetEntry> newValue) {
        switch (origin) {
            case AUTHOR:
                authorList = newValue;
                break;
            case USER_AGENT:
                userAgentList = newValue;
                break;
            case INLINE:
                inlineList = newValue;
                break;
            default:
                throw new IllegalArgumentException("illegal origin:" + origin);
        }
    }

    @Override
    public <T> void setStylesheets(@Nonnull StyleOrigin origin, @Nullable URI documentHome, @Nullable List<T> stylesheets) {
        invalidate();
        LinkedHashMap<Object, ParsedStylesheetEntry> oldMap = getMap(origin);
        if (stylesheets == null) {
            oldMap.clear();
            return;
        }
        LinkedHashMap<Object, ParsedStylesheetEntry> newMap = new LinkedHashMap<>();
        for (T t : stylesheets) {
            if (t instanceof URI) {
                URI uri = (URI) t;
                URI resolvedUri = documentHome == null ? uri : documentHome.resolve(uri);
                ParsedStylesheetEntry old = oldMap.get(resolvedUri);
                if (false && old != null) { // XXX we always need to reload the file!
                    newMap.put(resolvedUri, old);
                } else {
                    newMap.put(resolvedUri, new ParsedStylesheetEntry(origin, resolvedUri));
                }
            } else if (t instanceof String) {
                ParsedStylesheetEntry old = oldMap.get(t);
                if (old != null) {
                    newMap.put(t, old);
                } else {
                    newMap.put(t, new ParsedStylesheetEntry(origin, (String) t));
                }
            } else {
                throw new IllegalArgumentException("illegal item " + t);
            }
        }
        setMap(origin, newMap);
    }

    @Nonnull
    protected Collection<ParsedStylesheetEntry> getAuthorStylesheets() {
        return authorList.values();
    }

    @Nonnull
    protected Collection<ParsedStylesheetEntry> getUserAgentStylesheets() {
        return userAgentList.values();
    }

    @Nonnull
    protected Collection<ParsedStylesheetEntry> getInlineStylesheets() {
        return inlineList.values();
    }

    @Override
    public void applyStylesheetsTo(E elem) {
        SelectorModel<E> selectorModel = getSelectorModel();


        // The stylesheet is a user-agent stylesheet
        Collection<ParsedStylesheetEntry> uaStylesheets = getUserAgentStylesheets();
        Map<String, ReadableList<CssToken>> uaCustomProperties = getUserAgentCustomProperties();
        for (Declaration d : collectApplicableDeclarations(elem, uaStylesheets)) {
            doSetAttribute(selectorModel, elem, StyleOrigin.USER_AGENT, d.getPropertyNamespace(), d.getPropertyName(), d.getTerms(), uaCustomProperties);
        }

        // The value of a property was set by the user through a call to a set method with StyleOrigin.USER
        // ... nothing to do!
        // The stylesheet is an external file
        Map<String, ReadableList<CssToken>> authorCustomProperties = getAuthorCustomProperties();
        for (Declaration d : collectApplicableDeclarations(elem, getAuthorStylesheets())) {
            doSetAttribute(selectorModel, elem, StyleOrigin.AUTHOR, d.getPropertyNamespace(), d.getPropertyName(), d.getTerms(), authorCustomProperties);
        }

        // The stylesheet is an internal file
        Map<String, ReadableList<CssToken>> inlineCustomProperties = getInlineCustomProperties();
        for (Declaration d : collectApplicableDeclarations(elem, getInlineStylesheets())) {
            doSetAttribute(selectorModel, elem, StyleOrigin.INLINE, d.getPropertyNamespace(), d.getPropertyName(), d.getTerms(), inlineCustomProperties);
        }

        // 'inline style attributes' can override all other values
        if (selectorModel.hasAttribute(elem, null, "style")) {
            Map<QualifiedName, ReadableList<CssToken>> inlineDeclarations = new HashMap<>();
            String styleValue = selectorModel.getAttributeAsString(elem, null, "style");
            try {
                for (Declaration d : parser.parseDeclarationList(styleValue)) {
                    // Declarations without terms are ignored
                    if (d.getTerms().isEmpty()) {
                        continue;
                    }

                    inlineDeclarations.put(new QualifiedName(d.getPropertyNamespace(), d.getPropertyName()), d.getTerms());
                }
            } catch (IOException ex) {
                System.err.println("DOMStyleManager: Invalid style attribute on element. style=" + styleValue);
                ex.printStackTrace();
            }
            Map<String, ReadableList<CssToken>> inlineStyleAttrCustomProperties = Collections.emptyMap();
            for (Map.Entry<QualifiedName, ReadableList<CssToken>> entry : inlineDeclarations.entrySet()) {
                doSetAttribute(selectorModel, elem, StyleOrigin.INLINE, entry.getKey().getNamespace(), entry.getKey().getName(), entry.getValue(), inlineStyleAttrCustomProperties);
            }
            inlineDeclarations.clear();
        }
    }

    private Map<String, ReadableList<CssToken>> getInlineCustomProperties() {
        if (cachedInlineCustomProperties == null) {
            cachedInlineCustomProperties = collectCustomProperties(getInlineStylesheets());
        }
        return cachedInlineCustomProperties;
    }

    private Map<String, ReadableList<CssToken>> getAuthorCustomProperties() {
        if (cachedAuthorCustomProperties == null) {
            cachedAuthorCustomProperties = collectCustomProperties(getAuthorStylesheets());
        }
        return cachedAuthorCustomProperties;
    }

    private Map<String, ReadableList<CssToken>> getUserAgentCustomProperties() {
        if (cachedUserAgentCustomProperties == null) {
            cachedUserAgentCustomProperties = collectCustomProperties(getUserAgentStylesheets());
        }
        return cachedUserAgentCustomProperties;
    }

    /**
     * Collects all declarations in all specified stylesheets which are
     * applicable to the specified element.
     *
     * @param elem        an element
     * @param stylesheets the stylesheets
     * @return list of applicable declarations
     */
    private List<Declaration> collectApplicableDeclarations(E elem, Collection<ParsedStylesheetEntry> stylesheets) {
        List<Map.Entry<Integer, Declaration>> applicableDeclarations = new ArrayList<>();
        for (ParsedStylesheetEntry e : stylesheets) {
            Stylesheet s = e.getStylesheet();
            if (s == null) {
                continue;
            }
            collectApplicableDeclarations(elem, s, applicableDeclarations);
        }

        return applicableDeclarations.stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Nonnull
    private List<Map.Entry<Integer, Declaration>> collectApplicableDeclarations(E elem, Stylesheet s, @Nonnull List<Map.Entry<Integer, Declaration>> applicableDeclarations) {
        SelectorModel<E> selectorModel = getSelectorModel();
        for (StyleRule r : s.getStyleRules()) {
            Selector selector;
            if (null != (selector = r.getSelectorGroup().match(selectorModel, elem))) {
                for (Declaration d : r.getDeclarations()) {
                    // Declarations without terms are ignored
                    if (d.getTerms().isEmpty()) {
                        continue;
                    }

                    applicableDeclarations.add(new AbstractMap.SimpleEntry<>(selector.getSpecificity(), d));
                }
            }
        }
        return applicableDeclarations;
    }

    @Override
    public boolean applyStylesheetTo(StyleOrigin styleOrigin, @Nonnull Stylesheet s, E elem) {
        SelectorModel<E> selectorModel = getSelectorModel();
        final Map<String, ReadableList<CssToken>> customProperties = collectCustomProperties(s);

        ExtendedCssFunctionProcessor<E> processor = new ExtendedCssFunctionProcessor<>(selectorModel, customProperties);
        final List<Map.Entry<Integer, Declaration>> applicableDeclarations = collectApplicableDeclarations(elem, s,
                new ArrayList<>());
        if (applicableDeclarations.isEmpty()) {
            return false;
        }
        for (Map.Entry<Integer, Declaration> entry : applicableDeclarations) {
            Declaration d = entry.getValue();
            ReadableList<CssToken> value = preprocessTerms(elem, processor, d.getTerms());
            selectorModel.setAttribute(elem, styleOrigin, d.getPropertyNamespace(), d.getPropertyName(),
                    value.size() == 1 && value.get(0).getType() == CssTokenType.TT_IDENT
                            && CssTokenType.IDENT_INITIAL.equals(value.get(0).getStringValue()) ? null : value);
        }
        return true;
    }

    private Map<String, ReadableList<CssToken>> collectCustomProperties(Collection<ParsedStylesheetEntry> stylesheets) {
        Map<String, ReadableList<CssToken>> customProperties = new LinkedHashMap<>();
        for (ParsedStylesheetEntry s : stylesheets) {
            Stylesheet stylesheet = s.getStylesheet();
            if (stylesheet != null) {
                collectCustomProperties(stylesheet, customProperties);
            }
        }
        return customProperties;
    }

    private Map<String, ReadableList<CssToken>> collectCustomProperties(Stylesheet s) {
        Map<String, ReadableList<CssToken>> customProperties = new LinkedHashMap<>();
        collectCustomProperties(s, customProperties);
        return customProperties;
    }

    private void collectCustomProperties(Stylesheet s, Map<String, ReadableList<CssToken>> customProperties) {
        for (StyleRule styleRule : s.getStyleRules()) {
            for (Declaration declaration : styleRule.getDeclarations()) {
                if (declaration.getPropertyName().startsWith("--")) {
                    customProperties.put(declaration.getPropertyName(), declaration.getTerms());
                }
            }
        }
    }

    @Nonnull
    private ReadableList<CssToken> preprocessTerms(E elem, CssFunctionProcessor<E> processor, ReadableList<CssToken> terms) {
        String value;
        try {
            return processor.process(elem, terms);
        } catch (ParseException e) {
            return terms;
        }
    }

    protected class ParsedStylesheetEntry {

        private StyleOrigin origin;
        @Nullable
        private FutureTask<Stylesheet> future;
        @Nullable
        private Stylesheet stylesheet;

        public ParsedStylesheetEntry(StyleOrigin origin, @Nonnull URI uri) {
            this.origin = origin;
            this.future = new FutureTask<>(() -> {
                CssParser p = new CssParser();
                Stylesheet s = p.parseStylesheet(uri);
                LOGGER.info("Parsed " + uri + ".\nRules: " + s.getStyleRules());
                List<ParseException> parseExceptions = p.getParseExceptions();
                if (!parseExceptions.isEmpty()) {
                    LOGGER.info("Parsed " + uri + ".\nExceptions:\n  " + parseExceptions.stream().map(ParseException::getMessage).collect(Collectors.joining("\n  ")));
                }
                return s;
            });
            executor.execute(future);
        }

        public ParsedStylesheetEntry(StyleOrigin origin, @Nonnull String str) {
            this.origin = origin;
            this.future = new FutureTask<>(() -> {
                CssParser p = new CssParser();
                Stylesheet s = p.parseStylesheet(str);
                LOGGER.info("Parsed " + str + ".\nRules: " + s.getStyleRules());
                List<ParseException> parseExceptions = p.getParseExceptions();
                if (!parseExceptions.isEmpty()) {
                    LOGGER.info("Parsed " + str + ".\nExceptions:\n  " + parseExceptions.stream().map(ParseException::getMessage).collect(Collectors.joining("\n  ")));
                }
                return s;
            });
            executor.execute(future);
        }

        public StyleOrigin getOrigin() {
            return origin;
        }

        @Nullable
        public Stylesheet getStylesheet() {
            if (future != null) {
                try {
                    stylesheet = future.get();
                    future = null;
                } catch (InterruptedException ex) {
                    // retry later
                } catch (ExecutionException ex) {
                    LOGGER.throwing(getClass().getName(), "getStylesheet", ex);
                    ex.printStackTrace();
                    stylesheet = null;
                    future = null;
                }
            }
            return stylesheet;
        }

    }
}

/*
 * @(#)SimpleStylesheetsManager.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.css.StyleOrigin;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.Selector;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Stylesheet;
import org.jhotdraw8.css.function.CssFunction;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * SimpleStylesheetsManager.
 *
 * @param <E> the element type that can be styled by this style manager
 * @author Werner Randelshofer
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
    private LinkedHashMap<Object, StylesheetEntry> userAgentList = new LinkedHashMap<>();
    /**
     * @see #userAgentList
     */
    private LinkedHashMap<Object, StylesheetEntry> authorList = new LinkedHashMap<>();
    /**
     * @see #userAgentList
     */
    private LinkedHashMap<Object, StylesheetEntry> inlineList = new LinkedHashMap<>();
    @NonNull
    private Executor executor = Executors.newCachedThreadPool();
    @Nullable
    private Map<String, ImmutableList<CssToken>> cachedAuthorCustomProperties;
    @Nullable
    private Map<String, ImmutableList<CssToken>> cachedInlineCustomProperties;
    @Nullable
    private Map<String, ImmutableList<CssToken>> cachedUserAgentCustomProperties;
    private final static Logger LOGGER = Logger.getLogger(SimpleStylesheetsManager.class.getName());

    public SimpleStylesheetsManager(SelectorModel<E> selectorModel) {
        this(selectorModel, Collections.emptyList());
    }

    public SimpleStylesheetsManager(SelectorModel<E> selectorModel, @Nullable List<CssFunction<E>> functions) {
        this.selectorModel = selectorModel;
        this.functions = functions;
    }

    private void doSetAttribute(@NonNull SelectorModel<E> selectorModel1, @NonNull E elem, @NonNull StyleOrigin styleOrigin,
                                @Nullable String namespace, @NonNull String name, @Nullable ImmutableList<CssToken> value,
                                Map<String, ImmutableList<CssToken>> customProperties) throws ParseException {
        if (value == null) {
            selectorModel1.setAttribute(elem, styleOrigin, namespace, name, null);
        } else {
            if (!functions.isEmpty()) {
                final CssFunctionProcessor<E> functionProcessor = createCssFunctionProcessor(selectorModel1, customProperties);
                ImmutableList<CssToken> processed = preprocessTerms(elem, functionProcessor, value);
                selectorModel1.setAttribute(elem, styleOrigin, namespace, name, processed);
            } else {
                selectorModel1.setAttribute(elem, styleOrigin, namespace, name, value);
            }
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
    public void addStylesheet(@NonNull StyleOrigin origin, @Nullable URI documentHome, @NonNull URI uri) {
        URI resolvedUri = documentHome == null ? uri : documentHome.resolve(uri);
        invalidate();
        getMap(origin).put(resolvedUri, new StylesheetEntry(origin, resolvedUri));
    }

    @Override
    public void addStylesheet(@NonNull StyleOrigin origin, @NonNull Stylesheet stylesheet) {
        invalidate();
        getMap(origin).put(stylesheet, new StylesheetEntry(origin, stylesheet));
    }

    @Override
    public void addStylesheet(@NonNull StyleOrigin origin, @NonNull String str) {
        invalidate();
        getMap(origin).put(str, new StylesheetEntry(origin, str));
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

    private LinkedHashMap<Object, StylesheetEntry> getMap(@NonNull StyleOrigin origin) {
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

    private void setMap(@NonNull StyleOrigin origin, LinkedHashMap<Object, StylesheetEntry> newValue) {
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
    public <T> void setStylesheets(@NonNull StyleOrigin origin, @Nullable URI documentHome, @Nullable List<T> stylesheets) {
        invalidate();
        LinkedHashMap<Object, StylesheetEntry> oldMap = getMap(origin);
        if (stylesheets == null) {
            oldMap.clear();
            return;
        }
        LinkedHashMap<Object, StylesheetEntry> newMap = new LinkedHashMap<>();
        for (T t : stylesheets) {
            if (t instanceof URI) {
                URI uri = (URI) t;
                URI resolvedUri = documentHome == null ? uri : documentHome.resolve(uri);
                StylesheetEntry old = oldMap.get(resolvedUri);
                if (false && old != null) { // XXX we always need to reload the file!
                    newMap.put(resolvedUri, old);
                } else {
                    newMap.put(resolvedUri, new StylesheetEntry(origin, resolvedUri));
                }
            } else if (t instanceof String) {
                StylesheetEntry old = oldMap.get(t);
                if (old != null) {
                    newMap.put(t, old);
                } else {
                    newMap.put(t, new StylesheetEntry(origin, (String) t));
                }
            } else {
                throw new IllegalArgumentException("illegal item " + t);
            }
        }
        setMap(origin, newMap);
    }

    @NonNull
    protected Collection<StylesheetEntry> getAuthorStylesheets() {
        return authorList.values();
    }

    @NonNull
    protected Collection<StylesheetEntry> getUserAgentStylesheets() {
        return userAgentList.values();
    }

    @NonNull
    protected Collection<StylesheetEntry> getInlineStylesheets() {
        return inlineList.values();
    }

    @Override
    public void applyStylesheetsTo(@NonNull E elem) {
        applyStylesheetsTo(Collections.singleton(elem));
    }

    @Override
    public void applyStylesheetsTo(@NonNull Iterable<E> iterable) {
        SelectorModel<E> selectorModel = getSelectorModel();

        // Compute custom properties
        Map<String, ImmutableList<CssToken>> customProperties = computeCustomProperties();

        for (E elem : iterable) {
            // Clear stylesheet values
            selectorModel.reset(elem);

            // The stylesheet is a user-agent stylesheet
            for (Declaration d : collectApplicableDeclarations(elem, getUserAgentStylesheets())) {
                try {
                    doSetAttribute(selectorModel, elem, StyleOrigin.USER_AGENT, d.getPropertyNamespace(), d.getPropertyName(), d.getTerms(), customProperties);
                } catch (ParseException e) {
                    LOGGER.throwing(SimpleStylesheetsManager.class.getName(), "applyStylesheetsTo", e);
                }
            }

            // The value of a property was set by the user through a call to a set method with StyleOrigin.USER
            // ... nothing to do!
            // The stylesheet is an external file
            for (Declaration d : collectApplicableDeclarations(elem, getAuthorStylesheets())) {
                try {
                    doSetAttribute(selectorModel, elem, StyleOrigin.AUTHOR, d.getPropertyNamespace(), d.getPropertyName(), d.getTerms(), customProperties);
                } catch (ParseException e) {
                    LOGGER.throwing(SimpleStylesheetsManager.class.getName(), "applyStylesheetsTo", e);
                }
            }

            // The stylesheet is an internal file
            for (Declaration d : collectApplicableDeclarations(elem, getInlineStylesheets())) {
                try {
                    doSetAttribute(selectorModel, elem, StyleOrigin.INLINE, d.getPropertyNamespace(), d.getPropertyName(), d.getTerms(), customProperties);
                } catch (ParseException e) {
                    LOGGER.throwing(SimpleStylesheetsManager.class.getName(), "applyStylesheetsTo", e);
                }
            }

            // 'inline style attributes' can override all other values
            if (selectorModel.hasAttribute(elem, null, "style")) {
                Map<QualifiedName, ImmutableList<CssToken>> inlineDeclarations = new HashMap<>();
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
                Map<String, ImmutableList<CssToken>> inlineStyleAttrCustomProperties = Collections.emptyMap();
                for (Map.Entry<QualifiedName, ImmutableList<CssToken>> entry : inlineDeclarations.entrySet()) {
                    try {
                        doSetAttribute(selectorModel, elem, StyleOrigin.INLINE, entry.getKey().getNamespace(), entry.getKey().getName(), entry.getValue(), inlineStyleAttrCustomProperties);
                    } catch (ParseException e) {
                        LOGGER.throwing(SimpleStylesheetsManager.class.getName(), "applyStylesheetsTo", e);
                    }
                }
                inlineDeclarations.clear();
            }
        }
    }

    @org.jhotdraw8.annotation.NonNull
    private Map<String, ImmutableList<CssToken>> computeCustomProperties() {
        Map<String, ImmutableList<CssToken>> customProperties = new LinkedHashMap<>();
        customProperties.putAll(getUserAgentCustomProperties());
        customProperties.putAll(getAuthorCustomProperties());
        customProperties.putAll(getInlineCustomProperties());
        return customProperties;
    }

    @NonNull
    private Map<String, ImmutableList<CssToken>> getInlineCustomProperties() {
        if (cachedInlineCustomProperties == null) {
            cachedInlineCustomProperties = collectCustomProperties(getInlineStylesheets());
        }
        return cachedInlineCustomProperties;
    }

    @NonNull
    private Map<String, ImmutableList<CssToken>> getAuthorCustomProperties() {
        if (cachedAuthorCustomProperties == null) {
            cachedAuthorCustomProperties = collectCustomProperties(getAuthorStylesheets());
        }
        return cachedAuthorCustomProperties;
    }

    @NonNull
    private Map<String, ImmutableList<CssToken>> getUserAgentCustomProperties() {
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
    private List<Declaration> collectApplicableDeclarations(E elem, @NonNull Collection<StylesheetEntry> stylesheets) {
        List<Map.Entry<Integer, Declaration>> applicableDeclarations = new ArrayList<>();
        for (StylesheetEntry e : stylesheets) {
            Stylesheet s = e.getStylesheet();
            if (s == null) {
                continue;
            }
            collectApplicableDeclarations(elem, s, applicableDeclarations);
        }

        return applicableDeclarations.stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @NonNull
    private List<Map.Entry<Integer, Declaration>> collectApplicableDeclarations(E elem, @NonNull Stylesheet s, @NonNull List<Map.Entry<Integer, Declaration>> applicableDeclarations) {
        SelectorModel<E> selectorModel = getSelectorModel();
        for (StyleRule r : s.getStyleRules()) {
            Selector selector;
            if (null != (selector = r.getSelectorGroup().matchSelector(selectorModel, elem))) {
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
    public boolean applyStylesheetTo(@NonNull StyleOrigin styleOrigin, @NonNull Stylesheet s, @NonNull E elem, boolean suppressParseException) throws ParseException {
        SelectorModel<E> selectorModel = getSelectorModel();
        final Map<String, ImmutableList<CssToken>> customProperties = collectCustomProperties(s);

        CssFunctionProcessor<E> processor = createCssFunctionProcessor(selectorModel, customProperties);
        final List<Map.Entry<Integer, Declaration>> applicableDeclarations = collectApplicableDeclarations(elem, s,
                new ArrayList<>());
        if (applicableDeclarations.isEmpty()) {
            return false;
        }
        for (Map.Entry<Integer, Declaration> entry : applicableDeclarations) {
            Declaration d = entry.getValue();
            ImmutableList<CssToken> value = preprocessTerms(elem, processor, d.getTerms());
            try {
                selectorModel.setAttribute(elem, styleOrigin, d.getPropertyNamespace(), d.getPropertyName(),
                        value.size() == 1 && value.get(0).getType() == CssTokenType.TT_IDENT
                                && CssTokenType.IDENT_INITIAL.equals(value.get(0).getStringValue()) ? null : value);
            } catch (ParseException e) {
                if (suppressParseException) {
                    LOGGER.throwing(SimpleStylesheetsManager.class.getName(), "applyStylesheetsTo", e);
                } else {
                    throw e;
                }
            }
        }
        return true;
    }

    private CssFunctionProcessor<E> createCssFunctionProcessor(SelectorModel<E> selectorModel, Map<String, ImmutableList<CssToken>> customProperties) {
        return new SimpleCssFunctionProcessor<>(functions, selectorModel, customProperties);
    }


    private List<CssFunction<E>> functions = new ArrayList<>();

    public List<CssFunction<E>> getFunctions() {
        return functions;
    }

    public void setFunctions(List<CssFunction<E>> functions) {
        this.functions = functions;
    }

    @NonNull
    @Override
    public String getHelpText() {
        StringBuilder buf = new StringBuilder();
        for (CssFunction<E> value : functions) {
            if (buf.length() != 0) {
                buf.append("\n");
            }
            buf.append(value.getHelpText());
        }
        return buf.toString();
    }

    @NonNull
    private Map<String, ImmutableList<CssToken>> collectCustomProperties(@NonNull Collection<StylesheetEntry> stylesheets) {
        Map<String, ImmutableList<CssToken>> customProperties = new LinkedHashMap<>();
        for (StylesheetEntry s : stylesheets) {
            Stylesheet stylesheet = s.getStylesheet();
            if (stylesheet != null) {
                collectCustomProperties(stylesheet, customProperties);
            }
        }
        return customProperties;
    }

    @NonNull
    private Map<String, ImmutableList<CssToken>> collectCustomProperties(@NonNull Stylesheet s) {
        Map<String, ImmutableList<CssToken>> customProperties = new LinkedHashMap<>();
        collectCustomProperties(s, customProperties);
        return customProperties;
    }

    private void collectCustomProperties(@NonNull Stylesheet s, @NonNull Map<String, ImmutableList<CssToken>> customProperties) {
        for (StyleRule styleRule : s.getStyleRules()) {
            for (Declaration declaration : styleRule.getDeclarations()) {
                if (declaration.getPropertyName().startsWith("--")) {
                    customProperties.put(declaration.getPropertyName(), declaration.getTerms());
                }
            }
        }
    }

    @NonNull
    private ImmutableList<CssToken> preprocessTerms(E elem, @NonNull CssFunctionProcessor<E> processor, @NonNull ImmutableList<CssToken> terms) {
        String value;
        try {
            return processor.process(elem, terms);
        } catch (ParseException e) {
            LOGGER.throwing(getClass().getName(), "error preprocessing token", e);
            return terms;
        }
    }

    protected class StylesheetEntry {

        private StyleOrigin origin;
        @Nullable
        private FutureTask<Stylesheet> future;
        @Nullable
        private Stylesheet stylesheet;

        public StylesheetEntry(StyleOrigin origin, @NonNull URI uri) {
            this.origin = origin;
            this.future = new FutureTask<>(() -> {
                CssParser p = new CssParser();
                Stylesheet s = p.parseStylesheet(uri);
                LOGGER.info("Parsed " + uri + ".\n#rules: " + s.getStyleRules().size());
                List<ParseException> parseExceptions = p.getParseExceptions();
                if (!parseExceptions.isEmpty()) {
                    LOGGER.info("Parsed " + uri + ".\nExceptions:\n  " + parseExceptions.stream().map(ParseException::getMessage).collect(Collectors.joining("\n  ")));
                }
                return s;
            });
            executor.execute(future);
        }

        public StylesheetEntry(StyleOrigin origin, @NonNull Stylesheet stylesheet) {
            this.origin = origin;
            this.stylesheet = stylesheet;
        }

        public StylesheetEntry(StyleOrigin origin, @NonNull String str) {
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

/*
 * @(#)AbstractStyleAttributesInspector.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.css.StyleOrigin;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.CssParser;
import org.jhotdraw8.css.CssPrettyPrinter;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.QualifiedName;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.css.ast.AndCombinator;
import org.jhotdraw8.css.ast.ClassSelector;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.IdSelector;
import org.jhotdraw8.css.ast.Selector;
import org.jhotdraw8.css.ast.SelectorGroup;
import org.jhotdraw8.css.ast.SimplePseudoClassSelector;
import org.jhotdraw8.css.ast.SimpleSelector;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Stylesheet;
import org.jhotdraw8.css.ast.TypeSelector;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.draw.figure.TextFontableFigure;
import org.jhotdraw8.draw.popup.BooleanPicker;
import org.jhotdraw8.draw.popup.CssColorPicker;
import org.jhotdraw8.draw.popup.CssFontPicker;
import org.jhotdraw8.draw.popup.EnumPicker;
import org.jhotdraw8.draw.popup.FontFamilyPicker;
import org.jhotdraw8.draw.popup.PaintablePicker;
import org.jhotdraw8.draw.popup.Picker;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Intentionally does not implement the inspector interface, so
 * that subclasses can use this inspector on different subject
 * types.
 *
 * @param <E>
 */
public abstract class AbstractStyleAttributesInspector<E> {
    /**
     * The name of the {@link #showingProperty}.
     */
    public static final String SHOWING_PROPERTY = "showing";

    protected final @NonNull BooleanProperty showing = new SimpleBooleanProperty(this, SHOWING_PROPERTY, true);

    {
        showing.addListener((o, oldv, newv) -> {
            if (newv) {
                Platform.runLater(this::validateTextArea);
            }
        });
    }

    public @NonNull BooleanProperty showingProperty() {
        return showing;
    }

    public boolean isShowing() {
        return showingProperty().get();
    }

    public void setShowing(boolean newValue) {
        showingProperty().set(newValue);
    }

    /**
     * This placeholder is displayed to indicate that no value has
     * been specified for this property.
     * <p>
     * The placeholder should be a comment, e.g. "/* unspecified value * /",
     * or white space, e.g. "  ", or one of the keywords
     * {@link CssTokenType#IDENT_INITIAL},
     * {@link CssTokenType#IDENT_INHERIT},
     * {@link CssTokenType#IDENT_REVERT},
     * {@link CssTokenType#IDENT_UNSET},
     */
    public static final String UNSPECIFIED_VALUE_PLACEHOLDER = "  ";//"/* unspecified value */";
    /**
     * This placeholder is displayed to indicate that multiple values have
     * been specified for this property.
     * <p>
     * The placeholder should be a comment, e.g. "/* multiple values * /",
     * or white space, e.g. "  ".
     */
    public static final String MULTIPLE_VALUES_PLACEHOLDER = "/* multiple values */";
    private final ObjectProperty<Predicate<QualifiedName>> attributeFilter = new SimpleObjectProperty<>(k -> true);

    private final ReadOnlyMapProperty<WritableStyleableMapAccessor<?>, Picker<?>> accessorPickerMap = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));
    private @NonNull SetProperty<E> selection = new SimpleSetProperty<>();

    {
        SetChangeListener<E> listener = change -> {
            invalidateTextArea(selection);
        };
        selection.addListener((o, oldv, newv) -> {
            if (oldv != null) {
                oldv.removeListener(listener);
            }
            if (newv != null) {
                newv.addListener(listener);
                invalidateTextArea(selection);
            }
        });
    }

    private Node node;
    private final @NonNull Map<QualifiedName, String> helpTexts = new HashMap<>();
    private final @NonNull List<LookupEntry> lookupTable = new ArrayList<>();
    @FXML
    private Button applyButton;
    @FXML
    private Button selectButton;
    @FXML
    private CheckBox updateContentsCheckBox;
    @FXML
    private CheckBox updateSelectorCheckBox;
    @FXML
    private CheckBox composeAttributesCheckBox;
    @FXML
    private TextArea textArea;
    @FXML
    private RadioButton showAttributeValues;
    @FXML
    private ToggleGroup shownValues;
    @FXML
    private RadioButton showStylesheetValues;
    @FXML
    private RadioButton showUserAgentValues;
    @FXML
    private RadioButton showAppliedValues;
    private boolean textAreaValid = true;

    public AbstractStyleAttributesInspector() {
        this(StyleAttributesInspector.class.getResource("StyleAttributesInspector.fxml"));
    }

    public AbstractStyleAttributesInspector(@NonNull URL fxmlUrl) {
        init(fxmlUrl);
    }

    public @NonNull ReadOnlyMapProperty<WritableStyleableMapAccessor<?>, Picker<?>> accessorPickerMapProperty() {
        return accessorPickerMap;
    }

    private void apply(ActionEvent event) {
        isApplying = true;
        CssParser parser = new CssParser();
        TextArea textArea = getTextArea();
        try {
            Stylesheet stylesheet = parser.parseStylesheet(textArea.getText(), null);
            if (!parser.getParseExceptions().isEmpty()) {
                System.out.println("StyleAttributesInspector:\n" + parser.getParseExceptions().toString().replace(',', '\n'));
                ParseException e = parser.getParseExceptions().get(0);
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                textArea.positionCaret(e.getErrorOffset());
                textArea.requestFocus();
                return;
            }

            ObservableMap<String, Set<E>> pseudoStyles = createPseudoStyles();

            StylesheetsManager<E> sm = getStyleManager();
            if (sm == null) {
                return;
            }
            SelectorModel<E> fsm = sm.getSelectorModel();
            fsm.additionalPseudoClassStatesProperty().setValue(pseudoStyles);
            // This must not be done in parallel, because we may have observers on
            // the entities.
            for (E entity : getEntities()) {
                if (sm.applyStylesheetTo(StyleOrigin.USER, stylesheet, entity, false)) {
                    fireInvalidated(entity);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            textArea.positionCaret(e.getErrorOffset());
            textArea.requestFocus();
        }
        isApplying = false;
    }

    /**
     * Attribute filter can be used to show only a specific set
     * of attributes in the inspector.
     *
     * @return attribute filter
     */
    public @NonNull Property<Predicate<QualifiedName>> attributeFilter() {
        return attributeFilter;
    }

    protected abstract @NonNull Iterable<E> getEntities();

    private @Nullable String buildString(@Nullable List<CssToken> attribute) {
        if (attribute == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (CssToken t : attribute) {
            buf.append(t.fromToken());
        }
        return buf.toString();
    }

    protected void collectHelpTexts(@NonNull Collection<E> figures) {
        StylesheetsManager<E> styleManager = getStyleManager();
        SelectorModel<E> selectorModel = styleManager.getSelectorModel();

        for (E f : figures) {
            for (QualifiedName qname : selectorModel.getAttributeNames(f)) {
                Converter<?> c = getConverter(selectorModel, f, qname.getNamespace(), qname.getName());
                String helpText = c == null ? null : c.getHelpText();
                if (helpText != null) {
                    helpTexts.put(qname, helpText);
                }
            }
        }
    }

    private @NonNull <T> Picker<T> createAndCachePicker(@NonNull WritableStyleableMapAccessor<T> acc) {
        ObservableMap<WritableStyleableMapAccessor<?>, Picker<?>> amap = getAccessorPickerMap();
        @SuppressWarnings("unchecked") Picker<T> picker = (Picker<T>) amap.get(acc);
        if (picker == null) {
            picker = createPicker(acc);
            amap.put(acc, picker);
        }
        return picker;
    }

    @SuppressWarnings("unchecked")
    protected @NonNull <T> Picker<T> createPicker(@NonNull WritableStyleableMapAccessor<T> acc) {
        Class<T> type = acc.getRawValueType();
        boolean nullable = true;
        if (acc.getCssConverter() instanceof CssConverter) {
            CssConverter<T> converter = (CssConverter<T>) acc.getCssConverter();
            nullable = converter.isNullable();
        }
        Picker<?> p = null;
        if (type == Boolean.class) {
            p = new BooleanPicker(nullable);
        } else if (type == CssColor.class) {
            p = new CssColorPicker();
        } else if (type == Paintable.class) {
            p = new PaintablePicker();
        } else if (type == CssFont.class) {
            p = new CssFontPicker();
        } else if (acc == TextFontableFigure.FONT_FAMILY) {
            p = new FontFamilyPicker();
        } else if (type.isEnum()) {
            Class<? extends Enum<?>> enumClazz = (Class<? extends Enum<?>>) type;
            @SuppressWarnings("rawtypes")
            EnumPicker suppress = new EnumPicker(enumClazz, acc.getCssConverter());
            p = suppress;
        }

        return (Picker<T>) p;
    }

    private @NonNull ObservableMap<String, Set<E>> createPseudoStyles() {
        ObservableMap<String, Set<E>> pseudoStyles = FXCollections.observableHashMap();
        Set<E> fs = new LinkedHashSet<>(selection.get());
        // handling of emptyness must be consistent with code in
        // handleSelectionChanged() method
        if (fs.isEmpty()) {
            fs.add(getRoot());
        }

        pseudoStyles.put("selected", fs);
        return pseudoStyles;
    }

    /**
     * This method is invoked when this inspector has changed properties of
     * the specified element.
     *
     * @param f an element
     */
    protected abstract void fireInvalidated(E f);

    protected abstract @Nullable Object get(E f, WritableStyleableMapAccessor<Object> finalSelectedAccessor);

    protected abstract @Nullable WritableStyleableMapAccessor<?> getAccessor(SelectorModel<E> fsm, E f, String propertyNamespace, String propertyName);

    public ObservableMap<WritableStyleableMapAccessor<?>, Picker<?>> getAccessorPickerMap() {
        return accessorPickerMap.get();
    }

    public Predicate<QualifiedName> getAttributeFilter() {
        return attributeFilter.get();
    }

    public void setAttributeFilter(Predicate<QualifiedName> attributeFilter) {
        this.attributeFilter.set(attributeFilter);
    }

    protected abstract @Nullable Converter<?> getConverter(SelectorModel<E> selectorModel, E f, String namespace, String name);

    private @Nullable LookupEntry getLookupEntryAt(@NonNull int caretPosition) {
        int insertionPoint = Collections.binarySearch(lookupTable, new LookupEntry(caretPosition, null, null));
        if (insertionPoint < 0) {
            insertionPoint = (-(insertionPoint) - 1) - 1;
        }
        LookupEntry d = null;
        if (0 <= insertionPoint && insertionPoint < lookupTable.size()) {
            LookupEntry entry = lookupTable.get(insertionPoint);
            if (caretPosition <= entry.declaration.getEndPos()) {
                d = entry;
            }
        }
        return d;
    }

    public Node getNode() {
        return node;
    }

    protected abstract @Nullable E getRoot();

    public ObservableSet<E> getSelection() {
        ObservableSet<E> es = selection.get();
        return es == null ? FXCollections.emptyObservableSet() : es;
    }

    protected abstract @Nullable StylesheetsManager<E> getStyleManager();

    protected TextArea getTextArea() {
        return textArea;
    }

    protected void onCaretPositionChanged(Observable o, Number oldv, @NonNull Number newv) {
        LookupEntry entry = getLookupEntryAt(newv.intValue());
        Declaration d = entry == null ? null : entry.declaration;
        String helpText = null;
        if (d != null) {
            helpText = helpTexts.get(new QualifiedName(d.getNamespace(), d.getPropertyName()));
        }

        StylesheetsManager<E> sm = getStyleManager();

        String smHelpText = sm.getHelpText();
        if (helpText == null) {
            helpText = smHelpText;
        } else if (smHelpText == null || !smHelpText.isEmpty()) {
            helpText = helpText + "\n\n" + smHelpText;
        }


        setHelpText(helpText);
    }

    private void onTextAreaClicked(@NonNull MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2 && mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            mouseEvent.consume();
            int caretPosition = getTextArea().getCaretPosition();
            showPicker(caretPosition, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    }

    protected void init(@NonNull URL fxmlUrl) {
        // We must use invoke and wait here, because we instantiate Tooltips
        // which immediately instanciate a Window and a Scene.
        PlatformUtil.invokeAndWait(() -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(InspectorLabels.getResources().asResourceBundle());
            loader.setController(this);

            try (InputStream in = fxmlUrl.openStream()) {
                node = loader.load(in);
            } catch (IOException ex) {
                throw new InternalError(ex);
            }
        });
        Preferences prefs = Preferences.userNodeForPackage(StyleAttributesInspector.class);
        updateContentsCheckBox.setSelected(prefs.getBoolean("updateContents", true));
        updateContentsCheckBox.selectedProperty().addListener((o, oldValue, newValue)
                -> prefs.putBoolean("updateContents", newValue));
        updateSelectorCheckBox.setSelected(prefs.getBoolean("updateSelector", true));
        updateSelectorCheckBox.selectedProperty().addListener((o, oldValue, newValue)
                -> prefs.putBoolean("updateSelector", newValue));
        composeAttributesCheckBox.setSelected(prefs.getBoolean("composeAttributes", true));
        composeAttributesCheckBox.selectedProperty().addListener((o, oldValue, newValue)
                -> prefs.putBoolean("composeAttributes", newValue));

        applyButton.setOnAction(this::apply);
        selectButton.setOnAction(this::select);
        composeAttributesCheckBox.setOnAction(event -> invalidateTextArea(null));
        textArea.textProperty().addListener(this::updateLookupTable);
        textArea.caretPositionProperty().addListener(this::onCaretPositionChanged);
        EventHandler<? super KeyEvent> eventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(@NonNull KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER &&
                        (event.isAltDown() || event.isControlDown())) {
                    event.consume();
                    apply(null);
                }
            }
        };
        textArea.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);


        switch (prefs.get("shownValues", "user")) {
            case "author":
                showStylesheetValues.setSelected(true);
                break;
            case "user":
                showAttributeValues.setSelected(true);
                break;
            case "userAgent":
                showUserAgentValues.setSelected(true);
                break;
            case "styled":
            default:
                showAppliedValues.setSelected(true);
                break;
        }

        shownValues.selectedToggleProperty().addListener(this::updateShownValues);
        TextArea textArea = getTextArea();
        textArea.textProperty().addListener(this::updateLookupTable);
        textArea.caretPositionProperty().addListener(this::onCaretPositionChanged);
        textArea.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onTextAreaClicked);
    }

    protected abstract void remove(E f, WritableStyleableMapAccessor<Object> finalSelectedAccessor);

    private void select(ActionEvent event) {
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(textArea.getText(), null);
            if (!parser.getParseExceptions().isEmpty()) {
                System.err.println("StyleAttributesInspector:\n" + parser.getParseExceptions().toString().replace(',', '\n'));
            }

            ObservableMap<String, Set<E>> pseudoStyles = FXCollections.observableHashMap();
            Set<E> fs = new LinkedHashSet<>(getSelection());
            pseudoStyles.put("selected", fs);

            List<E> matchedFigures = new ArrayList<>();
            StylesheetsManager<E> sm = getStyleManager();
            SelectorModel<E> fsm = sm.getSelectorModel();
            fsm.additionalPseudoClassStatesProperty().setValue(pseudoStyles);
            for (E f : getEntities()) {
                if (sm.matchesElement(s, f)) {
                    matchedFigures.add(f);
                }
            }

            getSelection().clear();
            getSelection().addAll(matchedFigures);

            showSelection();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

    }

    @NonNull SetProperty<E> selectionProperty() {
        return selection;
    }

    protected abstract void set(E f, WritableStyleableMapAccessor<Object> finalSelectedAccessor, Object o);

    protected abstract void setHelpText(String helpText);

    private void showPicker(int caretPosition, double screenX, double screenY) {
        LookupEntry entry = getLookupEntryAt(caretPosition);
        Declaration declaration = entry == null ? null : entry.declaration;
        StyleRule styleRule = entry == null ? null : entry.styleRule;

        if (styleRule != null && declaration != null) {
            ObservableMap<String, Set<E>> pseudoStyles = createPseudoStyles();

            StylesheetsManager<E> sm = getStyleManager();
            SelectorModel<E> fsm = sm.getSelectorModel();
            fsm.additionalPseudoClassStatesProperty().setValue(pseudoStyles);
            Set<E> selectedF = new LinkedHashSet<>();
            WritableStyleableMapAccessor<?> selectedAccessor = null;
            boolean multipleAccessorTypes = false;
            for (E f : getEntities()) {
                if (null != styleRule.getSelectorGroup().matchSelector(fsm, f)) {
                    WritableStyleableMapAccessor<?> accessor = getAccessor(fsm, f, declaration.getNamespace(), declaration.getPropertyName());
                    if (selectedAccessor == null || selectedAccessor == accessor) {
                        selectedAccessor = accessor;
                        selectedF.add(f);
                    } else {
                        multipleAccessorTypes = true;
                    }
                }
            }
            if (!multipleAccessorTypes && selectedAccessor != null && !selectedF.isEmpty()) {
                @SuppressWarnings("unchecked")
                Picker<Object> picker = (Picker<Object>) createAndCachePicker(selectedAccessor);

                if (picker != null) {
                    Object initialValue = null;
                    @SuppressWarnings("unchecked")
                    WritableStyleableMapAccessor<Object> finalSelectedAccessor
                            = (WritableStyleableMapAccessor<Object>) selectedAccessor;
                    for (E f : selectedF) {
                        initialValue = get(f, finalSelectedAccessor);
                        break;
                    }
                    BiConsumer<Boolean, Object> lambda = (b, o) -> {
                        if (b) {
                            for (E f : selectedF) {
                                AbstractStyleAttributesInspector.this.set(f, finalSelectedAccessor, o);
                            }
                        } else {
                            for (E f : selectedF) {
                                AbstractStyleAttributesInspector.this.remove(f, finalSelectedAccessor);
                            }
                        }
                        invalidateTextArea(null);
                    };
                    picker.show(getTextArea(), screenX, screenY,
                            initialValue, lambda);
                }
            }
        }

    }

    private boolean isApplying;

    /**
     * This method shows the selection in the drawing view, by scrolling
     * the selected elements into the view and "jiggling" the handles.
     * <p>
     * This method is called when the user hits the "select" button.
     */
    protected abstract void showSelection();

    protected void invalidateTextArea(Observable observable) {
        if (!isApplying && textAreaValid && updateContentsCheckBox.isSelected()) {
            textAreaValid = false;
            if (isShowing()) {
                Platform.runLater(this::validateTextArea);
            }
        }
    }

    private void validateTextArea() {
        if (!textAreaValid) {
            if (updateContentsCheckBox.isSelected()) {
                updateTextArea();
            }
            textAreaValid = true;
        }
    }

    protected void updateLookupTable(Observable o) {
        lookupTable.clear();
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(getTextArea().getText(), null);
            for (StyleRule r : s.getStyleRules()) {
                for (Declaration d : r.getDeclarations()) {
                    lookupTable.add(new LookupEntry(d.getStartPos(), r, d));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void updateShownValues(Observable o) {
        Preferences prefs = Preferences.userNodeForPackage(StyleAttributesInspector.class);
        String origin;
        if (showAttributeValues.isSelected()) {
            origin = "user";
        } else if (showStylesheetValues.isSelected()) {
            origin = "author";
        } else if (showUserAgentValues.isSelected()) {
            origin = "userAgent";
        } else {
            origin = "styled";
        }
        prefs.put("shownValues", origin);

        invalidateTextArea(null);
    }

    protected void updateTextArea() {
        final boolean decompose = !composeAttributesCheckBox.isSelected();

        // handling of emptyness must be consistent with code in apply() method
        Set<E> selectedOrRoot = new LinkedHashSet<>(getSelection());
        if (selectedOrRoot.isEmpty()) {
            selectedOrRoot.add(getRoot());
        }

        StylesheetsManager<E> styleManager = getStyleManager();
        ObservableMap<String, Set<E>> pseudoStyles = FXCollections.observableHashMap();
        Set<E> fs = new LinkedHashSet<>(selectedOrRoot);
        pseudoStyles.put("selected", fs);
        StylesheetsManager<E> sm = getStyleManager();
        if (sm == null) {
            return;
        }
        SelectorModel<E> selectorModel = sm.getSelectorModel();
        selectorModel.additionalPseudoClassStatesProperty().setValue(pseudoStyles);
        SelectorGroup selector = updateSelector(selectedOrRoot, selectorModel);

        List<E> matchedFigures;
        if (updateSelectorCheckBox.isSelected()) {
            matchedFigures = new ArrayList<>(getSelection());
        } else {
            matchedFigures =
                    StreamSupport.stream(getEntities().spliterator(), true).filter(entity ->
                            selector.matches(selectorModel, entity)).collect(Collectors.toList());
        }


        collectHelpTexts(selectedOrRoot);
        Map<QualifiedName, String> attr = collectAttributeValues(decompose, matchedFigures, selectorModel);

        StringBuilder buf = new StringBuilder();
        CssPrettyPrinter pp = new CssPrettyPrinter(buf);
        selector.produceTokens(t -> pp.append(t.fromToken()));
        pp.append(" {");
        for (Map.Entry<QualifiedName, String> a : attr.entrySet()) {
            pp.append("\n  ").append(a.getKey().getName()).append(": ");
            pp.append(a.getValue());
            pp.append(";");
        }
        pp.append("\n}");

        updateStylesheetInfo(pp, matchedFigures, styleManager);

        textArea.setText(buf.toString());
        int rows = 1;
        for (int i = 0; i < buf.length(); i++) {
            if (buf.charAt(i) == '\n') {
                rows++;
            }
        }
        textArea.setPrefRowCount(Math.min(Math.max(5, rows), 25));
    }

    private void updateStylesheetInfo(CssPrettyPrinter pp, List<E> matchedFigures, StylesheetsManager<E> styleManager) {
        final List<StylesheetsManager.StylesheetInfo> stylesheets = styleManager.getStylesheets();
        Map<StylesheetsManager.StylesheetInfo, Set<StyleRule>> matchedInfos = new LinkedHashMap<>();

        final ArrayList<StylesheetsManager.StylesheetInfo> stylesheetInfos = new ArrayList<>();
        for (StylesheetsManager.StylesheetInfo stylesheet : stylesheets) {
            final StyleOrigin origin = stylesheet.getOrigin();
            switch (origin) {

                case USER_AGENT:
                    if (showUserAgentValues.isSelected())
                        stylesheetInfos.add(stylesheet);
                    break;
                case USER:
                    break;
                case AUTHOR:
                    if (showStylesheetValues.isSelected())
                        stylesheetInfos.add(stylesheet);
                    break;
                case INLINE:
                    break;
            }
        }

        if (!stylesheetInfos.isEmpty()) {
            for (E f : matchedFigures) {
                for (StylesheetsManager.StylesheetInfo info : stylesheetInfos) {
                    final List<StyleRule> matchingRules = styleManager.getMatchingRulesForElement(info.getStylesheet(), f);
                    if (!matchingRules.isEmpty()) {
                        matchedInfos.computeIfAbsent(info, k -> new LinkedHashSet<>()).addAll(matchingRules);
                        break;
                    }
                }
            }
        }
        if (!matchedInfos.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            buf.append("\n/*");
            buf.append("\nThe following stylesheets match:");
            for (Map.Entry<StylesheetsManager.StylesheetInfo, Set<StyleRule>> matchedInfo : matchedInfos.entrySet()) {
                buf.append("\n  ");
                buf.append(matchedInfo.getKey().getOrigin());
                buf.append(": ");
                buf.append(matchedInfo.getKey().getUri().toString());
                buf.append("\n  Rules:");
                for (StyleRule rule : matchedInfo.getValue()) {
                    buf.append("\n    ");
                    rule.getSelectorGroup().produceTokens(token -> buf.append(token.fromToken()));
                }
            }
            buf.append("\n*/");
            pp.append(buf.toString());
        }
    }

    private @NonNull Map<QualifiedName, String> collectAttributeValues(boolean decompose, @NonNull List<E> matchedFigures, @NonNull SelectorModel<E> selectorModel) {
        final StyleOrigin origin;
        if (showAttributeValues.isSelected()) {
            origin = StyleOrigin.USER;
        } else if (showStylesheetValues.isSelected()) {
            origin = StyleOrigin.AUTHOR;
        } else if (showUserAgentValues.isSelected()) {
            origin = StyleOrigin.USER_AGENT;
        } else {
            origin = null;
        }
        Map<QualifiedName, String> attr = new TreeMap<>();
        Predicate<QualifiedName> filter = getAttributeFilter();
        boolean first = true;
        for (E f : matchedFigures) {
            selectorModel.getAttributeNames(f);

            if (first) {
                first = false;
                for (QualifiedName qname : decompose ? selectorModel.getDecomposedAttributeNames(f) : selectorModel.getComposedAttributeNames(f)) {
                    if (!filter.test(qname)) {
                        continue;
                    }
                    String attribute = buildString(selectorModel.getAttribute(f, origin, qname.getNamespace(), qname.getName()));
                    attr.put(qname, attribute == null ? UNSPECIFIED_VALUE_PLACEHOLDER : attribute);
                }
            } else {
                attr.keySet().retainAll(selectorModel.getAttributeNames(f));
                for (QualifiedName qname : attr.keySet()) {
                    if (!filter.test(qname)) {
                        continue;
                    }
                    String oldAttrValue = attr.get(qname);
                    if (oldAttrValue != null) {
                        String newAttrValue = buildString(selectorModel.getAttribute(f, origin, qname.getNamespace(), qname.getName()));
                        if (newAttrValue == null) {
                            newAttrValue = UNSPECIFIED_VALUE_PLACEHOLDER;
                        }
                        if (!oldAttrValue.equals(newAttrValue)) {
                            attr.put(qname, MULTIPLE_VALUES_PLACEHOLDER);
                        }
                    }
                }
            }
        }
        return attr;
    }

    private SelectorGroup updateSelector(@NonNull Set<E> selection, @NonNull SelectorModel<E> selectorModel) {
        if (updateSelectorCheckBox.isSelected()) {
            return createSelector(selection, selectorModel);
        } else {
            return parseSelector();
        }
    }

    private SelectorGroup parseSelector() {
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(textArea.getText(), null);
            if (!parser.getParseExceptions().isEmpty()) {
                System.err.println("StyleAttributesInspector:\n" + parser.getParseExceptions().toString().replace(',', '\n'));
                return new SelectorGroup(Collections.emptyList());
            }
            for (StyleRule styleRule : s.getStyleRules()) {
                return styleRule.getSelectorGroup();
            }

            return new SelectorGroup(Collections.emptyList());
        } catch (IOException e) {
            return new SelectorGroup(Collections.emptyList());
        }
    }

    private @NonNull SelectorGroup createSelector(@NonNull Set<E> selection, @NonNull SelectorModel<E> selectorModel) {
        String id = null;
        String type = null;
        Set<String> styleClasses = new TreeSet<>();
        boolean first = true;
        for (E f : selection) {
            if (first) {
                id = selectorModel.getId(f);
                type = selectorModel.getType(f);
                first = false;
                styleClasses.addAll(selectorModel.getStyleClasses(f).asCollection());
            } else {
                id = null;
                type = Objects.equals(selectorModel.getType(f), type) ? type : null;
                styleClasses.retainAll(selectorModel.getStyleClasses(f).asCollection());
            }
        }

        List<SimpleSelector> selectors = new ArrayList<>();
        if (type != null && !type.isEmpty()) {
            selectors.add(new TypeSelector(null, type));
        }
        if (id != null && id.length() > 0) {
            selectors.add(new IdSelector(id));
        }
        for (String clazz : styleClasses) {
            selectors.add(new ClassSelector(clazz));
        }
        selectors.add(new SimplePseudoClassSelector("selected"));

        Selector prev = null;
        Collections.reverse(selectors);
        for (SimpleSelector s : selectors) {
            if (prev != null) {
                prev = new AndCombinator(s, prev);
            } else {
                prev = s;
            }
        }
        return new SelectorGroup(Arrays.asList(prev));
    }

    private static class LookupEntry implements Comparable<LookupEntry> {

        final int position;
        final StyleRule styleRule;
        final Declaration declaration;

        public LookupEntry(int position, StyleRule styleRule, Declaration declaration) {
            this.position = position;
            this.styleRule = styleRule;
            this.declaration = declaration;
        }

        @Override
        public int compareTo(@NonNull LookupEntry o) {
            return this.position - o.position;
        }

    }
}

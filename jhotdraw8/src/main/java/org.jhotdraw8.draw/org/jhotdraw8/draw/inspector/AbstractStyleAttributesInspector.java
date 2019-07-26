/*
 * @(#)AbstractStyleAttributesInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.SetProperty;
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
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.CssParser;
import org.jhotdraw8.css.CssPrettyPrinter;
import org.jhotdraw8.css.CssStroke;
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
import org.jhotdraw8.css.text.CssIdentConverter;
import org.jhotdraw8.draw.figure.TextFontableFigure;
import org.jhotdraw8.draw.popup.BooleanPicker;
import org.jhotdraw8.draw.popup.CssColorPicker;
import org.jhotdraw8.draw.popup.CssFontPicker;
import org.jhotdraw8.draw.popup.CssStrokePicker;
import org.jhotdraw8.draw.popup.EnumPicker;
import org.jhotdraw8.draw.popup.FontFamilyPicker;
import org.jhotdraw8.draw.popup.PaintablePicker;
import org.jhotdraw8.draw.popup.Picker;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

/**
 * Intentionally does not implement the inspector interface, so
 * that subclasses can use this inspector on different subject
 * types.
 *
 * @param <E>
 */
public abstract class AbstractStyleAttributesInspector<E> {
    private final ObjectProperty<Predicate<QualifiedName>> attributeFilter = new SimpleObjectProperty<>(k -> true);
    private final CssIdentConverter cssIdentConverter = new CssIdentConverter(false);
    private final ReadOnlyMapProperty<Class<?>, Picker<?>> valueTypePickerMap = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));
    private final ReadOnlyMapProperty<WriteableStyleableMapAccessor<?>, Picker<?>> accessorPickerMap = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));
    private SetProperty<E> selection = new SimpleSetProperty<>();

    {
        SetChangeListener<E> listener = change -> {
            textAreaInvalidated(selection);
        };
        selection.addListener((o, oldv, newv) -> {
            if (oldv != null) {
                oldv.removeListener(listener);
            }
            if (newv != null) {
                newv.addListener(listener);
                textAreaInvalidated(selection);
            }
        });
    }

    private Node node;
    @Nonnull
    private Map<QualifiedName, String> helpTexts = new HashMap<>();
    @Nonnull
    private List<LookupEntry> lookupTable = new ArrayList<>();
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

    public AbstractStyleAttributesInspector(@Nonnull URL fxmlUrl) {
        init(fxmlUrl);
    }

    public ReadOnlyMapProperty<WriteableStyleableMapAccessor<?>, Picker<?>> accessorPickerMapProperty() {
        return accessorPickerMap;
    }

    private void apply(ActionEvent event) {
        isApplying = true;
        CssParser parser = new CssParser();
        TextArea textArea = getTextArea();
        try {
            Stylesheet s = parser.parseStylesheet(textArea.getText());
            if (!parser.getParseExceptions().isEmpty()) {
                System.out.println("StyleAttributesInspector:\n" + parser.getParseExceptions().toString().replace(',', '\n'));
            }

            ObservableMap<String, Set<E>> pseudoStyles = createPseudoStyles();

            StylesheetsManager<E> sm = getStyleManager();
            SelectorModel<E> fsm = sm.getSelectorModel();
            fsm.additionalPseudoClassStatesProperty().setValue(pseudoStyles);
            for (E f : getEntities()) {
                if (sm.applyStylesheetTo(StyleOrigin.USER, s, f, false)) {
                    fireInvalidated(f);
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
    public Property<Predicate<QualifiedName>> attributeFilter() {
        return attributeFilter;
    }

    protected abstract Iterable<E> getEntities();

    @Nullable
    private String buildString(@Nullable List<CssToken> attribute) {
        if (attribute == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (CssToken t : attribute) {
            buf.append(t.fromToken());
        }
        return buf.toString();
    }

    protected void collectHelpTexts(@Nonnull Collection<E> figures) {
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

    private <T> Picker<T> createAndCachePicker(WriteableStyleableMapAccessor<T> acc) {
        ObservableMap<WriteableStyleableMapAccessor<?>, Picker<?>> amap = getAccessorPickerMap();
        @SuppressWarnings("unchecked") Picker<T> picker = (Picker<T>) amap.get(acc);
        if (picker == null) {
            picker = createPicker(acc);
            amap.put(acc, picker);
        }
        return picker;
    }

    @SuppressWarnings("unchecked")
    protected <T> Picker<T> createPicker(WriteableStyleableMapAccessor<T> acc) {
        Class<T> type = acc.getValueType();
        boolean nullable = true;
        if (acc.getConverter() instanceof CssConverter) {
            CssConverter<T> converter = (CssConverter) acc.getConverter();
            nullable = converter.isNullable();
        }
        Picker<?> p = null;
        if (type == Boolean.class) {
            p = new BooleanPicker(nullable);
        } else if (type == CssColor.class) {
            p = new CssColorPicker();
        } else if (type == CssStroke.class) {
            p = new CssStrokePicker();
        } else if (type == Paintable.class) {
            p = new PaintablePicker();
        } else if (type == CssFont.class) {
            p = new CssFontPicker();
        } else if (acc == TextFontableFigure.FONT_FAMILY) {
            p = new FontFamilyPicker();
        } else if (type.isEnum()) {
            Class<? extends Enum<?>> enumClazz = (Class<? extends Enum<?>>) type;
            @SuppressWarnings("rawtypes")
            EnumPicker suppress = new EnumPicker(enumClazz, acc.getConverter());
            p = suppress;
        }

        return (Picker<T>) p;
    }

    @Nonnull
    private ObservableMap<String, Set<E>> createPseudoStyles() {
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

    protected abstract void fireInvalidated(E f);

    protected abstract Object get(E f, WriteableStyleableMapAccessor<Object> finalSelectedAccessor);

    protected abstract WriteableStyleableMapAccessor<?> getAccessor(SelectorModel<E> fsm, E f, String propertyNamespace, String propertyName);

    public ObservableMap<WriteableStyleableMapAccessor<?>, Picker<?>> getAccessorPickerMap() {
        return accessorPickerMap.get();
    }

    public Predicate<QualifiedName> getAttributeFilter() {
        return attributeFilter.get();
    }

    public void setAttributeFilter(Predicate<QualifiedName> attributeFilter) {
        this.attributeFilter.set(attributeFilter);
    }

    protected abstract Converter<?> getConverter(SelectorModel<E> selectorModel, E f, String namespace, String name);

    @Nullable
    private LookupEntry getLookupEntryAt(@Nonnull int caretPosition) {
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

    protected abstract E getRoot();

    public ObservableSet<E> getSelection() {
        ObservableSet<E> es = selection.get();
        return es == null ? FXCollections.emptyObservableSet() : es;
    }

    protected abstract StylesheetsManager<E> getStyleManager();

    protected TextArea getTextArea() {
        return textArea;
    }

    protected void handleCaretPositionChanged(Observable o, Number oldv, @Nonnull Number newv) {
        LookupEntry entry = getLookupEntryAt(newv.intValue());
        Declaration d = entry == null ? null : entry.declaration;
        String helpText = null;
        if (d != null) {
            helpText = helpTexts.get(new QualifiedName(d.getPropertyNamespace(), d.getPropertyName()));
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

    private void handleTextAreaClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2 && mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            mouseEvent.consume();
            int caretPosition = getTextArea().getCaretPosition();
            showPicker(caretPosition, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    }

    protected void init(@Nonnull URL fxmlUrl) {
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
        composeAttributesCheckBox.setOnAction(event -> updateTextArea());
        node.visibleProperty().addListener(this::textAreaInvalidated);

        textArea.textProperty().addListener(this::updateLookupTable);
        textArea.caretPositionProperty().addListener(this::handleCaretPositionChanged);
        EventHandler<? super KeyEvent> eventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
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
        textArea.caretPositionProperty().addListener(this::handleCaretPositionChanged);
        textArea.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleTextAreaClicked);
    }

    protected abstract void remove(E f, WriteableStyleableMapAccessor<Object> finalSelectedAccessor);

    private void select(ActionEvent event) {
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(textArea.getText());
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

    SetProperty<E> selectionProperty() {
        return selection;
    }

    protected abstract void set(E f, WriteableStyleableMapAccessor<Object> finalSelectedAccessor, Object o);

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
            WriteableStyleableMapAccessor<?> selectedAccessor = null;
            boolean multipleAccessorTypes = false;
            for (E f : getEntities()) {
                if (null != styleRule.getSelectorGroup().match(fsm, f)) {
                    WriteableStyleableMapAccessor<?> accessor = getAccessor(fsm, f, declaration.getPropertyNamespace(), declaration.getPropertyName());
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
                    WriteableStyleableMapAccessor<Object> finalSelectedAccessor
                            = (WriteableStyleableMapAccessor<Object>) selectedAccessor;
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

                    };
                    picker.show(getTextArea(), screenX, screenY,
                            initialValue, lambda);
                }
            }
        }

    }

    private boolean isApplying;

    protected abstract void showSelection();

    protected void textAreaInvalidated(Observable observable) {
        if (!isApplying && textAreaValid) {
            textAreaValid = false;
            if (updateContentsCheckBox.isSelected()) {
                Platform.runLater((Runnable) this::updateTextArea);
            }
        }
    }

    protected void updateLookupTable(Observable o) {
        lookupTable.clear();
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(getTextArea().getText());
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

        updateTextArea();
    }

    protected void updateTextArea() {
        final boolean decompose = !composeAttributesCheckBox.isSelected();
        textAreaValid = true;


        // handling of emptyness must be consistent with code in apply() method
        Set<E> newValue = new LinkedHashSet<>(getSelection());
        if (newValue.isEmpty()) {
            newValue.add(getRoot());
        }

        StylesheetsManager<E> styleManager = getStyleManager();
        ObservableMap<String, Set<E>> pseudoStyles = FXCollections.observableHashMap();
        Set<E> fs = new LinkedHashSet<>(newValue);
        pseudoStyles.put("selected", fs);
        List<E> matchedFigures = new ArrayList<>();
        StylesheetsManager<E> sm = getStyleManager();
        SelectorModel<E> selectorModel = sm.getSelectorModel();
        selectorModel.additionalPseudoClassStatesProperty().setValue(pseudoStyles);
        SelectorGroup selector = updateSelector(selection, selectorModel);

        for (E entity : getEntities()) {
            if (selector.matches(selectorModel, entity)) {
                matchedFigures.add(entity);
            }
        }


        collectHelpTexts(newValue);
        Map<QualifiedName, String> attr = new TreeMap<>();
        Map<QualifiedName, String> description = new TreeMap<>();

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
                    attr.put(qname, attribute == null ? CssTokenType.IDENT_INITIAL : attribute);
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
                            newAttrValue = CssTokenType.IDENT_INITIAL;
                        }
                        if (!oldAttrValue.equals(newAttrValue)) {
                            attr.put(qname, "/* multiple values */");
                        }
                    }
                }
            }
        }

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

        textArea.setText(buf.toString());
        int rows = 1;
        for (int i = 0; i < buf.length(); i++) {
            if (buf.charAt(i) == '\n') {
                rows++;
            }
        }
        textArea.setPrefRowCount(Math.min(Math.max(5, rows), 25));
    }

    private SelectorGroup updateSelector(Set<E> selection, SelectorModel<E> selectorModel) {
        if (updateSelectorCheckBox.isSelected()) {
            return createSelector(selection, selectorModel);
        } else {
            return parseSelector();
        }
    }

    private SelectorGroup parseSelector() {
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(textArea.getText());
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

    @Nonnull
    private SelectorGroup createSelector(Set<E> selection, SelectorModel<E> selectorModel) {
        String id = null;
        String type = null;
        Set<String> styleClasses = new TreeSet<>();
        boolean first = true;
        for (E f : selection) {
            selectorModel.getAttributeNames(f);

            if (first) {
                id = selectorModel.getId(f);
                type = selectorModel.getType(f);
                first = false;
                styleClasses.addAll(selectorModel.getStyleClasses(f));
            } else {
                id = null;
                type = Objects.equals(selectorModel.getType(f), type) ? type : null;
                styleClasses.retainAll(selectorModel.getStyleClasses(f));
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
        public int compareTo(LookupEntry o) {
            return this.position - o.position;
        }

    }
}

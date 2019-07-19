/* @(#)StyleAttributesInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.css.StyleOrigin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssParser;
import org.jhotdraw8.css.CssPrettyPrinter;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.QualifiedName;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Stylesheet;
import org.jhotdraw8.css.text.CssIdentConverter;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.css.FigureSelectorModel;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.popup.BooleanPicker;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.prefs.Preferences;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StyleAttributesInspector extends AbstractSelectionInspector {

    @FXML
    private Button applyButton;

    @FXML
    private Button selectButton;

    @FXML
    private CheckBox updateContentsCheckBox;
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

    private final ObjectProperty<Predicate<QualifiedName>> attributeFilter = new SimpleObjectProperty<>(k -> true);

    private Node node;
    private final CssIdentConverter cssIdentConverter = new CssIdentConverter(false);

    @Nonnull
    private Map<QualifiedName, String> helpTexts = new HashMap<>();

    private final InvalidationListener modelInvalidationHandler = new InvalidationListener() {

        @Override
        public void invalidated(Observable observable) {
            if (node.isVisible() && updateContentsCheckBox.isSelected()) {
                invalidateTextArea();
            }
        }

    };

    @Nullable
    private final ChangeListener<DrawingModel> modelChangeHandler = (ObservableValue<? extends DrawingModel> observable, DrawingModel oldValue, DrawingModel newValue) -> {
        if (oldValue != null) {
            newValue.removeListener(modelInvalidationHandler);
        }
        if (newValue != null) {
            newValue.addListener(modelInvalidationHandler);
        }
    };
    private boolean textAreaValid = true;

    public StyleAttributesInspector() {
        this(StyleAttributesInspector.class.getResource("StyleAttributesInspector.fxml"));
    }

    public StyleAttributesInspector(@Nonnull URL fxmlUrl) {
        init(fxmlUrl);
    }

    private void init(@Nonnull URL fxmlUrl) {
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
        composeAttributesCheckBox.setSelected(prefs.getBoolean("composeAttributes", true));
        composeAttributesCheckBox.selectedProperty().addListener((o, oldValue, newValue)
                -> prefs.putBoolean("composeAttributes", newValue));

        applyButton.setOnAction(this::apply);
        selectButton.setOnAction(this::select);
        composeAttributesCheckBox.setOnAction(event -> updateTextArea());
        node.visibleProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                invalidateTextArea();
            }
        });

        textArea.textProperty().addListener(this::updateLookupTable);
        textArea.caretPositionProperty().addListener(this::handleCaretPositionChanged);
        textArea.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleTextAreaClicked);

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
    }

    private void handleTextAreaClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            mouseEvent.consume();
            int caretPosition = textArea.getCaretPosition();
            LookupEntry entry = getLookupEntryAt(caretPosition);
            Declaration declaration = entry == null ? null : entry.declaration;
            StyleRule styleRule = entry == null ? null : entry.styleRule;
            System.out.println("declaration double clicked: " + declaration);

            if (drawingView != null && styleRule != null && declaration != null) {
                Drawing d = drawingView.getDrawing();
                drawingView.getSelectedFigures();
                System.out.println("style rule: " + styleRule);
                ObservableMap<String, Set<Figure>> pseudoStyles = createPseudoStyles(d);

                StylesheetsManager<Figure> sm = d.getStyleManager();
                FigureSelectorModel fsm = (FigureSelectorModel) sm.getSelectorModel();
                fsm.additionalPseudoClassStatesProperty().setValue(pseudoStyles);
                Set<Figure> selected = new LinkedHashSet<>();
                WriteableStyleableMapAccessor<?> selectedAccessor = null;
                boolean multipleAccessorTypes = false;
                for (Figure f : d.breadthFirstIterable()) {
                    if (null != styleRule.getSelectorGroup().match(fsm, f)) {
                        System.out.println("matches " + f);
                        WriteableStyleableMapAccessor<?> accessor = fsm.getAccessor(f, declaration.getPropertyNamespace(), declaration.getPropertyName());
                        if (selectedAccessor == null || selectedAccessor == accessor) {
                            selectedAccessor = accessor;
                            selected.add(f);
                        } else {
                            multipleAccessorTypes = true;
                        }
                    }
                }
                if (!multipleAccessorTypes && selectedAccessor != null && !selected.isEmpty()) {
                    if (selectedAccessor.getValueType() == Boolean.class) {
                        BooleanPicker cp = new BooleanPicker();
                        cp.setFigures(FXCollections.observableSet(selected));
                        cp.setMapAccessor((WriteableStyleableMapAccessor<Boolean>) selectedAccessor);
                        cp.setDrawingView(drawingView);
                        cp.show(textArea, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    }
                }
            }
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

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    protected void handleDrawingViewChanged(@Nullable DrawingView oldValue, @Nullable DrawingView newValue) {
        if (oldValue != null) {
            oldValue.modelProperty().removeListener(modelChangeHandler);
            modelChangeHandler.changed(oldValue.modelProperty(), oldValue.getModel(), null);
        }
        if (newValue != null) {
            newValue.modelProperty().addListener(modelChangeHandler);
            modelChangeHandler.changed(newValue.modelProperty(), null, newValue.getModel());
            invalidateTextArea();
        }
    }

    @Override
    protected void handleSelectionChanged(Set<Figure> newValue) {
        if (node.isVisible() && updateContentsCheckBox.isSelected()) {
            invalidateTextArea();
        }
    }

    protected void updateTextArea() {
        final boolean decompose = !composeAttributesCheckBox.isSelected();
        textAreaValid = true;

        if (drawingView == null || drawingView.getDrawing() == null) {
            textArea.setText("");
            textArea.setPrefRowCount(5);
            return;
        }

        Drawing drawing = drawingView.getDrawing();

        // handling of emptyness must be consistent with code in apply() method
        Set<Figure> newValue = drawingView.getSelectedFigures();
        if (newValue.isEmpty()) {
            newValue = new LinkedHashSet<>();
            newValue.add(drawing);
        }
        collectHelpTexts(newValue);
        StylesheetsManager<Figure> styleManager = drawing.getStyleManager();
        SelectorModel<Figure> selectorModel = styleManager.getSelectorModel();
        String id = null;
        String type = null;
        Set<String> styleClasses = new TreeSet<>();
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
        for (Figure f : newValue) {
            selectorModel.getAttributeNames(f);

            if (first) {
                id = selectorModel.getId(f);
                type = selectorModel.getType(f);
                first = false;
                styleClasses.addAll(selectorModel.getStyleClasses(f));
                for (QualifiedName qname : decompose ? selectorModel.getDecomposedAttributeNames(f) : selectorModel.getComposedAttributeNames(f)) {
                    if (!filter.test(qname)) {
                        continue;
                    }
                    String attribute = buildString(selectorModel.getAttribute(f, origin, qname.getNamespace(), qname.getName()));
                    attr.put(qname, attribute == null ? CssTokenType.IDENT_INITIAL : attribute);
                }
            } else {
                attr.keySet().retainAll(selectorModel.getAttributeNames(f));
                id = null;
                type = Objects.equals(selectorModel.getType(f), type) ? type : null;
                styleClasses.retainAll(selectorModel.getStyleClasses(f));
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
        if (type != null && !type.isEmpty()) {
            pp.append(cssIdentConverter.toString(type));
        }
        if (id != null && id.length() > 0) {
            pp.append('#').append(cssIdentConverter.toString(id));
        }
        for (String clazz : styleClasses) {
            pp.append('.').append(cssIdentConverter.toString(clazz));
        }
        pp.append(":selected {");
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

    private void select(ActionEvent event) {
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(textArea.getText());
            if (!parser.getParseExceptions().isEmpty()) {
                System.err.println("StyleAttributesInspector:\n" + parser.getParseExceptions().toString().replace(',', '\n'));
            }

            Drawing d = drawingView.getDrawing();
            DrawingModel m = drawingView.getModel();
            ObservableMap<String, Set<Figure>> pseudoStyles = FXCollections.observableHashMap();
            Set<Figure> fs = new HashSet<>(drawingView.getSelectedFigures());

            // handling of emptyness must be consistent with code in
            // handleSelectionChanged() method
            if (fs.isEmpty()) {
                fs.add(d);
            }

            pseudoStyles.put("selected", fs);

            List<Figure> matchedFigures = new ArrayList<>();
            StylesheetsManager<Figure> sm = d.getStyleManager();
            FigureSelectorModel fsm = (FigureSelectorModel) sm.getSelectorModel();
            fsm.additionalPseudoClassStatesProperty().setValue(pseudoStyles);
            for (Figure f : d.breadthFirstIterable()) {
                if (sm.matchesElement(s, f)) {
                    matchedFigures.add(f);
                }
            }

            drawingView.getSelectedFigures().clear();
            drawingView.getSelectedFigures().addAll(matchedFigures);
            drawingView.scrollSelectedFiguresToVisible();
            drawingView.jiggleHandles();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

    }

    private void apply(ActionEvent event) {
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(textArea.getText());
            if (!parser.getParseExceptions().isEmpty()) {
                System.out.println("StyleAttributesInspector:\n" + parser.getParseExceptions().toString().replace(',', '\n'));
            }

            Drawing d = drawingView.getDrawing();
            DrawingModel m = drawingView.getModel();
            ObservableMap<String, Set<Figure>> pseudoStyles = createPseudoStyles(d);

            StylesheetsManager<Figure> sm = d.getStyleManager();
            FigureSelectorModel fsm = (FigureSelectorModel) sm.getSelectorModel();
            fsm.additionalPseudoClassStatesProperty().setValue(pseudoStyles);
            for (Figure f : d.breadthFirstIterable()) {
                if (sm.applyStylesheetTo(StyleOrigin.USER, s, f, true)) {
                    m.fireStyleInvalidated(f);
                    m.fireNodeInvalidated(f);
                    m.fireTransformInvalidated(f);
                    m.fireLayoutInvalidated(f);
                }
            }
            drawingView.recreateHandles();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    private ObservableMap<String, Set<Figure>> createPseudoStyles(Drawing d) {
        ObservableMap<String, Set<Figure>> pseudoStyles = FXCollections.observableHashMap();
        Set<Figure> fs = new LinkedHashSet<>(drawingView.getSelectedFigures());

        // handling of emptyness must be consistent with code in
        // handleSelectionChanged() method
        if (fs.isEmpty()) {
            fs.add(d);
        }

        pseudoStyles.put("selected", fs);
        return pseudoStyles;
    }

    private void invalidateTextArea() {
        if (textAreaValid) {
            textAreaValid = false;
            Platform.runLater((Runnable) this::updateTextArea);
        }
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

    @Nonnull
    private List<LookupEntry> lookupTable = new ArrayList<>();

    protected void updateLookupTable(Observable o) {
        lookupTable.clear();
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(textArea.getText());
            for (StyleRule r : s.getStyleRules()) {
                for (Declaration d : r.getDeclarations()) {
                    lookupTable.add(new LookupEntry(d.getStartPos(), r, d));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void handleCaretPositionChanged(Observable o, Number oldv, @Nonnull Number newv) {
        LookupEntry entry = getLookupEntryAt(newv.intValue());
        Declaration d = entry == null ? null : entry.declaration;
        String helpText = null;
        if (d != null) {
            helpText = helpTexts.get(new QualifiedName(d.getPropertyNamespace(), d.getPropertyName()));
        }

        if (drawingView != null) {
            Drawing drawing = drawingView.getDrawing();
            StylesheetsManager<Figure> sm = drawing.getStyleManager();

            String smHelpText = sm.getHelpText();
            if (helpText == null) {
                helpText = smHelpText;
            } else if (smHelpText == null || !smHelpText.isEmpty()) {
                helpText = helpText + "\n\n" + smHelpText;
            }


            drawingView.getEditor().setHelpText(helpText);
        }
    }

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

    protected void collectHelpTexts(@Nonnull Collection<Figure> figures) {
        Drawing drawing = drawingView.getDrawing();
        StylesheetsManager<Figure> styleManager = drawing.getStyleManager();
        FigureSelectorModel selectorModel = (FigureSelectorModel) styleManager.getSelectorModel();

        for (Figure f : figures) {
            for (QualifiedName qname : selectorModel.getAttributeNames(f)) {
                Converter<?> c = selectorModel.getConverter(f, qname.getNamespace(), qname.getName());
                if (c != null && c.getHelpText() != null) {
                    helpTexts.put(qname, c.getHelpText());
                }
            }
        }
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

    public Predicate<QualifiedName> getAttributeFilter() {
        return attributeFilter.get();
    }

    public void setAttributeFilter(Predicate<QualifiedName> attributeFilter) {
        this.attributeFilter.set(attributeFilter);
    }
}

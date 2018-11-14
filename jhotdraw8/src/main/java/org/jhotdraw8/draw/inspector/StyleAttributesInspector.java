/* @(#)StyleAttributesInspector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssParser;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.QualifiedName;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.ast.Stylesheet;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.css.text.CssIdentConverter;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.draw.css.FigureSelectorModel;
import org.jhotdraw8.text.Converter;

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
    private RadioButton showSpecifiedValues;

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
            loader.setResources(Labels.getBundle());
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
        textArea.caretPositionProperty().addListener(this::updateHelpText);

        switch (prefs.get("shownValues", "user")) {
            case "author":
                showStylesheetValues.setSelected(true);
                break;
            case "user":
                showAttributeValues.setSelected(true);
                break;
            case "styled":
            default:
                showSpecifiedValues.setSelected(true);
                break;
        }

        shownValues.selectedToggleProperty().addListener(this::updateShownValues);
    }

    protected void updateShownValues(Observable o) {
        Preferences prefs = Preferences.userNodeForPackage(StyleAttributesInspector.class);
        String origin;
        if (showAttributeValues.isSelected()) {
            origin = "user";
        } else if (showStylesheetValues.isSelected()) {
            origin = "author";
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
        } else {
            origin = null;
        }

        boolean first = true;
        for (Figure f : newValue) {
            selectorModel.getAttributeNames(f);
            if (first) {
                first = false;
                id = selectorModel.getId(f);
                type = selectorModel.getType(f);
                styleClasses.addAll(selectorModel.getStyleClasses(f));
                for (QualifiedName qname : decompose ? selectorModel.getDecomposedAttributeNames(f) : selectorModel.getComposedAttributeNames(f)) {
                    String attribute = buildString(selectorModel.getAttribute(f, origin, qname.getNamespace(), qname.getName()));
                    attr.put(qname, attribute == null ? CssTokenType.IDENT_INITIAL : attribute);
                }
            } else {
                attr.keySet().retainAll(selectorModel.getAttributeNames(f));
                id = null;
                type = selectorModel.getType(f).equals(type) ? type : null;
                styleClasses.retainAll(selectorModel.getStyleClasses(f));
                for (QualifiedName qname : attr.keySet()) {
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
        if (type != null && type.length() > 0) {
            buf.append(cssIdentConverter.toString(type));
        }
        if (id != null && id.length() > 0) {
            buf.append('#').append(cssIdentConverter.toString(id));
        }
        for (String clazz : styleClasses) {
            buf.append('.').append(cssIdentConverter.toString(clazz));
        }
        buf.append(":selected {");
        for (Map.Entry<QualifiedName, String> a : attr.entrySet()) {
            buf.append("\n  ").append(a.getKey().getName()).append(": ");
            buf.append(a.getValue());
            buf.append(";");
        }
        buf.append("\n}");

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
        if (attribute==null)return null;
        StringBuilder buf = new StringBuilder();
        for (CssToken t:attribute)buf.append(t.fromToken());
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
            ObservableMap<String, Set<Figure>> pseudoStyles = FXCollections.observableHashMap();
            Set<Figure> fs = new HashSet<>(drawingView.getSelectedFigures());

            // handling of emptyness must be consistent with code in
            // handleSelectionChanged() method
            if (fs.isEmpty()) {
                fs.add(d);
            }

            pseudoStyles.put("selected", fs);

            StylesheetsManager<Figure> sm = d.getStyleManager();
            FigureSelectorModel fsm = (FigureSelectorModel) sm.getSelectorModel();
            fsm.additionalPseudoClassStatesProperty().setValue(pseudoStyles);
            for (Figure f : d.breadthFirstIterable()) {
                if (sm.applyStylesheetTo(StyleOrigin.USER, s, f)) {
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
        }
    }

    private void invalidateTextArea() {
        if (textAreaValid) {
            textAreaValid = false;
            Platform.runLater((Runnable) this::updateTextArea);
        }
    }

    private static class HelptextLookupEntry implements Comparable<HelptextLookupEntry> {

        final int position;
        final Declaration declaration;

        public HelptextLookupEntry(int position, Declaration declaration) {
            this.position = position;
            this.declaration = declaration;
        }

        @Override
        public int compareTo(HelptextLookupEntry o) {
            return this.position - o.position;
        }

    }

    @Nonnull
    private List<HelptextLookupEntry> helptextLookupTable = new ArrayList<>();

    protected void updateLookupTable(Observable o) {
        helptextLookupTable.clear();
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(textArea.getText());
            for (StyleRule r : s.getStyleRules()) {
                for (Declaration d : r.getDeclarations()) {
                    helptextLookupTable.add(new HelptextLookupEntry(d.getStartPos(), d));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void updateHelpText(Observable o, Number oldv, @Nonnull Number newv) {
        int insertionPoint = Collections.binarySearch(helptextLookupTable, new HelptextLookupEntry(newv.intValue(), null));
        if (insertionPoint < 0) {
            insertionPoint = (-(insertionPoint) - 1) - 1;
        }
        Declaration d = null;
        if (0 <= insertionPoint && insertionPoint < helptextLookupTable.size()) {
            HelptextLookupEntry entry = helptextLookupTable.get(insertionPoint);
            if (newv.intValue() <= entry.declaration.getEndPos()) {
                d = entry.declaration;
            }
        }
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


            drawingView.setHelpText(helpText);
        }
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
}

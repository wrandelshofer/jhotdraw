/* @(#)StyleAttributesInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import com.sun.javafx.scene.DirtyBits;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import org.jhotdraw.css.CssParser;
import org.jhotdraw.css.SelectorModel;
import org.jhotdraw.css.ast.Stylesheet;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.gui.PlatformUtil;
import org.jhotdraw.text.CssIdentConverter;
import org.jhotdraw.util.Resources;
import org.jhotdraw.css.StylesheetsManager;
import org.jhotdraw.draw.css.FigureSelectorModel;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class StyleAttributesInspector extends AbstractSelectionInspector {

    @FXML
    private Button applyButton;

    @FXML
    private CheckBox updateContentsCheckBox;

    @FXML
    private TextArea textArea;
    private Node node;
    
    private final CssIdentConverter cssIdentConverter = new CssIdentConverter();

    private final InvalidationListener modelInvalidationHandler = new InvalidationListener() {

        @Override
        public void invalidated(Observable observable) {
            if (node.isVisible()&&updateContentsCheckBox.isSelected()) {
                invalidateTextArea();
            }
        }

    };

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

    public StyleAttributesInspector(URL fxmlUrl) {
        init(fxmlUrl);
    }

    private void init(URL fxmlUrl) {
        // We must use invoke and wait here, because we instantiate Tooltips
        // which immediately instanciate a Window and a Scene. 
        PlatformUtil.invokeAndWait(() -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(Resources.getBundle("org.jhotdraw.draw.gui.Labels"));
            loader.setController(this);

            try (InputStream in = fxmlUrl.openStream()) {
                node = loader.load(in);
            } catch (IOException ex) {
                throw new InternalError(ex);
            }
        });
        Preferences prefs = Preferences.userNodeForPackage(GridInspector.class);
        updateContentsCheckBox.setSelected(prefs.getBoolean("updateContents", true));
        updateContentsCheckBox.selectedProperty().addListener((o, oldValue, newValue)
                -> prefs.putBoolean("updateContents", newValue));

        applyButton.setOnAction(event -> apply());

        node.visibleProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                invalidateTextArea();
            }
        });
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    protected void handleDrawingViewChanged(DrawingView oldValue, DrawingView newValue) {
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
        if (node.isVisible()&&updateContentsCheckBox.isSelected()) {
            invalidateTextArea();
        }
    }

    protected void updateTextArea() {
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
            newValue = new LinkedHashSet<Figure>();
            newValue.add(drawing);
        }

        StylesheetsManager<Figure> styleManager = drawing.getStyleManager();
        SelectorModel<Figure> selectorModel = styleManager.getSelectorModel();
        String id = null;
        String type = null;
        Set<String> styleClasses = new TreeSet<>();
        Map<String, String> attr = new TreeMap<>();
        Map<String, String> description = new TreeMap<>();

        boolean first = true;
        for (Figure f : newValue) {
            selectorModel.getAttributeNames(f);
            if (first) {
                first = false;
                id = selectorModel.getId(f);
                type = selectorModel.getType(f);
                styleClasses.addAll(selectorModel.getStyleClasses(f));
                for (String name : selectorModel.getNonDecomposedAttributeNames(f)) {
                    attr.put(name, selectorModel.getAttribute(f, name));
                }
            } else {
                attr.keySet().retainAll(selectorModel.getAttributeNames(f));
                id = null;
                type = selectorModel.getType(f).equals(type) ? type : null;
                styleClasses.retainAll(selectorModel.getStyleClasses(f));
                for (String name : attr.keySet()) {
                    String oldAttrValue = attr.get(name);
                    if (oldAttrValue != null) {
                        String newAttrValue = selectorModel.getAttribute(f, name);
                        if (!oldAttrValue.equals(newAttrValue)) {
                            attr.put(name, "/* multiple values */");
                        }
                    }
                }
            }
        }
        
        
        StringBuilder buf = new StringBuilder();
        if (type != null) {
            buf.append(cssIdentConverter.toString(type));
        }
        if (id != null) {
            buf.append('#').append(cssIdentConverter.toString(id)); 
        }
        for (String clazz : styleClasses) {
            buf.append('.').append(cssIdentConverter.toString(clazz)); 
        }
        buf.append(":selected {");
        for (Map.Entry<String, String> a : attr.entrySet()) {
            buf.append("\n  ").append(a.getKey()).append(": ");
            buf.append(a.getValue());
            buf.append(";");
        }
        buf.append("\n}");

        // XXX for some reason, textArea is never updated
        //     here we force the scene to update it.
        try {
            Method m = Node.class.getDeclaredMethod("impl_markDirty", DirtyBits.class);
            m.setAccessible(true);
            for (Parent p = textArea; p != null; p = p.getParent()) {
                m.invoke(p, DirtyBits.NODE_CONTENTS);
            }
        } catch (Exception e) {
            System.out.println("StylesAttributesInspector e:" + e);
        }

        textArea.setText(buf.toString());
        int rows=1;
        for (int i=0;i<buf.length();i++) {
            if (buf.charAt(i)=='\n')rows++;
        }
        textArea.setPrefRowCount(Math.min(Math.max(5,rows),25));
    }

    private void apply() {
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(textArea.getText());
            if (!parser.getParseExceptions().isEmpty()) {
                System.out.println("StyleAttributesInspector:\n" + parser.getParseExceptions().toString().replace(',', '\n'));
            }

            Drawing d = drawingView.getDrawing();
            DrawingModel m = drawingView.getModel();
            ObservableMap<String, Set<Figure>> pseudoStyles = FXCollections.observableHashMap();
            HashSet<Figure> fs = new HashSet<>(drawingView.getSelectedFigures());

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
                sm.applyStylesheetTo(StyleOrigin.USER, s, f);
                m.fireNodeInvalidated(f);
                m.fireTransformInvalidated(f);
                m.fireLayoutInvalidated(f);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private void invalidateTextArea() {
        if (textAreaValid) {
            textAreaValid = false;
            Platform.runLater(this::updateTextArea);
        }
    }

}

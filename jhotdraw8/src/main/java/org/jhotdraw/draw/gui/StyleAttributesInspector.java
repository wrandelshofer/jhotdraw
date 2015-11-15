/* @(#)StyleAttributesInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.css.StyleOrigin;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import org.jhotdraw.css.CssParser;
import org.jhotdraw.css.SelectorModel;
import org.jhotdraw.css.StyleManager;
import org.jhotdraw.css.ast.Stylesheet;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.gui.PlatformUtil;
import org.jhotdraw.util.Resources;

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

    public StyleAttributesInspector() {
        this(LayersInspector.class.getResource("StyleAttributesInspector.fxml"));
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

    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    protected void onSelectionChanged(Set<Figure> newValue) {
        if (!updateContentsCheckBox.isSelected()) {
            return;
        }
        if (drawingView == null || drawingView.getDrawing() == null) {
            textArea.setText("");
            return;
        }

        // handling of emptyness must be consistent with code in apply() method
        if (newValue.isEmpty()) {
            newValue = new HashSet<Figure>();
            newValue.add(drawingView.getDrawing());
        }

        Drawing d = drawingView.getDrawing();
        StyleManager<Figure> styleManager = d.getStyleManager();
        SelectorModel<Figure> selectorModel = styleManager.getSelectorModel();
        String id = null;
        String type = null;
        Set<String> styleClasses = new TreeSet<>();
        Map<String, String> attr = new TreeMap<>();

        boolean first = true;
        for (Figure f : newValue) {
            selectorModel.getAttributeNames(f);
            if (first) {
                first = false;
                id = selectorModel.getId(f);
                type = selectorModel.getType(f);
                styleClasses.addAll(selectorModel.getStyleClasses(f));
                for (String name : selectorModel.getAttributeNames(f)) {
                    attr.put(name, selectorModel.getAttributeValue(f, name));
                }
            } else {
                attr.keySet().retainAll(selectorModel.getAttributeNames(f));
                id = null;
                type = selectorModel.getType(f).equals(type) ? type : null;
                styleClasses.retainAll(selectorModel.getStyleClasses(f));
                for (String name : attr.keySet()) {
                    String oldAttrValue = attr.get(name);
                    if (oldAttrValue != null) {
                        String newAttrValue = selectorModel.getAttributeValue(f, name);
                        if (!oldAttrValue.equals(newAttrValue)) {
                            attr.put(name, "/* multiple values */");
                        }
                    }
                }
            }
        }
        StringBuilder buf = new StringBuilder();
        if (type != null) {
            buf.append(type);
        }
        if (id != null) {
            buf.append('#').append(id); // FIXME apply CSS escaping!!
        }
        for (String clazz : styleClasses) {
            buf.append('.').append(clazz); //  FIXME apply CSS escaping!!
        }
        buf.append(":selected {");
        for (Map.Entry<String, String> a : attr.entrySet()) {
            buf.append("\n  ").append(a.getKey()).append(": ");
            buf.append(a.getValue());
            buf.append(";");
        }
        buf.append("\n}");
        textArea.setText(buf.toString());
    }

    private void apply() {
        CssParser parser = new CssParser();
        try {
            Stylesheet s = parser.parseStylesheet(textArea.getText());
            if (!parser.getParseExceptions().isEmpty()) {
                System.out.println("StyleAttributesInspector:\n"+parser.getParseExceptions().toString().replace(',', '\n'));
            }

            Drawing d = drawingView.getDrawing();
            DrawingModel m = drawingView.getModel();
            HashMap<String, Set<Figure>> pseudoStyles = new HashMap<>();
            HashSet<Figure> fs = new HashSet<>(drawingView.getSelectedFigures());

            // handling of emptyness must be consistent with code in
            // onSelectionChanged() method
            if (fs.isEmpty()) {
                fs.add(d);
            }

            pseudoStyles.put("selected", fs);

            StyleManager<Figure> sm = d.getStyleManager();
            for (Figure f : d.breadthFirstIterable()) {
                sm.applyStylesheetTo(StyleOrigin.USER, s, f, pseudoStyles);
                m.fireNodeInvalidated(f);
                m.fireTransformInvalidated(f);
                m.fireLayoutInvalidated(f);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

}

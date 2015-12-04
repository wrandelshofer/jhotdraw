/* @(#)GrapherView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.samples.grapher;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.prefs.Preferences;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jhotdraw.app.AbstractView;
import org.jhotdraw.app.action.view.ToggleViewPropertyAction;
import org.jhotdraw.concurrent.BackgroundTask;
import org.jhotdraw.concurrent.TaskCompletionEvent;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.SimpleDrawingView;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.EditorView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.FillableFigure;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.figure.shape.RectangleFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.SimpleDrawingEditor;
import org.jhotdraw.draw.figure.misc.SimpleLabelFigure;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.StrokeableFigure;
import org.jhotdraw.draw.StyleableFigure;
import org.jhotdraw.draw.TextableFigure;
import org.jhotdraw.draw.action.BringToFrontAction;
import org.jhotdraw.draw.action.SendToBackAction;
import org.jhotdraw.draw.constrain.GridConstrainer;
import org.jhotdraw.draw.gui.DrawingInspector;
import org.jhotdraw.draw.gui.GridInspector;
import org.jhotdraw.draw.gui.Inspector;
import org.jhotdraw.draw.gui.LayersInspector;
import org.jhotdraw.draw.gui.StyleAttributesInspector;
import org.jhotdraw.draw.gui.StylesheetsInspector;
import org.jhotdraw.draw.gui.StyleClassesInspector;
import org.jhotdraw.draw.gui.ToolsToolbar;
import org.jhotdraw.draw.gui.ZoomToolbar;
import org.jhotdraw.draw.handle.HandleType;
import org.jhotdraw.draw.io.DefaultFigureFactory;
import org.jhotdraw.draw.io.FigureFactory;
import org.jhotdraw.draw.io.IdFactory;
import org.jhotdraw.draw.io.SimpleIdFactory;
import org.jhotdraw.draw.io.SimpleXmlIO;
import org.jhotdraw.draw.figure.shape.EllipseFigure;
import org.jhotdraw.draw.figure.shape.LineFigure;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.ConnectionTool;
import org.jhotdraw.draw.tool.SelectionTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.util.Resources;
import org.jhotdraw.util.prefs.PreferencesUtil;

/**
 *
 * @author werni
 */
public class GrapherView extends AbstractView implements EditorView {

    private Node node;

    @FXML
    private ToolBar toolsToolBar;
    @FXML
    private ScrollPane viewScrollPane;
    @FXML
    private ScrollPane detailsScrollPane;
    @FXML
    private SplitPane mainSplitPane;

    private DrawingView drawingView;

    private DrawingEditor editor;

    @FXML
    private VBox detailsVBox;

    private final static String GRAPHER_NAMESPACE_URI = "http://jhotdraw.org/samples/grapher";
    
    private final BooleanProperty detailsVisible = new SimpleBooleanProperty(this,"detailsVisible",true);

    /**
     * Counter for incrementing layer names.
     */
    private int counter;

    @Override
    public void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            node = loader.load(getClass().getResourceAsStream("GrapherView.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        drawingView = new SimpleDrawingView();
        // FIXME should use preferences!
        drawingView.setConstrainer(new GridConstrainer(0, 0, 10, 10, 11.25));
        //drawingView.setHandleType(HandleType.TRANSFORM);
        // 
        drawingView.getModel().addListener((InvalidationListener) drawingModel -> {
            modified.set(true);
        });

        editor = new SimpleDrawingEditor();
        editor.addDrawingView(drawingView);

        viewScrollPane.setContent(drawingView.getNode());

        //drawingView.setConstrainer(new GridConstrainer(0,0,10,10,45));
        ToolsToolbar ttbar = new ToolsToolbar(editor);
        Resources rsrc = Resources.getResources("org.jhotdraw.draw.Labels");
        Supplier<Layer> layerFactory = this::createLayer;
        Tool defaultTool;
        ttbar.addTool(defaultTool = new SelectionTool("selectionTool", rsrc), 0, 0);
        ttbar.addTool(new SelectionTool("selectionTool", HandleType.MOVE, rsrc), 0, 1);
        ttbar.addTool(new CreationTool("edit.createRectangle", rsrc, RectangleFigure::new, layerFactory), 1, 0);
        ttbar.addTool(new CreationTool("edit.createEllipse", rsrc, EllipseFigure::new, layerFactory), 2, 0);
        ttbar.addTool(new CreationTool("edit.createLine", rsrc, LineFigure::new, layerFactory), 1, 1);
        ttbar.addTool(new CreationTool("edit.createText", rsrc,//
                () -> new SimpleLabelFigure(0, 0, "Hello", FillableFigure.FILL_COLOR, null, StrokeableFigure.STROKE_COLOR, null), //
                layerFactory), 3, 1);
        ttbar.addTool(new ConnectionTool("edit.createLineConnection", rsrc, LineConnectionFigure::new, layerFactory), 2, 1);
        ttbar.setDrawingEditor(editor);
        editor.setDefaultTool(defaultTool);
        toolsToolBar.getItems().add(ttbar);

        ZoomToolbar ztbar = new ZoomToolbar();
        ztbar.setDrawingView(drawingView);
        toolsToolBar.getItems().add(ztbar);

        getActionMap().put(SendToBackAction.ID, new SendToBackAction(getApplication(), editor));
        getActionMap().put(BringToFrontAction.ID, new BringToFrontAction(getApplication(), editor));
        getActionMap().put("view.toggleProperties", new ToggleViewPropertyAction(getApplication(), this,
                detailsVisible,
                "view.toggleProperties",
                Resources.getResources("org.jhotdraw.samples.grapher.Labels")));

        BackgroundTask<List<Node>> bg = new BackgroundTask<List<Node>>() {

            @Override
            protected List<Node> call() throws Exception {
                List<Node> list = new LinkedList<>();
                addInspector(new StyleAttributesInspector(), "styleAttributes", Priority.ALWAYS, list);
                addInspector(new StylesheetsInspector(), "stylesheets", Priority.ALWAYS, list);
                addInspector(new StyleClassesInspector(), "styleClasses", Priority.NEVER, list);
                addInspector(new LayersInspector(layerFactory), "layers", Priority.ALWAYS, list);
                addInspector(new DrawingInspector(), "drawing", Priority.NEVER, list);
                addInspector(new GridInspector(), "grid", Priority.NEVER, list);

                return list;
            }

            @Override
            protected void succeeded(List<Node> list) {
                for (Node n : list) {
                    Inspector i = (Inspector) n.getProperties().get("inspector");
                    i.setDrawingView(drawingView);
                }
                detailsVBox.getChildren().addAll(list);
            }

        };
        getApplication().execute(bg);

        /*
        Preferences prefs = Preferences.userNodeForPackage(GrapherView.class);
        detailsScrollPane.setMinSize(0.0, 0.0);
        detailsScrollPane.visibleProperty().addListener((o, oldValue, newValue) -> {
            prefs.putBoolean("view.propertiesPane.visible", newValue);
            detailsScrollPane.setPrefHeight(newValue ? ScrollPane.USE_COMPUTED_SIZE : 0.0);
        });
        detailsScrollPane.visibleProperty().set(prefs.getBoolean("view.propertiesPane.visible", true));
*/
        detailsVBox.getStyleClass().add("inspector");
    }

    @Override
    public void start() {
getNode().getScene().getStylesheets().addAll(//
                GrapherApplication.class.getResource("/org/jhotdraw/draw/gui/inspector.css").toString(),//
                GrapherApplication.class.getResource("/org/jhotdraw/samples/grapher/grapher.css").toString()//
        );
        
        Preferences prefs = Preferences.userNodeForPackage(GrapherView.class);
        PreferencesUtil.installVisibilityPrefsHandlers(prefs, detailsScrollPane, detailsVisible, mainSplitPane, Side.RIGHT);
    }
    
    private void addInspector(Inspector inspector, String id, Priority grow, List<Node> list) {
        Resources r = Resources.getResources("org.jhotdraw.draw.gui.Labels");

        Accordion a = new Accordion();
        a.getStyleClass().setAll("inspector", "flush");
        Pane n = (Pane) inspector.getNode();
        TitledPane t = new TitledPane(r.getString(id + ".toolbar"), n);
        a.getPanes().add(t);
        list.add(a);
        a.getProperties().put("inspector", inspector);

        // Make sure that an expanded accordion has the specified grow priority.
        // But when it is collapsed it should have none.
        t.expandedProperty().addListener((o, oldValue, newValue) -> {
            VBox.setVgrow(a, newValue ? grow : Priority.NEVER);
        });

        PreferencesUtil.installBooleanPropertyHandler(//
                Preferences.userNodeForPackage(GrapherView.class), id + ".expanded", t.expandedProperty());
        if (t.isExpanded()) {
            a.setExpandedPane(t);
            VBox.setVgrow(a, grow);
        }
    }

    private void addHInspector(Inspector inspector, String id, List<Node> list) {
        Resources r = Resources.getResources("org.jhotdraw.draw.gui.Labels");

        Accordion a = new Accordion();
        a.getStyleClass().setAll("inspector", "flush");
        Node n = inspector.getNode();
        n.setRotate(90);
        ((Pane) n).setPrefHeight(129);
        Group g = new Group();
        g.getChildren().add(n);
        TitledPane t = new TitledPane(r.getString(id + ".toolbar"), g);

        a.getPanes().add(t);

        g = new Group();
        a.setRotate(-90);
        g.getChildren().add(a);
        list.add(g);
        g.getProperties().put("inspector", inspector);

        PreferencesUtil.installBooleanPropertyHandler(Preferences.userNodeForPackage(GrapherView.class), id + ".expanded", t.expandedProperty());
        if (t.isExpanded()) {
            a.setExpandedPane(t);
        }

    }

    @Override
    public Node getNode() {
        return node;
    }

    public Layer createLayer() {
        Layer layer = new SimpleLayer();
        layer.set(StyleableFigure.STYLE_ID, "layer" + (++counter));
        return layer;
    }

    @Override
    public void read(URI uri, boolean append, EventHandler<TaskCompletionEvent<?>> callback) {
        BackgroundTask<SimpleDrawing> t = new BackgroundTask<SimpleDrawing>() {

            @Override
            protected SimpleDrawing call() throws Exception {
                try {
                    IdFactory idFactory = new SimpleIdFactory();
                    FigureFactory factory = new DefaultFigureFactory(idFactory);
                    SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
                    SimpleDrawing drawing = (SimpleDrawing) io.read(uri, null);
                    drawing.applyCss();
                    return drawing;
                } catch (Exception e) {
                    throw e;
                }
            }

            @Override
            protected void succeeded(SimpleDrawing value) {
                drawingView.setDrawing(value);

            }

        };
        t.addCompletionHandler(callback);
        getApplication().execute(t);
    }

    @Override
    public void write(URI uri, EventHandler<TaskCompletionEvent<?>> callback) {
        BackgroundTask<Void> t = new BackgroundTask<Void>() {

            @Override
            protected Void call() throws Exception {
                IdFactory idFactory = new SimpleIdFactory();
                FigureFactory factory = new DefaultFigureFactory(idFactory);
                SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
                io.write(uri, drawingView.getDrawing());
                return null;
            }
        };
        t.addCompletionHandler(callback);
        getApplication().execute(t);
    }

    @Override
    public void clear(EventHandler<TaskCompletionEvent<?>> callback) {
        Drawing d = new SimpleDrawing();
        drawingView.setDrawing(d);
        clearModified();
        callback.handle(new TaskCompletionEvent<Void>());
    }

    @FXML
    public void buttonPerformed(ActionEvent event) {
        Instant now = Instant.now();
        for (Figure f : drawingView.getDrawing().childrenProperty()) {
            if (f instanceof TextableFigure) {
                TextableFigure tf = (TextableFigure) f;
                tf.set(TextableFigure.TEXT, now.toString());
            }
        }
    }

    @Override
    public DrawingEditor getEditor() {
        return editor;
    }

    public Node getPropertiesPane() {
        return detailsScrollPane;
    }

}

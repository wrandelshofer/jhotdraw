/* @(#)GrapherView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.samples.grapher;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import java.util.prefs.Preferences;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
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
import org.jhotdraw.concurrent.FXWorker;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.SimpleDrawingView;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.EditorView;
import org.jhotdraw.draw.figure.FillableFigure;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.SimpleDrawingEditor;
import org.jhotdraw.draw.figure.LabelFigure;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.figure.StrokeableFigure;
import org.jhotdraw.draw.figure.StyleableFigure;
import org.jhotdraw.draw.action.BringToFrontAction;
import org.jhotdraw.draw.action.GroupAction;
import org.jhotdraw.draw.action.SendToBackAction;
import org.jhotdraw.draw.action.UngroupAction;
import org.jhotdraw.draw.constrain.GridConstrainer;
import org.jhotdraw.draw.figure.ImageFigure;
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
import org.jhotdraw.draw.figure.EllipseFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.LineFigure;
import org.jhotdraw.draw.gui.HierarchyInspector;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.ConnectionTool;
import org.jhotdraw.draw.tool.ImageCreationTool;
import org.jhotdraw.draw.tool.SelectionTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.svg.SvgExportOutputFormat;
import org.jhotdraw.util.Resources;
import org.jhotdraw.util.prefs.PreferencesUtil;

/**
 * GrapherView.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
    
    private final BooleanProperty detailsVisible = new SimpleBooleanProperty(this, "detailsVisible", true);

    /**
     * Counter for incrementing layer names.
     */
    private Map<String,Integer> counters = new HashMap<>();
    
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
        Resources rsrc = Resources.getResources("org.jhotdraw.samples.grapher.Labels");
        Supplier<Layer> layerFactory = ()->createFigure(SimpleLayer::new);
        Tool defaultTool;
        ttbar.addTool(defaultTool = new SelectionTool("tool.selectFigure", rsrc), 0, 0);
        ttbar.addTool(new SelectionTool("tool.selectPoint", HandleType.MOVE, rsrc), 0, 1);
        ttbar.addTool(new SelectionTool("tool.transform", HandleType.TRANSFORM, rsrc), 1, 1);
        ttbar.addTool(new CreationTool("edit.createRectangle", rsrc, ()->createFigure(RectangleFigure::new), layerFactory), 2, 0);
        ttbar.addTool(new CreationTool("edit.createEllipse", rsrc, ()->createFigure(EllipseFigure::new), layerFactory), 3, 0);
        ttbar.addTool(new CreationTool("edit.createLine", rsrc, ()->createFigure(LineFigure::new), layerFactory), 2, 1);
        ttbar.addTool(new CreationTool("edit.createText", rsrc,//
                () -> new LabelFigure(0, 0, "Hello", FillableFigure.FILL_COLOR, null, StrokeableFigure.STROKE_COLOR, null), //
                layerFactory), 4, 1);
        ttbar.addTool(new ConnectionTool("edit.createLineConnection", rsrc, ()->createFigure(LineConnectionFigure::new), layerFactory), 3, 1);
        ttbar.addTool(new ImageCreationTool("edit.createImage", rsrc, ()->createFigure(ImageFigure::new), layerFactory), 4, 0);
        ttbar.setDrawingEditor(editor);
        editor.setDefaultTool(defaultTool);
        toolsToolBar.getItems().add(ttbar);
        
        ZoomToolbar ztbar = new ZoomToolbar();
        ztbar.zoomFactorProperty().bindBidirectional(drawingView.zoomFactorProperty());
        toolsToolBar.getItems().add(ztbar);
        
        getActionMap().put(SendToBackAction.ID, new SendToBackAction(getApplication(), editor));
        getActionMap().put(BringToFrontAction.ID, new BringToFrontAction(getApplication(), editor));
        getActionMap().put("view.toggleProperties", new ToggleViewPropertyAction(getApplication(), this,
                detailsVisible,
                "view.toggleProperties",
                Resources.getResources("org.jhotdraw.samples.grapher.Labels")));
        getActionMap().put(GroupAction.ID, new GroupAction(getApplication(), editor,()->createFigure(GroupFigure::new)));
        getActionMap().put(UngroupAction.ID, new UngroupAction(getApplication(), editor));
        
        FXWorker.supply(() -> {
            List<Node> list = new LinkedList<>();
            addInspector(new StyleAttributesInspector(), "styleAttributes", Priority.ALWAYS, list);
            addInspector(new StyleClassesInspector(), "styleClasses", Priority.NEVER, list);
            addInspector(new StylesheetsInspector(), "styleSheets", Priority.ALWAYS, list);
            addInspector(new LayersInspector(layerFactory), "layers", Priority.ALWAYS, list);
            addInspector(new HierarchyInspector(), "figureHierarchy", Priority.ALWAYS, list);
            addInspector(new DrawingInspector(), "drawing", Priority.NEVER, list);
            addInspector(new GridInspector(), "grid", Priority.NEVER, list);
            return list;
        }).thenAccept(list-> {
            for (Node n : list) {
                Inspector i = (Inspector) n.getProperties().get("inspector");
                i.setDrawingView(drawingView);
            }
            detailsVBox.getChildren().addAll(list);
        }).exceptionally(e->{e.printStackTrace();return null;});
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
    
    @Override
    public Node getNode() {
        return node;
    }
    
    /** Creates a figure with a unique id.
     * @param <T> the figure type
     * @param supplier the supplier
     * @return the created figure
     */
    public <T extends Figure> T createFigure(Supplier<T> supplier) {
        T created = supplier.get();
        String prefix = created.getTypeSelector().toLowerCase();
        Integer counter = counters.get(prefix);
        Set<String> ids = new HashSet<>();
        counter = counter == null ? 1 : counter+1;
        // XXX O(n) !!!
        for (Figure f : drawingView.getDrawing().preorderIterable()) {
            ids.add(f.getId());
        }
        String id=prefix+counter;
        while (ids.contains(id)) {
            counter++;
            id=prefix+counter;
        }
        counters.put(created.getTypeSelector(), counter);
        created.set(StyleableFigure.STYLE_ID, id);
        return created;
    }
    
    @Override
    public CompletionStage<Void> read(URI uri, boolean append) {
        return FXWorker.supply(() -> {
            IdFactory idFactory = new SimpleIdFactory();
            FigureFactory factory = new DefaultFigureFactory(idFactory);
            SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
            SimpleDrawing drawing = (SimpleDrawing) io.read(uri, null);
            drawing.updateCss();
            return drawing;
        }).thenAccept(drawing
                -> drawingView.setDrawing(drawing)
        );
    }
    
    @Override
    public CompletionStage<Void> write(URI uri) {
        return FXWorker.run(() -> {
            if (uri.getPath().endsWith(".svg")) {
                SvgExportOutputFormat io = new SvgExportOutputFormat();
                io.write(uri, drawingView.getDrawing());
            } else {
                IdFactory idFactory = new SimpleIdFactory();
                FigureFactory factory = new DefaultFigureFactory(idFactory);
                SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
                io.write(uri, drawingView.getDrawing());
            }
        });
    }
    
    @Override
    public CompletionStage<Void> clear() {
        Drawing d = new SimpleDrawing();
        drawingView.setDrawing(d);
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public DrawingEditor getEditor() {
        return editor;
    }
    
    public Node getPropertiesPane() {
        return detailsScrollPane;
    }
}

/* @(#)GrapherProject.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.samples.grapher;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
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
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jhotdraw8.app.AbstractDocumentProject;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.view.ToggleBooleanAction;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.concurrent.FXWorker;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.EditorView;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.SimpleDrawingEditor;
import org.jhotdraw8.draw.SimpleDrawingView;
import org.jhotdraw8.draw.action.AddToGroupAction;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.action.BringToFrontAction;
import org.jhotdraw8.draw.action.GroupAction;
import org.jhotdraw8.draw.action.RemoveFromGroupAction;
import org.jhotdraw8.draw.action.RemoveTransformationsAction;
import org.jhotdraw8.draw.action.SelectChildrenAction;
import org.jhotdraw8.draw.action.SendToBackAction;
import org.jhotdraw8.draw.action.UngroupAction;
import org.jhotdraw8.draw.constrain.GridConstrainer;
import org.jhotdraw8.draw.figure.EllipseFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.figure.GroupFigure;
import org.jhotdraw8.draw.figure.ImageFigure;
import org.jhotdraw8.draw.figure.LabelFigure;
import org.jhotdraw8.draw.figure.LineConnectionFigure;
import org.jhotdraw8.draw.figure.LineFigure;
import org.jhotdraw8.draw.figure.PageFigure;
import org.jhotdraw8.draw.figure.PolygonFigure;
import org.jhotdraw8.draw.figure.PolylineFigure;
import org.jhotdraw8.draw.figure.RectangleFigure;
import org.jhotdraw8.draw.figure.SliceFigure;
import org.jhotdraw8.draw.figure.StrokeableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.inspector.DrawingInspector;
import org.jhotdraw8.draw.inspector.GridInspector;
import org.jhotdraw8.draw.inspector.HierarchyInspector;
import org.jhotdraw8.draw.inspector.Inspector;
import org.jhotdraw8.draw.inspector.LayersInspector;
import org.jhotdraw8.draw.inspector.StyleAttributesInspector;
import org.jhotdraw8.draw.inspector.StyleClassesInspector;
import org.jhotdraw8.draw.inspector.StylesheetsInspector;
import org.jhotdraw8.draw.inspector.ToolsToolbar;
import org.jhotdraw8.draw.inspector.ZoomToolbar;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.input.MultiClipboardInputFormat;
import org.jhotdraw8.draw.input.MultiClipboardOutputFormat;
import org.jhotdraw8.draw.inspector.Labels;
import org.jhotdraw8.draw.io.DefaultFigureFactory;
import org.jhotdraw8.draw.io.FigureFactory;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.draw.io.SimpleXmlIO;
import org.jhotdraw8.draw.tool.ConnectionTool;
import org.jhotdraw8.draw.tool.CreationTool;
import org.jhotdraw8.draw.tool.ImageCreationTool;
import org.jhotdraw8.draw.tool.SelectionTool;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.io.SvgExportOutputFormat;
import org.jhotdraw8.draw.tool.PolyCreationTool;
import org.jhotdraw8.svg.SvgExporter;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.util.prefs.PreferencesUtil;
import org.jhotdraw8.app.DocumentProject;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.PageLabelFigure;
import org.jhotdraw8.text.CssSize2D;
import org.jhotdraw8.text.CssSizeInsets;

/**
 * GrapherProject.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GrapherProject extends AbstractDocumentProject implements DocumentProject, EditorView {

    private final static String GRAPHER_NAMESPACE_URI = "http://jhotdraw.org/samples/grapher";
    private static final String VIEWTOGGLE_PROPERTIES = "view.toggleProperties";
    /**
     * Counter for incrementing layer names.
     */
    private Map<String, Integer> counters = new HashMap<>();
    @FXML
    private ScrollPane detailsScrollPane;
    @FXML
    private VBox detailsVBox;
    private final BooleanProperty detailsVisible = new SimpleBooleanProperty(this, "detailsVisible", true);

    private DrawingView drawingView;

    private DrawingEditor editor;
    @FXML
    private SplitPane mainSplitPane;
    private Node node;
    @FXML
    private ToolBar toolsToolBar;
    @FXML
    private ScrollPane viewScrollPane;

    private void addInspector(Inspector inspector, String id, Priority grow, List<Node> list) {
        Resources r = Labels.getResources();

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

        PreferencesUtil.installBooleanPropertyHandler(Preferences.userNodeForPackage(GrapherProject.class), id + ".expanded", t.expandedProperty());
        if (t.isExpanded()) {
            a.setExpandedPane(t);
            VBox.setVgrow(a, grow);
        }
    }

    @Override
    public CompletionStage<Void> clear() {
        Drawing d = new SimpleDrawing();
        d.set(StyleableFigure.ID, "drawing1");
        drawingView.setDrawing(d);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Creates a figure with a unique id.
     *
     * @param <T> the figure type
     * @param supplier the supplier
     * @return the created figure
     */
    public <T extends Figure> T createFigure(Supplier<T> supplier) {
        T created = supplier.get();
        String prefix = created.getTypeSelector().toLowerCase();
        Integer counter = counters.get(prefix);
        Set<String> ids = new HashSet<>();
        counter = counter == null ? 1 : counter + 1;
        // XXX O(n) !!!
        for (Figure f : drawingView.getDrawing().preorderIterable()) {
            ids.add(f.getId());
        }
        String id = prefix + counter;
        while (ids.contains(id)) {
            counter++;
            id = prefix + counter;
        }
        counters.put(created.getTypeSelector(), counter);
        created.set(StyleableFigure.ID, id);
        return created;
    }

    @Override
    public DrawingEditor getEditor() {
        return editor;
    }

    @Override
    public Node getNode() {
        return node;
    }

    public Node getPropertiesPane() {
        return detailsScrollPane;
    }

    @Override
    protected void initActionMap(HierarchicalMap<String, Action> map) {
        map.put(RemoveTransformationsAction.ID, new RemoveTransformationsAction(getApplication(), editor));
        map.put(SelectChildrenAction.ID, new SelectChildrenAction(getApplication(), editor));
        map.put(SendToBackAction.ID, new SendToBackAction(getApplication(), editor));
        map.put(BringToFrontAction.ID, new BringToFrontAction(getApplication(), editor));
        map.put(VIEWTOGGLE_PROPERTIES, new ToggleBooleanAction(
                getApplication(), this,
                VIEWTOGGLE_PROPERTIES,
                Resources.getResources("org.jhotdraw8.samples.grapher.Labels"), detailsVisible));
        map.put(GroupAction.ID, new GroupAction(getApplication(), editor, () -> createFigure(GroupFigure::new)));
        map.put(UngroupAction.ID, new UngroupAction(getApplication(), editor));
        map.put(AddToGroupAction.ID, new AddToGroupAction(getApplication(), editor));
        map.put(RemoveFromGroupAction.ID, new RemoveFromGroupAction(getApplication(), editor));

    }

    private Supplier<Layer> initToolBar() throws MissingResourceException {
        //drawingView.setConstrainer(new GridConstrainer(0,0,10,10,45));
        ToolsToolbar ttbar = new ToolsToolbar(editor);
        Resources labels = Resources.getResources("org.jhotdraw8.samples.grapher.Labels");
        Supplier<Layer> layerFactory = () -> createFigure(SimpleLayer::new);
        Tool defaultTool;
        ttbar.addTool(defaultTool = new SelectionTool("tool.selectFigure", HandleType.RESIZE, null, HandleType.LEAD, labels), 0, 0);
        ttbar.addTool(new SelectionTool("tool.selectPoint", HandleType.POINT, labels), 0, 1);
        ttbar.addTool(new SelectionTool("tool.transform", HandleType.TRANSFORM, labels), 1, 1);
        ttbar.addTool(new CreationTool("edit.createRectangle", labels, () -> createFigure(RectangleFigure::new), layerFactory), 2, 0);
        ttbar.addTool(new CreationTool("edit.createEllipse", labels, () -> createFigure(EllipseFigure::new), layerFactory), 3, 0);
        ttbar.addTool(new ConnectionTool("edit.createLineConnection", labels, () -> createFigure(LineConnectionFigure::new), layerFactory), 3, 1);
        ttbar.addTool(new CreationTool("edit.createLine", labels, () -> createFigure(LineFigure::new), layerFactory), 2, 1);
        ttbar.addTool(new PolyCreationTool("edit.createPolyline", labels, PolylineFigure.POINTS, () -> createFigure(PolylineFigure::new), layerFactory), 4, 1);
        ttbar.addTool(new PolyCreationTool("edit.createPolygon", labels, PolygonFigure.POINTS, () -> createFigure(PolygonFigure::new), layerFactory), 5, 1);
        ttbar.addTool(new CreationTool("edit.createText", labels,//
                () -> createFigure(() -> new LabelFigure(0, 0, "Hello", FillableFigure.FILL_COLOR, null, StrokeableFigure.STROKE_COLOR, null)), //
                layerFactory), 6, 1);
        ttbar.addTool(new CreationTool("edit.createPageLabel", labels,//
                () -> createFigure(() -> new PageLabelFigure(0, 0,
                        labels.getFormatted("pageLabel.text", PageLabelFigure.PAGE_PLACEHOLDER, PageLabelFigure.NUM_PAGES_PLACEHOLDER),
                        FillableFigure.FILL_COLOR, null, StrokeableFigure.STROKE_COLOR, null)), //
                layerFactory), 9, 1);
        ttbar.addTool(new ImageCreationTool("edit.createImage", labels, () -> createFigure(ImageFigure::new), layerFactory), 4, 0);
        ttbar.addTool(new CreationTool("edit.createSlice", labels, () -> createFigure(SliceFigure::new), layerFactory), 8, 0);
        ttbar.addTool(new CreationTool("edit.createPage", labels, () -> createFigure(() -> {
            PageFigure pf = new PageFigure();
            pf.set(PageFigure.PAPER_SIZE, new CssSize2D(297, 210, "mm"));
            pf.set(PageFigure.PAGE_INSETS, new CssSizeInsets(2, 1, 2, 1, "cm"));
            PageLabelFigure pl = new PageLabelFigure(740, 570, labels.getFormatted("pageLabel.text",
                    PageLabelFigure.PAGE_PLACEHOLDER, PageLabelFigure.NUM_PAGES_PLACEHOLDER),
                    FillableFigure.FILL_COLOR, null, StrokeableFigure.STROKE_COLOR, null);
            pf.add(pl);
            return pf;
        }), layerFactory), 8, 1);
        ttbar.setDrawingEditor(editor);
        editor.setDefaultTool(defaultTool);
        toolsToolBar.getItems().add(ttbar);
        return layerFactory;
    }

    @Override
    public void initView() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            node = loader.load(getClass().getResourceAsStream("GrapherProject.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        drawingView = new SimpleDrawingView();
        // FIXME should use preferences!
        drawingView.setConstrainer(new GridConstrainer(0, 0, 10, 10, 11.25, 5, 5));
        //drawingView.setHandleType(HandleType.TRANSFORM);
        //
        drawingView.getModel().addListener((InvalidationListener) drawingModel -> {
            modified.set(true);
        });

        IdFactory idFactory = new SimpleIdFactory();
        FigureFactory factory = new DefaultFigureFactory(idFactory);
        SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
        drawingView.setClipboardOutputFormat(new MultiClipboardOutputFormat(
                io, new SvgExportOutputFormat(), new BitmapExportOutputFormat()));
        drawingView.setClipboardInputFormat(new MultiClipboardInputFormat(io));

        editor = new SimpleDrawingEditor();
        editor.addDrawingView(drawingView);

        viewScrollPane.setContent(drawingView.getNode());

        Supplier<Layer> layerFactory = initToolBar();

        ZoomToolbar ztbar = new ZoomToolbar();
        ztbar.zoomFactorProperty().bindBidirectional(drawingView.zoomFactorProperty());
        toolsToolBar.getItems().add(ztbar);

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
        }).thenAccept(list -> {
            for (Node n : list) {
                Inspector i = (Inspector) n.getProperties().get("inspector");
                i.setDrawingView(drawingView);
            }
            detailsVBox.getChildren().addAll(list);
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    @Override
    public CompletionStage<Void> print(PrinterJob job) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CompletionStage<Void> read(URI uri, DataFormat format, Map<? super Key<?>, Object> options, boolean append) {
        return FXWorker.supply(() -> {
            IdFactory idFactory = new SimpleIdFactory();
            FigureFactory factory = new DefaultFigureFactory(idFactory);
            SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
            SimpleDrawing drawing = (SimpleDrawing) io.read(uri, null);
            System.out.println("READING..." + uri);
            return drawing;
        }).thenAccept(drawingView::setDrawing);
    }

    @Override
    public void start() {
        getNode().getScene().getStylesheets().addAll(//
                GrapherApplication.class.getResource("/org/jhotdraw8/draw/inspector/inspector.css").toString(),//
                GrapherApplication.class.getResource("/org/jhotdraw8/samples/grapher/grapher.css").toString()//
        );

        Preferences prefs = Preferences.userNodeForPackage(GrapherProject.class);
        PreferencesUtil.installVisibilityPrefsHandlers(prefs, detailsScrollPane, detailsVisible, mainSplitPane, Side.RIGHT);
    }

    @Override
    public CompletionStage<Void> write(URI uri, DataFormat format, Map<? super Key<?>, Object> options) {
        Drawing drawing = drawingView.getDrawing();
        drawingView.setDrawing(new SimpleDrawing());
        return FXWorker.run(() -> {
            if (SvgExporter.SVG_FORMAT.equals(format) || uri.getPath().endsWith(".svg")) {
                SvgExportOutputFormat io = new SvgExportOutputFormat();
                io.setOptions(options);
                io.write(uri, drawing);
            } else if (BitmapExportOutputFormat.PNG_FORMAT.equals(format) || uri.getPath().endsWith(".png")) {
                BitmapExportOutputFormat io = new BitmapExportOutputFormat();
                io.setOptions(options);
                io.write(uri, drawing);
            } else {
                IdFactory idFactory = new SimpleIdFactory();
                FigureFactory factory = new DefaultFigureFactory(idFactory);
                SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
                io.setOptions(options);
                io.write(uri, drawing);
            }
        }).thenRun(() -> drawingView.setDrawing(drawing));
    }

}

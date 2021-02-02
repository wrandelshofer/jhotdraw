/*
 * @(#)GrapherActivity.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.grapher;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.AbstractFileBasedActivity;
import org.jhotdraw8.app.FileBasedActivity;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.BrowseFileDirectoryAction;
import org.jhotdraw8.app.action.file.ExportFileAction;
import org.jhotdraw8.app.action.file.PrintFileAction;
import org.jhotdraw8.app.action.view.ToggleBooleanAction;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.concurrent.FXWorker;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.css.CssDimension2D;
import org.jhotdraw8.css.CssInsets;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.EditorActivity;
import org.jhotdraw8.draw.SimpleDrawingEditor;
import org.jhotdraw8.draw.SimpleDrawingView;
import org.jhotdraw8.draw.action.AddToGroupAction;
import org.jhotdraw8.draw.action.AlignBottomAction;
import org.jhotdraw8.draw.action.AlignHorizontalAction;
import org.jhotdraw8.draw.action.AlignLeftAction;
import org.jhotdraw8.draw.action.AlignRightAction;
import org.jhotdraw8.draw.action.AlignTopAction;
import org.jhotdraw8.draw.action.AlignVerticalAction;
import org.jhotdraw8.draw.action.BringForwardAction;
import org.jhotdraw8.draw.action.BringToFrontAction;
import org.jhotdraw8.draw.action.DistributeHorizontallyAction;
import org.jhotdraw8.draw.action.DistributeVerticallyAction;
import org.jhotdraw8.draw.action.GroupAction;
import org.jhotdraw8.draw.action.RemoveFromGroupAction;
import org.jhotdraw8.draw.action.RemoveTransformationsAction;
import org.jhotdraw8.draw.action.SelectChildrenAction;
import org.jhotdraw8.draw.action.SelectSameAction;
import org.jhotdraw8.draw.action.SendBackwardAction;
import org.jhotdraw8.draw.action.SendToBackAction;
import org.jhotdraw8.draw.action.UngroupAction;
import org.jhotdraw8.draw.constrain.GridConstrainer;
import org.jhotdraw8.draw.figure.AbstractDrawing;
import org.jhotdraw8.draw.figure.BezierFigure;
import org.jhotdraw8.draw.figure.CombinedPathFigure;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.EllipseFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.figure.GroupFigure;
import org.jhotdraw8.draw.figure.ImageFigure;
import org.jhotdraw8.draw.figure.LabelFigure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.LayerFigure;
import org.jhotdraw8.draw.figure.LineConnectionWithMarkersFigure;
import org.jhotdraw8.draw.figure.LineFigure;
import org.jhotdraw8.draw.figure.PageFigure;
import org.jhotdraw8.draw.figure.PageLabelFigure;
import org.jhotdraw8.draw.figure.PolygonFigure;
import org.jhotdraw8.draw.figure.PolylineFigure;
import org.jhotdraw8.draw.figure.RectangleFigure;
import org.jhotdraw8.draw.figure.SimpleLayeredDrawing;
import org.jhotdraw8.draw.figure.SliceFigure;
import org.jhotdraw8.draw.figure.StrokableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.figure.TextAreaFigure;
import org.jhotdraw8.draw.gui.DrawingExportOptionsPane;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.input.MultiClipboardInputFormat;
import org.jhotdraw8.draw.input.MultiClipboardOutputFormat;
import org.jhotdraw8.draw.inspector.DrawingInspector;
import org.jhotdraw8.draw.inspector.GridInspector;
import org.jhotdraw8.draw.inspector.HandlesInspector;
import org.jhotdraw8.draw.inspector.HelpTextInspector;
import org.jhotdraw8.draw.inspector.HierarchyInspector;
import org.jhotdraw8.draw.inspector.Inspector;
import org.jhotdraw8.draw.inspector.InspectorLabels;
import org.jhotdraw8.draw.inspector.LayersInspector;
import org.jhotdraw8.draw.inspector.StyleAttributesInspector;
import org.jhotdraw8.draw.inspector.StyleClassesInspector;
import org.jhotdraw8.draw.inspector.StylesheetsInspector;
import org.jhotdraw8.draw.inspector.ToolsToolbar;
import org.jhotdraw8.draw.inspector.ZoomToolbar;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.io.DefaultFigureFactory;
import org.jhotdraw8.draw.io.FigureFactory;
import org.jhotdraw8.draw.io.PrinterExportFormat;
import org.jhotdraw8.draw.io.SimpleFigureIdFactory;
import org.jhotdraw8.draw.io.SimpleXmlStaxReader;
import org.jhotdraw8.draw.io.SimpleXmlWriter;
import org.jhotdraw8.draw.io.SvgExportOutputFormat;
import org.jhotdraw8.draw.io.XmlEncoderOutputFormat;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.draw.tool.BezierCreationTool;
import org.jhotdraw8.draw.tool.ConnectionTool;
import org.jhotdraw8.draw.tool.CreationTool;
import org.jhotdraw8.draw.tool.ImageCreationTool;
import org.jhotdraw8.draw.tool.LineCreationTool;
import org.jhotdraw8.draw.tool.PolyCreationTool;
import org.jhotdraw8.draw.tool.SelectionTool;
import org.jhotdraw8.draw.tool.TextCreationTool;
import org.jhotdraw8.draw.tool.TextEditingTool;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.gui.dock.DockChild;
import org.jhotdraw8.gui.dock.DockRoot;
import org.jhotdraw8.gui.dock.Dockable;
import org.jhotdraw8.gui.dock.SimpleDockRoot;
import org.jhotdraw8.gui.dock.SimpleDockable;
import org.jhotdraw8.gui.dock.SplitPaneTrack;
import org.jhotdraw8.gui.dock.TabbedAccordionTrack;
import org.jhotdraw8.gui.dock.Track;
import org.jhotdraw8.gui.dock.VBoxTrack;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.svg.io.FXSvgFullWriter;
import org.jhotdraw8.svg.io.FXSvgTinyWriter;
import org.jhotdraw8.util.Resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import static org.jhotdraw8.io.DataFormats.registerDataFormat;

/**
 * GrapherActivityController.
 *
 * @author Werner Randelshofer
 */
public class GrapherActivity extends AbstractFileBasedActivity implements FileBasedActivity, EditorActivity {

    private final static String GRAPHER_NAMESPACE_URI = "http://jhotdraw.org/samples/grapher";
    private static final String VIEWTOGGLE_PROPERTIES = "view.toggleProperties";
    /**
     * Counter for incrementing layer names.
     */
    @NonNull
    private Map<String, Integer> counters = new HashMap<>();
    @FXML
    private ScrollPane detailsScrollPane;
    @FXML
    private VBox detailsVBox;
    private final BooleanProperty detailsVisible = new SimpleBooleanProperty(this, "detailsVisible", true);

    private DrawingView drawingView;

    private DrawingEditor editor;
    @FXML
    private BorderPane contentPane;
    private Node node;
    @FXML
    private ToolBar toolsToolBar;
    private DockRoot dockRoot;

    @NonNull
    private Dockable addInspector(@NonNull Inspector<DrawingView> inspector, String id, Priority grow) {
        Resources r = InspectorLabels.getResources();
        Dockable dockable = new SimpleDockable(r.getString(id + ".toolbar"), inspector.getNode());
        inspector.showingProperty().bind(dockable.showingProperty());
        inspector.getNode().getProperties().put("inspector", inspector);
        return dockable;
    }

    @NonNull
    @Override
    public CompletionStage<Void> clear() {
        Drawing d = new SimpleLayeredDrawing();
        d.set(StyleableFigure.ID, "drawing1");
        LayerFigure layer = new LayerFigure();
        layer.set(StyleableFigure.ID, "layer1");
        d.addChild(layer);
        for (final Figure f : d.preorderIterable()) {
            f.addNotify(d);
        }
        applyUserAgentStylesheet(d);
        drawingView.setDrawing(d);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Creates a figure with a unique id.
     *
     * @param <T>      the figure type
     * @param supplier the supplier
     * @return the created figure
     */
    public <T extends Figure> T createFigure(@NonNull Supplier<T> supplier) {
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
    protected void initActions(@NonNull ObservableMap<String, Action> map) {
        super.initActions(map);
        map.put(PrintFileAction.ID, new PrintFileAction(this));
        map.put(ExportFileAction.ID, new ExportFileAction(this, DrawingExportOptionsPane::createDialog));
        map.put(RemoveTransformationsAction.ID, new RemoveTransformationsAction(editor));
        map.put(BrowseFileDirectoryAction.ID, new BrowseFileDirectoryAction(this));
        map.put(SelectSameAction.ID, new SelectSameAction(editor));
        map.put(SelectChildrenAction.ID, new SelectChildrenAction(editor));
        map.put(SendToBackAction.ID, new SendToBackAction(editor));
        map.put(BringToFrontAction.ID, new BringToFrontAction(editor));
        map.put(BringForwardAction.ID, new BringForwardAction(editor));
        map.put(SendBackwardAction.ID, new SendBackwardAction(editor));
        map.put(VIEWTOGGLE_PROPERTIES, new ToggleBooleanAction(
                this,
                VIEWTOGGLE_PROPERTIES,
                GrapherLabels.getResources(), detailsVisible));
        map.put(GroupAction.ID, new GroupAction(editor, () -> createFigure(GroupFigure::new)));
        map.put(GroupAction.COMBINE_PATHS_ID, new GroupAction(GroupAction.COMBINE_PATHS_ID, editor, () -> createFigure(CombinedPathFigure::new)));
        map.put(UngroupAction.ID, new UngroupAction(editor));
        map.put(AddToGroupAction.ID, new AddToGroupAction(editor));
        map.put(RemoveFromGroupAction.ID, new RemoveFromGroupAction(editor));
        map.put(AlignTopAction.ID, new AlignTopAction(editor));
        map.put(AlignRightAction.ID, new AlignRightAction(editor));
        map.put(AlignBottomAction.ID, new AlignBottomAction(editor));
        map.put(AlignLeftAction.ID, new AlignLeftAction(editor));
        map.put(AlignHorizontalAction.ID, new AlignHorizontalAction(editor));
        map.put(AlignVerticalAction.ID, new AlignVerticalAction(editor));
        map.put(DistributeHorizontallyAction.ID, new DistributeHorizontallyAction(editor));
        map.put(DistributeVerticallyAction.ID, new DistributeVerticallyAction(editor));
    }

    @NonNull
    private Supplier<Layer> initToolBar() throws MissingResourceException {
        //drawingView.setConstrainer(new GridConstrainer(0,0,10,10,45));
        ToolsToolbar ttbar = new ToolsToolbar(editor);
        Resources labels = GrapherLabels.getResources();
        Supplier<Layer> layerFactory = () -> createFigure(LayerFigure::new);
        Tool defaultTool;
        ttbar.addTool(defaultTool = new SelectionTool("tool.resizeFigure", HandleType.RESIZE, null, HandleType.LEAD, labels), 0, 0);
        ttbar.addTool(new SelectionTool("tool.moveFigure", HandleType.MOVE, null, HandleType.LEAD, labels), 1, 0);
        ttbar.addTool(new SelectionTool("tool.selectPoint", HandleType.POINT, labels), 0, 1);
        ttbar.addTool(new SelectionTool("tool.transform", HandleType.TRANSFORM, labels), 1, 1);
        ttbar.addTool(new TextEditingTool("tool.editText", labels), 2, 1);

        ttbar.addTool(new CreationTool("edit.createRectangle", labels, () -> createFigure(RectangleFigure::new), layerFactory), 13, 0, 16);
        ttbar.addTool(new CreationTool("edit.createEllipse", labels, () -> createFigure(EllipseFigure::new), layerFactory), 14, 0);
        ttbar.addTool(new ConnectionTool("edit.createLineConnection", labels, () -> createFigure(LineConnectionWithMarkersFigure::new), layerFactory), 14, 1);
        ttbar.addTool(new LineCreationTool("edit.createLine", labels, () -> createFigure(LineFigure::new), layerFactory), 13, 1, 16);
        ttbar.addTool(new PolyCreationTool("edit.createPolyline", labels, PolylineFigure.POINTS, () -> createFigure(PolylineFigure::new), layerFactory),
                15, 1);
        ttbar.addTool(new PolyCreationTool("edit.createPolygon", labels,
                        PolygonFigure.POINTS, () -> createFigure(PolygonFigure::new), layerFactory),
                15, 0, 0);
        ttbar.addTool(new BezierCreationTool("edit.createBezier", labels,
                        BezierFigure.PATH, () -> createFigure(BezierFigure::new), layerFactory),
                16, 1);
        ttbar.addTool(new TextCreationTool("edit.createText", labels,//
                () -> createFigure(() -> new LabelFigure(0, 0, "Hello", FillableFigure.FILL, null, StrokableFigure.STROKE, null)), //
                layerFactory), 17, 1);
        ttbar.addTool(new TextCreationTool("edit.createTextArea", labels,
                        () -> createFigure(TextAreaFigure::new), layerFactory),
                17, 0);
        ttbar.addTool(new ImageCreationTool("edit.createImage", labels,
                () -> createFigure(ImageFigure::new), layerFactory), 16, 0, 0);
        ttbar.addTool(new CreationTool("edit.createSlice", labels,
                () -> createFigure(SliceFigure::new), layerFactory), 21, 0, 0);
        ttbar.addTool(new CreationTool("edit.createPage", labels,
                () -> createFigure(() -> {
                    PageFigure pf = new PageFigure();
                    pf.set(PageFigure.PAPER_SIZE, new CssDimension2D(297, 210, "mm"));
                    pf.set(PageFigure.PAGE_INSETS, new CssInsets(2, 1, 2, 1, "cm"));
                    PageLabelFigure pl = new PageLabelFigure(940, 700, labels.getFormatted("pageLabel.text",
                            PageLabelFigure.PAGE_PLACEHOLDER, PageLabelFigure.NUM_PAGES_PLACEHOLDER),
                            FillableFigure.FILL, null, StrokableFigure.STROKE, null);
                    pf.addChild(pl);
                    return pf;
                }), layerFactory), 20, 0, 16);
        ttbar.addTool(new CreationTool("edit.createPageLabel", labels,//
                () -> createFigure(() -> new PageLabelFigure(0, 0,
                        labels.getFormatted("pageLabel.text", PageLabelFigure.PAGE_PLACEHOLDER, PageLabelFigure.NUM_PAGES_PLACEHOLDER),
                        FillableFigure.FILL, null, StrokableFigure.STROKE, null)), //
                layerFactory), 20, 1, 16);
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
            node = loader.load(getClass().getResourceAsStream("GrapherActivity.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        drawingView = new SimpleDrawingView();
        // FIXME should use preferences!
        drawingView.setConstrainer(new GridConstrainer(0, 0, 10, 10, 11.25, 5, 5));
        //drawingView.setHandleType(HandleType.TRANSFORM);
        //
        drawingView.getModel().addListener(drawingModel -> {
            modified.set(true);
        });

        FigureFactory factory = new DefaultFigureFactory();
        IdFactory idFactory = new SimpleFigureIdFactory();
        SimpleXmlWriter iow = new SimpleXmlWriter(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
        SimpleXmlStaxReader ior = new SimpleXmlStaxReader(factory, idFactory, GRAPHER_NAMESPACE_URI);
        drawingView.setClipboardOutputFormat(new MultiClipboardOutputFormat(
                iow, new SvgExportOutputFormat(), new BitmapExportOutputFormat()));
        drawingView.setClipboardInputFormat(new MultiClipboardInputFormat(ior));

        editor = new SimpleDrawingEditor();
        editor.addDrawingView(drawingView);

        ScrollPane viewScrollPane = new ScrollPane();
        viewScrollPane.setFitToHeight(true);
        viewScrollPane.setFitToWidth(true);
        viewScrollPane.getStyleClass().addAll("view", "flush");
        viewScrollPane.setContent(drawingView.getNode());

        Supplier<Layer> layerFactory = initToolBar();

        ZoomToolbar ztbar = new ZoomToolbar();
        ztbar.zoomFactorProperty().bindBidirectional(drawingView.zoomFactorProperty());
        toolsToolBar.getItems().add(ztbar);
        initInspectors(viewScrollPane, layerFactory);

    }

    private void initInspectors(ScrollPane viewScrollPane, Supplier<Layer> layerFactory) {
        // set up the docking framework
        SimpleDockRoot root = new SimpleDockRoot();
        this.dockRoot = root;
        root.setZSupplier(TabbedAccordionTrack::new);
        root.setSubYSupplier(VBoxTrack::new);
        root.setRootXSupplier(SplitPaneTrack::createHorizontalTrack);
        root.setRootYSupplier(SplitPaneTrack::createVerticalTrack);
        root.setSubYSupplier(SplitPaneTrack::createVerticalTrack);
        Dockable viewScrollPaneDockItem = new SimpleDockable(null, viewScrollPane);
        root.getDockChildren().add(viewScrollPaneDockItem);

        contentPane.setCenter(this.dockRoot.getNode());

        FXWorker.supply(() -> {
            List<Track> d = new ArrayList<>();
            Track track = new TabbedAccordionTrack();
            track.getDockChildren().addAll(addInspector(new StyleAttributesInspector(), "styleAttributes", Priority.ALWAYS),
                    addInspector(new StyleClassesInspector(), "styleClasses", Priority.NEVER),
                    addInspector(new StylesheetsInspector(), "styleSheets", Priority.ALWAYS));
            d.add(track);
            track = new TabbedAccordionTrack();
            track.getDockChildren().addAll(addInspector(new LayersInspector(layerFactory), "layers", Priority.ALWAYS),
                    addInspector(new HierarchyInspector(), "figureHierarchy", Priority.ALWAYS));
            d.add(track);
            track = new TabbedAccordionTrack();
            track.getDockChildren().addAll(addInspector(new DrawingInspector(), "drawing", Priority.NEVER),
                    addInspector(new GridInspector(), "grid", Priority.NEVER),
                    addInspector(new HandlesInspector(), "handles", Priority.NEVER),
                    addInspector(new HelpTextInspector(), "helpText", Priority.NEVER));
            d.add(track);
            return d;
        }).whenComplete((list, e) -> {
            if (e == null) {
                VBoxTrack vtrack = new VBoxTrack();
                Set<Dockable> items = new LinkedHashSet<>();
                for (Track track : list) {
                    for (DockChild n : track.getDockChildren()) {
                        if (n instanceof Dockable) {
                            Dockable dd = (Dockable) n;
                            items.add(dd);
                            @SuppressWarnings("unchecked")
                            Inspector<DrawingView> i = (Inspector<DrawingView>) dd.getNode().getProperties().get("inspector");
                            i.setSubject(drawingView);
                        }
                    }
                    vtrack.getDockChildren().add(track);
                }
                SplitPaneTrack htrack = SplitPaneTrack.createHorizontalTrack();
                htrack.getDockChildren().add(viewScrollPaneDockItem);
                htrack.getDockChildren().add(vtrack);
                this.dockRoot.getDockChildren().setAll(htrack);
                this.dockRoot.setDockablePredicate(items::contains);
            } else {
                e.printStackTrace();
            }
        }).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
    }

    @NonNull
    @Override
    public CompletionStage<Void> print(@NonNull PrinterJob job, @NonNull WorkState workState) {
        Drawing drawing = drawingView.getDrawing();
        return FXWorker.run(() -> {
            try {
                PrinterExportFormat pof = new PrinterExportFormat();
                pof.print(job, drawing);
            } finally {
                job.endJob();
            }
        });

    }

    @Override
    public CompletionStage<DataFormat> read(@NonNull URI uri, DataFormat format, @Nullable Map<Key<?>, Object> options, boolean insert, @NonNull WorkState workState) {
        return FXWorker.supply(() -> {
            FigureFactory factory = new DefaultFigureFactory();
            IdFactory idFactory = new SimpleFigureIdFactory();
            SimpleXmlStaxReader io = new SimpleXmlStaxReader(factory, idFactory, GRAPHER_NAMESPACE_URI);
            AbstractDrawing drawing = (AbstractDrawing) io.read(uri, null, workState);
            System.out.println("READING..." + uri);
            applyUserAgentStylesheet(drawing);
            return drawing;
        }).thenApply(drawing -> {
            drawingView.setDrawing(drawing);
            return format;
        });
    }

    @Override
    public CompletionStage<Void> write(@NonNull URI uri, DataFormat format, Map<Key<?>, Object> options, WorkState workState) {
        Drawing drawing = drawingView.getDrawing();
        return FXWorker.run(() -> {
            if (registerDataFormat(FXSvgTinyWriter.SVG_MIME_TYPE_WITH_VERSION).equals(format)) {
                SvgExportOutputFormat io = new SvgExportOutputFormat();
                io.setExporterFactory(FXSvgTinyWriter::new);
                io.getProperties().putAll(options);
                io.write(uri, drawing, workState);
            } else if (registerDataFormat(FXSvgFullWriter.SVG_MIME_TYPE).equals(format)
                    || registerDataFormat(FXSvgFullWriter.SVG_MIME_TYPE_WITH_VERSION).equals(format)
                    || uri.getPath().endsWith(".svg")) {
                SvgExportOutputFormat io = new SvgExportOutputFormat();
                io.getProperties().putAll(options);
                io.write(uri, drawing, workState);
            } else if (registerDataFormat(BitmapExportOutputFormat.PNG_MIME_TYPE).equals(format) || uri.getPath().endsWith(".png")) {
                BitmapExportOutputFormat io = new BitmapExportOutputFormat();
                io.getProperties().putAll(options);
                io.write(uri, drawing, workState);
            } else if (registerDataFormat(XmlEncoderOutputFormat.XML_SERIALIZER_MIME_TYPE).equals(format) || uri.getPath().endsWith(".ser.xml")) {
                XmlEncoderOutputFormat io = new XmlEncoderOutputFormat();
                io.write(uri, drawing, workState);
            } else {
                DefaultFigureFactory factory = new DefaultFigureFactory();
                IdFactory idFactory = factory.getIdFactory();
                SimpleXmlWriter io = new SimpleXmlWriter(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
                io.write(uri, drawing, workState);
            }
        }).handle((voidvalue, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            }
            return null;
        });
    }

    private void applyUserAgentStylesheet(@NonNull final Drawing d) {
        try {
            d.set(Drawing.USER_AGENT_STYLESHEETS,
                    ImmutableLists.of(
                            GrapherActivity.class.getResource("user-agent.css").toURI()));
            d.updateStyleManager();
            final SimpleRenderContext ctx = new SimpleRenderContext();
            for (final Figure f : d.preorderIterable()) {
                f.updateCss(ctx);
            }
            // d.layoutAll(ctx);

        } catch (final URISyntaxException e) {
            throw new RuntimeException("can't load my own resources", e);
        }
    }
}

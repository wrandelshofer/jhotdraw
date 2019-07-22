/* @(#)ModelerActivityController.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.app.AbstractDocumentBasedActivity;
import org.jhotdraw8.app.DocumentBasedActivity;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.BrowseFileDirectoryAction;
import org.jhotdraw8.app.action.view.ToggleBooleanAction;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ImmutableMaps;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.concurrent.FXWorker;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.css.CssInsets;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawStylesheets;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.EditorView;
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
import org.jhotdraw8.draw.action.GroupAction;
import org.jhotdraw8.draw.action.RemoveFromGroupAction;
import org.jhotdraw8.draw.action.RemoveTransformationsAction;
import org.jhotdraw8.draw.action.SelectChildrenAction;
import org.jhotdraw8.draw.action.SelectSameAction;
import org.jhotdraw8.draw.action.SendBackwardAction;
import org.jhotdraw8.draw.action.SendToBackAction;
import org.jhotdraw8.draw.action.UngroupAction;
import org.jhotdraw8.draw.constrain.GridConstrainer;
import org.jhotdraw8.draw.figure.BezierFigure;
import org.jhotdraw8.draw.figure.CombinedPathFigure;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.DrawingFigure;
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
import org.jhotdraw8.draw.figure.SliceFigure;
import org.jhotdraw8.draw.figure.StrokableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.figure.TextAreaFigure;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.input.MultiClipboardInputFormat;
import org.jhotdraw8.draw.input.MultiClipboardOutputFormat;
import org.jhotdraw8.draw.inspector.DrawingInspector;
import org.jhotdraw8.draw.inspector.GridInspector;
import org.jhotdraw8.draw.inspector.HandlesInspector;
import org.jhotdraw8.draw.inspector.HelpTextInspector;
import org.jhotdraw8.draw.inspector.HierarchyInspector;
import org.jhotdraw8.draw.inspector.Inspector;
import org.jhotdraw8.draw.inspector.LayersInspector;
import org.jhotdraw8.draw.inspector.StyleAttributesInspector;
import org.jhotdraw8.draw.inspector.StyleClassesInspector;
import org.jhotdraw8.draw.inspector.StylesheetsInspector;
import org.jhotdraw8.draw.inspector.ToolsToolbar;
import org.jhotdraw8.draw.inspector.ZoomToolbar;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.io.FigureFactory;
import org.jhotdraw8.draw.io.PrinterExportFormat;
import org.jhotdraw8.draw.io.SimpleFigureIdFactory;
import org.jhotdraw8.draw.io.SimpleXmlIO;
import org.jhotdraw8.draw.io.SvgExportOutputFormat;
import org.jhotdraw8.draw.io.XMLEncoderOutputFormat;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.draw.tool.BezierCreationTool;
import org.jhotdraw8.draw.tool.ConnectionTool;
import org.jhotdraw8.draw.tool.CreationTool;
import org.jhotdraw8.draw.tool.ImageCreationTool;
import org.jhotdraw8.draw.tool.PolyCreationTool;
import org.jhotdraw8.draw.tool.SelectionTool;
import org.jhotdraw8.draw.tool.TextCreationTool;
import org.jhotdraw8.draw.tool.TextEditingTool;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.gui.dock.Dock;
import org.jhotdraw8.gui.dock.DockItem;
import org.jhotdraw8.gui.dock.DockRoot;
import org.jhotdraw8.gui.dock.ScrollableVBoxTrack;
import org.jhotdraw8.gui.dock.SingleItemDock;
import org.jhotdraw8.gui.dock.SplitPaneTrack;
import org.jhotdraw8.gui.dock.TabbedAccordionDock;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.samples.modeler.figure.MLConstants;
import org.jhotdraw8.samples.modeler.figure.MLDiagramFigure;
import org.jhotdraw8.samples.modeler.figure.MLKeyword;
import org.jhotdraw8.samples.modeler.figure.UMLClassifierShapeFigure;
import org.jhotdraw8.samples.modeler.figure.UMLEdgeFigure;
import org.jhotdraw8.samples.modeler.io.ModelerFigureFactory;
import org.jhotdraw8.samples.modeler.model.MLCompartmentalizedData;
import org.jhotdraw8.svg.SvgExporter;
import org.jhotdraw8.util.Resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

/**
 * ModelerActivityController.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ModelerActivityController extends AbstractDocumentBasedActivity implements DocumentBasedActivity, EditorView {

    private final static String DIAGRAMMER_NAMESPACE_URI = "http://jhotdraw.org/samples/modeler";
    private static final String VIEWTOGGLE_PROPERTIES = "view.toggleProperties";
    /**
     * Counter for incrementing layer names.
     */
    @Nonnull
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

    @Nonnull
    private DockItem addInspector(Inspector inspector, String id, Priority grow) {
        Resources r = ModelerLabels.getInspectorResources();
        DockItem dockItem = new DockItem();
        dockItem.setText(r.getString(id + ".toolbar"));
        dockItem.setContent(inspector.getNode());
        dockItem.getProperties().put("inspector", inspector);
        return dockItem;
    }

    private void applyUserAgentStylesheet(Drawing d) {
        try {
            d.set(Drawing.USER_AGENT_STYLESHEETS,
                    ImmutableLists.of(
                            ModelerActivityController.class.getResource("user-agent.css").toURI()));
            d.updateStyleManager();
            SimpleRenderContext ctx = new SimpleRenderContext();
            for (Figure f : d.preorderIterable()) {
                f.updateCss();
            }
            d.layoutAll(ctx);

        } catch (URISyntaxException e) {
            throw new RuntimeException("can't load my own resources", e);
        }
    }

    @Nonnull
    @Override
    public CompletionStage<Void> clear() {
        Drawing d = new DrawingFigure();
        applyUserAgentStylesheet(d);
        d.set(StyleableFigure.ID, "drawing1");
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
    public <T extends Figure> T createFigure(@Nonnull Supplier<T> supplier) {
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
    protected void initActionMap(@Nonnull HierarchicalMap<String, Action> map) {
        map.put(RemoveTransformationsAction.ID, new RemoveTransformationsAction(getApplication(), editor));
        map.put(SelectSameAction.ID, new SelectSameAction(getApplication(), editor));
        map.put(SelectChildrenAction.ID, new SelectChildrenAction(getApplication(), editor));
        map.put(BrowseFileDirectoryAction.ID, new BrowseFileDirectoryAction(getApplication()));
        map.put(SendToBackAction.ID, new SendToBackAction(getApplication(), editor));
        map.put(BringToFrontAction.ID, new BringToFrontAction(getApplication(), editor));
        map.put(BringForwardAction.ID, new BringForwardAction(getApplication(), editor));
        map.put(SendBackwardAction.ID, new SendBackwardAction(getApplication(), editor));
        map.put(VIEWTOGGLE_PROPERTIES, new ToggleBooleanAction(
                getApplication(), this,
                VIEWTOGGLE_PROPERTIES,
                ModelerLabels.getResources(), detailsVisible));
        map.put(GroupAction.ID, new GroupAction(getApplication(), editor, () -> createFigure(GroupFigure::new)));
        map.put(GroupAction.COMBINE_PATHS_ID, new GroupAction(GroupAction.COMBINE_PATHS_ID, getApplication(), editor, () -> createFigure(CombinedPathFigure::new)));
        map.put(UngroupAction.ID, new UngroupAction(getApplication(), editor));
        map.put(AddToGroupAction.ID, new AddToGroupAction(getApplication(), editor));
        map.put(RemoveFromGroupAction.ID, new RemoveFromGroupAction(getApplication(), editor));
        map.put(AlignTopAction.ID, new AlignTopAction(getApplication(), editor));
        map.put(AlignRightAction.ID, new AlignRightAction(getApplication(), editor));
        map.put(AlignBottomAction.ID, new AlignBottomAction(getApplication(), editor));
        map.put(AlignLeftAction.ID, new AlignLeftAction(getApplication(), editor));
        map.put(AlignHorizontalAction.ID, new AlignHorizontalAction(getApplication(), editor));
        map.put(AlignVerticalAction.ID, new AlignVerticalAction(getApplication(), editor));

    }

    @Nonnull
    private Supplier<Layer> initToolBar() throws MissingResourceException {
        //drawingView.setConstrainer(new GridConstrainer(0,0,10,10,45));
        ToolsToolbar ttbar = new ToolsToolbar(editor);
        Resources labels = ModelerLabels.getResources();
        Supplier<Layer> layerFactory = () -> createFigure(LayerFigure::new);

        // selection tools -----------
        Tool defaultTool;
        ttbar.addTool(defaultTool = new SelectionTool("tool.resizeFigure", HandleType.RESIZE, null, HandleType.LEAD, labels), 0, 0);
        ttbar.addTool(new SelectionTool("tool.moveFigure", HandleType.MOVE, null, HandleType.LEAD, labels), 1, 0);
        ttbar.addTool(new SelectionTool("tool.selectPoint", HandleType.POINT, labels), 0, 1);
        ttbar.addTool(new SelectionTool("tool.transform", HandleType.TRANSFORM, labels), 1, 1);
        ttbar.addTool(new TextEditingTool("tool.editText", labels), 2, 1);


        // modeling shape creation tools -----------
        CreationTool createSysMLRequirementTool = new CreationTool("edit.createSysMLRequirementClassifier", labels, () -> createFigure(() -> {
            UMLClassifierShapeFigure f = new UMLClassifierShapeFigure();
            f.set(UMLClassifierShapeFigure.KEYWORD, MLKeyword.REQUIREMENT.getName());
            f.set(UMLClassifierShapeFigure.NAME, "Name");
            f.set(UMLClassifierShapeFigure.COMPARTMENTS, new MLCompartmentalizedData(
                    ImmutableMaps.of(MLKeyword.PROPERTIES.getName(), ImmutableLists.emptyList())
            ));
            return f;
        }), layerFactory);
        createSysMLRequirementTool.setDefaultWidth(120);
        createSysMLRequirementTool.setDefaultHeight(100);
        ttbar.addTool(createSysMLRequirementTool, 10, 0, 16);
        CreationTool createSysMLBlockTool = new CreationTool("edit.createSysMLBlockClassifier", labels, () -> createFigure(() -> {
            UMLClassifierShapeFigure f = new UMLClassifierShapeFigure();
            f.set(UMLClassifierShapeFigure.KEYWORD, MLKeyword.BLOCK.getName());
            f.set(UMLClassifierShapeFigure.NAME, "Name");
            f.set(UMLClassifierShapeFigure.COMPARTMENTS, new MLCompartmentalizedData(
                    ImmutableMaps.ofEntries(
                            ImmutableMaps.entry(MLKeyword.PARTS.getName(), ImmutableLists.emptyList()),
                            ImmutableMaps.entry(MLKeyword.REFERENCES.getName(), ImmutableLists.emptyList()),
                            ImmutableMaps.entry(MLKeyword.VALUES.getName(), ImmutableLists.emptyList()),
                            ImmutableMaps.entry(MLKeyword.CONSTRAINTS.getName(), ImmutableLists.emptyList()),
                            ImmutableMaps.entry(MLKeyword.OPERATIONS.getName(), ImmutableLists.emptyList()),
                            ImmutableMaps.entry(MLKeyword.PORTS.getName(), ImmutableLists.emptyList())
                    )
            ));
            return f;
        }), layerFactory);
        createSysMLBlockTool.setDefaultWidth(120);
        createSysMLBlockTool.setDefaultHeight(200);
        ttbar.addTool(createSysMLBlockTool, 11, 0, 0);
        CreationTool createUmlClassifierTool = new CreationTool("edit.createUmlClassClassifier", labels, () -> createFigure(() -> {
            UMLClassifierShapeFigure f = new UMLClassifierShapeFigure();
            f.set(UMLClassifierShapeFigure.KEYWORD, MLKeyword.CLASS.getName());
            f.set(UMLClassifierShapeFigure.NAME, "Name");
            f.set(UMLClassifierShapeFigure.COMPARTMENTS, new MLCompartmentalizedData(
                    ImmutableMaps.ofEntries(ImmutableMaps.entry(MLKeyword.ATTRIBUTES.getName(), ImmutableLists.emptyList()),
                            ImmutableMaps.entry(MLKeyword.OPERATIONS.getName(), ImmutableLists.emptyList()))
            ));
            return f;
        }), layerFactory);
        createUmlClassifierTool.setDefaultWidth(120);
        createUmlClassifierTool.setDefaultHeight(100);
        ttbar.addTool(createUmlClassifierTool, 12, 0, 0);

        // modeling edge creation tools --------
        ttbar.addTool(new ConnectionTool("edit.createUmlDependencyEdge", labels, () -> createFigure(() -> {
            UMLEdgeFigure f = new UMLEdgeFigure();
            f.set(UMLEdgeFigure.KEYWORD, MLKeyword.DEPENDENCY.getName());
            return f;
        }), layerFactory), 20, 1, 16);
        ttbar.addTool(new ConnectionTool("edit.createSysMLContainmentEdge", labels, () -> createFigure(() -> {
            UMLEdgeFigure f = new UMLEdgeFigure();
            f.set(UMLEdgeFigure.KEYWORD, MLKeyword.CONTAINMENT.getName());
            return f;
        }), layerFactory), 20, 0, 16);
        ttbar.addTool(new ConnectionTool("edit.createUmlGeneralizationEdge", labels, () -> createFigure(() -> {
            UMLEdgeFigure f = new UMLEdgeFigure();
            f.set(UMLEdgeFigure.KEYWORD, MLKeyword.GENERALIZATION.getName());
            return f;
        }), layerFactory), 21, 0, 0);

        // modeling diagram creation tools --------
        CreationTool createSysMLDiagramTool = new CreationTool("edit.createSysMLRequirementDiagram", labels, () -> createFigure(() -> {
            MLDiagramFigure f = new MLDiagramFigure();
            f.set(MLDiagramFigure.DIAGRAM_KIND, "req");
            f.set(MLDiagramFigure.MODEL_ELEMENT_NAME, "Name");
            return f;
        })
                , layerFactory);
        createSysMLDiagramTool.setDefaultWidth(240);
        createSysMLDiagramTool.setDefaultHeight(200);
        ttbar.addTool(createSysMLDiagramTool, 40, 0, 16);

        // general drawing element creation tools -----------

        ttbar.addTool(new CreationTool("edit.createRectangle", labels, () -> createFigure(RectangleFigure::new), layerFactory), 102, 0, 16);
        ttbar.addTool(new CreationTool("edit.createEllipse", labels, () -> createFigure(EllipseFigure::new), layerFactory), 103, 0);
        ttbar.addTool(new ConnectionTool("edit.createLineConnection", labels, () -> createFigure(LineConnectionWithMarkersFigure::new), layerFactory), 103, 1);
        ttbar.addTool(new CreationTool("edit.createLine", labels, () -> createFigure(LineFigure::new), layerFactory), 102, 1, 16);
        ttbar.addTool(new PolyCreationTool("edit.createPolyline", labels, PolylineFigure.POINTS, () -> createFigure(PolylineFigure::new), layerFactory), 104, 1);
        ttbar.addTool(new PolyCreationTool("edit.createPolygon", labels, PolygonFigure.POINTS, () -> createFigure(PolygonFigure::new), layerFactory), 105, 1, 0);
        ttbar.addTool(new BezierCreationTool("edit.createBezier", labels, BezierFigure.PATH, () -> createFigure(BezierFigure::new), layerFactory), 106, 1);
        ttbar.addTool(new TextCreationTool("edit.createText", labels,//
                () -> createFigure(() -> new LabelFigure(0, 0, "Hello", FillableFigure.FILL, null, StrokableFigure.STROKE, null)), //
                layerFactory), 106, 0);
        ttbar.addTool(new TextCreationTool("edit.createTextArea", labels, () -> createFigure(TextAreaFigure::new), layerFactory), 106, 1);
        ttbar.addTool(new ImageCreationTool("edit.createImage", labels, () -> createFigure(ImageFigure::new), layerFactory), 105, 0, 0);

        // --------- page and slice elements creation tools

        ttbar.addTool(new CreationTool("edit.createSlice", labels, () -> createFigure(SliceFigure::new), layerFactory), 200, 0, 16);
        ttbar.addTool(new CreationTool("edit.createPage", labels, () -> createFigure(() -> {
            PageFigure pf = new PageFigure();
            pf.set(PageFigure.PAPER_SIZE, new CssPoint2D(297, 210, "mm"));
            pf.set(PageFigure.PAGE_INSETS, new CssInsets(2, 1, 2, 1, "cm"));
            PageLabelFigure pl = new PageLabelFigure(940, 700, labels.getFormatted("pageLabel.text",
                    PageLabelFigure.PAGE_PLACEHOLDER, PageLabelFigure.NUM_PAGES_PLACEHOLDER),
                    FillableFigure.FILL, null, StrokableFigure.STROKE, null);
            pf.addChild(pl);
            return pf;
        }), layerFactory), 200, 1, 16);
        ttbar.addTool(new CreationTool("edit.createPageLabel", labels,//
                () -> createFigure(() -> new PageLabelFigure(0, 0,
                        labels.getFormatted("pageLabel.text", PageLabelFigure.PAGE_PLACEHOLDER, PageLabelFigure.NUM_PAGES_PLACEHOLDER),
                        FillableFigure.FILL, null, StrokableFigure.STROKE, null)), //
                layerFactory), 201, 1);

        // --------
        ttbar.setDrawingEditor(editor);
        editor.setDefaultTool(defaultTool);
        toolsToolBar.getItems().add(ttbar);

        toolsToolBar.getItems().add(new Separator());
        ZoomToolbar ztbar = new ZoomToolbar();
        ztbar.zoomFactorProperty().bindBidirectional(drawingView.zoomFactorProperty());
        toolsToolBar.getItems().add(ztbar);
        ztbar.setMin(-5);
        ztbar.setMax(5);



        return layerFactory;
    }

    @Override
    public void initView() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            node = loader.load(getClass().getResourceAsStream("ModelerActivity.fxml"));
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

        FigureFactory factory = new ModelerFigureFactory();
        IdFactory idFactory = new SimpleFigureIdFactory();
        SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, DIAGRAMMER_NAMESPACE_URI, null);
        drawingView.setClipboardOutputFormat(new MultiClipboardOutputFormat(
                io, new SvgExportOutputFormat(), new BitmapExportOutputFormat()));
        drawingView.setClipboardInputFormat(new MultiClipboardInputFormat(io));

        editor = new SimpleDrawingEditor();
        editor.addDrawingView(drawingView);

        ScrollPane viewScrollPane = new ScrollPane();
        viewScrollPane.setFitToHeight(true);
        viewScrollPane.setFitToWidth(true);
        viewScrollPane.getStyleClass().addAll("view", "flush");
        viewScrollPane.setContent(drawingView.getNode());

        Supplier<Layer> layerFactory = initToolBar();

        initInspectors(viewScrollPane, layerFactory);

    }

    private void initInspectors(ScrollPane viewScrollPane, Supplier<Layer> layerFactory) {
        // set up the docking framework
        dockRoot = new DockRoot();
        dockRoot.setDockFactory(TabbedAccordionDock::new);
        dockRoot.setVerticalInnerTrackFactory(ScrollableVBoxTrack::new);
        dockRoot.setHorizontalTrackFactory(SplitPaneTrack::createHorizontalTrack);
        dockRoot.getVerticalTrackFactoryMap().put(SingleItemDock.class, SplitPaneTrack::createVerticalTrack);
        dockRoot.setVerticalRootTrackFactory(SplitPaneTrack::createVerticalTrack);
        DockItem dockItem = new DockItem(null, viewScrollPane);
        SingleItemDock singleItemDock = new SingleItemDock(dockItem);
        dockRoot.addDock(singleItemDock);

        contentPane.setCenter(dockRoot);

        FXWorker.supply(() -> {
            Set<Dock> d = new LinkedHashSet<>();
            Dock dock = new TabbedAccordionDock();

            StyleAttributesInspector modelAttrInspector = new StyleAttributesInspector();
            modelAttrInspector.setAttributeFilter(k ->
                    "id".equals(k.getName()) || MLConstants.MODEL_NAMESPACE_PREFIX.equals(k.getNamespace())
            );
            dock.getItems().add(addInspector(modelAttrInspector, "modelAttributes", Priority.ALWAYS));
            StyleAttributesInspector styleAttrInspector = new StyleAttributesInspector();
            styleAttrInspector.setAttributeFilter(k ->
                    !MLConstants.MODEL_NAMESPACE_PREFIX.equals(k.getNamespace())
            );
            dock.getItems().add(addInspector(styleAttrInspector, "styleAttributes", Priority.ALWAYS));
            dock.getItems().add(addInspector(new StyleClassesInspector(), "styleClasses", Priority.NEVER));
            dock.getItems().add(addInspector(new StylesheetsInspector(), "styleSheets", Priority.ALWAYS));
            d.add(dock);
            dock = new TabbedAccordionDock();
            dock.getItems().add(addInspector(new LayersInspector(layerFactory), "layers", Priority.ALWAYS));
            dock.getItems().add(addInspector(new HierarchyInspector(), "figureHierarchy", Priority.ALWAYS));
            d.add(dock);
            dock = new TabbedAccordionDock();
            dock.getItems().add(addInspector(new DrawingInspector(), "drawing", Priority.NEVER));
            dock.getItems().add(addInspector(new GridInspector(), "grid", Priority.NEVER));
            dock.getItems().add(addInspector(new HandlesInspector(), "handles", Priority.NEVER));
            dock.getItems().add(addInspector(new HelpTextInspector(), "helpText", Priority.NEVER));
            d.add(dock);
            return d;
        }).whenComplete((list, e) -> {
            if (e == null) {
                ScrollableVBoxTrack vtrack = new ScrollableVBoxTrack();
                Set<DockItem> items = new LinkedHashSet<>();
                for (Dock dock : list) {
                    for (DockItem n : dock.getItems()) {
                        items.add(n);
                        Inspector<DrawingView> i = (Inspector<DrawingView>) n.getProperties().get("inspector");
                        i.setSubject(drawingView);
                    }
                    vtrack.getItems().add(dock.getNode());
                }
                SplitPaneTrack htrack = SplitPaneTrack.createHorizontalTrack();
                htrack.getItems().add(vtrack.getNode());
                dockRoot.addTrack(htrack);
                dockRoot.setDockableItems(FXCollections.observableSet(items));
            } else {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletionStage<Void> print(@Nonnull PrinterJob job, @Nonnull WorkState workState) {
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
    public CompletionStage<DataFormat> read(@Nonnull URI uri, DataFormat format, Map<? super Key<?>, Object> options, boolean insert, WorkState workState) {
        return FXWorker.supply(() -> {
            FigureFactory factory = new ModelerFigureFactory();
            IdFactory idFactory = new SimpleFigureIdFactory();
            SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, DIAGRAMMER_NAMESPACE_URI, null);
            DrawingFigure drawing = (DrawingFigure) io.read(uri, null, workState);
            System.out.println("READING..." + uri);
            applyUserAgentStylesheet(drawing);
            return drawing;
        }).thenApply(drawing -> {
            drawingView.setDrawing(drawing);
            return format;
        });
    }

    @Override
    public void start() {
        getNode().getScene().getStylesheets().addAll(//
                DrawStylesheets.getInspectorsStylesheet(),//
                ModelerApplication.class.getResource("/org/jhotdraw8/samples/modeler/modeler.css").toString()//
        );

        Preferences prefs = Preferences.userNodeForPackage(ModelerActivityController.class);
//        PreferencesUtil.installVisibilityPrefsHandlers(prefs, detailsScrollPane, detailsVisible, mainSplitPane, Side.RIGHT);
    }

    @Override
    public CompletionStage<Void> write(@Nonnull URI uri, DataFormat format, Map<? super Key<?>, Object> options, WorkState workState) {
        Drawing drawing = drawingView.getDrawing();
        return FXWorker.run(() -> {
            if (SvgExporter.SVG_FORMAT.equals(format) || uri.getPath().endsWith(".svg")) {
                SvgExportOutputFormat io = new SvgExportOutputFormat();
                io.setOptions(options);
                io.write(uri, drawing, workState);
            } else if (BitmapExportOutputFormat.PNG_FORMAT.equals(format) || uri.getPath().endsWith(".png")) {
                BitmapExportOutputFormat io = new BitmapExportOutputFormat();
                io.setOptions(options);
                io.write(uri, drawing, workState);
            } else if (XMLEncoderOutputFormat.XML_SERIALIZER_FORMAT.equals(format) || uri.getPath().endsWith(".ser.xml")) {
                XMLEncoderOutputFormat io = new XMLEncoderOutputFormat();
                io.write(uri, drawing, workState);
            } else {
                FigureFactory factory = new ModelerFigureFactory();
                IdFactory idFactory = new SimpleFigureIdFactory();
                SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, DIAGRAMMER_NAMESPACE_URI, null);
                io.write(uri, drawing, workState);

                // automatically export to SVG
                if (uri.getPath().endsWith(".xml")) {
                    URI svgUri = new URI(uri.getScheme(),
                            uri.getHost(),
                            uri.getPath().substring(0, uri.getPath().length() - 4) + ".svg"
                            , uri.getFragment());
                    SvgExportOutputFormat io2 = new SvgExportOutputFormat();
                    Map<? super Key<?>, Object> options2 = new HashMap<>();
                    SvgExportOutputFormat.EXPORT_DRAWING_KEY.put(options2, true);
                    io2.setOptions(options2);
                    io2.write(svgUri, drawing, workState);
                }
            }
        });
    }

}

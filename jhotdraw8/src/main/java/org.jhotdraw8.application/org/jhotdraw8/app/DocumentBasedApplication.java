/*
 * @(#)DocumentBasedApplication.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.Actions;
import org.jhotdraw8.app.action.ScreenMenuBarProxyAction;
import org.jhotdraw8.app.action.file.ClearRecentFilesMenuAction;
import org.jhotdraw8.app.action.file.CloseFileAction;
import org.jhotdraw8.app.action.file.OpenRecentFileAction;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.collection.BooleanKey;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.concurrent.FXWorker;
import org.jhotdraw8.concurrent.SimpleWorkState;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.util.prefs.PreferencesUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static java.lang.Math.min;

/**
 * An {@link DocumentBasedApplication} handles the life-cycle of {@link DocumentBasedActivity} objects and
 * provides windows to present them on screen.
 * <p>
 * This implementation supports the following command line parameters:
 * <pre>
 *     [uri ...]
 * </pre>
 * <dl>
 * <dt>uri</dt><dd>The URI of a document. Opens a DocumentBasedActivity for each provided URI.</dd>
 * </dl>
 *
 * @author Werner Randelshofer
 */
public class DocumentBasedApplication extends AbstractApplication {

    @Nullable
    private final static Key<ChangeListener<Boolean>> FOCUS_LISTENER_KEY = new ObjectKey<>("focusListener", ChangeListener.class, new Class<?>[]{Boolean.class}, null);
    private final static BooleanKey QUIT_APPLICATION = new BooleanKey("quitApplication", false);
    private Logger LOGGER = Logger.getLogger(DocumentBasedApplication.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    protected HierarchicalMap<String, Action> actionMap = new HierarchicalMap<>();

    private final ReadOnlyObjectWrapper<Activity> activeView = new ReadOnlyObjectWrapper<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), (Runnable r) -> {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler((Thread t1, Throwable e) -> {
            throw (RuntimeException) e;
        });
        return t;
    });
    private boolean isSystemMenuSupported;
    private ApplicationModel model;
    private final SetProperty<Activity> views = new SimpleSetProperty<>(FXCollections.observableSet());
    @Nonnull
    private ArrayList<Action> systemMenuActiveViewtActions = new ArrayList<>();
    private List<Menu> systemMenus;

    {
        activeView.addListener((o, oldv, newv) -> {
            if (oldv != null) {
                handleViewDeactivated((DocumentBasedActivity) oldv);
            }
            if (newv != null) {
                handleViewActivated((DocumentBasedActivity) newv);
            }
        });
    }

    {
        views.addListener((SetChangeListener.Change<? extends Activity> change) -> {
            if (change.wasAdded()) {
                handleViewAdded((DocumentBasedActivity) change.getElementAdded());
            } else {
                handleViewRemoved((DocumentBasedActivity) change.getElementRemoved());
            }
        });
    }

    public DocumentBasedApplication() {
        recentUrisProperty().get().addListener(this::updateRecentMenuItemsInAllMenuBars);
    }

    @Override
    public ReadOnlyObjectProperty<Activity> activeActivityProperty() {
        return activeView.getReadOnlyProperty();
    }

    /**
     * Creates a menu bar and sets it to the stage or to the system menu.
     *
     * @param stage   the stage, or null to set the system menu
     * @param actions the action map
     * @return the menu bar
     */
    protected MenuBar createMenuBar(@Nullable Stage stage, @Nonnull HierarchicalMap<String, Action> actions) {
        MenuBar mb = model.createMenuBar();
        Deque<Menu> todo = new LinkedList<>(mb.getMenus());
        final List<KeyCombination> accelerators = new ArrayList<>();
        while (!todo.isEmpty()) {
            for (MenuItem mi : todo.remove().getItems()) {
                if (mi instanceof Menu) {
                    todo.add((Menu) mi);
                } else {
                    Action a = actions.get(mi.getId());
                    if (a != null) {
                        Actions.bindMenuItem(mi, a);
                    } else {
                        a = new ScreenMenuBarProxyAction(this, mi.getId());
                        a.set(Action.LABEL, mi.getText());
                        systemMenuActiveViewtActions.add(a);
                        Actions.bindMenuItem(mi, a, true);
                    }
                    KeyCombination accelerator = mi.getAccelerator();
                    if (accelerator != null) {
                        accelerators.add(accelerator);
                    }
                }
            }
        }
        updateRecentMenuItemsMB(mb.getMenus());

        // Filter all key codes which are defined in the menu bar
        // XXX maybe this is needed on Mac OS X only
        if (stage != null) {
            stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                for (KeyCombination acc : accelerators) {
                    if (acc.match(event)) {
                        event.consume();
                    }
                }
            });
        }

        return mb;
    }

    @Override
    public CompletionStage<Activity> createActivity() {
        return FXWorker.supply(() -> getModel().createActivity())
                .handle((v, e) -> {
                    if (e != null) {
                        e.printStackTrace();
                        final Resources labels = ApplicationLabels.getResources();
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                labels.getString("application.createView.error"));
                        alert.getDialogPane().setMaxWidth(640.0);
                        alert.show();
                    }
                    return v;
                });
    }

    private void disambiguateViews() {
        HashMap<String, ArrayList<Activity>> titles = new HashMap<>();
        for (Activity v : views) {
            String t = v.getTitle();
            titles.computeIfAbsent(t, k -> new ArrayList<>()).add(v);
        }
        for (ArrayList<Activity> list : titles.values()) {
            if (list.size() == 1) {
                list.get(0).setDisambiguation(0);
            } else {
                int max = 0;
                for (Activity v : list) {
                    max = Math.max(max, v.getDisambiguation());
                }
                Collections.sort(list, (a, b) -> a.getDisambiguation() - b.getDisambiguation());
                int prev = 0;
                for (Activity v : list) {
                    int current = v.getDisambiguation();
                    if (current == prev) {
                        v.setDisambiguation(++max);
                    }
                    prev = current;
                }
            }
        }
    }

    @Override
    public void execute(@Nonnull Runnable r) {
        executor.execute(r);
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public HierarchicalMap<String, Action> getActionMap() {
        return actionMap;
    }

    /**
     * Gets the resource bundle.
     *
     * @return the resource bundle
     */
    protected Resources getLabels() {
        return ApplicationLabels.getResources();
    }

    @Override
    public ApplicationModel getModel() {
        return model;
    }

    @Override
    public void setModel(ApplicationModel newValue) {
        model = newValue;
    }

    /**
     * Called immediately when a views needs to be activated.
     *
     * @param view the view
     */
    protected void handleViewActivated(@Nonnull DocumentBasedActivity view) {

    }

    /**
     * Called immediately after a view has been added to the views
     * property.
     *
     * @param view the view
     */
    protected void handleViewAdded(@Nonnull DocumentBasedActivity view) {
        if (view.getApplication() != this) {
            view.setApplication(this);
            view.init();

        }

        view.getActionMap().setParent(getActionMap());
        view.setApplication(DocumentBasedApplication.this);
        view.setTitle(getLabels().getString("unnamedFile"));
        HierarchicalMap<String, Action> map = view.getActionMap();
        map.put(CloseFileAction.ID, new CloseFileAction(DocumentBasedApplication.this, view));

        Stage stage = createStage(view);

        PreferencesUtil.installStagePrefsHandler(model.getPreferences(), "stage", stage);

        stage.setOnCloseRequest(event -> {
            event.consume();

            for (StackTraceElement element : new Throwable().getStackTrace()) {
                if (element.getMethodName().contains("Quit")) {
                    view.set(QUIT_APPLICATION, true);
                    break;
                }
            }

            view.getActionMap().get(CloseFileAction.ID).handle(new ActionEvent(event.getSource(), event.getTarget()));
        });

        stage.focusedProperty().addListener((observer, oldValue, newValue) -> {
            if (newValue) {
                activeView.set(view);
            }
        });
        stage.titleProperty().bind(CustomBinding.formatted(getLabels().getString("frame.title"),
                view.titleProperty(), getModel().getName(), view.disambiguationProperty(), view.modifiedProperty()));
        view.titleProperty().addListener(this::handleTitleChanged);
        ChangeListener<Boolean> focusListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue == true) {
                activeView.set(view);
            }
        };
        view.set(FOCUS_LISTENER_KEY, focusListener);
        stage.focusedProperty().addListener(focusListener);
        disambiguateViews();

        Screen screen = Screen.getPrimary();
        if (screen != null) {
            Rectangle2D bounds = screen.getVisualBounds();
            Random r = new Random();
            if (activeView.get() != null) {
                Window w = activeView.get().getNode().getScene().getWindow();
                //stage.setWidth(w.getWidth());
                //stage.setHeight(w.getHeight());
                stage.setX(min(w.getX() + 22, bounds.getMaxX()
                        - stage.getWidth()));
                stage.setY(min(w.getY() + 22, bounds.getMaxY()
                        - stage.getHeight()));
            } else {
                //stage.setWidth(bounds.getWidth() / 4);
                //stage.setHeight(bounds.getHeight() / 3);
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
            }

            Outer:
            for (int retries = views.getSize(); retries > 0; retries--) {
                for (Activity v : views) {
                    if (v != view) {
                        Window w = v.getNode().getScene().getWindow();
                        if (Math.abs(w.getX() - stage.getX()) < 10
                                || Math.abs(w.getY() - stage.getY()) < 10) {
                            stage.setX(min(w.getX() + 20, bounds.getMaxX()
                                    - stage.getWidth()));
                            stage.setY(min(w.getY() + 20, bounds.getMaxY()
                                    - stage.getHeight()));
                            continue Outer;
                        }
                    }
                }
                break;
            }
        }
        stage.show();
        Platform.runLater(view::start);
    }

    @Nonnull
    protected Stage createStage(@Nonnull DocumentBasedActivity view) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNIFIED);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(view.getNode());
        if (!isSystemMenuSupported) {
            MenuBar mb = createMenuBar(stage, view.getActionMap());
            mb.setUseSystemMenuBar(true);
            borderPane.setTop(mb);
        }
        Scene scene = new Scene(borderPane);
        scene.getStylesheets().addAll(model.getSceneStylesheets().asList());

        stage.setScene(scene);
        return stage;
    }

    /**
     * Called immediately when a view needs to be deactivated.
     *
     * @param view the view
     */
    protected void handleViewDeactivated(@Nonnull DocumentBasedActivity view) {

    }

    /**
     * Called immediately after a view has been removed from the views
     * property.
     *
     * @param view the view
     */
    protected void handleViewRemoved(@Nonnull DocumentBasedActivity view) {
        Stage stage = (Stage) view.getNode().getScene().getWindow();
        view.stop();
        ChangeListener<Boolean> focusListener = view.get(FOCUS_LISTENER_KEY);
        if (focusListener != null) {
            stage.focusedProperty().removeListener(focusListener);
        }
        stage.close();
        view.destroy();
        view.setApplication(null);
        view.getActionMap().setParent(null);

        if (activeView.get() == view) {
            activeView.set(null);
        }

        // Auto close feature
        if (views.isEmpty() && !isSystemMenuSupported) {
            exit();
        }
    }

    protected void handleTitleChanged(Observable obs) {
        disambiguateViews();
    }

    @Nonnull
    @Override
    public SetProperty<Activity> activitiesProperty() {
        return views;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            isSystemMenuSupported = false; //Toolkit.getToolkit().getSystemMenu().isSupported();
        } catch (IllegalAccessError e) {
            System.err.println("Warning: can not access com.sun.javafx.tk.Toolkit");
        }

        actionMap = model.createApplicationActionMap(this);
        loadRecentUris(model.getName());
        if (isSystemMenuSupported) {
            /*
            Platform.setImplicitExit(false);
            systemMenus = new ArrayList<>();
            ArrayList<MenuBase> menus = new ArrayList<>();
            MenuBar mb = createMenuBar(null, getActionMap());
            for (Menu m : mb.getMenus()) {
                systemMenus.add(m);
                menus.add(GlobalMenuAdapter.adapt(m));
            }
            Toolkit.getToolkit().getSystemMenu().setMenus(menus);
*/
        }

        List<URI> urisToOpen = getUrisToOpen();
        if (urisToOpen.isEmpty()) {
            openEmptyView();
        } else {
            for (URI uri : urisToOpen) {
                openView(uri);
            }
        }
    }

    private void openView(URI uri) {
        final Resources labels = ApplicationLabels.getResources();
        createActivity().whenComplete((pv, ex1) -> {
            DocumentBasedActivity v = (DocumentBasedActivity) pv;
            if (ex1 != null) {
                ex1.printStackTrace();
                final Alert alert = new Alert(Alert.AlertType.ERROR,
                        labels.getString("application.createView.error"));
                alert.getDialogPane().setMaxWidth(640.0);
                alert.show();
                return;
            }
            add(v);
            v.addDisabler(this);
            v.read(uri, null, null, false, new SimpleWorkState()).whenComplete((result, ex) -> {
                if (ex != null) {
                    ex.printStackTrace();
                    final Alert alert = new Alert(Alert.AlertType.ERROR,
                            labels.getFormatted("file.open.couldntOpen.message", uri)
                                    + "\n" + ex.getMessage());
                    alert.getDialogPane().setMaxWidth(640.0);
                    alert.show();
                } else {
                    v.setURI(uri);
                    v.setDataFormat(result);
                    v.clearModified();
                    v.setTitle(UriUtil.getName(uri));
                }
                v.removeDisabler(this);
            });
        }).handle((v, ex) -> {
                    ex.printStackTrace();
                    final Alert alert = new Alert(Alert.AlertType.ERROR,
                            labels.getString("application.createView.error"));
                    alert.getDialogPane().setMaxWidth(640.0);
                    alert.showAndWait();
                    exit();
                    return null;
                }
        );
    }

    private void openEmptyView() {
        final Resources labels = ApplicationLabels.getResources();
        createActivity().whenComplete((pv, ex1) -> {
            DocumentBasedActivity v = (DocumentBasedActivity) pv;
            if (ex1 != null) {
                ex1.printStackTrace();
                final Alert alert = new Alert(Alert.AlertType.ERROR,
                        labels.getString("application.createView.error"));
                alert.getDialogPane().setMaxWidth(640.0);
                alert.show();
                return;
            }
            add(v);
            v.addDisabler(this);
            v.clear().whenComplete((result, ex) -> {
                if (ex != null) {
                    ex.printStackTrace();
                    final Alert alert = new Alert(Alert.AlertType.ERROR,
                            labels.getString("application.createView.error"));
                    alert.getDialogPane().setMaxWidth(640.0);
                    alert.show();
                } else {
                    v.removeDisabler(this);
                }
            });
        }).handle((v, ex) -> {
                    ex.printStackTrace();
                    final Alert alert = new Alert(Alert.AlertType.ERROR,
                            labels.getString("application.createView.error"));
                    alert.getDialogPane().setMaxWidth(640.0);
                    alert.showAndWait();
                    exit();
                    return null;
                }
        );
    }


    protected List<URI> getUrisToOpen() {
        List<URI> uris = new ArrayList<>();
        for (String s : getParameters().getUnnamed()) {
            try {
                URI uri = new URI(s);
                if (uri.getScheme() == null) {
                    uri = Paths.get(s).toUri();
                }
                uris.add(uri);
            } catch (URISyntaxException e) {
                LOGGER.warning("Ignoring unnamed parameter, because it is not a legal URI: " + s);
            }
        }
        return uris;
    }

    private void updateRecentMenuItemsInAllMenuBars(Observable o) {
        if (isSystemMenuSupported) {
            if (systemMenus != null) {
                updateRecentMenuItemsMB(systemMenus);
            }
        } else {
            for (Activity v : activities()) {
                BorderPane bp = (BorderPane) v.getNode().getScene().getRoot();
                MenuBar mb = (MenuBar) bp.getTop();
                if (mb != null) {
                    updateRecentMenuItemsMB((mb.getMenus()));
                }
            }
        }
    }

    private void updateRecentMenuItemsMB(List<Menu> mb) {

        Deque<List<?>> todo = new ArrayDeque<>();
        todo.add(mb);
        while (!todo.isEmpty()) {
            for (Object mi : todo.remove()) {
                if (mi instanceof Menu) {
                    Menu mmi = (Menu) mi;
                    if ("file.openRecentMenu".equals(mmi.getId())) {
                        mmi.getItems().clear();
                        for (Map.Entry<URI, DataFormat> entry : recentUrisProperty()) {
                            URI uri = entry.getKey();
                            DataFormat format = entry.getValue();
                            MenuItem mii = new MenuItem();
                            Action a = new OpenRecentFileAction(this, uri, format);
                            Actions.bindMenuItem(mii, a);
                            ((Menu) mi).getItems().add(mii);
                        }
                        MenuItem mii = new MenuItem();
                        Action a = new ClearRecentFilesMenuAction(this);
                        Actions.bindMenuItem(mii, a);
                        mmi.getItems().add(new SeparatorMenuItem());
                        mmi.getItems().add(mii);
                    } else {
                        todo.add(mmi.getItems());
                    }
                }
            }
        }
    }

    public static URL getDocumentOrientedMenu() {
        return DocumentBasedApplication.class.getResource("DocumentOrientedMenu.fxml");
    }
}

/*
 * @(#)FileBasedApplication.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
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
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.Actions;
import org.jhotdraw8.app.action.ScreenMenuBarProxyAction;
import org.jhotdraw8.app.action.app.AboutAction;
import org.jhotdraw8.app.action.app.ExitAction;
import org.jhotdraw8.app.action.edit.ClearSelectionAction;
import org.jhotdraw8.app.action.edit.CopyAction;
import org.jhotdraw8.app.action.edit.CutAction;
import org.jhotdraw8.app.action.edit.DeleteAction;
import org.jhotdraw8.app.action.edit.PasteAction;
import org.jhotdraw8.app.action.edit.SelectAllAction;
import org.jhotdraw8.app.action.file.ClearRecentFilesMenuAction;
import org.jhotdraw8.app.action.file.CloseFileAction;
import org.jhotdraw8.app.action.file.NewFileAction;
import org.jhotdraw8.app.action.file.OpenFileAction;
import org.jhotdraw8.app.action.file.OpenRecentFileAction;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.concurrent.SimpleWorkState;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.reflect.TypeToken;
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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static java.lang.Math.min;

/**
 * An {@link SimpleFileBasedApplication} handles the life-cycle of {@link FileBasedActivity} objects and
 * provides windows to present them on screen.
 * <p>
 * This implementation supports the following command line parameters:
 * <pre>
 *     [path ...]
 * </pre>
 * <dl>
 * <dt>path</dt><dd>The URI to a file. Opens a {@link FileBasedActivity} for each provided path.</dd>
 * </dl>
 *
 * @author Werner Randelshofer
 */
public class SimpleFileBasedApplication extends AbstractFileBasedApplication {

    @NonNull
    private final static Key<ChangeListener<Boolean>> FOCUS_LISTENER_KEY = new ObjectKey<>("focusListener",
            new TypeToken<ChangeListener<Boolean>>() {
            }, null);
    @NonNull
    private final static Key<Stage> STAGE_KEY = new ObjectKey<>("stage", Stage.class);
    @NonNull
    public static final String WINDOW_MENU_ID = "window";
    public static final String FILE_OPEN_RECENT_MENU = "file.openRecentMenu";
    private Logger LOGGER = Logger.getLogger(SimpleFileBasedApplication.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private final ReadOnlyObjectWrapper<Activity> activeActivity = new ReadOnlyObjectWrapper<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), (Runnable r) -> {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler((Thread t1, Throwable e) -> {
            throw (RuntimeException) e;
        });
        return t;
    });
    private boolean isSystemMenuSupported;


    @NonNull
    private ArrayList<Action> systemMenuActiveViewActions = new ArrayList<>();
    private List<Menu> systemMenus;

    {
        activeActivity.addListener((o, oldv, newv) -> {
            if (oldv != null) {
                onViewDeactivated((FileBasedActivity) oldv);
            }
            if (newv != null) {
                onViewActivated((FileBasedActivity) newv);
            }
        });
    }

    {
        getActivities().addListener((SetChangeListener<? super Activity>) c -> {
            if (c.wasRemoved()) {
                onActivityRemoved((FileBasedActivity) c.getElementRemoved());
            }
            if (c.wasAdded()) {
                onActivityAdded((FileBasedActivity) c.getElementAdded());
            }
        });
    }

    public SimpleFileBasedApplication() {
        recentUrisProperty().get().addListener(this::updateRecentMenuItemsInAllMenuBars);
    }

    @Override
    public ReadOnlyObjectProperty<Activity> activeActivityProperty() {
        return activeActivity.getReadOnlyProperty();
    }

    /**
     * Creates a menu bar and sets it to the stage or to the system menu.
     *
     * @param stage   the stage, or null to set the system menu
     * @param actions the action map
     * @return the menu bar
     */
    @Nullable
    protected MenuBar createMenuBar(@Nullable Activity activity, @Nullable Stage stage, @NonNull Map<String, Action> actions) {
        Supplier<MenuBar> factory = getMenuBarFactory();
        MenuBar mb = factory == null ? null : factory.get();
        if (mb == null) {
            return null;
        }
        Deque<Menu> todo = new LinkedList<>(mb.getMenus());
        final List<KeyCombination> accelerators = new ArrayList<>();
        while (!todo.isEmpty()) {
            final Menu menu = todo.remove();
            if (WINDOW_MENU_ID.equals(menu.getId())) {
                createWindowMenu(activity, menu);
                continue;
            }
            for (MenuItem mi : menu.getItems()) {
                if (mi instanceof Menu) {
                    todo.add((Menu) mi);
                } else {
                    Action a = actions.get(mi.getId());
                    if (a != null) {
                        Actions.bindMenuItem(mi, a);
                    } else {
                        a = new ScreenMenuBarProxyAction(this, mi.getId());
                        a.set(Action.LABEL, mi.getText());
                        systemMenuActiveViewActions.add(a);
                        Actions.bindMenuItem(mi, a, true);
                    }
                    KeyCombination accelerator = mi.getAccelerator();
                    if (accelerator != null) {
                        accelerators.add(accelerator);
                    }
                }
            }
        }
        updateRecentMenuItemsInMenuBar(mb.getMenus());

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

    private void createWindowMenu(@Nullable Activity activity, Menu menu) {
        Map<Activity, CheckMenuItem> menuItemMap = new WeakHashMap<>();
        CustomBinding.bindListContentToSet(menu.getItems(), getActivities(),
                v -> menuItemMap.computeIfAbsent(v, k -> {
                    final CheckMenuItem menuItem = new CheckMenuItem();
                    menuItem.textProperty().bind(v.titleProperty());
                    menuItem.setOnAction(evt -> {
                        final Stage s = v.get(STAGE_KEY);
                        if (s != null) {
                            s.requestFocus();
                        }
                        menuItem.setSelected(v == activity);
                    });
                    menuItem.setSelected(v == activity);
                    return menuItem;
                })
        );
    }

    private void disambiguateViews() {
        HashMap<String, ArrayList<Activity>> titles = new HashMap<>();
        for (Activity v : getActivities()) {
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
    public void exit() {
        System.exit(0);
    }

    /**
     * Gets the resource bundle.
     *
     * @return the resource bundle
     */
    protected Resources getLabels() {
        return ApplicationLabels.getResources();
    }


    /**
     * Called immediately when a views needs to be activated.
     *
     * @param view the view
     */
    protected void onViewActivated(@NonNull FileBasedActivity view) {

    }

    /**
     * Called immediately after a view has been added to the views
     * property.
     *
     * @param activity the activity
     */
    protected void onActivityAdded(@NonNull FileBasedActivity activity) {
        activity.setApplication(this);
        activity.init();

        activity.setTitle(getLabels().getString("unnamedFile"));
        ObservableMap<String, Action> map = activity.getActions();
        map.put(CloseFileAction.ID, new CloseFileAction(activity));

        Stage stage = createStage(activity);
        activity.put(STAGE_KEY, stage);

        PreferencesUtil.installStagePrefsHandler(getPreferences(), "stage", stage);

        stage.setOnCloseRequest(event -> {
            event.consume();
            activity.getActions().get(CloseFileAction.ID).handle(new ActionEvent(event.getSource(), event.getTarget()));
        });

        stage.focusedProperty().addListener((observer, oldValue, newValue) -> {
            if (newValue) {
                activeActivity.set(activity);
            }
        });
        stage.titleProperty().bind(CustomBinding.formatted(getLabels().getString("frame.title"),
                activity.titleProperty(), get(NAME_KEY), activity.disambiguationProperty(), activity.modifiedProperty()));
        activity.titleProperty().addListener(this::onTitleChanged);
        ChangeListener<Boolean> focusListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                activeActivity.set(activity);
            }
        };
        activity.set(FOCUS_LISTENER_KEY, focusListener);
        stage.focusedProperty().addListener(focusListener);
        disambiguateViews();

        Screen screen = Screen.getPrimary();
        if (screen != null) {
            Rectangle2D bounds = screen.getVisualBounds();
            Random r = new Random();
            if (activeActivity.get() != null) {
                Window w = activeActivity.get().getNode().getScene().getWindow();
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
            for (int retries = getActivities().size(); retries > 0; retries--) {
                for (Activity v : getActivities()) {
                    if (v != activity) {
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
        Platform.runLater(activity::start);
    }

    @NonNull
    protected Stage createStage(@NonNull FileBasedActivity activity) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNIFIED);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(activity.getNode());
        if (!isSystemMenuSupported) {
            Map<String, Action> allActions = new LinkedHashMap<>();
            allActions.putAll(getActions());
            allActions.putAll(getActions());
            MenuBar mb = createMenuBar(activity, stage, allActions);
            if (mb != null) {
                mb.setUseSystemMenuBar(true);
                borderPane.setTop(mb);
            }
        }
        Scene scene = new Scene(borderPane);
        scene.getStylesheets().addAll(getStylesheets());

        stage.setScene(scene);
        return stage;
    }

    /**
     * Called immediately when a view needs to be deactivated.
     *
     * @param view the view
     */
    protected void onViewDeactivated(@NonNull FileBasedActivity view) {

    }

    /**
     * Called immediately after a view has been removed from the views
     * property.
     *
     * @param view the view
     */
    protected void onActivityRemoved(@NonNull FileBasedActivity view) {
        Stage stage = (Stage) view.getNode().getScene().getWindow();
        view.stop();
        ChangeListener<Boolean> focusListener = view.get(FOCUS_LISTENER_KEY);
        if (focusListener != null) {
            stage.focusedProperty().removeListener(focusListener);
        }
        stage.close();
        view.destroy();

        if (activeActivity.get() == view) {
            activeActivity.set(null);
        }

        // Auto close feature
        if (getActivities().isEmpty() && !isSystemMenuSupported) {
            exit();
        }
    }

    protected void onTitleChanged(Observable obs) {
        disambiguateViews();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        startUserAgentStylesheet();
        try {
            isSystemMenuSupported = false; //Toolkit.getToolkit().getSystemMenu().isSupported();
        } catch (IllegalAccessError e) {
            System.err.println("Warning: can not access com.sun.javafx.tk.Toolkit");
        }

        loadRecentUris(get(NAME_KEY));
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

    protected void startUserAgentStylesheet() {
    }


    private void openView(@NonNull URI uri) {
        final Resources labels = ApplicationLabels.getResources();
        createActivity().whenComplete((pv, ex1) -> {
            FileBasedActivity v = (FileBasedActivity) pv;
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
            FileBasedActivity v = (FileBasedActivity) pv;
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
                    v.clearModified();
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
                updateRecentMenuItemsInMenuBar(systemMenus);
            }
        } else {
            for (Activity v : getActivities()) {
                BorderPane bp = (BorderPane) v.getNode().getScene().getRoot();
                MenuBar mb = (MenuBar) bp.getTop();
                if (mb != null) {
                    updateRecentMenuItemsInMenuBar((mb.getMenus()));
                }
            }
        }
    }

    private void updateRecentMenuItemsInMenuBar(List<Menu> mb) {

        Deque<List<?>> todo = new ArrayDeque<>();
        todo.add(mb);
        while (!todo.isEmpty()) {
            for (Object mi : todo.remove()) {
                if (mi instanceof Menu) {
                    Menu mmi = (Menu) mi;
                    if (FILE_OPEN_RECENT_MENU.equals(mmi.getId())) {
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
        return SimpleFileBasedApplication.class.getResource("DocumentBasedMenu.fxml");
    }

    @Override
    public final void init() throws Exception {
        initResourceBundle();
        initProperties();
        initFactories();
        initActions(getActions());
    }

    protected void initResourceBundle() {
    }

    protected void initProperties() {
        put(NAME_KEY, getClass().getSimpleName());
        put(VERSION_KEY, getClass().getPackage().getImplementationVersion());
        put(COPYRIGHT_KEY, getClass().getPackage().getImplementationVendor());
    }

    protected void initFactories() {
    }

    protected void initActions(@NonNull ObservableMap<String, Action> map) {
        map.put(AboutAction.ID, new AboutAction(this));
        map.put(ExitAction.ID, new ExitAction(this));
        map.put(NewFileAction.ID, new NewFileAction(this));
        map.put(OpenFileAction.ID, new OpenFileAction(this));
        map.put(CutAction.ID, new CutAction(this));
        map.put(CopyAction.ID, new CopyAction(this));
        map.put(PasteAction.ID, new PasteAction(this));
        map.put(DeleteAction.ID, new DeleteAction(this));
        map.put(SelectAllAction.ID, new SelectAllAction(this));
        map.put(ClearSelectionAction.ID, new ClearSelectionAction(this));
    }
}

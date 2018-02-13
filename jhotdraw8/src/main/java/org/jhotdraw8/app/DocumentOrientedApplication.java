/* @(#)DocumentOrientedApplication.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
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
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.util.prefs.PreferencesUtil;

/**
 * An {@code DocumentOrientedApplication} handles the life-cycle of {@link DocumentOrientedViewModel} objects and
 * provides windows to present them on screen.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DocumentOrientedApplication extends AbstractApplication {

    private final static Key<ChangeListener<Boolean>> FOCUS_LISTENER_KEY = new ObjectKey<>("focusListener", ChangeListener.class, new Class<?>[]{Boolean.class}, null);
    private final static BooleanKey QUIT_APPLICATION = new BooleanKey("quitApplication", false);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    protected HierarchicalMap<String, Action> actionMap = new HierarchicalMap<>();

    private final ReadOnlyObjectWrapper<ViewController> activeView = new ReadOnlyObjectWrapper<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), (Runnable r) -> {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler((Thread t1, Throwable e) -> {
            throw (RuntimeException) e;
        });
        return t;
    });
    private boolean isSystemMenuSupported;
    private ApplicationModel model;
    private final SetProperty<ViewController> views = new SimpleSetProperty<>(FXCollections.observableSet());
    private ArrayList<Action> systemMenuActiveViewtActions = new ArrayList<>();
    private List<Menu> systemMenus;

    {
        activeView.addListener((o, oldv, newv) -> {
            if (oldv != null) {
                handleViewDeactivated((DocumentOrientedViewModel) oldv);
            }
            if (newv != null) {
                handleViewActivated((DocumentOrientedViewModel) newv);
            }
        });
    }

    {
        views.addListener((SetChangeListener.Change<? extends ViewController> change) -> {
            if (change.wasAdded()) {
                handleViewAdded((DocumentOrientedViewModel) change.getElementAdded());
            } else {
                handleViewRemoved((DocumentOrientedViewModel) change.getElementRemoved());
            }
        });
    }

    public DocumentOrientedApplication() {
        recentUrisProperty().get().addListener(this::updateRecentMenuItemsInAllMenuBars);
    }

    @Override
    public ReadOnlyObjectProperty<ViewController> activeViewProperty() {
        return activeView.getReadOnlyProperty();
    }

    /**
     * Creates a menu bar and sets it to the stage or to the system menu.
     *
     * @param stage the stage, or null to set the system menu
     * @param actions the action map
     * @return the menu bar
     */
    protected MenuBar createMenuBar(Stage stage, HierarchicalMap<String, Action> actions) {
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
    public CompletionStage<ViewController> createView() {
        return FXWorker.supply(() -> getModel().createView())
                .handle((v, e) -> {
                    if (e != null) {
                        e.printStackTrace();
                        final Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                labels.getString("application.createView.error"));
                        alert.getDialogPane().setMaxWidth(640.0);
                        alert.show();
                    }
                    return v;
                });
    }

    private void disambiguateViews() {
        HashMap<String, ArrayList<ViewController>> titles = new HashMap<>();
        for (ViewController v : views) {
            String t = v.getTitle();
            titles.computeIfAbsent(t, k -> new ArrayList<>()).add(v);
        }
        for (ArrayList<ViewController> list : titles.values()) {
            if (list.size() == 1) {
                list.get(0).setDisambiguation(0);
            } else {
                int max = 0;
                for (ViewController v : list) {
                    max = Math.max(max, v.getDisambiguation());
                }
                Collections.sort(list, (a, b) -> a.getDisambiguation() - b.getDisambiguation());
                int prev = 0;
                for (ViewController v : list) {
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
    public void execute(Runnable r) {
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
        return Resources.getResources("org.jhotdraw8.app.Labels");
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
    protected void handleViewActivated(DocumentOrientedViewModel view) {
        view.activate();
    }

    /**
     * Called immediately after a view has been added to the views
     * property.
     *
     * @param view the view
     */
    protected void handleViewAdded(DocumentOrientedViewModel view) {
        if (view.getApplication() != this) {
            view.setApplication(this);
            view.init();

        }

        view.getActionMap().setParent(getActionMap());
        view.setApplication(DocumentOrientedApplication.this);
        view.setTitle(getLabels().getString("unnamedFile"));
        HierarchicalMap<String, Action> map = view.getActionMap();
        map.put(CloseFileAction.ID, new CloseFileAction(DocumentOrientedApplication.this, view));

        Stage stage = new Stage();
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(view.getNode());
        if (!isSystemMenuSupported) {
            MenuBar mb = createMenuBar(stage, view.getActionMap());
            mb.setUseSystemMenuBar(true);
            borderPane.setTop(mb);
        }
        Scene scene = new Scene(borderPane);

        PreferencesUtil.installStagePrefsHandler(model.getPreferences(), "stage", stage);

        stage.setScene(scene);
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
                stage.setX(Math.min(w.getX() + 22, bounds.getMaxX()
                        - stage.getWidth()));
                stage.setY(Math.min(w.getY() + 22, bounds.getMaxY()
                        - stage.getHeight()));
            } else {
                //stage.setWidth(bounds.getWidth() / 4);
                //stage.setHeight(bounds.getHeight() / 3);
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
            }

            Outer:
            for (int retries = views.getSize(); retries > 0; retries--) {
                for (ViewController v : views) {
                    if (v != view) {
                        Window w = v.getNode().getScene().getWindow();
                        if (Math.abs(w.getX() - stage.getX()) < 10
                                || Math.abs(w.getY() - stage.getY()) < 10) {
                            stage.setX(Math.min(w.getX() + 20, bounds.getMaxX()
                                    - stage.getWidth()));
                            stage.setY(Math.min(w.getY() + 20, bounds.getMaxY()
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

    /**
     * Called immediately when a view needs to be deactivated.
     *
     * @param view the view
     */
    protected void handleViewDeactivated(DocumentOrientedViewModel view) {
        view.deactivate();
    }

    /**
     * Called immediately after a view has been removed from the views
    * property.
     *
     * @param view the view
     */
    protected void handleViewRemoved(DocumentOrientedViewModel view) {
        Stage stage = (Stage) view.getNode().getScene().getWindow();
        view.stop();
        ChangeListener<Boolean> focusListener = view.get(FOCUS_LISTENER_KEY);
        if (focusListener != null) {
            stage.focusedProperty().removeListener(focusListener);
        }
        stage.close();
        view.dispose();
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

    /**
     * Called immediately after a view has been removed from the views
 set.
     *
     * @param obs the observable
     */
    protected void handleTitleChanged(Observable obs) {
        disambiguateViews();
    }

    @Override
    public SetProperty<ViewController> viewsProperty() {
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

        final Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
        createView().whenComplete((pv, ex1) -> {
            DocumentOrientedViewModel v = (DocumentOrientedViewModel) pv;
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

    private void updateRecentMenuItemsInAllMenuBars(Observable o) {
        if (isSystemMenuSupported) {
            if (systemMenus != null) {
                updateRecentMenuItemsMB(systemMenus);
            }
        } else {
            for (ViewController v : views()) {
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
                        for (URI uri : recentUrisProperty()) {
                            MenuItem mii = new MenuItem();
                            Action a = new OpenRecentFileAction(this, uri);
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
}

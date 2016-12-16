/* @(#)DocumentOrientedApplication.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app;

import com.sun.javafx.menu.MenuBase;
import com.sun.javafx.scene.control.GlobalMenuAdapter;
import com.sun.javafx.tk.Toolkit;
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
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.Observable;
import org.jhotdraw8.collection.HierarchicalMap;
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
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jhotdraw8.app.action.AbstractViewAction;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.Actions;
import org.jhotdraw8.app.action.file.ClearRecentFilesMenuAction;
import org.jhotdraw8.app.action.file.CloseFileAction;
import org.jhotdraw8.app.action.file.OpenRecentFileAction;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.collection.BooleanKey;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.SimpleKey;
import org.jhotdraw8.concurrent.FXWorker;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.util.prefs.PreferencesUtil;

/**
 * DocumentOrientedApplication.
 *
 * @author Werner Randelshofer
 * @version $Id: DocumentOrientedApplication.java 1120 2016-01-15 17:37:49Z
 * rawcoder $
 */
public class DocumentOrientedApplication extends AbstractApplication<DocumentView> {

    private final static Key<ChangeListener<Boolean>> FOCUS_LISTENER_KEY = new SimpleKey<>("focusListener", ChangeListener.class, new Class<?>[]{Boolean.class}, null);
    private final static BooleanKey QUIT_APPLICATION = new BooleanKey("quitApplication", false);
    private boolean isSystemMenuSupported;
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), (Runnable r) -> {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler((Thread t1, Throwable e) -> {
            throw (RuntimeException) e;
        });
        return t;
    });
    protected HierarchicalMap<String, Action> actionMap = new HierarchicalMap<>();

    private final ReadOnlyObjectWrapper<DocumentView> activeView = new ReadOnlyObjectWrapper<>();
    private final SetProperty<DocumentView> views = new SimpleSetProperty<>(FXCollections.observableSet());
    private ApplicationModel<DocumentView> model;
    private List<Menu> systemMenus;

    public DocumentOrientedApplication() {
        recentUrisProperty().get().addListener(this::updateRecentMenuItemsInAllMenuBars);
    }

    {
        views.addListener((SetChangeListener.Change<? extends DocumentView> change) -> {
            if (change.wasAdded()) {
                handleViewAdded(change.getElementAdded());
            } else {
                handleViewRemoved(change.getElementRemoved());
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        isSystemMenuSupported = false&&Toolkit.getToolkit().getSystemMenu().isSupported();
        actionMap = model.createApplicationActionMap(this);
        if (isSystemMenuSupported) {
            Platform.setImplicitExit(false);
            systemMenus = new ArrayList<>();
            ArrayList<MenuBase> menus = new ArrayList<>();
            MenuBar mb = createMenuBar(getActionMap());
            for (Menu m : mb.getMenus()) {
                systemMenus.add(m);
                menus.add(GlobalMenuAdapter.adapt(m));
            }
            Toolkit.getToolkit().getSystemMenu().setMenus(menus);
        }

        // FIXME - We suppress here 2 exceptions!
        final Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
        createView().whenComplete((v, ex1) -> {
            if (ex1 != null) {
                ex1.printStackTrace();
                new Alert(Alert.AlertType.ERROR,
                        labels.getString("application.createView.error")).show();
                return;
            }
            add(v);
            v.addDisabler(this);
            v.clear().whenComplete((result, ex) -> {
                if (ex != null) {
                    new Alert(Alert.AlertType.ERROR,
                            labels.getString("application.createView.error")).show();
                } else {
                    v.removeDisabler(this);
                }
            });
        });
    }

    @Override
    public SetProperty<DocumentView> viewsProperty() {
        return views;
    }

    @Override
    public ReadOnlyObjectProperty<DocumentView> activeViewProperty() {
        return activeView.getReadOnlyProperty();
    }

    @Override
    public CompletionStage<DocumentView> createView() {
        return FXWorker.supply(() -> getModel().instantiateView())
                .thenApply((v) -> {
                    v.setApplication(DocumentOrientedApplication.this);
                    v.init();
                    return v;
                }).exceptionally(e -> {
            e.printStackTrace();
            final Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    labels.getString("application.createView.error"));
            alert.show();
            return null;
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Called immediately after a view has been added to the views set.
     *
     * @param view the view
     */
    protected void handleViewAdded(DocumentView view) {
        view.getActionMap().setParent(getActionMap());
        view.setApplication(DocumentOrientedApplication.this);
        view.setTitle(getLabels().getString("unnamedFile"));
        HierarchicalMap<String, Action> map = view.getActionMap();
        map.put(CloseFileAction.ID, new CloseFileAction(DocumentOrientedApplication.this, view));

        Stage stage = new Stage();
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(view.getNode());
        if (!isSystemMenuSupported) {
            MenuBar mb = createMenuBar(view.getActionMap());
            mb.setUseSystemMenuBar(true);
            borderPane.setTop(mb);
        }
        Scene scene = new Scene(borderPane);

        PreferencesUtil.installStagePrefsHandler(Preferences.userNodeForPackage(DocumentOrientedApplication.class), "stage", stage);

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
                for (DocumentView v : views) {
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
     * Called immediately after a view has been removed from the views set.
     *
     * @param obs the observable
     */
    protected void handleTitleChanged(Observable obs) {
        disambiguateViews();
    }

    /**
     * Called immediately after a view has been removed from the views set.
     *
     * @param view the view
     */
    protected void handleViewRemoved(DocumentView view) {
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
     * Gets the resource bundle.
     *
     * @return the resource bundle
     */
    protected Resources getLabels() {
        return Resources.getResources("org.jhotdraw8.app.Labels");
    }

    /**
     * Creates a menu bar.
     *
     * @param actions the action map
     * @return the menu bar
     */
    protected MenuBar createMenuBar(HierarchicalMap<String, Action> actions) {
        MenuBar mb = model.createMenuBar();

        LinkedList<Menu> todo = new LinkedList<>(mb.getMenus());
        while (!todo.isEmpty()) {
            for (MenuItem mi : todo.remove().getItems()) {
                if (mi instanceof Menu) {
                    todo.add((Menu) mi);
                } else {
                    Action a = actions.get(mi.getId());
                    if (a != null) {
                        Actions.bindMenuItem(mi, a);
                    } else {
                        System.err.println("DocumentOrientedApplication: Warning: no action for menu item with id="
                                + mi.getId());
                        a = new AbstractViewAction(this, null) {
                            @Override
                            protected void onActionPerformed(ActionEvent event) {
                                Action ava = (Action) getActiveView().getActionMap().get(mi.getId());
                                if (ava != null) {
                                    ava.handle(event);
                                }
                            }
                        };
                        Actions.bindMenuItem(mi, a, false);
                    }
                }
            }
        }
        updateRecentMenuItemsMB(mb.getMenus());
        return mb;
    }

    @Override
    public HierarchicalMap<String, Action> getActionMap() {
        return actionMap;
    }

    @Override
    public void execute(Runnable r) {
        executor.execute(r);
    }

    @Override
    public ApplicationModel<DocumentView> getModel() {
        return model;
    }

    @Override
    public void setModel(ApplicationModel<DocumentView> newValue) {
        model = newValue;
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    private void disambiguateViews() {
        HashMap<String, ArrayList<DocumentView>> titles = new HashMap<>();
        for (DocumentView v : views) {
            String t = v.getTitle();
            titles.computeIfAbsent(t, k -> new ArrayList<>()).add(v);
        }
        for (ArrayList<DocumentView> list : titles.values()) {
            if (list.size() == 1) {
                list.get(0).setDisambiguation(0);
            } else {
                int max = 0;
                for (DocumentView v : list) {
                    max = Math.max(max, v.getDisambiguation());
                }
                Collections.sort(list, (a, b) -> a.getDisambiguation()
                        - b.getDisambiguation());
                int prev = 0;
                for (DocumentView v : list) {
                    int current = v.getDisambiguation();
                    if (current == prev) {
                        v.setDisambiguation(++max);
                    }
                    prev = current;
                }
            }
        }
    }

    private void updateRecentMenuItemsInAllMenuBars(Observable o) {
        if (isSystemMenuSupported) {
            updateRecentMenuItemsMB(systemMenus);
        } else {
            for (DocumentView v : views()) {
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

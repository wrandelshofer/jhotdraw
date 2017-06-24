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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.Actions;
import org.jhotdraw8.app.action.ScreenMenuBarProxyAction;
import org.jhotdraw8.app.action.file.ClearRecentFilesMenuAction;
import org.jhotdraw8.app.action.file.CloseFileAction;
import org.jhotdraw8.app.action.file.OpenRecentFileAction;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.collection.BooleanKey;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
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

    private final ReadOnlyObjectWrapper<Project> activeProject = new ReadOnlyObjectWrapper<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), (Runnable r) -> {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler((Thread t1, Throwable e) -> {
            throw (RuntimeException) e;
        });
        return t;
    });
    private boolean isSystemMenuSupported;
    private ApplicationModel model;
    private final SetProperty<Project> projects = new SimpleSetProperty<>(FXCollections.observableSet());
    private ArrayList<Action> systemMenuActiveProjectActions = new ArrayList<>();
    private List<Menu> systemMenus;

    {
        activeProject.addListener((o, oldv, newv) -> {
            if (oldv != null) {
                handleProjectDeactivate((DocumentProject) oldv);
            }
            if (newv != null) {
                handleProjectActivate((DocumentProject) newv);
            }
        });
    }

    {
        projects.addListener((SetChangeListener.Change<? extends Project> change) -> {
            if (change.wasAdded()) {
                handleProjectAdded((DocumentProject) change.getElementAdded());
            } else {
                handleProjectRemoved((DocumentProject) change.getElementRemoved());
            }
        });
    }

    public DocumentOrientedApplication() {
        recentUrisProperty().get().addListener(this::updateRecentMenuItemsInAllMenuBars);
    }

    @Override
    public ReadOnlyObjectProperty<Project> activeProjectProperty() {
        return activeProject.getReadOnlyProperty();
    }

    /**
     * Creates a menu bar.
     *
     * @param actions the action map
     * @return the menu bar
     */
    protected MenuBar createMenuBar(HierarchicalMap<String, Action> actions) {
        MenuBar mb = model.createMenuBar();
        Deque<Menu> todo = new LinkedList<>(mb.getMenus());
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
                        systemMenuActiveProjectActions.add(a);
                        Actions.bindMenuItem(mi, a, true);
                    }
                }
            }
        }
        updateRecentMenuItemsMB(mb.getMenus());

        return mb;
    }

    @Override
    public CompletionStage<Project> createProject() {
        return FXWorker.supply(() -> getModel().createProject())
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

    private void disambiguateProjects() {
        HashMap<String, ArrayList<Project>> titles = new HashMap<>();
        for (Project v : projects) {
            String t = v.getTitle();
            titles.computeIfAbsent(t, k -> new ArrayList<>()).add(v);
        }
        for (ArrayList<Project> list : titles.values()) {
            if (list.size() == 1) {
                list.get(0).setDisambiguation(0);
            } else {
                int max = 0;
                for (Project v : list) {
                    max = Math.max(max, v.getDisambiguation());
                }
                Collections.sort(list, (a, b) -> a.getDisambiguation() - b.getDisambiguation());
                int prev = 0;
                for (Project v : list) {
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
     * Called immediately when a project needs to be activated.
     *
     * @param project the project
     */
    protected void handleProjectActivate(DocumentProject project) {
        project.activate();
    }

    /**
     * Called immediately after a project has been added to the projects
     * property.
     *
     * @param project the project
     */
    protected void handleProjectAdded(DocumentProject project) {
        if (project.getApplication() != this) {
            project.setApplication(this);
            project.init();

        }

        project.getActionMap().setParent(getActionMap());
        project.setApplication(DocumentOrientedApplication.this);
        project.setTitle(getLabels().getString("unnamedFile"));
        HierarchicalMap<String, Action> map = project.getActionMap();
        map.put(CloseFileAction.ID, new CloseFileAction(DocumentOrientedApplication.this, project));

        Stage stage = new Stage();
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(project.getNode());
        if (!isSystemMenuSupported) {
            MenuBar mb = createMenuBar(project.getActionMap());
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
                    project.set(QUIT_APPLICATION, true);
                    break;
                }
            }

            project.getActionMap().get(CloseFileAction.ID).handle(new ActionEvent(event.getSource(), event.getTarget()));
        });

        //stage.addEventFilter(KeyEvent.KEY_RELEASED, event -> System.out.println(event));
        stage.focusedProperty().addListener((observer, oldValue, newValue) -> {
            if (newValue) {
                activeProject.set(project);
            }
        });
        stage.titleProperty().bind(CustomBinding.formatted(getLabels().getString("frame.title"),
                project.titleProperty(), getModel().getName(), project.disambiguationProperty(), project.modifiedProperty()));
        project.titleProperty().addListener(this::handleTitleChanged);
        ChangeListener<Boolean> focusListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue == true) {
                activeProject.set(project);
            }
        };
        project.set(FOCUS_LISTENER_KEY, focusListener);
        stage.focusedProperty().addListener(focusListener);
        disambiguateProjects();

        Screen screen = Screen.getPrimary();
        if (screen != null) {
            Rectangle2D bounds = screen.getVisualBounds();
            Random r = new Random();
            if (activeProject.get() != null) {
                Window w = activeProject.get().getNode().getScene().getWindow();
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
            for (int retries = projects.getSize(); retries > 0; retries--) {
                for (Project v : projects) {
                    if (v != project) {
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
        Platform.runLater(project::start);
    }

    /**
     * Called immediately when a project needs to be deactivated.
     *
     * @param project the project
     */
    protected void handleProjectDeactivate(DocumentProject project) {
        project.deactivate();
    }

    /**
     * Called immediately after a project has been removed from the projects
     * property.
     *
     * @param project the project
     */
    protected void handleProjectRemoved(DocumentProject project) {
        Stage stage = (Stage) project.getNode().getScene().getWindow();
        project.stop();
        ChangeListener<Boolean> focusListener = project.get(FOCUS_LISTENER_KEY);
        if (focusListener != null) {
            stage.focusedProperty().removeListener(focusListener);
        }
        stage.close();
        project.dispose();
        project.setApplication(null);
        project.getActionMap().setParent(null);

        if (activeProject.get() == project) {
            activeProject.set(null);
        }

        // Auto close feature
        if (projects.isEmpty() && !isSystemMenuSupported) {
            exit();
        }
    }

    /**
     * Called immediately after a project has been removed from the projects
     * set.
     *
     * @param obs the observable
     */
    protected void handleTitleChanged(Observable obs) {
        disambiguateProjects();
    }

    @Override
    public SetProperty<Project> projectsProperty() {
        return projects;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            isSystemMenuSupported = Toolkit.getToolkit().getSystemMenu().isSupported();
        } catch (IllegalAccessError e) {
            System.err.println("Warning: can not access com.sun.javafx.tk.Toolkit");
        }
        actionMap = model.createApplicationActionMap(this);
        loadRecentUris(model.getName());
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

        final Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
        createProject().whenComplete((pv, ex1) -> {
            DocumentProject v = (DocumentProject) pv;
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
            for (Project v : projects()) {
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

/* @(#)DocumentOrientedApplication.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app;

import com.sun.javafx.menu.MenuBase;
import com.sun.javafx.scene.control.GlobalMenuAdapter;
import com.sun.javafx.tk.Toolkit;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import org.jhotdraw.collection.HierarchicalMap;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.app.action.Actions;
import org.jhotdraw.app.action.app.AboutAction;
import org.jhotdraw.app.action.app.ExitAction;
import org.jhotdraw.app.action.edit.CopyAction;
import org.jhotdraw.app.action.edit.CutAction;
import org.jhotdraw.app.action.edit.DeleteAction;
import org.jhotdraw.app.action.edit.PasteAction;
import org.jhotdraw.app.action.edit.SelectAllAction;
import org.jhotdraw.app.action.file.CloseFileAction;
import org.jhotdraw.app.action.file.NewFileAction;
import org.jhotdraw.app.action.file.OpenFileAction;
import org.jhotdraw.app.action.file.SaveFileAction;
import org.jhotdraw.app.action.file.SaveFileAsAction;
import org.jhotdraw.binding.BindingUtil;
import org.jhotdraw.collection.Key;
import org.jhotdraw.concurrent.BackgroundTask;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.io.DefaultFigureFactory;
import org.jhotdraw.draw.io.FigureFactory;
import org.jhotdraw.draw.io.SimpleXmlIO;
import org.jhotdraw.gui.FileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.util.Resources;

/**
 * DocumentOrientedApplication.
 * @author Werner Randelshofer
 */
public class DocumentOrientedApplication extends javafx.application.Application implements org.jhotdraw.app.Application, ApplicationModel {

    private final static Key<Optional<ChangeListener>> FOCUS_LISTENER_KEY = new Key<>("focusListener", Optional.class, "<ChangeListener>", Optional.empty());
    private boolean isSystemMenuSupported;
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    protected HierarchicalMap<String, Action> actionMap = new HierarchicalMap<>();

    private final ReadOnlyObjectWrapper<View> activeView = new ReadOnlyObjectWrapper<>();
    private final SetProperty<View> views = new SimpleSetProperty<>(FXCollections.observableSet());
    private final ReadOnlyBooleanWrapper disabled = new ReadOnlyBooleanWrapper();
    private final SetProperty<Object> disablers = new SimpleSetProperty<>(FXCollections.observableSet());

    public DocumentOrientedApplication() {
        disabled.bind(Bindings.not(disablers.emptyProperty()));
    }

    @Override
    public SetProperty<Object> disablersProperty() {
        return disablers;
    }

    {
        views.addListener((SetChangeListener.Change<? extends View> change) -> {
            if (change.wasAdded()) {
                onViewAdded(change.getElementAdded());
            } else {
                onViewRemoved(change.getElementRemoved());
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        isSystemMenuSupported = Toolkit.getToolkit().getSystemMenu().isSupported();
        // isSystemMenuSupported=false;
        actionMap = createApplicationActionMap();
        if (isSystemMenuSupported) {
            Platform.setImplicitExit(false);
            ArrayList<MenuBase> menus = new ArrayList<>();
            MenuBar mb = createMenuBar(getActionMap());
            for (Menu m : mb.getMenus()) {
                menus.add(GlobalMenuAdapter.adapt(m));
            }
            Toolkit.getToolkit().getSystemMenu().setMenus(menus);
        }
        createView(v -> add(v));
    }

    @Override
    public SetProperty<View> viewsProperty() {
        return views;
    }

    @Override
    public ReadOnlyObjectProperty<View> activeViewProperty() {
        return activeView.getReadOnlyProperty();
    }

    @Override
    public final void createView(Consumer<View> callback) {
        BackgroundTask<View> t = new BackgroundTask<View>() {

            @Override
            protected View call() throws Exception {
                return instantiateView();
            }

            @Override
            protected void succeeded(View v) {
                v.init();
                v.setTitle(getLabels().getString("unnamedFile"));
                HierarchicalMap<String, Action> map = v.getActionMap();
                map.put(CloseFileAction.ID, new CloseFileAction(DocumentOrientedApplication.this, Optional.of(v)));
                callback.accept(v);
            }
        };
        execute(t);

    }

    protected View instantiateView() {
        TextAreaView v = new TextAreaView();
        return v;
    }

    @Override
    public URIChooser createOpenChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.getFileChooser().getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return c;
    }

    @Override
    public URIChooser createSaveChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.SAVE);
        c.getFileChooser().getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return c;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public String getCopyright() {
        return getClass().getPackage().getImplementationVendor();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /** Called immediately after a view has been added to the views set.
     * @param view the view */
    protected void onViewAdded(View view) {
        view.getActionMap().setParent(Optional.of(getActionMap()));
        Stage stage = new Stage();
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(view.getNode());
        if (!isSystemMenuSupported) {
            MenuBar mb = createMenuBar(view.getActionMap());
            //mb.setUseSystemMenuBar(true);
            borderPane.setTop(mb);
        }
        Scene scene = new Scene(borderPane);
        view.setApplication(this);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            event.consume();
            view.getActionMap().get(CloseFileAction.ID).handle(new ActionEvent(event.getSource(), event.getTarget()));
        });
        stage.focusedProperty().addListener((observer, oldValue, newValue) -> {
            if (newValue) {
                activeView.set(view);
            }
        });
        stage.titleProperty().bind(BindingUtil.formatted(getLabels().getString("frame.title"),
                view.titleProperty(), getModel().getName(), view.disambiguationProperty(), view.modifiedProperty()));
        view.titleProperty().addListener(this::onTitleChanged);
        ChangeListener<Boolean> focusListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue == true) {
                activeView.set(view);
            }
        };
        view.set(FOCUS_LISTENER_KEY, Optional.of(focusListener));
        stage.focusedProperty().addListener(focusListener);
        disambiguateViews();

        Screen screen = Screen.getPrimary();
        if (screen != null) {
            Rectangle2D bounds = screen.getVisualBounds();
            Random r = new Random();
            if (activeView.get() != null) {
                Window w = activeView.get().getNode().getScene().getWindow();
                stage.setWidth(w.getWidth());
                stage.setHeight(w.getHeight());
                stage.setX(Math.min(w.getX() + 22, bounds.getMaxX()
                        - stage.getWidth()));
                stage.setY(Math.min(w.getY() + 22, bounds.getMaxY()
                        - stage.getHeight()));
            } else {
                stage.setWidth(bounds.getWidth() / 4);
                stage.setHeight(bounds.getHeight() / 3);
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
            }

            Outer:
            for (int retries = views.getSize(); retries > 0; retries--) {
                for (View v : views) {
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
    }

    /** Called immediately after a view has been removed from the views set.
     * @param obs the observable */
    protected void onTitleChanged(Observable obs) {
        disambiguateViews();
    }

    /** Called immediately after a view has been removed from the views set.
     * @param view the view */
    protected void onViewRemoved(View view) {
        Stage stage = (Stage) view.getNode().getScene().getWindow();
        view.getActionMap().setParent(Optional.empty());
        view.setApplication(null);
        Optional<ChangeListener> focusListener = view.get(FOCUS_LISTENER_KEY);
        if (focusListener.isPresent()) {
            stage.focusedProperty().removeListener(focusListener.get());
        }
        stage.close();
        view.dispose();

        // Auto close feature
        if (views.isEmpty() && !isSystemMenuSupported) {
            exit();
        }
    }

    /** Gets the resource bundle.
     * @return the resource bundle */
    protected Resources getLabels() {
        return Resources.getBundle("org.jhotdraw.app.Labels");
    }

    /** Creates a menu bar.
     * @param actions the action map
     * @return the menu bar */
    protected MenuBar createMenuBar(HierarchicalMap<String, Action> actions) {
        FXMLLoader loader = new FXMLLoader();
        try {
            MenuBar mb = loader.load(DocumentOrientedApplication.class.getResourceAsStream("DocumentOrientedMenu.fxml"));

            LinkedList<Menu> todo = new LinkedList<>(mb.getMenus());
            while (!todo.isEmpty()) {
                for (MenuItem mi : todo.remove().getItems()) {
                    if (mi instanceof Menu) {
                        todo.add((Menu) mi);
                    } else {
                        Optional<Action> a = actions.getOrParent(mi.getId());
                        if (a.isPresent()) {
                            Actions.bindMenuItem(mi, a.get());
                        } else if (mi.getId() != null) {
                            System.err.println("No action for menu item with id="
                                    + mi.getId());
                            mi.setVisible(false);
                        }
                    }
                }
            }
            return mb;
        } catch (IOException e) {
            throw new InternalError(e);
        }

    }

    public HierarchicalMap<String, Action> createApplicationActionMap() {
        HierarchicalMap<String, Action> map = new HierarchicalMap<>();
        map.put(AboutAction.ID, new AboutAction(this));
        map.put(ExitAction.ID, new ExitAction(this));
        map.put(NewFileAction.ID, new NewFileAction(this));
        map.put(OpenFileAction.ID, new OpenFileAction(this));
        map.put(SaveFileAction.ID, new SaveFileAction(this));
        map.put(SaveFileAsAction.ID, new SaveFileAsAction(this));
        map.put(CloseFileAction.ID, new CloseFileAction(this));
        map.put(CutAction.ID, new CutAction(this));
        map.put(CopyAction.ID, new CopyAction(this));
        map.put(PasteAction.ID, new PasteAction(this));
        map.put(DeleteAction.ID, new DeleteAction(this));
        map.put(SelectAllAction.ID, new SelectAllAction(this));
        return map;
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
    public ApplicationModel getModel() {
        return this;
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public ReadOnlyBooleanProperty disabledProperty() {
        return disabled.getReadOnlyProperty();
    }

    private void disambiguateViews() {
        HashMap<String, ArrayList<View>> titles = new HashMap<>();
        for (View v : views) {
            String t = v.getTitle();
            if (!titles.containsKey(t)) {
                titles.put(t, new ArrayList<>());
            }
            titles.get(t).add(v);
        }
        for (ArrayList<View> list : titles.values()) {
            if (list.size() == 1) {
                list.get(0).setDisambiguation(0);
            } else {
                int max = 0;
                for (View v : list) {
                    max = Math.max(max, v.getDisambiguation());
                }
                Collections.sort(list, (a, b) -> a.getDisambiguation()
                        - b.getDisambiguation());
                int prev = 0;
                for (View v : list) {
                    int current = v.getDisambiguation();
                    if (current == prev) {
                        v.setDisambiguation(++max);
                    }
                    prev = current;
                }
            }
        }
    }

    private MapProperty<Key<?>, Object> properties;

    @Override
    public final MapProperty<Key<?>, Object> properties() {
        if (properties == null) {
            properties = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<Key<?>, Object>()));
        }
        return properties;
    }

    @Override
    public boolean isAllowMultipleViewsPerURI() {
        return false;
    }

    @Override
    public void addRecentURI(URI uri) {
        // FIXME implement me
    }

}

/* @(#)DocumentApplication.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import java.util.MissingResourceException;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.prefs.Preferences;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SetProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.util.prefs.PreferencesUtil;

/**
 * DocumentApplication.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DocumentApplication extends AbstractApplication {

    private final ReadOnlyObjectWrapper<Project> activeProject = new ReadOnlyObjectWrapper<>();

    @Override
    public ReadOnlyObjectProperty<Project> activeProjectProperty() {
        return activeProject.getReadOnlyProperty();
    }

    @Override
    public void addProject() {
        addProject(new Stage());
    }

    @SuppressWarnings("unchecked")
    private void addProject(Stage stage) {
        final Function<Throwable, ?> exceptionHandler = (ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                Resources labels = getLabels();
                Alert alert=new Alert(Alert.AlertType.ERROR,
                        labels.getString("application.createView.error"));
                alert.getDialogPane().setMaxWidth(640.0);
                alert.showAndWait();
            };
            stage.close();
            return null;
        };

        PreferencesUtil.installStagePrefsHandler(Preferences.userNodeForPackage(getClass()), "stage", stage);
        final CompletionStage<Project> createProject = getModel().createProjectAsync();
        final CompletionStage<MenuBar> createMenuBar = getModel().createMenuBarAsync();

        stage.setTitle(getLabels().getString("unnamedFile"));
        stage.show();
        BorderPane rootPane = new BorderPane();

        createMenuBar.handle((mb, ex) -> {
            if (ex != null) {
                exceptionHandler.apply(ex);
            } else {
                if (mb != null) {
                    mb.setUseSystemMenuBar(true);
                    rootPane.setTop(mb);
                }
            }
            return mb;
        }).exceptionally((Function<Throwable, ? extends MenuBar>) exceptionHandler);

        createProject.handle((p, ex) -> {
            if (ex != null) {
                exceptionHandler.apply(ex);
            } else {
                p.init();
                rootPane.setCenter(p.getNode());
                stage.setScene(new Scene(rootPane));
                p.start();
            }
            return p;
        }).exceptionally((Function<Throwable, ? extends Project>) exceptionHandler);
    }

    @Override
    public CompletionStage<Project> createProject() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void execute(Runnable r) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalMap<String, Action> getActionMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Resources getLabels() throws MissingResourceException {
        Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
        return labels;
    }

    @Override
    public SetProperty<Project> projectsProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        addProject(primaryStage);
    }

}

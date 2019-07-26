/*
 * @(#)SimpleApplicationModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.app.AboutAction;
import org.jhotdraw8.app.action.app.ExitAction;
import org.jhotdraw8.app.action.edit.ClearSelectionAction;
import org.jhotdraw8.app.action.edit.CopyAction;
import org.jhotdraw8.app.action.edit.CutAction;
import org.jhotdraw8.app.action.edit.DeleteAction;
import org.jhotdraw8.app.action.edit.PasteAction;
import org.jhotdraw8.app.action.edit.SelectAllAction;
import org.jhotdraw8.app.action.file.CloseFileAction;
import org.jhotdraw8.app.action.file.ExportFileAction;
import org.jhotdraw8.app.action.file.NewFileAction;
import org.jhotdraw8.app.action.file.OpenFileAction;
import org.jhotdraw8.app.action.file.RevertFileAction;
import org.jhotdraw8.app.action.file.SaveFileAction;
import org.jhotdraw8.app.action.file.SaveFileAsAction;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.gui.FileURIChooser;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.gui.URIExtensionFilter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

/**
 * SimpleApplicationModel.
 *
 * @author Werner Randelshofer
 */
public class SimpleApplicationModel implements ApplicationModel {

    private final List<URIExtensionFilter> openExtensionFilters = new ArrayList<>();
    private final List<URIExtensionFilter> saveExtensionFilters = new ArrayList<>();
    private final List<URIExtensionFilter> importExtensionFilters = new ArrayList<>();
    private final List<URIExtensionFilter> exportExtensionFilters = new ArrayList<>();
    private String name;
    private Supplier<DocumentBasedActivity> activityFactory;
    private Supplier<MenuBar> menuFactory;
    private String license;

    public SimpleApplicationModel() {

    }

    public SimpleApplicationModel(
            Supplier<DocumentBasedActivity> activityFactory,
            URL menuFxml,
            String fileDescription,
            DataFormat format,
            String fileExtension) {
        this(null, activityFactory, menuFxml, fileDescription, format, fileExtension);
    }

    public SimpleApplicationModel(String name,
                                  URL activityFxml,
                                  URL menuFxml,
                                  String fileDescription,
                                  DataFormat format,
                                  String fileExtension) {
        this.name = name;
        this.menuFactory = () -> SimpleApplicationModel.createMenuBar(menuFxml, this.getResources());
        URIExtensionFilter fef = new URIExtensionFilter(fileDescription, format, fileExtension);
        this.openExtensionFilters.add(fef);
        this.saveExtensionFilters.add(fef);
        this.activityFactory = () -> SimpleApplicationModel.createActivity(activityFxml, this.getResources());
    }

    public SimpleApplicationModel(String name,
                                  Supplier<DocumentBasedActivity> activityFactory,
                                  URL menuFxml,
                                  String fileDescription,
                                  DataFormat format,
                                  String fileExtension) {
        this.name = name;
        this.menuFactory = () -> SimpleApplicationModel.createMenuBar(menuFxml, this.getResources());
        URIExtensionFilter fef = new URIExtensionFilter(fileDescription, format, fileExtension);
        this.openExtensionFilters.add(fef);
        this.saveExtensionFilters.add(fef);
        this.activityFactory = activityFactory;
    }

    public SimpleApplicationModel(String name,
                                  Supplier<DocumentBasedActivity> activityFactory,
                                  Supplier<MenuBar> menuFactory,
                                  String fileDescription,
                                  DataFormat format,
                                  String fileExtension) {
        this.name = name;
        this.menuFactory = menuFactory;
        URIExtensionFilter fef = new URIExtensionFilter(fileDescription, format, fileExtension);
        this.openExtensionFilters.add(fef);
        this.saveExtensionFilters.add(fef);
        this.activityFactory = activityFactory;
    }

    private static MenuBar createMenuBar(URL fxml, ResourceBundle resources) {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resources);
        try (InputStream in = fxml.openStream()) {
            return loader.load(in);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
    }

    private static DocumentBasedActivity createActivity(URL fxml, ResourceBundle resources) {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resources);
        try (InputStream in = fxml.openStream()) {
            Node node = loader.load(in);
            return loader.getController();
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
    }

    @Override
    public DocumentBasedActivity createActivity() {
        return activityFactory.get();
    }

    public HierarchicalMap<String, Action> createApplicationActionMap(Application app) {
        HierarchicalMap<String, Action> map = new HierarchicalMap<>();
        map.put(AboutAction.ID, new AboutAction(app));
        map.put(ExitAction.ID, new ExitAction(app));
        map.put(NewFileAction.ID, new NewFileAction(app));
        map.put(RevertFileAction.ID, new RevertFileAction(app, null));
        map.put(OpenFileAction.ID, new OpenFileAction(app));
        map.put(SaveFileAction.ID, new SaveFileAction(app));
        map.put(SaveFileAsAction.ID, new SaveFileAsAction(app));
        map.put(ExportFileAction.ID, new ExportFileAction(app));
        map.put(CloseFileAction.ID, new CloseFileAction(app));
        map.put(CutAction.ID, new CutAction(app));
        map.put(CopyAction.ID, new CopyAction(app));
        map.put(PasteAction.ID, new PasteAction(app));
        map.put(DeleteAction.ID, new DeleteAction(app));
        map.put(SelectAllAction.ID, new SelectAllAction(app));
        map.put(ClearSelectionAction.ID, new ClearSelectionAction(app));
        return map;
    }

    @Nonnull
    @Override
    public URIChooser createExportChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.SAVE);
        c.setExtensionFilters(exportExtensionFilters);
        return c;
    }

    @Nonnull
    @Override
    public URIChooser createImportChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.setExtensionFilters(importExtensionFilters);
        return c;
    }

    @Nullable
    @Override
    public MenuBar createMenuBar() {
        return menuFactory == null ? null : menuFactory.get();
    }

    @Nonnull
    @Override
    public URIChooser createOpenChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.setExtensionFilters(openExtensionFilters);
        return c;
    }

    @Nonnull
    @Override
    public URIChooser createSaveChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.SAVE);
        c.setExtensionFilters(saveExtensionFilters);
        return c;
    }

    public Supplier<DocumentBasedActivity> getActivityFactory() {
        return activityFactory;
    }

    public void setActivityFactory(Supplier<DocumentBasedActivity> factory) {
        this.activityFactory = factory;
    }

    @Nonnull
    public List<URIExtensionFilter> getExportExtensionFilters() {
        return exportExtensionFilters;
    }

    @Nonnull
    public List<URIExtensionFilter> getImportExtensionFilters() {
        return importExtensionFilters;
    }

    @Override
    public @Nullable String getLicense() {
        return license;
    }

    public void setLicense(@Nullable String license) {
        this.license = license;
    }

    public Supplier<MenuBar> getMenuFactory() {
        return menuFactory;
    }

    public void setMenuFactory(Supplier<MenuBar> factory) {
        this.menuFactory = factory;
    }

    @Override
    public String getName() {
        return name != null ? name : getClass().getPackage().getImplementationVersion();
    }

    public void setName(String newValue) {
        name = newValue;
    }

    @Nonnull
    public List<URIExtensionFilter> getOpenExtensionFilters() {
        return openExtensionFilters;
    }

    @Override
    public Preferences getPreferences() {
        return Preferences.userNodeForPackage(getClass());
    }

    @Override
    public ResourceBundle getResources() {
        return ApplicationLabels.getResources().asResourceBundle();
    }

    @Nonnull
    public List<URIExtensionFilter> getSaveExtensionFilters() {
        return saveExtensionFilters;
    }

    @Override
    public String getVendor() {
        return getClass().getPackage().getImplementationVendor();
    }

    @Override
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public boolean isAllowMultipleViewsPerURI() {
        return false;
    }

    public void setMenuFxml(URL fxml) {
        this.menuFactory = () -> SimpleApplicationModel.createMenuBar(fxml, getResources());
    }
}

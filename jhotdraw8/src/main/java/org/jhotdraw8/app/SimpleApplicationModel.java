/* @(#)SimpleApplicationModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.prefs.Preferences;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.input.DataFormat;
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
import org.jhotdraw8.util.Resources;

/**
 * SimpleApplicationModel.
 *
 * @author Werner Randelshofer
 * @version $Id: SimpleApplicationModel.java 1199 2016-12-16 13:14:53Z rawcoder
 * $
 */
public class SimpleApplicationModel implements ApplicationModel {

    private String name;
    private final List<URIExtensionFilter> openExtensionFilters = new ArrayList<>();
    private final List<URIExtensionFilter> saveExtensionFilters = new ArrayList<>();
    private final List<URIExtensionFilter> importExtensionFilters = new ArrayList<>();
    private final List<URIExtensionFilter> exportExtensionFilters = new ArrayList<>();
    private Supplier<DocumentProject> projectFactory;
    private URL menuFxml;

    public SimpleApplicationModel() {

    }

    public SimpleApplicationModel(
            Supplier<DocumentProject> viewFactory,
            URL menuFxml,
            String fileDescription,
            DataFormat format,
            String fileExtension) {
        this(null, viewFactory, menuFxml, fileDescription, format, fileExtension);
    }

    public SimpleApplicationModel(String name,
            Supplier<DocumentProject> projectFactory,
            URL menuFxml,
            String fileDescription,
            DataFormat format,
            String fileExtension) {
        this.name = name;
        this.menuFxml = menuFxml;
        URIExtensionFilter fef = new URIExtensionFilter(fileDescription, format, fileExtension);
        openExtensionFilters.add(fef);
        saveExtensionFilters.add(fef);
        this.projectFactory = projectFactory;
    }

    @Override
    public Preferences getPreferences() {
        return Preferences.userNodeForPackage(getClass());
    }

    public Supplier<DocumentProject> getProjectFactory() {
        return projectFactory;
    }

    public void setProjectFactory(Supplier<DocumentProject> projectFactory) {
        this.projectFactory = projectFactory;
    }

    public URL getMenuFxml() {
        return menuFxml;
    }

    public void setMenuFxml(URL menuFxml) {
        this.menuFxml = menuFxml;
    }

    public List<URIExtensionFilter> getOpenExtensionFilters() {
        return openExtensionFilters;
    }

    public List<URIExtensionFilter> getSaveExtensionFilters() {
        return saveExtensionFilters;
    }

    public List<URIExtensionFilter> getImportExtensionFilters() {
        return importExtensionFilters;
    }

    public List<URIExtensionFilter> getExportExtensionFilters() {
        return exportExtensionFilters;
    }

    @Override
    public DocumentProject createProject() {
        return projectFactory.get();
    }

    @Override
    public URIChooser createOpenChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.setExtensionFilters(openExtensionFilters);
        return c;
    }

    @Override
    public URIChooser createSaveChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.SAVE);
        c.setExtensionFilters(saveExtensionFilters);
        return c;
    }

    @Override
    public URIChooser createImportChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.setExtensionFilters(importExtensionFilters);
        return c;
    }

    @Override
    public URIChooser createExportChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.SAVE);
        c.setExtensionFilters(exportExtensionFilters);
        return c;
    }

    @Override
    public String getName() {
        return name != null ? name : getClass().getPackage().getImplementationVersion();
    }

    public void setName(String newValue) {
        name = newValue;
    }

    @Override
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public String getCopyright() {
        return getClass().getPackage().getImplementationVendor();
    }

    @Override
    public boolean isAllowMultipleViewsPerURI() {
        return false;
    }

    @Override
    public MenuBar createMenuBar() {
        if (menuFxml == null) {
            return null;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(getResources());
        try (InputStream in = menuFxml.openStream()) {
            return loader.load(in);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
    }

    @Override
    public ResourceBundle getResources() {
        return Resources.getResources("org.jhotdraw8.app.Labels");
    }

    public HierarchicalMap<String, Action> createApplicationActionMap(Application app) {
        HierarchicalMap<String, Action> map = new HierarchicalMap<>();
        map.put(AboutAction.ID, new AboutAction(app));
        map.put(ExitAction.ID, new ExitAction(app));
        map.put(NewFileAction.ID, new NewFileAction(app));
        map.put(RevertFileAction.ID, new RevertFileAction(app,null));
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

}

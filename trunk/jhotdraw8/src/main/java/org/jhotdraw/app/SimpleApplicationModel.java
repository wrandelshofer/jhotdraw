/* @(#)SimpleApplicationModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.app.action.app.AboutAction;
import org.jhotdraw.app.action.app.ExitAction;
import org.jhotdraw.app.action.edit.ClearSelectionAction;
import org.jhotdraw.app.action.edit.CopyAction;
import org.jhotdraw.app.action.edit.CutAction;
import org.jhotdraw.app.action.edit.DeleteAction;
import org.jhotdraw.app.action.edit.PasteAction;
import org.jhotdraw.app.action.edit.SelectAllAction;
import org.jhotdraw.app.action.file.CloseFileAction;
import org.jhotdraw.app.action.file.ExportFileAction;
import org.jhotdraw.app.action.file.NewFileAction;
import org.jhotdraw.app.action.file.OpenFileAction;
import org.jhotdraw.app.action.file.SaveFileAction;
import org.jhotdraw.app.action.file.SaveFileAsAction;
import org.jhotdraw.collection.HierarchicalMap;
import org.jhotdraw.gui.FileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.util.Resources;

/**
 * SimpleApplicationModel.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleApplicationModel implements ApplicationModel {
    private  String name;
    private final List<FileChooser.ExtensionFilter> openExtensionFilters = new ArrayList<>();
    private final List<FileChooser.ExtensionFilter> saveExtensionFilters = new ArrayList<>();
    private final List<FileChooser.ExtensionFilter> importExtensionFilters = new ArrayList<>();
    private final List<FileChooser.ExtensionFilter> exportExtensionFilters = new ArrayList<>();
    private  Supplier<View> viewFactory;
    private URL menuFxml;

    public SimpleApplicationModel() {
        
    }
    public SimpleApplicationModel(String name, Supplier<View> viewFactory, URL menuFxml, String fileDescription, String fileExtension) {
        this.name = name;
        this.menuFxml=menuFxml;
        FileChooser.ExtensionFilter fef = new FileChooser.ExtensionFilter(fileDescription,fileExtension);
        openExtensionFilters.add(fef);
        saveExtensionFilters.add(fef);
        this.viewFactory = viewFactory;
    }

    public Supplier<View> getViewFactory() {
        return viewFactory;
    }

    public void setViewFactory(Supplier<View> viewFactory) {
        this.viewFactory = viewFactory;
    }

    public URL getMenuFxml() {
        return menuFxml;
    }

    public void setMenuFxml(URL menuFxml) {
        this.menuFxml = menuFxml;
    }

    public List<FileChooser.ExtensionFilter> getOpenExtensionFilters() {
        return openExtensionFilters;
    }

    public List<FileChooser.ExtensionFilter> getSaveExtensionFilters() {
        return saveExtensionFilters;
    }

    public List<FileChooser.ExtensionFilter> getImportExtensionFilters() {
        return importExtensionFilters;
    }

    public List<FileChooser.ExtensionFilter> getExportExtensionFilters() {
        return exportExtensionFilters;
    }

    
    @Override
    public View instantiateView() {
       return viewFactory.get();
    }

 @Override
    public URIChooser createOpenChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.getFileChooser().getExtensionFilters().addAll(openExtensionFilters);
        return c;
    }

    @Override
    public URIChooser createSaveChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.SAVE);
        c.getFileChooser().getExtensionFilters().addAll(saveExtensionFilters);
        return c;
    }

 @Override
    public URIChooser createImportChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.getFileChooser().getExtensionFilters().addAll(importExtensionFilters);
        return c;
    }

    @Override
    public URIChooser createExportChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.SAVE);
        c.getFileChooser().getExtensionFilters().addAll(exportExtensionFilters);
        return c;
    }

    @Override
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
         name=newValue;
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
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(getResources());
        try (InputStream in = menuFxml.openStream()){
            return loader.load(in);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
    }

    @Override
    public ResourceBundle getResources() {
        return Resources.getResources("org.jhotdraw.app.Labels");
    }
    public HierarchicalMap<String, Action> createApplicationActionMap(Application app) {
        HierarchicalMap<String, Action> map = new HierarchicalMap<>();
        map.put(AboutAction.ID, new AboutAction(app));
        map.put(ExitAction.ID, new ExitAction(app));
        map.put(NewFileAction.ID, new NewFileAction(app));
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

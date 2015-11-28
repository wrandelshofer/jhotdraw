/* @(#)SimpleApplicationModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import org.jhotdraw.gui.FileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.util.Resources;

/**
 * SimpleApplicationModel.
 * @author Werner Randelshofer
 */
public class SimpleApplicationModel implements ApplicationModel {
    private  String name;
    private  String fileDescription;
    private  String fileExtension;
    private  Supplier<View> viewFactory;
    private URL menuFxml;

    public SimpleApplicationModel() {
        
    }
    public SimpleApplicationModel(String name, Supplier<View> viewFactory, URL menuFxml, String fileDescription, String fileExtension) {
        this.name = name;
        this.menuFxml=menuFxml;
        this.fileDescription = fileDescription;
        this.fileExtension = fileExtension;
        this.viewFactory = viewFactory;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
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

    
    @Override
    public View instantiateView() {
       return viewFactory.get();
    }

 @Override
    public URIChooser createOpenChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.getFileChooser().getExtensionFilters().add(new FileChooser.ExtensionFilter(fileDescription, fileExtension));
        return c;
    }

    @Override
    public URIChooser createSaveChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.SAVE);
        c.getFileChooser().getExtensionFilters().add(new FileChooser.ExtensionFilter(fileDescription, fileExtension));
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

}

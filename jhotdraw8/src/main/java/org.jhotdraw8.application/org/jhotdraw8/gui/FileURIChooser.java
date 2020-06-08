/*
 * @(#)FileURIChooser.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * FileURIChooser.
 *
 * @author Werner Randelshofer
 */
public class FileURIChooser implements URIChooser {

    /**
     * The associated file chooser object.
     */
    private final FileChooser chooser = new FileChooser();

    private final ObservableList<URIExtensionFilter> filters = FXCollections.observableArrayList();

    private void updateFilters() {
        ObservableList<FileChooser.ExtensionFilter> cfilters = chooser.getExtensionFilters();
        cfilters.clear();
        for (URIExtensionFilter f : filters) {
            cfilters.add(f.getFileChooserExtensionFilter());
        }
    }

    public enum Mode {

        OPEN, SAVE
    }

    private Mode mode;

    public FileURIChooser() {
        this(Mode.OPEN);
    }

    public FileURIChooser(Mode newValue) {
        mode = newValue;
    }

    public void setMode(Mode newValue) {
        mode = newValue;
    }

    public Mode getMode() {
        return mode;
    }

    @NonNull
    public FileChooser getFileChooser() {
        return chooser;
    }

    @Nullable
    @Override
    public URI showDialog(Window parent) {
        updateFilters();
        File f = null;
        switch (mode) {
            case OPEN:
                f = chooser.showOpenDialog(parent);
                break;
            case SAVE:
                f = chooser.showSaveDialog(parent);
                break;
        }
        return f == null ? null : f.toURI();
    }

    public void setExtensionFilters(List<URIExtensionFilter> filters) {
        this.filters.setAll(filters);
    }

    @Nullable
    @Override
    public DataFormat getDataFormat() {
        for (URIExtensionFilter f : filters) {
            if (f.getFileChooserExtensionFilter() == chooser.getSelectedExtensionFilter()) {
                return f.getDataFormat();
            }
        }
        return null;
    }
}

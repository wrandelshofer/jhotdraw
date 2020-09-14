/*
 * @(#)AbstractFileBasedActivity.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.file.CloseFileAction;
import org.jhotdraw8.app.action.file.ExportFileAction;
import org.jhotdraw8.app.action.file.RevertFileAction;
import org.jhotdraw8.app.action.file.SaveFileAction;
import org.jhotdraw8.app.action.file.SaveFileAsAction;

import java.net.URI;

/**
 * AbstractDocumentBasedActivity.
 *
 * @author Werner Randelshofer
 * $$
 */
public abstract class AbstractFileBasedActivity extends AbstractActivity implements FileBasedActivity {

    protected final BooleanProperty modified = new SimpleBooleanProperty() {
        @Override
        public void set(boolean newValue) {
            super.set(newValue); //To change body of generated methods, choose Tools | Templates.
        }

    };
    protected final ObjectProperty<URI> uri = new SimpleObjectProperty<>();
    protected final ObjectProperty<DataFormat> dataFormat = new SimpleObjectProperty<>();

    @NonNull
    @Override
    public BooleanProperty modifiedProperty() {
        return modified;
    }

    @Override
    public void clearModified() {
        modified.set(false);
    }

    protected void markAsModified() {
        modified.set(true);
    }

    @NonNull
    @Override
    public ObjectProperty<URI> uriProperty() {
        return uri;
    }

    @NonNull
    @Override
    public ObjectProperty<DataFormat> dataFormatProperty() {
        return dataFormat;
    }

    @Override
    protected void initActions(@NonNull ObservableMap<String, Action> map) {
        map.put(RevertFileAction.ID, new RevertFileAction(this));
        map.put(SaveFileAction.ID, new SaveFileAction(this));
        map.put(SaveFileAsAction.ID, new SaveFileAsAction(this));
        map.put(ExportFileAction.ID, new ExportFileAction(this));
        map.put(CloseFileAction.ID, new CloseFileAction(this));
    }
}

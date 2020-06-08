/*
 * @(#)AbstractFileBasedActivity.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;

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

}

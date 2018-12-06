/* @(#)AbstractDocumentOrientedActivity.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import java.net.URI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.DataFormat;
import javax.annotation.Nonnull;

/**
 * AbstractDocumentOrientedActivity.
 *
 * @author Werner Randelshofer
 * @version $Id$
 $$
 */
public abstract class AbstractDocumentOrientedActivity extends AbstractActivity implements DocumentOrientedActivity {

    protected final BooleanProperty modified = new SimpleBooleanProperty() {
        @Override
        public void set(boolean newValue) {
            super.set(newValue); //To change body of generated methods, choose Tools | Templates.
        }
        
    };
    protected final ObjectProperty<URI> uri = new SimpleObjectProperty<>();
    protected final ObjectProperty<DataFormat> dataFormat = new SimpleObjectProperty<>();

    @Nonnull
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

    @Nonnull
    @Override
    public ObjectProperty<URI> uriProperty() {
        return uri;
    }

    @Nonnull
    @Override
    public ObjectProperty<DataFormat> dataFormatProperty() {
        return dataFormat;
    }

}

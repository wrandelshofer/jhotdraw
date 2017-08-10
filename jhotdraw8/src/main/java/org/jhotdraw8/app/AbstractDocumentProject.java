/* @(#)AbstractDocumentProject.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import java.net.URI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.DataFormat;

/**
 * AbstractDocumentProject.
 *
 * @author Werner Randelshofer
 * @version $Id$
 $$
 */
public abstract class AbstractDocumentProject extends AbstractProject implements DocumentProject {

    protected final BooleanProperty modified = new SimpleBooleanProperty();
    protected final ObjectProperty<URI> uri = new SimpleObjectProperty<>();
    protected final ObjectProperty<DataFormat> dataFormat = new SimpleObjectProperty<>();

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

    @Override
    public ObjectProperty<URI> uriProperty() {
        return uri;
    }

    @Override
    public ObjectProperty<DataFormat> dataFormatProperty() {
        return dataFormat;
    }

}

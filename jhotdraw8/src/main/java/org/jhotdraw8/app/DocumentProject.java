/* @(#)DocumentProject.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.print.PrinterJob;
import javafx.scene.input.DataFormat;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.collection.Key;

/**
 * A {@code DocumentProject} is a specialization of {@link Project} for document
 * oriented applications.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DocumentProject extends Project {

    /**
     * The modified property is set to true by the view.
     *
     * @return the property
     */
        public ReadOnlyBooleanProperty modifiedProperty();

    default public boolean isModified() {
        return modifiedProperty().get();
    }

    /**
     * Clears the modified property.
     */
    public void clearModified();

        public ObjectProperty<URI> uriProperty();

    @Nullable
    default public URI getURI() {
        return uriProperty().get();
    }

    default public void setURI(@Nullable URI newValue) {
        uriProperty().set(newValue);
    }

        public ObjectProperty<DataFormat> dataFormatProperty();

    @Nullable
    default public DataFormat getDataFormat() {
        return dataFormatProperty().get();
    }

    default public void setDataFormat(@Nullable DataFormat newValue) {
        dataFormatProperty().set(newValue);
    }

    /**
     * Asynchronously reads data from the specified URI and appends it to the
     * content of the view. This method must not change the current document in
     * case of a read failure.
     * <p>
     * The application typically installs a disabler on the view during a read
     * operation. The disabler is removed when the callback is invoked.
     * </p>
     *
     * @param uri the URI
     * @param format the desired data format, null means default data format
     * should be used
     * @param options read options
     * @param append whether to append to the current document or to replace it.
     * @return Returns a CompletionStage which is completed when the read
     * operation has finished.
     */
        public CompletionStage<Void> read( URI uri, @Nullable DataFormat format, @Nullable Map<? super Key<?>, Object> options, boolean append);

    /**
     * Asynchronously writes the content data of view to the specified URI using
     * a Worker.
     * <p>
     * The application typically installs a disabler on the view during a read
     * operation. The disabler is removed when the callback is invoked.
     *
     * @param uri the URI
     * @param format the desired data format, null means default data format
     * should be used
     * @param options write options
     * @return Returns a CompletionStage which is completed when the write
     * operation has finished.
     */
        public CompletionStage<Void> write( URI uri, @Nullable DataFormat format,  Map<? super Key<?>, Object> options);

    /**
     * Clears the view.
     *
     * @return Returns a CompletionStage which is completed when the clear
     * operation has finished. For example
     * {@code return CompletableFuture.completedFuture(null);}
     */
        public CompletionStage<Void> clear();

    /**
     * Prints the current document.
     *
     * @param job the printer job
     * @return Returns a CompletionStage which is completed when the print
     * operation has finished. For example
     * {@code return CompletableFuture.completedFuture(null);}
     */
        public CompletionStage<Void> print( PrinterJob job);

    /**
     * The action map of the view.
     *
     * @return the action map
     */
        public HierarchicalMap<String, Action> getActionMap();

        public IntegerProperty disambiguationProperty();

    default public int getDisambiguation() {
        return disambiguationProperty().get();
    }

    default public void setDisambiguation(int newValue) {
        disambiguationProperty().set(newValue);
    }

    default public boolean isEmpty() {
        return !isModified() && getURI() == null;
    }
}

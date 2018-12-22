/* @(#)DocumentOrientedViewModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.print.PrinterJob;
import javafx.scene.input.DataFormat;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.concurrent.WorkState;

/**
 * A {@code DocumentOrientedViewModel} is a specialization of {@link Activity} for document
 * oriented applications.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DocumentOrientedActivity extends Activity {

    /**
     * The modified property is set to true by the view.
     *
     * @return the property
     */
    ReadOnlyBooleanProperty modifiedProperty();

    default boolean isModified() {
        return modifiedProperty().get();
    }

    /**
     * Clears the modified property.
     */
    void clearModified();

    ObjectProperty<URI> uriProperty();

    @Nullable
    default URI getURI() {
        return uriProperty().get();
    }

    default void setURI(@Nullable URI newValue) {
        uriProperty().set(newValue);
    }

    ObjectProperty<DataFormat> dataFormatProperty();

    @Nullable
    default DataFormat getDataFormat() {
        return dataFormatProperty().get();
    }

    default void setDataFormat(@Nullable DataFormat newValue) {
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
     * @param workState
     * @return Returns a CompletionStage with the data format that was actually used to load the file.
     */
    CompletionStage<DataFormat> read(URI uri, @Nullable DataFormat format, @Nullable Map<? super Key<?>, Object> options, boolean append, WorkState workState);

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
     * @param workState
     * @return Returns a CompletionStage which is completed when the write
     * operation has finished.
     */
    CompletionStage<Void> write(URI uri, @Nullable DataFormat format, Map<? super Key<?>, Object> options, WorkState workState);

    /**
     * Clears the view.
     *
     * @return Returns a CompletionStage which is completed when the clear
     * operation has finished. For example
     * {@code return CompletableFuture.completedFuture(null);}
     */
    CompletionStage<Void> clear();

    /**
     * Prints the current document.
     *
     * @param job the printer job
     * @param workState the work state
     * @return Returns a CompletionStage which is completed when the print
     * operation has finished. For example
     * {@code return CompletableFuture.completedFuture(null);}
     */
    CompletionStage<Void> print(@Nonnull PrinterJob job, @Nonnull WorkState workState);

    default boolean isEmpty() {
        return !isModified() && getURI() == null;
    }
}

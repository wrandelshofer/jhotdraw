/*
 * @(#)FileBasedActivity.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.print.PrinterJob;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.concurrent.SimpleWorkState;
import org.jhotdraw8.concurrent.WorkState;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * A {@link FileBasedActivity} is a specialization of {@link Activity} for document
 * based applications.
 *
 * @author Werner Randelshofer
 */
public interface FileBasedActivity extends Activity {
    String MODIFIED_PROPERTY = "modified";
    String URI_PROPERTY = "uri";
    String DATA_FORMAT_PROPERTY = "dataFormat";

    /**
     * The modified property indicates that the document has been
     * modified and needs to be saved.
     * <p>
     * This property is set to true by the activity itself.
     * <p>
     * The activity does not set the property to false by itself.
     * The property is only set to false by calling {@link #clearModified()}.
     * This is typically done by an {@code Action} invoked by the user,
     * or by an automatic save function managed by the {@code Application}.
     *
     * @return the modified property
     */
    @NonNull ReadOnlyBooleanProperty modifiedProperty();

    default boolean isModified() {
        return modifiedProperty().get();
    }

    /**
     * Clears the modified property.
     *
     * @see #modifiedProperty()
     */
    void clearModified();

    /**
     * This property is used to identify the resource that is
     * used for storing the document persistently.
     * <p>
     * This property is managed by the {@code Action}s that load
     * and save the document.
     *
     * @return the resource
     */
    @NonNull ObjectProperty<URI> uriProperty();

    @Nullable
    default URI getURI() {
        return uriProperty().get();
    }

    default void setURI(@Nullable URI newValue) {
        uriProperty().set(newValue);
    }

    /**
     * This property specifies the format that is used for
     * storing the document persistently.
     * <p>
     * This property is managed by {@code Action}s.
     * Typically by actionss that load or save the document,
     * and actions that manage document properties.
     *
     * @return the resource
     */
    @NonNull ObjectProperty<DataFormat> dataFormatProperty();

    @Nullable
    default DataFormat getDataFormat() {
        return dataFormatProperty().get();
    }

    default void setDataFormat(@Nullable DataFormat newValue) {
        dataFormatProperty().set(newValue);
    }

    /**
     * Asynchronously reads document data from the specified URI.
     * <p>
     * This method must not change the current document if reading fails or is canceled.
     * <p>
     * The activity must be disabled with a {@link SimpleWorkState} during a read.
     * See {@link Disableable}.
     * <p>
     * Usage:
     * <pre><code>
     * WorkState ws = new WorkState("read");
     * activity.addDisablers(ws);
     * activity.read(uri, format, options, insert, workState).handle((fmt,ex)-&gt;{
     *    ...
     *    activity.removeDisablers(ws);
     * });
     * </code></pre>
     *
     * @param uri       the URI
     * @param format    the desired data format, null means default data format
     *                  should be used
     * @param options   read options
     * @param insert    whether to insert into the current document or to replace it.
     * @param workState the work state for monitoring this invocation of the read method.
     *                  The work state is updated by the read method. The worker can be used
     *                  to cancel the read.
     * @return Returns a CompletionStage which is completed with the data format that was
     * actually used for reading the file.
     */
    CompletionStage<DataFormat> read(URI uri, @Nullable DataFormat format, @Nullable Map<Key<?>, Object> options, boolean insert, WorkState workState);

    /**
     * Asynchronously writes document data to the specified URI.
     * <p>
     * This method must not change the current document.
     * <p>
     * The activity must be disabled with a {@link SimpleWorkState} during a read.
     * See usage example in {@link #read}.
     *
     * @param uri       the URI
     * @param format    the desired data format, null means default data format
     *                  should be used
     * @param options   write options
     * @param workState the work state for monitoring this invocation of the write method.
     *                  The work state is updated by the write method. The worker can be used
     *                  to cancel the write.
     * @return Returns a CompletionStage which is completed when the write
     * operation has finished.
     */
    CompletionStage<Void> write(URI uri, @Nullable DataFormat format, Map<Key<?>, Object> options, WorkState workState);

    /**
     * Clears the document.
     *
     * @return Returns a CompletionStage which is completed when the clear
     * operation has finished. For example
     * {@code return CompletableFuture.completedFuture(null);}
     */
    CompletionStage<Void> clear();

    /**
     * Prints the current document.
     * <p>
     * This method must not change the current document.
     * <p>
     * The activity must be disabled with a {@link SimpleWorkState} during printing.
     * See usage example in {@link #read}.
     *
     * @param job       the printer job
     * @param workState the work state for monitoring this invocation of the print method.
     *                  The work state is updated by the print method. The worker can be used
     *                  to cancel printing.
     * @return Returns a CompletionStage which is completed when the print
     * operation has finished. For example
     * {@code return CompletableFuture.completedFuture(null);}
     */
    CompletionStage<Void> print(@NonNull PrinterJob job, @NonNull WorkState workState);

    /**
     * Returns true if this document is empty and can be replaced by
     * another document without that the user loses data.
     *
     * @return true if empty
     */
    default boolean isEmpty() {
        return !isModified() && getURI() == null;
    }
}

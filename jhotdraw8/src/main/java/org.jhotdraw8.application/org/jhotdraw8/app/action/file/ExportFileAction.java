/*
 * @(#)ExportFileAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.scene.control.Dialog;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.FileBasedActivity;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.gui.FileURIChooser;
import org.jhotdraw8.gui.URIChooser;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Presents a file chooser to the user and then exports the contents of the
 * active view to the chosen file.
 * <p>
 *
 * @author Werner Randelshofer
 */
public class ExportFileAction extends AbstractSaveFileAction {

    public static final String ID = "file.export";
    private final Function<DataFormat, Dialog<Map<? super Key<?>, Object>>> optionsDialogFactory;
    @NonNull
    public final static Key<URIChooser> EXPORT_CHOOSER_KEY = new ObjectKey<>("exportChooser", URIChooser.class);
    @NonNull
    public final static Key<Supplier<URIChooser>> EXPORT_CHOOSER_FACTORY_KEY = new ObjectKey<>("exportChooserFactory", Supplier.class, new Class[]{URIChooser.class}, null);

    /**
     * Creates a new instance.
     *
     * @param activity the view
     */
    public ExportFileAction(@NonNull FileBasedActivity activity) {
        this(activity, ID, null);
    }

    public ExportFileAction(@NonNull FileBasedActivity activity, Function<DataFormat, Dialog<Map<? super Key<?>, Object>>> optionsDialog) {
        this(activity, ID, optionsDialog);
    }


    /**
     * Creates a new instance.
     *
     * @param activity      the view, nullable
     * @param id            the id, nonnull
     * @param optionsDialog the dialog for specifying export options
     */
    public ExportFileAction(@NonNull FileBasedActivity activity, String id, Function<DataFormat, Dialog<Map<? super Key<?>, Object>>> optionsDialog) {
        super(activity, id, true);
        this.optionsDialogFactory = optionsDialog;
    }

    @Nullable
    protected URIChooser getChooser(FileBasedActivity view) {
        URIChooser chooser = app.get(EXPORT_CHOOSER_KEY);
        if (chooser == null) {
            Supplier<URIChooser> factory = app.get(EXPORT_CHOOSER_FACTORY_KEY);
            chooser = factory == null ? new FileURIChooser(FileURIChooser.Mode.SAVE) : factory.get();
            app.put(SAVE_CHOOSER_KEY, chooser);
        }
        return chooser;
    }

    @Nullable
    @Override
    protected Dialog<Map<? super Key<?>, Object>> createOptionsDialog(DataFormat format) {
        return optionsDialogFactory == null ? null : optionsDialogFactory.apply(format);
    }

    @Override
    protected void onSaveSucceeded(FileBasedActivity v, URI uri, DataFormat format) {
        // empty
    }
}

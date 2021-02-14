/*
 * @(#)ExportFileAction.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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
import org.jhotdraw8.reflect.TypeToken;

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
    private final Function<DataFormat, Dialog<Map<Key<?>, Object>>> optionsDialogFactory;
    public static final @NonNull Key<URIChooser> EXPORT_CHOOSER_KEY = new ObjectKey<>("exportChooser", URIChooser.class);
    public static final @NonNull Key<Supplier<URIChooser>> EXPORT_CHOOSER_FACTORY_KEY = new ObjectKey<>("exportChooserFactory",
            new TypeToken<Supplier<URIChooser>>() {
            }, null);

    /**
     * Creates a new instance.
     *
     * @param activity the view
     */
    public ExportFileAction(@NonNull FileBasedActivity activity) {
        this(activity, ID, null);
    }

    public ExportFileAction(@NonNull FileBasedActivity activity, Function<DataFormat, Dialog<Map<Key<?>, Object>>> optionsDialog) {
        this(activity, ID, optionsDialog);
    }


    /**
     * Creates a new instance.
     *
     * @param activity      the view, nullable
     * @param id            the id, nonnull
     * @param optionsDialog the dialog for specifying export options
     */
    public ExportFileAction(@NonNull FileBasedActivity activity, String id, Function<DataFormat, Dialog<Map<Key<?>, Object>>> optionsDialog) {
        super(activity, id, true);
        this.optionsDialogFactory = optionsDialog;
    }

    protected @Nullable URIChooser getChooser(FileBasedActivity view) {
        URIChooser chooser = app.get(EXPORT_CHOOSER_KEY);
        if (chooser == null) {
            Supplier<URIChooser> factory = app.get(EXPORT_CHOOSER_FACTORY_KEY);
            chooser = factory == null ? new FileURIChooser(FileURIChooser.Mode.SAVE) : factory.get();
            app.set(EXPORT_CHOOSER_KEY, chooser);
        }
        return chooser;
    }

    @Override
    protected @Nullable Dialog<Map<Key<?>, Object>> createOptionsDialog(DataFormat format) {
        return optionsDialogFactory == null ? null : optionsDialogFactory.apply(format);
    }

    @Override
    protected void onSaveSucceeded(FileBasedActivity v, URI uri, DataFormat format) {
        // empty
    }
}

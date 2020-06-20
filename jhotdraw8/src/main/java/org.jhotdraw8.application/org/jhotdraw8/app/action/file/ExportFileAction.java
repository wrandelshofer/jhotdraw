/*
 * @(#)ExportFileAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.scene.control.Dialog;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Application;
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
    private static final long serialVersionUID = 1L;

    private final Function<DataFormat, Dialog<Map<? super Key<?>, Object>>> optionsDialogFactory;
    @NonNull
    public final static Key<URIChooser> EXPORT_CHOOSER_KEY = new ObjectKey<>("exportChooser", URIChooser.class);
    @NonNull
    public final static Key<Supplier<URIChooser>> EXPORT_CHOOSER_FACTORY_KEY = new ObjectKey<>("exportChooserFactory", Supplier.class, new Class[]{URIChooser.class}, null);

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public ExportFileAction(Application app) {
        this(app, null, ID, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app  the application
     * @param view the view
     */
    public ExportFileAction(Application app, FileBasedActivity view) {
        this(app, view, ID, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app           the application, nonnull
     * @param optionsDialog the dialog for specifying export options
     */
    public ExportFileAction(Application app, Function<DataFormat, Dialog<Map<? super Key<?>, Object>>> optionsDialog) {
        this(app, null, ID, optionsDialog);
    }

    /**
     * Creates a new instance.
     *
     * @param app           the application, nonnull
     * @param view          the view, nullable
     * @param id            the id, nonnull
     * @param optionsDialog the dialog for specifying export options
     */
    public ExportFileAction(Application app, FileBasedActivity view, String id, Function<DataFormat, Dialog<Map<? super Key<?>, Object>>> optionsDialog) {
        super(app, view, id, true);
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

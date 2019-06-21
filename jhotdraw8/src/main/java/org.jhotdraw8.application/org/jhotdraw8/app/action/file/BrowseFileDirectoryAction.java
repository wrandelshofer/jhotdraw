package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.DocumentBasedActivity;
import org.jhotdraw8.app.action.AbstractViewControllerAction;

import java.awt.Desktop;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class BrowseFileDirectoryAction extends AbstractViewControllerAction<DocumentBasedActivity> {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.browseFileDirectory";

    /**
     * Creates a new instance.
     *
     * @param app  the application
     * @param view the view
     */
    public BrowseFileDirectoryAction(Application app, DocumentBasedActivity view) {
        super(app, view, DocumentBasedActivity.class);
        ApplicationLabels.getResources().configureAction(this, ID);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public BrowseFileDirectoryAction(Application app) {
        this(app, null);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, @Nonnull DocumentBasedActivity view) {
        if (isDisabled()) {
            return;
        }
        final URI uri = view.getURI();
        doIt(uri);
    }

    private void doIt(@Nullable URI uri) {
        if (uri == null) {
            return;
        }
        try {
            Path path = Paths.get(uri);
            if (path != null) {
                //Desktop.getDesktop().browseFileDirectory(path.toFile());
                try {

                    try {
                        Desktop.class.getMethod("browseFileDirectory", File.class).invoke(Desktop.getDesktop(), path.toFile());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileSystemNotFoundException e) {
            Logger.getLogger(BrowseFileDirectoryAction.class.getName()).warning(e.getMessage());
        }
    }

}
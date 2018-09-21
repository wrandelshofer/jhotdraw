/* @(#)AsyncFXMLLoader.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.fxml;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import javafx.fxml.FXMLLoader;
import javax.annotation.Nonnull;
import org.jhotdraw8.concurrent.FXWorker;

/**
 * Loads an FXML file asynchronously.
 * <p> 
 * Note that this loader can only be used for FXML files, which do not instantiate windows (for example
 * FXML files with Tooltips can't be loaded).
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AsyncFXMLLoader {

    /**
     * Asynchronously loads the specified FXML file on the common fork-join pool, and returns a completion
     * stage with the FXMLLoader.
     *
     * @param location the location of the FXML file
     * @return the FXMLLoader.
     */
    public static CompletionStage<FXMLLoader> load(URL location) {
        return load(location, null, ForkJoinPool.commonPool());
    }
    public static CompletionStage<FXMLLoader> load(URL location, ResourceBundle resources) {
        return load(location, resources, ForkJoinPool.commonPool());
    }
    /**
     * Asynchronously loads the specified FXML file on the specified executor, and returns a completion
     * stage with the FXMLLoader.
     * <p>
     * You can then get the root and the controller of the loaded scene from the FXMLLoader.
     * 
     * <p>
     * Example usage:
     * <pre>
     *  // Load a menu bar asynchronously and add it to the top of a border pane.
     * 
     *  BorderPane borderPane = new BordePane();
     * 
     *  AsyncFXMLLoader.load(MainApp.class.getResource("MainMenuBar.fxml"))
     *         .whenComplete((loader, throwable) -&gt; {
     *              if (throwable != null) {
     *                  // Loading failed! Put a placeholder into the menu bar.
     *                  borderPane.setTop(new Label(throwable.getMessage()));
     *                  throwable.printStackTrace();
     *              } else { 
     *                  // Loading succeeded.  
     *                  borderPane.setTop(loader.getRoot());
     *                  loader.&gt;MainMenuBarController&lt;getController().setFileController(fileController);
     *                 return loader.getController();
     *              }
     *          });
     * </pre>
     *
     * @param location the location of the FXML file
     * @param resources the resource file for internationalized texts in the FXML file
     * @param executor the executor on which the task should be executed
     * @return the FXMLLoader.
     */
    public static CompletionStage<FXMLLoader> load(URL location, ResourceBundle resources, @Nonnull Executor executor) {
        return FXWorker.supply(executor, 
                () -> {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(location);
                loader.setResources(resources);
                loader.load();
                return loader;
            });
    }
}

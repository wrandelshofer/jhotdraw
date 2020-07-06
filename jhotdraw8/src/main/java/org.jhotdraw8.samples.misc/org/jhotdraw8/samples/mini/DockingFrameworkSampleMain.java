/*
 * @(#)DockingFrameworkSampleMain.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.gui.dock.DockPane;
import org.jhotdraw8.gui.dock.SimpleDockPane;
import org.jhotdraw8.gui.dock.SimpleDockable;
import org.jhotdraw8.gui.dock.TabPaneDock;
import org.jhotdraw8.gui.dock.TabbedAccordionDock;

public class DockingFrameworkSampleMain extends Application {

    @NonNull
    public DockPane initStage(String title,
                              @NonNull Stage primaryStage) {
        SimpleDockPane root = new SimpleDockPane();
        root.setZSupplier(TabbedAccordionDock::new);
        Scene scene = new Scene(root.getNode(), 300, 250);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
        return root;
    }

    @Override
    public void start(@NonNull Stage primaryStage) {


        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> e.printStackTrace());

        DockPane dock = initStage("DockRoot initially empty", primaryStage);

        dock = initStage("DockRoot initially 3 tabs", new Stage());
        TabPaneDock zComp = new TabPaneDock();

        zComp.getDockChildren().add(new SimpleDockable("Label 1", new Label("The quick brown fox 1")));
        zComp.getDockChildren().add(new SimpleDockable("Label 2", new Label("The quick brown fox 2")));
        zComp.getDockChildren().add(new SimpleDockable("Label 3", new Label("The quick brown fox 3")));
        dock.getDockChildren().add(zComp);

        dock = initStage("DockRoot initially 3 tabs", new Stage());
        zComp = new TabPaneDock();
        zComp.getDockChildren().add(new SimpleDockable("Label 4", new Label("The quick brown fox 4")));
        zComp.getDockChildren().add(new SimpleDockable("Label 5", new Label("The quick brown fox 5")));
        zComp.getDockChildren().add(new SimpleDockable("Label 6", new Label("The quick brown fox 6")));
        dock.getDockChildren().add(zComp);


        dock = initStage("DockRoot initially central view", new Stage());
        final TextArea textArea = new TextArea();
        textArea.setText("This is a text area\nin a dock leaf directly added to the Dock.");
        SimpleDockable leaf = new SimpleDockable(textArea);
        dock.getDockChildren().add(leaf);
        //dp.getVerticalTrackFactoryMap().put(SingleItemDock.class, () -> new SplitPaneTrack(Orientation.VERTICAL));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

/*
 * @(#)DockingFrameworkSample.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.gui.dockold.Dock;
import org.jhotdraw8.gui.dockold.DockItem;
import org.jhotdraw8.gui.dockold.DockRoot;
import org.jhotdraw8.gui.dockold.ScrollableSplitPaneTrack;
import org.jhotdraw8.gui.dockold.ScrollableVBoxTrack;
import org.jhotdraw8.gui.dockold.SingleItemDock;
import org.jhotdraw8.gui.dockold.SplitPaneTrack;
import org.jhotdraw8.gui.dockold.TabPaneDock;
import org.jhotdraw8.gui.dockold.TabbedAccordionDock;
import org.jhotdraw8.gui.dockold.Track;

import java.util.function.Supplier;

/**
 * DockingFrameworkSample.
 *
 * @author Werner Randelshofer
 */
public class OldDockingFrameworkSample extends Application {

    private int counter;

    @NonNull
    public DockRoot initStage(String title, @NonNull Stage primaryStage, int numTabs, Supplier<Dock> dockSupp, Supplier<Track> hbSupp, Supplier<Track> vbSupp) {
        DockRoot root = new DockRoot();
        root.setDockFactory(dockSupp);
        root.setVerticalInnerTrackFactory(vbSupp);
        root.setHorizontalTrackFactory(hbSupp);
        if (numTabs > 0) {
            final TabPaneDock tabPane = new TabPaneDock();
            for (int i = 0; i < numTabs; i++) {
                final DockItem tab = new DockItem();
                tab.setText("tab" + (++counter));
                tab.setContent(new Text("Lorem " + counter + "\nThe quick\nbrown fox\njumps over\nthe lazy\ndog."));
                tabPane.getItems().add(tab);
            }
            Track db = new SplitPaneTrack();
            db.getItems().add(tabPane);
            root.addTrack(db);
        }

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
        return root;
    }

    @Override
    public void start(@NonNull Stage primaryStage) {
// Thread.currentThread().setUncaughtExceptionHandler((t,e)->e.printStackTrace());

        initStage("SplitPaneTrack", primaryStage, 0, TabPaneDock::new, () -> new SplitPaneTrack(Orientation.HORIZONTAL), () -> new SplitPaneTrack(Orientation.VERTICAL));

        initStage("SplitPaneTrack and ScrollableSplitPaneTrack", new Stage(), 3, TabPaneDock::new, () -> new SplitPaneTrack(Orientation.HORIZONTAL), ScrollableSplitPaneTrack::new);

        DockRoot dp = initStage("SplitPaneTrack and ScrollableVBoxTrack", new Stage(), 3, TabbedAccordionDock::new, () -> new SplitPaneTrack(Orientation.HORIZONTAL), ScrollableVBoxTrack::new);
        final TextArea textArea = new TextArea();
        dp.getRootTrack().getItems().add(new SingleItemDock(new DockItem(textArea)));
        dp.getVerticalTrackFactoryMap().put(SingleItemDock.class, () -> new SplitPaneTrack(Orientation.VERTICAL));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        launch(args);
    }

}

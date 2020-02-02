package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.gui.docknew.RootDock;
import org.jhotdraw8.gui.docknew.SimpleDockItem;
import org.jhotdraw8.gui.docknew.SimpleRootDock;
import org.jhotdraw8.gui.docknew.TabPaneDock;

public class NewDockingFrameworkSampleMain extends Application {

    @NonNull
    public RootDock initStage(String title,
                              @NonNull Stage primaryStage) {
        RootDock root = new SimpleRootDock();
        Scene scene = new Scene(root.getNode(), 300, 250);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
        return root;
    }

    @Override
    public void start(@NonNull Stage primaryStage) {


        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> e.printStackTrace());

        RootDock dock = initStage("DockRoot initially empty", primaryStage);

        dock = initStage("DockRoot initially 3 tabs", new Stage());
        TabPaneDock zComp = new TabPaneDock();
        zComp.getChildComponents().add(new SimpleDockItem("Label 1", new Label("The quick brown fox 1")));
        zComp.getChildComponents().add(new SimpleDockItem("Label 2", new Label("The quick brown fox 2")));
        zComp.getChildComponents().add(new SimpleDockItem("Label 3", new Label("The quick brown fox 3")));
        dock.getChildComponents().add(zComp);

        dock = initStage("DockRoot initially 3 tabs", new Stage());
        zComp = new TabPaneDock();
        zComp.getChildComponents().add(new SimpleDockItem("Label 4", new Label("The quick brown fox 4")));
        zComp.getChildComponents().add(new SimpleDockItem("Label 5", new Label("The quick brown fox 5")));
        zComp.getChildComponents().add(new SimpleDockItem("Label 6", new Label("The quick brown fox 6")));
        dock.getChildComponents().add(zComp);


        dock = initStage("DockRoot initially central view", new Stage());
        final TextArea textArea = new TextArea();
        textArea.setText("This is a text area\nin a dock leaf directly added to the Dock.");
        SimpleDockItem leaf = new SimpleDockItem(textArea);
        dock.getChildComponents().add(leaf);
        //dp.getVerticalTrackFactoryMap().put(SingleItemDock.class, () -> new SplitPaneTrack(Orientation.VERTICAL));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

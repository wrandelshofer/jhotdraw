/* @(#)FontChooserMain.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javax.annotation.Nonnull;
import org.jhotdraw8.gui.fontchooser.FontDialog;

/**
 * FontChooserMain.
 *
 * @author Werner Randelshofer
 */
public class FontChooserMain extends Application {

    @Override
    public void start(@Nonnull Stage primaryStage) {
                FontDialog fd = new FontDialog();
        Button btn = new Button();
        btn.setText("Open FontChooser");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String fontName = fd.showAndWait().orElse(null);
                if (fontName != null) {
                    btn.setFont(new Font(fontName, 13));
                }
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

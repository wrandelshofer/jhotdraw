package org.jhotdraw8.samples.mini;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

    public class JDK8102170Main extends Application {

        public static void main(String[] args) {
            Application.launch(args);
        }

        @Override
        public void start(Stage stage) throws Exception {
           //System.out.println(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());

            VBox box = new VBox();
            Text text = new Text("Monaco Some text 000");
            text.setFont(new Font("Monaco", 18));
            box.getChildren().add(text);
            text = new Text("Helvetica Some text 000");
            text.setFont(new Font("Helvetica", 18));
            box.getChildren().add(text);
            text = new Text("Monaco Some other text 000");
            text.setFont(Font.font("Monaco", FontWeight.BOLD, FontPosture.ITALIC, 24));
            box.getChildren().add(text);
            text = new Text("Helvetica ome other text 000");
            text.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 24));
            box.getChildren().add(text);

            StringBuffer buffer = new StringBuffer();
            buffer.append("<html>");
            buffer.append("<head>");
            buffer.append("</head>");
            buffer.append("<body>");
            buffer.append("<div>");
            buffer.append("<span style=\'color:blue;font-family: Monaco; font-size: 18px;\'>Monaco Some text 000</span>");
            buffer.append("<br>");
            buffer.append("<span style=\'color:blue;font-family: Helvetica; font-size: 18px;\'>Helvetica Some text 000</span>");
            buffer.append("<br>");
            buffer.append("<span style=\'color:blue;font-family: Monaco; font-style: italic; font-weight: bold; font-size: 24px;\'>Monaco Some other text 000</span>");
            buffer.append("<br>");
            buffer.append("<span style=\'color:blue;font-family: Helvetica; font-style: italic; font-weight: bold; font-size: 24px;\'>Helvetica Some other text 000</span>");
            buffer.append("</body>");
            buffer.append("</html>");

            WebView web = new WebView();
            web.getEngine().loadContent(buffer.toString());
            box.getChildren().add(web);

            Scene scene = new Scene(box, 300, 300);
            stage.setTitle("My JavaFX Application");
            stage.setScene(scene);
            stage.show();
        }
    }
/*
 * @(#)SvgTinySceneGraphReaderTest.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.io;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.OrderedPair;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.render.SimpleDrawingRenderer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class FigureSvgReaderOldTest {

    /**
     * Set this constant to the path of the directory into which you checked
     * out the web-platform-tests/wpt repository from github.
     * <p>
     * <a href="https://github.com/web-platform-tests/wpt">github</a>
     */
    private static final String WPT_PATH = "/Users/Shared/Developer/SVG/web-platform-tests/github/wpt";
    private static final String ICONS_PATH = ".";

    private static final boolean INTERACTIVE = true;

    @BeforeAll
    public static void startJFX() throws InterruptedException, ExecutionException, TimeoutException {
        Platform.setImplicitExit(false);
//        new JFXPanel(); // Initializes the JavaFx Platform
    }


    @Disabled
    @TestFactory
    public @NonNull Stream<DynamicTest> webPlatformTestFactory() throws IOException {
        if (!Files.isDirectory(Path.of(WPT_PATH))) {
            System.err.println("Please fix the path to web-platform-tests: " + WPT_PATH);
            return Stream.empty();
        }


        return
                Files.walk(Path.of(WPT_PATH))
                        //   .filter(p->Files.isRegularFile(p))
                        .filter(p -> p.toString().endsWith("-ref.svg"))
                        .map(p -> new OrderedPair<>(Path.of(p.toString().substring(0, p.toString().length() - "-ref.svg".length()) + ".svg"), p))
                        // .filter(op->Files.isRegularFile(op.first()))
                        .sorted(Comparator.comparing(p -> p.first().getName(p.first().getNameCount() - 1)))
                        .map(p -> dynamicTest(p.first().getName(p.first().getNameCount() - 1).toString(), () -> doWebPlatformTest(p.first(), p.second())));

    }

    @TestFactory
    @Disabled
    public @NonNull Stream<DynamicTest> iconsTestFactory() throws IOException {
        if (!Files.isDirectory(Path.of(ICONS_PATH))) {
            System.err.println("Please fix the icons path: " + ICONS_PATH);
            return Stream.empty();
        }


        return
                Files.walk(Path.of(ICONS_PATH))
                        .filter(p -> p.toString().endsWith(".svg"))
                        .sorted(Comparator.comparing(p -> p.getName(p.getNameCount() - 1)))
                        .map(p -> dynamicTest(p.getName(p.getNameCount() - 1).toString(), () -> doIconTest(p)));

    }

    private void doIconTest(Path testFile) throws Exception {
        System.out.println(testFile);
        System.out.println(testFile.toAbsolutePath());
        FigureSvgReaderOld instance = new FigureSvgReaderOld();
        Figure testNode = instance.read(testFile);

    }

    private void doWebPlatformTest(Path testFile, Path referenceFile) throws Exception {
        System.out.println(testFile);
        System.out.println(referenceFile);

        FigureSvgReaderOld instance = new FigureSvgReaderOld();
        Figure testFigure = instance.read(testFile);
        Figure referenceFigure = instance.read(referenceFile);
        SimpleDrawingRenderer r = new SimpleDrawingRenderer();
        Node testNode = r.render(testFigure);
        Node referenceNode = r.render(referenceFigure);

        CompletableFuture<OrderedPair<WritableImage, WritableImage>> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                WritableImage testImage = testNode.snapshot(new SnapshotParameters(), null);
                WritableImage referenceImage = referenceNode.snapshot(new SnapshotParameters(), null);
                if (INTERACTIVE) {
                    Stage stage = new Stage();
                    HBox hbox = new HBox();
                    hbox.getChildren().addAll(new ImageView(testImage),
                            new ImageView(referenceImage));
                    stage.setScene(new Scene(hbox));
                    stage.sizeToScene();
                    stage.setWidth(Math.max(100, stage.getWidth()));
                    stage.setHeight(Math.max(100, stage.getHeight()));
                    stage.setTitle(testFile.getName(testFile.getNameCount() - 1).toString());
                    stage.show();
                    stage.setOnCloseRequest(evt -> {
                        System.out.println("stage.close requested");
                        future.complete(new OrderedPair<>(testImage, referenceImage));
                        stage.close();
                    });
                } else {
                    future.complete(new OrderedPair<>(testImage, referenceImage));
                }
            } catch (Throwable t) {
                t.printStackTrace();
                future.completeExceptionally(t);

            }
        });

        OrderedPair<WritableImage, WritableImage> pair = future.get(1, TimeUnit.MINUTES);
        WritableImage actualImage = pair.first();
        WritableImage expectedImage = pair.second();

        IntBuffer actualBuffer = createIntBuffer(actualImage);
        IntBuffer expectedBuffer = createIntBuffer(expectedImage);

        assertArrayEquals(expectedBuffer.array(), actualBuffer.array());

    }

    private @NonNull IntBuffer createIntBuffer(WritableImage actualImage) {
        int w = (int) actualImage.getWidth();
        int h = (int) actualImage.getHeight();
        IntBuffer intBuffer = IntBuffer.allocate(w * h);
        actualImage.getPixelReader().getPixels(0, 0, w, h,
                WritablePixelFormat.getIntArgbInstance(), intBuffer, w);
        return intBuffer;
    }
}

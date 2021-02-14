/*
 * @(#)SvgTinySceneGraphReaderTest.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.io;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
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

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * The tests in this class are disabled. They are used for analysing tests
 * from the tests suites. The tests refer to external folders which are
 * not shipped with JHotDraw 8.
 */
public class AnalysisOfSvgTestSuitesTest {

    /**
     * Set this constant to the path of the folder into which you checked
     * out the web-platform-tests/wpt repository from github.
     * <p>
     * <a href="https://github.com/web-platform-tests/wpt">github</a>
     */
    private static final String WPT_PATH = "/Users/Shared/Developer/SVG/web-platform-tests/github/wpt";
    /**
     * Set this constant to the path of the folder into which you checked
     * out the SVG Tiny 1.2 test suite.
     * <p>
     * <a href="https://dev.w3.org/cvsweb/SVG/profiles/1.2T/test/archives/W3C_SVG_12_TinyTestSuite.tar.gz">dev.w3.org</a>
     */
    private static final String W3C_SVG_12_TINY_TEST_SUITE = "/Users/Shared/Developer/SVG/W3C_SVG_12_TinyTestSuite";

    private static final boolean INTERACTIVE = true;
    private static final long INTERACTIVE_TIMEOUT_SECONDS = 60;

    /**
     * This launcher starts the JavaFX application thread.
     */
    public static class Launcher extends Application {
        public Launcher() {

        }

        @Override
        public void start(Stage primaryStage) throws Exception {
        }

        public void launch() {
            new Thread(() -> {
                Application.launch();
            }).start();
        }
    }

    @BeforeAll
    public static void startJavaFX() throws InterruptedException, ExecutionException, TimeoutException {
        Platform.setImplicitExit(false);
        new Launcher().launch();
    }

    /**
     * Tests the {@link FigureSvgTinyReader} against the {@code W3C SVG Tiny 1.2
     * } test suite.
     * <p>
     * The test suite contains of test files and rendered reference files.
     * A rendering of a test file must match the rendered reference file.
     * <p>
     * The test files are contained in the folder "svggen" and their filename
     * ends with ".svg".
     * <p>
     * The corresponding reference files are contained in the folder "png"
     * and their filename is the same as the test file but ends with ".png".
     * <p>
     *
     * @return
     * @throws IOException
     */
    @TestFactory
    public @NonNull Stream<DynamicTest> w3cSvgTiny12TestSuiteTestFactory() throws IOException {
        if (!Files.isDirectory(Path.of(W3C_SVG_12_TINY_TEST_SUITE))) {
            System.err.println("Please fix the path to W3C SVG 1.2 Tiny Test Suite: " +
                    Path.of(W3C_SVG_12_TINY_TEST_SUITE).toAbsolutePath());
            return Stream.empty();
        }

        return Files.walk(Path.of(W3C_SVG_12_TINY_TEST_SUITE, "svggen"))
                .filter(f -> f.getFileName().toString().endsWith(".svg"))
                .filter(f->!f.getFileName().toString().startsWith("animate-")
                &&!f.getFileName().toString().startsWith("conf-")
                &&!f.getFileName().toString().startsWith("coords-")
                &&!f.getFileName().toString().startsWith("extend-")
                &&!f.getFileName().toString().startsWith("fonts-")
                &&!f.getFileName().toString().startsWith("interact-")
                &&!f.getFileName().toString().startsWith("intro-compat-")
                &&!f.getFileName().toString().startsWith("linking-")
                &&!f.getFileName().toString().startsWith("media-")
                &&!f.getFileName().toString().startsWith("metadata-")
                &&!f.getFileName().toString().startsWith("script-")
                &&!f.getFileName().toString().startsWith("udom-")
                )
                .filter(f->f.getFileName().toString().startsWith("paint-"))
                .map(f -> new OrderedPair<Path, Path>(f,
                        f.getParent().getParent().resolve(
                                Path.of("png",
                                        f.getFileName().toString().substring(0, f.getFileName().toString().length() - 4)
                                                + ".png")
                        )
                ))
                .sorted(Comparator.comparing(p -> getLastTwoPathElements(p.first())))
                .map(p -> dynamicTest(getLastTwoPathElements(p.first())
                        , () -> doW3CSvg12TinyTest(p.first(), p.second())));

    }


    /**
     * Tests the {@link FigureSvgTinyReader} against the {@code web-platform}
     * test suite.
     * <p>
     * The test suite contains of test files and reference files.
     * A rendering of a test file must match the rendering of a golden file.
     * <p>
     * If a file name ends with "-ref.svg" then it is a reference file.
     * The corresponding test file has the same file name without "-ref".
     *
     * @return
     * @throws IOException
     */
    @Disabled
    @TestFactory
    public @NonNull Stream<DynamicTest> webPlatformTestFactory() throws IOException {
        if (!Files.isDirectory(Path.of(WPT_PATH))) {
            System.err.println("Please fix the path to web-platform-tests: " +
                    Path.of(WPT_PATH).toAbsolutePath());
            return Stream.empty();
        }

        Set<String> unwantedTests = new HashSet<>();
        unwantedTests.add("bearing/absolute.svg"); // No browser currently supports bearing
        unwantedTests.add("bearing/relative.svg"); // No browser currently supports bearing
        unwantedTests.add("bearing/zero.svg"); // No browser currently supports bearing
        unwantedTests.add("closepath/segment-completing.svg"); // No browser renders this illegal path


        return Files.walk(Path.of(WPT_PATH))
                .filter(p -> p.toString().endsWith("-ref.svg"))
                .map(p -> new OrderedPair<>(Path.of(p.toString().substring(0, p.toString().length() - "-ref.svg".length()) + ".svg"), p))
                .filter(p -> !unwantedTests.contains(getLastTwoPathElements(p.first())))
                .sorted(Comparator.comparing(p -> getLastTwoPathElements(p.first())))
                .map(p -> dynamicTest(getLastTwoPathElements(p.first())
                        , () -> doWebPlatformTest(p.first(), p.second())));

    }

    @NonNull
    protected String getLastTwoPathElements(Path p) {
        return p.getName(p.getNameCount() - 2).toString()
                + "/" + p.getName(p.getNameCount() - 1).toString();
    }

     private void doW3CSvg12TinyTest(Path testFile, Path referenceFile) throws Exception {
        System.out.println("Performing W3C SVG 1.2 Tiny test:");
        System.out.println("  test file     : " + testFile);
        System.out.println("  reference file: " + referenceFile);

        FigureSvgTinyReader instance = new FigureSvgTinyReader();
        instance.setBestEffort(true);
        Figure testFigure = instance.read(new StreamSource(testFile.toFile()));
        System.out.println(instance.getCopyOfErrors().stream().collect(Collectors.joining("\n")));
        dump(testFigure, 0);

        SimpleDrawingRenderer r = new SimpleDrawingRenderer();
        Node testNode = r.render(testFigure);

        CompletableFuture<OrderedPair<WritableImage, WritableImage>> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                WritableImage testImage = testNode.snapshot(new SnapshotParameters(), null);
                Image referenceImageX=new Image(referenceFile.toUri().toURL().toString());
                PixelReader pixelReader = referenceImageX.getPixelReader();
                WritableImage referenceImage=new WritableImage(pixelReader,
                        (int)referenceImageX.getWidth(),(int)referenceImageX.getHeight());
                future.complete(new OrderedPair<>(testImage, referenceImage));
            } catch (Throwable t) {
                t.printStackTrace();
                future.completeExceptionally(t);
            }
        });

        OrderedPair<WritableImage, WritableImage> pair = future.get(1, TimeUnit.MINUTES);
        WritableImage actualImage = pair.first();
        WritableImage expectedImage = pair.second();
        checkImages(testFile, actualImage,expectedImage);
    }

    private void doWebPlatformTest(Path testFile, Path referenceFile) throws Exception {
        System.out.println("Performing web-platform test:");
        System.out.println("  test file     : " + testFile);
        System.out.println("  reference file: " + referenceFile);

        FigureSvgTinyReader instance = new FigureSvgTinyReader();
        instance.setBestEffort(true);
        Figure testFigure = instance.read(new StreamSource(testFile.toFile()));
        System.out.println(instance.getCopyOfErrors().stream().collect(Collectors.joining("\n")));
        dump(testFigure, 0);
        Figure referenceFigure = instance.read(new StreamSource(referenceFile.toFile()));
        SimpleDrawingRenderer r = new SimpleDrawingRenderer();
        Node testNode = r.render(testFigure);
        Node referenceNode = r.render(referenceFigure);

        CompletableFuture<OrderedPair<WritableImage, WritableImage>> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                WritableImage testImage = testNode.snapshot(new SnapshotParameters(), null);
                WritableImage referenceImage = referenceNode.snapshot(new SnapshotParameters(), null);
                future.complete(new OrderedPair<>(testImage, referenceImage));
            } catch (Throwable t) {
                t.printStackTrace();
                future.completeExceptionally(t);
            }
        });

        OrderedPair<WritableImage, WritableImage> pair = future.get(1, TimeUnit.MINUTES);
        WritableImage actualImage = pair.first();
        WritableImage expectedImage = pair.second();
        checkImages(testFile, actualImage,expectedImage);
    }

    private void checkImages(Path testFile,
                             WritableImage actualImage , WritableImage expectedImage) throws InterruptedException, ExecutionException, TimeoutException {

        IntBuffer actualBuffer = createIntBuffer(actualImage);
        IntBuffer expectedBuffer = createIntBuffer(expectedImage);

        if (INTERACTIVE && !actualBuffer.equals(expectedBuffer)) {
            CompletableFuture<Boolean> waitUntilClosed = new CompletableFuture<>();
            AtomicReference<Stage> stageRef=new AtomicReference<>(null);
            Platform.runLater(() -> {
                try {
                    Stage stage = new Stage();
                    stageRef.set(stage);
                    HBox hbox = new HBox();
                    hbox.getChildren().addAll(new ImageView(actualImage),
                            new ImageView(expectedImage));
                    stage.setScene(new Scene(hbox));
                    stage.sizeToScene();
                    stage.setWidth(Math.max(100, stage.getWidth()));
                    stage.setHeight(Math.max(100, stage.getHeight()));
                    stage.setTitle(testFile.getName(testFile.getNameCount() - 1).toString());
                    stage.show();
                    stage.setOnCloseRequest(evt -> {
                        stage.close();
                        waitUntilClosed.complete(Boolean.TRUE);
                    });
                } catch (Throwable t) {
                    t.printStackTrace();
                    waitUntilClosed.completeExceptionally(t);

                }
            });
            try {
                waitUntilClosed.get(INTERACTIVE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                // close stage, move to next test
                Platform.runLater(() -> {
                    Stage stage = stageRef.get();
                    if (stage!=null)stage.close();
                });
            }
        }

        assertArrayEquals(expectedBuffer.array(), actualBuffer.array());
    }

    private void dump(Figure f, int depth) {
        System.out.println(".".repeat(depth) + f.getTypeSelector() + " " + f.getId());
        for (Figure child : f.getChildren()) {
            dump(child, depth + 1);
        }
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

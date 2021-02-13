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
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.OrderedPair;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.figure.DefaultableFigure;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.key.DefaultableStyleableMapAccessor;
import org.jhotdraw8.draw.render.SimpleDrawingRenderer;
import org.jhotdraw8.svg.figure.SvgRectFigure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class FigureSvgTinyReaderTest {

    /**
     * Set this constant to the path of the directory into which you checked
     * out the web-platform-tests/wpt repository from github.
     * <p>
     * <a href="https://github.com/web-platform-tests/wpt">github</a>
     */
    private static final String WPT_PATH = "/Users/Shared/Developer/SVG/web-platform-tests/github/wpt";
    private static final String ICONS_PATH = ".";

    private static final boolean INTERACTIVE = true;

    public static class Launcher extends Application {
public Launcher() {

}
        @Override
        public void start(Stage primaryStage) throws Exception {
        }
        public void launch() {
            Application.launch();
        }
    };
    @BeforeAll
    public static void startJavaFX() throws InterruptedException, ExecutionException, TimeoutException {
        Platform.setImplicitExit(false);
            Launcher launcher = new Launcher();
        new Thread(()-> {
            launcher.launch();
        }).start();
    }


    @Disabled
    @TestFactory
    public @NonNull Stream<DynamicTest> webPlatformTestFactory() throws IOException {
        if (!Files.isDirectory(Path.of(WPT_PATH))) {
            System.err.println("Please fix the path to web-platform-tests: " + WPT_PATH);
            return Stream.empty();
        }

        Set<String> unwantedTests = new HashSet<>();
        unwantedTests.add("bearing/absolute.svg"); // No browser currently supports bearing
        unwantedTests.add("bearing/relative.svg"); // No browser currently supports bearing
        unwantedTests.add("bearing/zero.svg"); // No browser currently supports bearing
        unwantedTests.add("closepath/segment-completing.svg"); // No browser renders this illegal path


        return Files.walk(Path.of(WPT_PATH))
                //   .filter(p->Files.isRegularFile(p))
                .filter(p -> p.toString().endsWith("-ref.svg"))
                .map(p -> new OrderedPair<>(Path.of(p.toString().substring(0, p.toString().length() - "-ref.svg".length()) + ".svg"), p))
                // .filter(op->Files.isRegularFile(op.first()))
                .filter(p -> !unwantedTests.contains(getLastTwoPathElements(p)))
                .sorted(Comparator.comparing(p -> getLastTwoPathElements(p)))
                .map(p -> dynamicTest(getLastTwoPathElements(p)
                        , () -> doWebPlatformTest(p.first(), p.second())));

    }

    @NonNull
    protected String getLastTwoPathElements(OrderedPair<Path, Path> p) {
        return p.first().getName(p.first().getNameCount() - 2).toString()
                + "/" + p.first().getName(p.first().getNameCount() - 1).toString();
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
        FigureSvgTinyReader instance = new FigureSvgTinyReader();
        instance.setBestEffort(true);
        Figure testNode = instance.read(new StreamSource(testFile.toFile()));
        System.err.println(instance.getCopyOfErrors().stream().collect(Collectors.joining("\n")));

        Drawing drawing = (Drawing) testNode;
        assertEquals(new CssSize(22), drawing.get(Drawing.WIDTH), "width");
        assertEquals(new CssSize(22), drawing.get(Drawing.HEIGHT), "height");
    }

    @NonNull
    @TestFactory
    public List<DynamicTest> svgWithDefaultableAttributesFactory() {
        return Arrays.asList(
                dynamicTest("rect with fill value", () -> testDefaultable(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                                "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" baseProfile=\"tiny\" version=\"1.2\">\n" +
                                "  <rect id=\"r\" fill=\"#ff0000\" height=\"200\" width=\"100\" x=\"10\" y=\"20\"/>\n" +
                                "</svg>\n", "r", SvgRectFigure.FILL_KEY,
                        CssColor.valueOf("#ff0000"))),
                dynamicTest("rect inherits fill value from svg element", () -> testDefaultable(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                                "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"" +
                                " baseProfile=\"tiny\" version=\"1.2\"" +
                                " fill=\"#ff0000\">\n" +
                                "  <rect id=\"r\" height=\"200\" width=\"100\" x=\"10\" y=\"20\"/>\n" +
                                "</svg>\n", "r", SvgRectFigure.FILL_KEY,
                        CssColor.valueOf("#ff0000"))),
                dynamicTest("rect inherits fill value from g element", () -> testDefaultable(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                                "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"" +
                                " baseProfile=\"tiny\" version=\"1.2\">\n" +
                                " <g fill=\"#ff0000\">\n" +
                                "  <rect id=\"r\" height=\"200\" width=\"100\" x=\"10\" y=\"20\"/>\n" +
                                "</g>\n"
                                + "</svg>\n",
                        "r", SvgRectFigure.FILL_KEY,
                        CssColor.valueOf("#ff0000")))
        );
    }

    private <T> void testDefaultable(String svg, String id, DefaultableStyleableMapAccessor<T> key, T expected) throws IOException {
        FigureSvgTinyReader instance = new FigureSvgTinyReader();
        Figure drawing = instance.read(new StreamSource(new StringReader(svg)));
        for (Figure f : drawing.depthFirstIterable()) {
            if (f instanceof DefaultableFigure) {
                DefaultableFigure df = (DefaultableFigure) f;
                if (id.equals(f.getId())) {
                    T actual = df.getDefaultableStyled(key);
                    assertEquals(expected, actual);
                }
            }
        }
    }




    private void doWebPlatformTest(Path testFile, Path referenceFile) throws Exception {
        System.out.println(testFile);
        System.out.println(referenceFile);

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

        IntBuffer actualBuffer = createIntBuffer(actualImage);
        IntBuffer expectedBuffer = createIntBuffer(expectedImage);

        if (INTERACTIVE && !actualBuffer.equals(expectedBuffer)) {
            CompletableFuture<Boolean> waitUntilClosed = new CompletableFuture<>();
            Platform.runLater(() -> {
                try {
                    Stage stage = new Stage();
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
                waitUntilClosed.get(1, TimeUnit.MINUTES);
            } catch (TimeoutException e) {
                // keep stage open, move to next test
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

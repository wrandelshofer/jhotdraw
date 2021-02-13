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

}

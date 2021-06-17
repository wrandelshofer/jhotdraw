/*
 * @(#)CssSvgPathConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.geom.FXPathBuilder;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Converts an SVG path to a AWT path.
 * <p>
 * The null value will be converted to the CSS identifier "none".
 *
 * @author Werner Randelshofer
 */
public class CssFXSvgPathConverter extends AbstractCssConverter<List<PathElement>> {


    public CssFXSvgPathConverter(boolean nullable) {
        super(nullable);
    }

    @Override
    public @NonNull List<PathElement> parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        tt.requireNextToken(CssTokenType.TT_STRING, "⟨SvgPath⟩: String expected.");
        final String svgPathString = tt.currentStringNonNull();

        try {
            final FXPathBuilder builder = new FXPathBuilder();
            Shapes.buildFromSvgString(builder, svgPathString);
            return builder.build().getElements();
        } catch (final ParseException ex) {
            List<PathElement> p = new ArrayList<>();
            p.add(new MoveTo(0, 0));
            p.add(new LineTo(10, 0));
            p.add(new LineTo(10, 10));
            p.add(new LineTo(0, 10));
            p.add(new ClosePath());
            return p;
        }
    }

    @Override
    protected <TT extends List<PathElement>> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_STRING, Shapes.doubleSvgStringFromElements(value)));
    }

    @Override
    public @NonNull String getHelpText() {
        StringBuilder buf = new StringBuilder("Format of ⟨SvgPath⟩: \" ⟨moveTo ⟩｛ moveTo｜⟨lineTo⟩｜⟨quadTo⟩｜⟨cubicTo⟩｜⟨arcTo⟩｜⟨closePath⟩ ｝ \"");
        buf.append("\nFormat of ⟨moveTo ⟩: M ⟨x⟩ ⟨y⟩ ｜m ⟨dx⟩ ⟨dy⟩ ");
        buf.append("\nFormat of ⟨lineTo ⟩: L ⟨x⟩ ⟨y⟩ ｜l ⟨dx⟩ ⟨dy⟩ | H ⟨x⟩ | h ⟨dx⟩ | V ⟨y⟩ | v ⟨dy⟩");
        buf.append("\nFormat of ⟨quadTo ⟩: Q ⟨x⟩ ⟨y⟩  ⟨x1⟩ ⟨y1⟩ ｜q ⟨dx⟩ ⟨dy⟩  ⟨x1⟩ ⟨y1⟩ ｜T ⟨x⟩ ⟨y⟩ ｜t ⟨dx⟩ ⟨dy⟩");
        buf.append("\nFormat of ⟨cubicTo ⟩: C ⟨x⟩ ⟨y⟩  ⟨x1⟩ ⟨y1⟩  ⟨x2⟩ ⟨y2⟩ ｜c ⟨dx⟩ ⟨dy⟩  ⟨dx1⟩ ⟨dy1⟩  ⟨dx2⟩ ⟨dy2⟩｜ S ⟨x⟩ ⟨y⟩  ⟨x1⟩ ⟨y1⟩ ｜s ⟨dx⟩ ⟨dy⟩  ⟨dx1⟩ ⟨dy1⟩");
        buf.append("\nFormat of ⟨arcTo ⟩: A ⟨x⟩ ⟨y⟩ ⟨r1⟩ ⟨r2⟩ ⟨angle⟩ ⟨larrgeArcFlag⟩ ⟨sweepFlag⟩ ｜a ⟨dx⟩ ⟨dy⟩ ⟨r1⟩ ⟨r2⟩ ⟨angle⟩ ⟨larrgeArcFlag⟩ ⟨sweepFlag⟩ ");
        buf.append("\nFormat of ⟨closePath ⟩: Z ｜z ");
        return buf.toString();
    }


    @Override
    public @Nullable List<PathElement> getDefaultValue() {
        return null;
    }


}

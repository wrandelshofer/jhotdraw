/* @(#)CssPoint2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

/**
 * Converts a {@code Dimension2D} into a {@code String} and vice versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssPaperSizeConverter implements Converter<Dimension2D> {

    private final CssSizeConverter sizeConverter = new CssSizeConverter(false);
    @Nonnull
    private final static Map<String, Dimension2D> paperSizes;
    @Nonnull
    private final static Map<Dimension2D, String> sizePapers;

    static {
        Map<String, Dimension2D> m = new LinkedHashMap<>();
        m.put("A0", new Dimension2D(new CssDimension(841, "mm"), new CssDimension(1189, "mm")));
        m.put("A1", new Dimension2D(new CssDimension(594, "mm"), new CssDimension(841, "mm")));
        m.put("A2", new Dimension2D(new CssDimension(420, "mm"), new CssDimension(594, "mm")));
        m.put("A3", new Dimension2D(new CssDimension(297, "mm"), new CssDimension(420, "mm")));
        m.put("A4", new Dimension2D(new CssDimension(210, "mm"), new CssDimension(297, "mm")));
        m.put("A5", new Dimension2D(new CssDimension(148, "mm"), new CssDimension(210, "mm")));
        m.put("A6", new Dimension2D(new CssDimension(105, "mm"), new CssDimension(148, "mm")));
        m.put("DesignatedLong", new Dimension2D(new CssDimension(110, "mm"), new CssDimension(220, "mm")));
        m.put("Letter", new Dimension2D(new CssDimension(8.5, "in"), new CssDimension(11, "in")));
        m.put("Legal", new Dimension2D(new CssDimension(8.4, "in"), new CssDimension(14, "in")));
        m.put("Tabloid", new Dimension2D(new CssDimension(11.0, "in"), new CssDimension(17.0, "in")));
        m.put("Executive", new Dimension2D(new CssDimension(7.25, "in"), new CssDimension(10.5, "in")));
        m.put("x8x10", new Dimension2D(new CssDimension(8, "in"), new CssDimension(10, "in")));
        m.put("MonarchEnvelope", new Dimension2D(new CssDimension(3.87, "in"), new CssDimension(7.5, "in")));
        m.put("Number10Envelope", new Dimension2D(new CssDimension(4.125, "in"), new CssDimension(9.5, "in")));
        m.put("C", new Dimension2D(new CssDimension(17.0, "in"), new CssDimension(22.0, "in")));
        m.put("B4", new Dimension2D(new CssDimension(257, "mm"), new CssDimension(364, "mm")));
        m.put("B5", new Dimension2D(new CssDimension(182, "mm"), new CssDimension(257, "mm")));
        m.put("B6", new Dimension2D(new CssDimension(128, "mm"), new CssDimension(182, "mm")));
        m.put("JapanesePostcard", new Dimension2D(new CssDimension(100, "mm"), new CssDimension(148, "mm")));
        paperSizes = m;

        Map<Dimension2D, String> x = new LinkedHashMap<>();
        for (Map.Entry<String, Dimension2D> e : m.entrySet()) {
            final Dimension2D v = e.getValue();
            x.put(v, e.getKey() + " portrait");
            x.put(new Dimension2D(v.getY(), v.getX()), e.getKey() + " landscape");
        }
        sizePapers = x;
    }

    private final static String LANDSCAPE = "landscape";
    private final static String PORTRAIT = "portrait";

    @Nullable
    private Dimension2D parsePageSize(CssTokenizer tt, IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() == CssTokenType.TT_IDENT) {
            Dimension2D paperSize = paperSizes.get(tt.currentString());
            if (paperSize == null) {
                throw new ParseException("Illegal paper format:" + tt.currentString(), tt.getStartPosition());
            }
            if (tt.next() == CssTokenType.TT_IDENT) {
                switch (tt.currentString()) {
                    case LANDSCAPE:
                        paperSize = new Dimension2D(paperSize.getY(), paperSize.getX());
                        break;
                    case PORTRAIT:
                        break;
                    default:
                        tt.pushBack();
                }
            } else {
                tt.pushBack();
            }
            return paperSize;
        } else {
            tt.pushBack();
            CssDimension x = sizeConverter.parse(tt,idFactory);
            CssDimension y = sizeConverter.parse(tt, idFactory);
            return new Dimension2D(x, y);
        }
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nonnull Dimension2D value) throws IOException {
        String paper = sizePapers.get(value);
        if (paper != null) {
            out.append(paper);
        } else {
            out.append(sizeConverter.toString(value.getX()));
            out.append(' ');
            out.append(sizeConverter.toString(value.getY()));
        }
    }

    @Nullable
    @Override
    public Dimension2D fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new StreamCssTokenizer(buf);
        return parsePageSize(tt, idFactory);

    }

    @Nullable
    @Override
    public Dimension2D getDefaultValue() {
        return new Dimension2D(new CssDimension(0, null), new CssDimension(0, null));
    }

    @Nonnull
    @Override
    public String getHelpText() {
        StringBuilder buf = new StringBuilder();
        for (String s : paperSizes.keySet()) {
            if (buf.length() != 0) {
                buf.append('｜');
            }
            buf.append(s);
        }
        return "Format of ⟨PageSize⟩: " + "⟨width⟩mm ⟨height⟩mm｜⟨PaperFormat⟩ landscape｜⟨PaperFormat⟩ portrait"
                + "\nFormat of ⟨PaperFormat⟩: " + buf.toString();
    }
}

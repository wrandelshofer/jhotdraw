/*
 * @(#)CssPaperSizeConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts a {@code CssPoint2D} into a {@code String} and vice versa.
 *
 * @author Werner Randelshofer
 */
public class CssPaperSizeConverter implements Converter<CssPoint2D> {

    private final CssSizeConverter sizeConverter = new CssSizeConverter(false);
    @NonNull
    private final static Map<String, CssPoint2D> paperSizes;
    @NonNull
    private final static Map<CssPoint2D, String> sizePapers;

    static {
        Map<String, CssPoint2D> m = new LinkedHashMap<>();
        m.put("A0", new CssPoint2D(new CssSize(841, "mm"), new CssSize(1189, "mm")));
        m.put("A1", new CssPoint2D(new CssSize(594, "mm"), new CssSize(841, "mm")));
        m.put("A2", new CssPoint2D(new CssSize(420, "mm"), new CssSize(594, "mm")));
        m.put("A3", new CssPoint2D(new CssSize(297, "mm"), new CssSize(420, "mm")));
        m.put("A4", new CssPoint2D(new CssSize(210, "mm"), new CssSize(297, "mm")));
        m.put("A5", new CssPoint2D(new CssSize(148, "mm"), new CssSize(210, "mm")));
        m.put("A6", new CssPoint2D(new CssSize(105, "mm"), new CssSize(148, "mm")));
        m.put("DesignatedLong", new CssPoint2D(new CssSize(110, "mm"), new CssSize(220, "mm")));
        m.put("Letter", new CssPoint2D(new CssSize(8.5, "in"), new CssSize(11, "in")));
        m.put("Legal", new CssPoint2D(new CssSize(8.4, "in"), new CssSize(14, "in")));
        m.put("Tabloid", new CssPoint2D(new CssSize(11.0, "in"), new CssSize(17.0, "in")));
        m.put("Executive", new CssPoint2D(new CssSize(7.25, "in"), new CssSize(10.5, "in")));
        m.put("x8x10", new CssPoint2D(new CssSize(8, "in"), new CssSize(10, "in")));
        m.put("MonarchEnvelope", new CssPoint2D(new CssSize(3.87, "in"), new CssSize(7.5, "in")));
        m.put("Number10Envelope", new CssPoint2D(new CssSize(4.125, "in"), new CssSize(9.5, "in")));
        m.put("C", new CssPoint2D(new CssSize(17.0, "in"), new CssSize(22.0, "in")));
        m.put("B4", new CssPoint2D(new CssSize(257, "mm"), new CssSize(364, "mm")));
        m.put("B5", new CssPoint2D(new CssSize(182, "mm"), new CssSize(257, "mm")));
        m.put("B6", new CssPoint2D(new CssSize(128, "mm"), new CssSize(182, "mm")));
        m.put("JapanesePostcard", new CssPoint2D(new CssSize(100, "mm"), new CssSize(148, "mm")));
        paperSizes = m;

        Map<CssPoint2D, String> x = new LinkedHashMap<>();
        for (Map.Entry<String, CssPoint2D> e : m.entrySet()) {
            final CssPoint2D v = e.getValue();
            x.put(v, e.getKey() + " portrait");
            x.put(new CssPoint2D(v.getY(), v.getX()), e.getKey() + " landscape");
        }
        sizePapers = x;
    }

    private final static String LANDSCAPE = "landscape";
    private final static String PORTRAIT = "portrait";

    @Nullable
    private CssPoint2D parsePageSize(@NonNull CssTokenizer tt, IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() == CssTokenType.TT_IDENT) {
            CssPoint2D paperSize = paperSizes.get(tt.currentString());
            if (paperSize == null) {
                throw new ParseException("Illegal paper format:" + tt.currentString(), tt.getStartPosition());
            }
            if (tt.next() == CssTokenType.TT_IDENT) {
                switch (tt.currentString()) {
                    case LANDSCAPE:
                        paperSize = new CssPoint2D(paperSize.getY(), paperSize.getX());
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
            CssSize x = sizeConverter.parse(tt, idFactory);
            CssSize y = sizeConverter.parse(tt, idFactory);
            return new CssPoint2D(x, y);
        }
    }

    @Override
    public void toString(@NonNull Appendable out, IdFactory idFactory, @NonNull CssPoint2D value) throws IOException {
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
    public CssPoint2D fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new StreamCssTokenizer(buf);
        return parsePageSize(tt, idFactory);

    }

    @Nullable
    @Override
    public CssPoint2D getDefaultValue() {
        return new CssPoint2D(new CssSize(0), new CssSize(0));
    }

    @NonNull
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

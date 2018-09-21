/* @(#)CssSizeListConverter.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import javafx.geometry.Point2D;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.UnitConverter;

/**
 * CssSizeListConverter.
 * <p>
 Parses a list ofCollection sizes.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssPoint2DListConverter implements Converter<ImmutableList<Point2D>> {

    @Nonnull
    private CssDoubleConverter doubleConverter = new CssDoubleConverter();
    @Nonnull
    private UnitConverter unitConverter = DefaultUnitConverter.getInstance();

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, ImmutableList<Point2D> value) throws IOException {
        toStringFromCollection(out, idFactory, value);
    }

    public void toStringFromCollection(@Nonnull Appendable out, IdFactory idFactory, @Nullable Collection<Point2D> value) throws IOException {
        if (value == null || value.isEmpty()) {
            out.append("none");
            return;
        }
        boolean first = true;
        for (Point2D p : value) {
            if (first) {
                first = false;
            } else {
                out.append(' ');
            }
            out.append(doubleConverter.toString(p.getX()));
            out.append(',');
            out.append(doubleConverter.toString(p.getY()));
        }
    }

    @Override
    public ImmutableList<Point2D> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        ArrayList<Point2D> l = new ArrayList<>();
        CssTokenizerInterface tt = new CssTokenizer(buf);
        tt.setSkipWhitespaces(true);
        if (tt.nextToken() == CssTokenizer.TT_IDENT && "none".equals(tt.currentStringValue())) {
            tt.skipWhitespace();
            return ImmutableList.ofCollection(l);
        } else {
            tt.pushBack();
        }

        Loop:
        while (tt.nextToken() != CssTokenizer.TT_EOF) {
            tt.pushBack();
            double x = readCoordinate(tt, idFactory);
            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
            double y = readCoordinate(tt, idFactory);
            l.add(new Point2D(x, y));
            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
        }
        tt.skipWhitespace();
        return ImmutableList.ofCollection(l);
    }

    private double readCoordinate(CssTokenizerInterface tt, IdFactory idFactory) throws ParseException, IOException {
        double x;
        switch (tt.nextToken()) {
            case CssTokenizerInterface.TT_DIMENSION: {
                double value = tt.currentNumericValue().doubleValue();
                x = unitConverter.convert(value, tt.currentStringValue(), "px");
                break;
            }
            case CssTokenizerInterface.TT_PERCENTAGE: {
                double value = tt.currentNumericValue().doubleValue() / 100.0;
                x = unitConverter.convert(value, "%", "px");
                break;
            }
            case CssTokenizerInterface.TT_NUMBER: {
                double value = tt.currentNumericValue().doubleValue();
                x = value;
                break;
            }
            case CssTokenizerInterface.TT_IDENT: {
                double value;
                switch (tt.currentStringValue()) {
                    case "INF":
                        value = Double.POSITIVE_INFINITY;
                        break;
                    case "-INF":
                        value = Double.NEGATIVE_INFINITY;
                        break;
                    case "NaN":
                        value = Double.NaN;
                        break;
                    default:
                        throw new ParseException("number expected:" + tt.currentStringValue(), tt.getStartPosition());
                }
                x = value;
                break;
            }
            default:
                throw new ParseException("coordinate expected:" + tt.currentStringValue(), tt.getStartPosition());
        }
        return x;
    }

    @Override
    public ImmutableList<Point2D> getDefaultValue() {
        return ImmutableList.emptyList();
    }

    @Nonnull
    public String toStringFromCollection(Collection<Point2D> value) {
        StringBuilder out = new StringBuilder();
        try {
            toStringFromCollection(out, value);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
        return out.toString();
    }

    public void toStringFromCollection(@Nonnull Appendable out, Collection<Point2D> value) throws IOException {
        toStringFromCollection(out, null, value);
    }
}

/* @(#)CssSizeListConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javafx.geometry.Point2D;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.IdFactory;

/**
 * CssSizeListConverter.
 * <p>
 * Parses a list of sizes.
 *
 * @author Werner Randelshofer
 */
public class CssPoint2DListConverter implements Converter<ImmutableObservableList<Point2D>> {

    private CssSizeConverter doubleConverter = new CssSizeConverter();

    @Override
    public void toString(Appendable out, IdFactory idFactory, ImmutableObservableList<Point2D> value) throws IOException {
        toStringFromCollection(out, idFactory, value);
    }

    public void toStringFromCollection(Appendable out, IdFactory idFactory, Collection<Point2D> value) throws IOException {
        if (value == null || value.isEmpty()) {
            out.append("none");
            return;
        }
        boolean first = true;
        for (Point2D p : value) {
            if (first) {
                first = false;
            } else {
                out.append(", ");
            }
            out.append(doubleConverter.toString(p.getX()));
            out.append(' ');
            out.append(doubleConverter.toString(p.getY()));
        }
    }

    @Override
    public ImmutableObservableList<Point2D> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        ArrayList<Point2D> l = new ArrayList<>();
        CssTokenizerInterface tt = new CssTokenizer(buf);
        tt.setSkipWhitespaces(true);
        if (tt.nextToken() == CssTokenizer.TT_IDENT && "none".equals(tt.currentStringValue())) {
            tt.skipWhitespace();
            return new ImmutableObservableList<>(l);
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
        return new ImmutableObservableList<>(l);
    }

    private double readCoordinate(CssTokenizerInterface tt, IdFactory idFactory) throws ParseException, IOException {
        double x;
        switch (tt.nextToken()) {
            case CssTokenizerInterface.TT_DIMENSION: {
                double value = tt.currentNumericValue().doubleValue();
                x = idFactory.convert(value, tt.currentStringValue(), "px");
                break;
            }
            case CssTokenizerInterface.TT_PERCENTAGE: {
                double value = tt.currentNumericValue().doubleValue() / 100.0;
                x = idFactory.convert(value, "%", "px");
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
    public ImmutableObservableList<Point2D> getDefaultValue() {
        return ImmutableObservableList.emptyList();
    }

    public String toStringFromCollection(Collection<Point2D> value) {
        StringBuilder out = new StringBuilder();
        try {
            toStringFromCollection(out, value);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
        return out.toString();
    }

    public void toStringFromCollection(Appendable out, Collection<Point2D> value) throws IOException {
        toStringFromCollection(out, null, value);
    }
}

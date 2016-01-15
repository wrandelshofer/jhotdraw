/* @(#)SvgTransformListConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw.css.CssTokenizer;
import org.jhotdraw.draw.io.IdFactory;
import static java.lang.Math.*;

/**
 * SvgTransformListConverter.
 * <p>
 * Parses a transform list given in the following EBNF:
 * <pre>
 * TransformList = [ Transform { S, Transform } ] ;
 * Transform     = ( Matrix | Translate | Scale | Rotate | SkewX | SkewY ) ;
 *
 * Matrix        = "matrix(" , [S] , a , C , b , C , c , C , d , C , e , C , f , [S], ")" ;
 * Translate     = "translate(" , [S] , tx , [ C , ty ] , [S], ")" ;
 * Scale         = "scale(" , [S] , sx , [ C , sy ] , [S], ")" ;
 * Rotate        = "rotate(" , [S] , rotate-angle , [ C , cs, C , cy ] , [S], ")" ;
 * SkewX         = "skewX(" , [S] , skew-angle , [S], ")" ;
 * SkewY         = "skewY(" , [S] , skew-angle , [S], ")" ;
 *
 * C             = ( S , { S } | { S } , "," , { S } ) ;
 * S             = (* white space *)
 * </pre>
 * <p>
 * References:
 * <a href="http://www.w3.org/TR/2008/REC-SVGTiny12-20081222/coords.html#TransformAttribute">
 * SVG Tiny 1.2, The 'transform' attribute.
 * </a>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SvgTransformListConverter implements Converter<List<Transform>> {

    private final CssSizeConverter nb = new CssSizeConverter();

    @Override
    public void toString(Appendable buf, IdFactory idFactory, List<Transform> txs) throws IOException {
        boolean first = true;
        for (Transform tx : txs) {
            if (!tx.isIdentity()) {
                if (first) {
                    first = false;
                } else {
                    buf.append(' ');
                }
                if (tx instanceof Translate) {
                    Translate tr = (Translate) tx;
                    buf.append("translate(")
                            .append(nb.toString(tr.getTx()));
                    if (tr.getTy() != 0.0) {
                        buf.append(',')
                                .append(nb.toString(tr.getTy()));
                    }

                    buf.append(')');
                } else if (tx instanceof Scale) {
                    Scale ts = (Scale) tx;
                    if (ts.getPivotX() != 0.0 || ts.getPivotY() != 0.0) {
                        // [a c e]   [ x 0 (1-x)*pivotx ]
                        // [b d f] = [ 0 y (1-y)*pivoty ]
                        // [0 0 1]   [ 0 0      1       ]
                        buf.append("matrix(")
                                .append(nb.toString(ts.getMxx()))//a
                                .append(",0") // b
                                .append(",0,") // c
                                .append(nb.toString(ts.getMyy())) // d
                                .append(',')
                                .append(nb.toString(ts.getTx())) // e
                                .append(',')
                                .append(nb.toString(ts.getTy())) // f
                                .append(')');
                    } else {
                        buf.append("scale(")
                                .append(nb.toString(ts.getX()));
                        if (ts.getTy() != ts.getTx()) {
                            buf.append(',')
                                    .append(nb.toString(ts.getY()));
                        }
                        buf.append(')');
                    }
                } else if (tx instanceof Rotate) {
                    Rotate tr = (Rotate) tx;
                    buf.append("rotate(")
                            .append(nb.toString(tr.getAngle()));
                    if (tr.getPivotX() != 0.0 || tr.getPivotY() != 0.0) {
                        buf.append(',')
                                .append(nb.toString(tr.getPivotX()))
                                .append(',')
                                .append(nb.toString(tr.getPivotY()));
                    }
                    buf.append(')');
                } else {
                    // [a c e]   [ mxx mxy tx ]
                    // [b d f] = [ myx myy ty ]
                    // [0 0 1]   [  0   0   1 ]
                    buf.append("matrix(")
                            .append(nb.toString(tx.getMxx()))//a
                            .append(',')
                            .append(nb.toString(tx.getMyx()))//b
                            .append(',')
                            .append(nb.toString(tx.getMxy()))//c
                            .append(',')
                            .append(nb.toString(tx.getMyy()))//d
                            .append(',')
                            .append(nb.toString(tx.getTx()))//e
                            .append(',')
                            .append(nb.toString(tx.getTy()))//f
                            .append(')');

                }
            }
        }
    }

    @Override
    public List<Transform> fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        List<Transform> txs = new ArrayList<>();
        CssTokenizer tt = new CssTokenizer(new StringReader(in.toString()));

        while (tt.nextToken() != CssTokenizer.TT_EOF) {
            tt.pushBack();
            tt.skipWhitespace();
            if (tt.nextToken() != CssTokenizer.TT_FUNCTION) {
                throw new ParseException("function expected: \"" + tt.currentStringValue() + "\"", tt.getPosition());
            }
            String func = tt.currentStringValue();
            tt.skipWhitespace();
            switch (func) {
                case "skewX": {
                    if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                        throw new ParseException("skew-angle expected: \"" + tt.currentStringValue() + "\"", tt.getPosition());
                    }
                    double a = tt.currentNumericValue().doubleValue();
                    txs.add(Transform.affine(1, 0, tan(a), 1, 0, 0));
                    break;
                }
                case "skewY": {
                    if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                        throw new ParseException("skew-angle expected: \"" + tt.currentStringValue() + "\"", tt.getPosition());
                    }
                    double a = tt.currentNumericValue().doubleValue();
                    txs.add(Transform.affine(1, tan(a), 0, 1, 0, 0));
                    break;
                }
                case "translate": {
                    if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                        throw new ParseException("tx expected: \"" + tt.currentStringValue() + "\"", tt.getPosition());
                    }
                    double tx = tt.currentNumericValue().doubleValue();
                    tt.skipWhitespace();
                    if (tt.nextToken() == ',') {
                        tt.skipWhitespace();
                    } else {
                        tt.pushBack();
                    }
                    double ty;
                    if (tt.nextToken() == CssTokenizer.TT_NUMBER) {
                        ty = tt.currentNumericValue().doubleValue();

                    } else {
                        ty = 0;
                        tt.pushBack();
                    }
                    txs.add(Transform.translate(tx, ty));
                    break;
                }
                case "scale": {
                    if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                        throw new ParseException("sx expected: \"" + tt.currentStringValue() + "\"", tt.getPosition());
                    }
                    double sx = tt.currentNumericValue().doubleValue();
                    tt.skipWhitespace();
                    if (tt.nextToken() == ',') {
                        tt.skipWhitespace();
                    } else {
                        tt.pushBack();
                    }
                    double sy;
                    if (tt.nextToken() == CssTokenizer.TT_NUMBER) {
                        sy = tt.currentNumericValue().doubleValue();

                    } else {
                        sy = 1;
                        tt.pushBack();
                    }
                    txs.add(Transform.scale(sx, sy));
                    break;
                }
                case "rotate": {
                    if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                        throw new ParseException("rotate-angle expected: \"" + tt.currentStringValue() + "\"", tt.getPosition());
                    }
                    double angle = tt.currentNumericValue().doubleValue();
                    tt.skipWhitespace();
                    if (tt.nextToken() == ',') {
                        tt.skipWhitespace();
                    } else {
                        tt.pushBack();
                    }
                    double cx;
                    double cy;
                    if (tt.nextToken() == CssTokenizer.TT_NUMBER) {
                        cx = tt.currentNumericValue().doubleValue();
                        tt.skipWhitespace();
                        if (tt.nextToken() == ',') {
                            tt.skipWhitespace();
                        } else {
                            tt.pushBack();
                        }
                        if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                            throw new ParseException("cy expected: \"" + tt.currentStringValue() + "\"", tt.getPosition());
                        }
                        cy = tt.currentNumericValue().doubleValue();
                    } else {
                        cx = 0;
                        cy = 0;
                        tt.pushBack();
                    }
                    txs.add(Transform.rotate(angle, cx, cy));
                    break;
                }
                case "matrix": {
                    double[] m = new double[6];
                    for (int i = 0; i < m.length; i++) {
                        tt.skipWhitespace();
                        if (tt.nextToken() == ',') {
                            tt.skipWhitespace();
                        } else {
                            tt.pushBack();
                        }
                        if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                            throw new ParseException(((char) ('a' + i)) + " expected: \"" + tt.currentStringValue() + "\"", tt.getPosition());
                        }
                        m[i] = tt.currentNumericValue().doubleValue();
                    }
                    txs.add(Transform.affine(m[0], m[1], m[2], m[3], m[4], m[5]));
                    break;
                }
                default:
                    throw new ParseException("unsupported function: \"" + tt.currentStringValue() + "\"", tt.getPosition());
            }
            tt.skipWhitespace();
            if (tt.nextToken() != ')') {
                throw new ParseException("')' expected: \"" + tt.currentStringValue() + "\"", tt.getPosition());
            }
        }
        
        in.position(in.limit());
        return txs;
    }

    @Override
    public List<Transform> getDefaultValue() {
        return Collections.emptyList();
    }
}

/*
 * @(#)SvgTransformConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.text;

import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.text.AbstractCssConverter;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Math.tan;

/**
 * CssTransformConverter.
 * <p>
 * Parses a transform given in the following EBNF:
 * <pre>
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
 */
public class SvgTransformConverter extends AbstractCssConverter<Transform> {

    public SvgTransformConverter(boolean nullable) {
        super(nullable);
    }

    @Override
    protected <TT extends Transform> void produceTokensNonNull(@NonNull TT tx, @Nullable IdFactory idFactory, @NonNull Consumer<CssToken> out) {
        if (tx instanceof Translate) {
            Translate tr = (Translate) tx;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, "translate"));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, tr.getTx()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, tr.getTy()));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
/*        } else if (tx instanceof Scale) {
            Scale ts = (Scale) tx;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, "scale"));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, ts.getX()));
            if (ts.getY() != ts.getX() || ts.getZ() != 1 || ts.getPivotX() != 0 || ts.getPivotY() != 0) {
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, ts.getY()));
            }
            if (ts.getPivotX() != 0 || ts.getPivotY() != 0) {
                System.err.println("SvgTransformConverter pivot not implemented yet");
            }
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
*/
        } else if (tx instanceof Rotate) {
            Rotate tr = (Rotate) tx;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, "rotate"));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, tr.getAngle()));
            if (tr.getPivotX() != 0.0 || tr.getPivotY() != 0.0) {
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, tr.getPivotX()));
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, tr.getPivotY()));
            }
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
        } else if (tx.isType2D()) {
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, "matrix"));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, tx.getMxx()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, tx.getMyx()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, tx.getMxy()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, tx.getMyy()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, tx.getTx()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, tx.getTy()));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
        } else {
            throw new UnsupportedOperationException("Unsupported transformation " + tx);
        }
    }

    @NonNull
    @Override
    public String getHelpText() {
        return "Format of ⟨Transform⟩: ⟨Translate⟩｜⟨Scale⟩｜⟨Rotate⟩｜⟨SkewX⟩｜⟨SkewY⟩｜⟨Matrix⟩"
                + "\nFormat of ⟨Translate⟩: translate(⟨tx⟩,⟨ty⟩)"
                + "\nFormat of ⟨Scale⟩: scale(⟨sx⟩,⟨sy⟩)"
                + "\nFormat of ⟨Rotate⟩: rotate(⟨angle⟩［,⟨pivotx⟩,⟨pivoty⟩］)"
                + "\nFormat of ⟨SkewX⟩: skewX(⟨skew-angle⟩)"
                + "\nFormat of ⟨SkewY⟩: skewY(⟨skew-angle⟩)"
                + "\nFormat of ⟨Matrix⟩: matrix(⟨xx⟩,⟨yx⟩, ⟨xy⟩,⟨yy⟩, ⟨tx⟩,⟨ty⟩)"
                ;
    }

    @NonNull
    @Override
    public Transform parseNonNull(@NonNull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "⟨Transform⟩: function expected");
        String func = tt.currentStringNonNull();
        int funcPos = tt.getStartPosition();
        List<Double> m = new ArrayList<>();
        while (tt.next() != ')' && tt.current() != CssTokenType.TT_EOF) {
            if (tt.current() != ',') {
                tt.pushBack();
            }
            if (tt.next() != CssTokenType.TT_NUMBER) {
                throw new ParseException("coefficient nb " + m.size() + " expected: \"" + tt.currentString() + "\"", tt.getStartPosition());
            }
            m.add(tt.currentNumberNonNull().doubleValue());
        }
        if (tt.current() != ')') {
            throw new ParseException("')' expected: \"" + tt.currentString() + "\"", tt.getStartPosition());
        }
        switch (func) {
            case "matrix": {
                switch (m.size()) {
                    case 0:
                        return new Affine(//
                                1, 0, 0,//
                                0, 1, 0//
                        );
                    case 6:
                        return new Affine(//
                                m.get(0), m.get(2), m.get(4),//
                                m.get(1), m.get(3), m.get(5)//
                        );
                    default:
                        throw new ParseException("6 or 12 coefficients expected, but found " + m.size(), tt.getStartPosition());
                }
            }
            case "skewX": {
                switch (m.size()) {
                    case 0:
                        return Transform.translate(//
                                0, 0//
                        );//
                    case 1:
                        double a = m.get(0);
                        return Transform.affine(1, 0, tan(a), 1, 0, 0);

                    default:
                        throw new ParseException("1 coefficient expected, but found " + m.size(), tt.getStartPosition());
                }
            }
            case "skewY": {
                switch (m.size()) {
                    case 0:
                        return Transform.translate(//
                                0, 0//
                        );//
                    case 1:
                        double a = m.get(0);
                        return Transform.affine(1, tan(a), 0, 1, 0, 0);

                    default:
                        throw new ParseException("1 coefficient expected, but found " + m.size(), tt.getStartPosition());
                }
            }
            case "translate": {
                switch (m.size()) {
                    case 0:
                        return Transform.translate(//
                                0, 0//
                        );//
                    case 1:
                        return Transform.translate(
                                m.get(0), 0//
                        );
                    case 2:
                        return Transform.translate(
                                m.get(0), m.get(1)//
                        );
                    default:
                        throw new ParseException("1, 2 or 3 coefficients expected, but found " + m.size(), tt.getStartPosition());
                }
            }
            case "scale": {
                switch (m.size()) {
                    case 0:
                        return Transform.scale(//
                                1, 1//
                        );
                    case 1:
                        return Transform.scale(//
                                m.get(0), m.get(0)//
                        );
                    case 2:
                        return Transform.scale(
                                m.get(0), m.get(1)//
                        );
                    default:
                        throw new ParseException("1, 2, or 3 coefficients expected, but found " + m.size(), tt.getStartPosition());
                }
            }
            case "rotate": {
                switch (m.size()) {
                    case 0:
                        return Transform.rotate(//
                                0,//
                                0, 0//
                        );
                    case 1:
                        return Transform.rotate(//
                                m.get(0),//
                                0, 0//
                        );
                    case 3:
                        return Transform.rotate(//
                                m.get(0),//
                                m.get(1), m.get(2)//
                        );
                    default:
                        throw new ParseException("1 or 3 coefficients expected, but found " + m.size(), tt.getStartPosition());
                }
            }
            default:
                throw new ParseException("unsupported function: \"" + func + "\"", funcPos);
        }

    }

}

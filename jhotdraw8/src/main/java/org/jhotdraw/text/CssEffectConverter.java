/* @(#)CssEffectConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import org.jhotdraw.css.CssTokenizer;
import org.jhotdraw.draw.io.IdFactory;

/**
 * CssEffectConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Effect := (DropShadow|InnerShadow) ;
 * DropShadow := "dropshadow(" , [ blurType , "," , color , "," ,
 *                radius , "," ,  spread , "," ,  xOffset , "," ,  yOffset ] , ")";
 * InnerShadow := "innershadow(" , [ , blurType , "," , color , "," ,
 *                radius , ",", choke , "," ,  xOffset , "," ,  yOffset ] , ")";
 *  ...TODO...
 * </pre>
 * <p>
 *
 * @author Werner Randelshofer
 */
public class CssEffectConverter implements Converter<Effect> {

    CssPaintConverter colorConverter = new CssPaintConverter();
    CssSizeConverter sizeConverter = new CssSizeConverter();

    @Override
    public void toString(Appendable out, IdFactory idFactory, Effect value) throws IOException {
        if (value instanceof DropShadow) {
            DropShadow fx = (DropShadow) value;
            out.append("dropshadow(");
            out.append(fx.getBlurType().toString().toLowerCase().replace('_', '-'));
            out.append(',');
            colorConverter.toString(out, idFactory, fx.getColor());
            out.append(',');
            sizeConverter.toString(out, idFactory, fx.getRadius());
            out.append(',');
            sizeConverter.toString(out, idFactory, fx.getSpread());
            out.append(',');
            sizeConverter.toString(out, idFactory, fx.getOffsetX());
            out.append(',');
            sizeConverter.toString(out, idFactory, fx.getOffsetY());
            out.append(')');
        } else if (value instanceof InnerShadow) {
            InnerShadow fx = (InnerShadow) value;
            out.append("innershadow(");
            out.append(fx.getBlurType().toString().toLowerCase().replace('_', '-'));
            out.append(',');
            colorConverter.toString(out, idFactory, fx.getColor());
            out.append(',');
            sizeConverter.toString(out, idFactory, fx.getRadius());
            out.append(',');
            sizeConverter.toString(out, idFactory, fx.getChoke());
            out.append(',');
            sizeConverter.toString(out, idFactory, fx.getOffsetX());
            out.append(',');
            sizeConverter.toString(out, idFactory, fx.getOffsetY());
            out.append(')');
        } else {
            out.append("none");
        }
    }

    @Override
    public Effect fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new StringReader(in.toString()));
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if ("none".equals(tt.currentStringValue())) {
                in.position(in.limit());
                return null;
            } else {
                throw new ParseException("CSS Effect: \"<none>\" or \"<dropshadow>(\" or \"<innershadow>(\"  expected", tt.getPosition());
            }
        }
        if (tt.currentToken() != CssTokenizer.TT_FUNCTION) {
            throw new ParseException("CSS Effect: \"<dropshadow>(\" or \"<innershadow>(\"  expected", tt.getPosition());
        }

        boolean isDropShadow = false;
        String func;
        switch (tt.currentStringValue()) {
            case "dropshadow":
                isDropShadow = true;
                break;
            case "innershadow":
                isDropShadow = false;
                break;
            default:
                throw new ParseException("CSS Effect: \"<dropshadow>(\" or \"<innershadow>(\"  expected", tt.getPosition());
        }
        func = tt.currentStringValue();
        BlurType blurType = BlurType.GAUSSIAN;
        Color color = new Color(0, 0, 0, 0.75);
        double radius = 10.0;
        double spreadOrChocke = 0.0;
        double offsetX = 0.0;
        double offsetY = 4.0;

        tt.skipWhitespace();
        if (tt.nextToken() != ')') {
            if (tt.currentToken() != CssTokenizer.TT_IDENT) {
                throw new ParseException("CSS Effect: " + func + "(<blur-type>,color,radius,spread,offset-x,offset-y) expected", tt.getPosition());
            }
            switch (tt.currentStringValue()) {
                case "gaussian":
                    blurType = BlurType.GAUSSIAN;
                    break;
                case "one-pass-box":
                    blurType = BlurType.ONE_PASS_BOX;
                    break;
                case "three-pass-box":
                    blurType = BlurType.THREE_PASS_BOX;
                    break;
                case "two-pass-box":
                    blurType = BlurType.TWO_PASS_BOX;
                    break;
                default:
                    throw new ParseException("CSS Effect: " + func + "(<gaussian | one-pass-box | three-pass-box | two-pass-box>  expected", tt.getPosition());
            }
            tt.skipWhitespace();
            if (tt.nextToken() != ',') {
                throw new ParseException("CSS Effect: " + func + "(blur-type<,>color,radius,spread,offset-x,offset-y) expected", tt.getPosition());
            }
            tt.skipWhitespace();
            if (tt.nextToken() == CssTokenizer.TT_HASH) {
                color = Color.web('#' + tt.currentStringValue());
            } else if (tt.currentToken() == CssTokenizer.TT_IDENT) {
                color = Color.web(tt.currentStringValue());
            } else if (tt.currentToken() == CssTokenizer.TT_FUNCTION) {
                StringBuilder buf = new StringBuilder();
                buf.append(tt.currentStringValue());
                buf.append('(');
                while (tt.nextToken() != CssTokenizer.TT_EOF) {
                    if (tt.currentToken() == ')') {
                        buf.append(')');
                        break;
                    }
                    buf.append(tt.currentStringValue());
                }
                color = Color.web(buf.toString());
            } else {
                throw new ParseException("CSS Effect: " + func + "(" + blurType.toString().toLowerCase().replace('_', '-') + ",  <color> expected", tt.getPosition());
            }
            tt.skipWhitespace();
            if (tt.nextToken() != ',') {
                throw new ParseException("CSS Effect: " + func + "(blur-type,color<,>radius,spread,offset-x,offset-y) expected", tt.getPosition());
            }
            tt.skipWhitespace();
            if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                throw new ParseException("CSS Effect: radius number expected", tt.getPosition());
            }
            radius = tt.currentNumericValue().doubleValue();

            tt.skipWhitespace();
            if (tt.nextToken() != ',') {
                throw new ParseException("CSS Effect: " + func + "(blur-type,color,radius<,>spread,offset-x,offset-y) expected", tt.getPosition());
            }
            tt.skipWhitespace();
            if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                throw new ParseException("CSS Effect: spread or chocke number expected", tt.getPosition());
            }
            spreadOrChocke = tt.currentNumericValue().doubleValue();
            tt.skipWhitespace();
            if (tt.nextToken() != ',') {
                throw new ParseException("CSS Effect: " + func + "(blur-type,color,radius,spread<,>offset-x,offset-y) expected", tt.getPosition());
            }
            tt.skipWhitespace();
            if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                throw new ParseException("CSS Effect: offset-x number expected", tt.getPosition());
            }
            offsetX = tt.currentNumericValue().doubleValue();
            tt.skipWhitespace();
            if (tt.nextToken() != ',') {
                throw new ParseException("CSS Effect: " + func + "(blur-type,color,radius,spread,offset-x<,>offset-y) expected", tt.getPosition());
            }
            tt.skipWhitespace();
            if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                throw new ParseException("CSS Effect: offset-y number expected", tt.getPosition());
            }
            offsetY = tt.currentNumericValue().doubleValue();
            tt.skipWhitespace();
            if (tt.nextToken() != ')') {
                throw new ParseException("CSS Effect: ')'  expected", tt.getPosition());
            }
        }
        in.position(in.limit());

        if (isDropShadow) {
            return new DropShadow(blurType, color, radius, spreadOrChocke, offsetX, offsetY);
        } else {
            return new InnerShadow(blurType, color, radius, spreadOrChocke, offsetX, offsetY);
        }
    }

    @Override
    public Effect getDefaultValue() {
        return null;
    }

}

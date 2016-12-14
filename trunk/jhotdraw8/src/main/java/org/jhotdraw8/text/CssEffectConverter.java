/* @(#)CssEffectConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.draw.io.IdFactory;

/**
 * CssEffectConverter.
 * <p>
 * Parses the following EBNF:
 * </p>
 * <pre>
 * Effect = "none" | ( DropShadow | InnerShadow ) , { Effect };
 * DropShadow = "drop-shadow(" , [
 *                 blurType , Sep , color , Sep ,
 *                 radius , Sep ,  spread , Sep ,  xOffset , Sep ,  yOffset
 *              ] , ")";
 * InnerShadow = "inner-shadow(" , [
 *                 blurType , Sep , color , Sep ,
 *                 radius , Sep, choke , Sep ,  xOffset , Sep ,  yOffset
 *               ] , ")";
 *
 * Sep         = ( S , { S } | { S } , "," , { S } ) ;
 * S           = (* white space character *)
 * </pre>
 * <p>
 * References:
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 *
 * @author Werner Randelshofer
 */
public class CssEffectConverter implements Converter<Effect> {
    private static final String DROP_SHADOW = "drop-shadow";
    private static final String INNER_SHADOW = "inner-shadow";

    private CssColorConverter colorConverter = new CssColorConverter();
    private CssSizeConverter nb = new CssSizeConverter();

    @Override
    public void toString(Appendable out, IdFactory idFactory, Effect value) throws IOException {
        if (value instanceof DropShadow) {
            DropShadow fx = (DropShadow) value;
            out.append(DROP_SHADOW).append('(');
            out.append(fx.getBlurType().toString().toLowerCase().replace('_', '-'));
            out.append(',');
            colorConverter.toString(out, idFactory, new CssColor(fx.getColor()));
            out.append(',');
            nb.toString(out, idFactory, fx.getRadius());
            out.append(',');
            nb.toString(out, idFactory, fx.getSpread()*100.0);
            out.append("%,");
            nb.toString(out, idFactory, fx.getOffsetX());
            out.append(',');
            nb.toString(out, idFactory, fx.getOffsetY());
            out.append(')');
            if (fx.getInput() != null) {
                out.append(' ');
                toString(out, idFactory, fx.getInput());
            }
        } else if (value instanceof InnerShadow) {
            InnerShadow fx = (InnerShadow) value;
            out.append(INNER_SHADOW).append('(');
            out.append(fx.getBlurType().toString().toLowerCase().replace('_', '-'));
            out.append(',');
            colorConverter.toString(out, idFactory, new CssColor(fx.getColor()));
            out.append(',');
            nb.toString(out, idFactory, fx.getRadius());
            out.append(',');
            nb.toString(out, idFactory, fx.getChoke()*100.0);
            out.append("%,");
            out.append(',');
            nb.toString(out, idFactory, fx.getOffsetX());
            out.append(',');
            nb.toString(out, idFactory, fx.getOffsetY());
            out.append(')');
            if (fx.getInput() != null) {
                out.append(' ');
                toString(out, idFactory, fx.getInput());
            }
        } else {
            out.append("none");
        }
    }

    @Override
    public Effect fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizerInterface tt = new CssTokenizer(new StringReader(in.toString()));
        tt.setSkipWhitespaces(true);
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if ("none".equals(tt.currentStringValue())) {
                tt.skipWhitespace();
                in.position(tt.getStartPosition());
                return null;
            } else {
                throw new ParseException("CSS Effect: \"<none>\" or \"<dropshadow>(\" or \"<innershadow>(\"  expected", tt.getStartPosition());
            }
        }
        tt.pushBack();
        Effect effect = parseEffect(tt);
        tt.skipWhitespace();
        in.position(tt.getStartPosition());
        return effect;
    }

    private Effect parseEffect(CssTokenizerInterface tt) throws ParseException, IOException {
        Effect first = null;
        Effect previous = null;
        while (tt.nextToken() == CssTokenizer.TT_FUNCTION) {

            Effect current = null;
            switch (tt.currentStringValue()) {
                case DROP_SHADOW:
                    current = parseDropShadow(tt);
                    break;
                case INNER_SHADOW:
                    current = parseInnerShadow(tt);
                    break;
                default:
                    throw new ParseException("CSS Effect: \""+DROP_SHADOW+"(\" or \""+INNER_SHADOW+"(\"  expected", tt.getStartPosition());
            }
            if (first == null) {
                first = previous = current;
            } else {
                try {
                    previous.getClass().getDeclaredMethod("setInput", Effect.class).invoke(previous, current);
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    ParseException pe = new ParseException("CSS Effect: can not combine effects", tt.getStartPosition());
                    pe.initCause(ex);
                    throw pe;
                }
                previous = current;
            }
        }
        return first;
    }

    private Effect parseDropShadow(CssTokenizerInterface tt) throws ParseException, IOException {
        return parseDropShadowOrInnerShadow(tt, true);
    }

    private Effect parseInnerShadow(CssTokenizerInterface tt) throws ParseException, IOException {
        return parseDropShadowOrInnerShadow(tt, false);
    }

    private Effect parseDropShadowOrInnerShadow(CssTokenizerInterface tt, boolean isDropShadow) throws ParseException, IOException {
        String func = isDropShadow ?DROP_SHADOW : INNER_SHADOW;
        BlurType blurType = BlurType.GAUSSIAN;
        Color color = new Color(0, 0, 0, 0.75);
        double radius = 10.0;
        double spreadOrChocke = 0.0;
        double offsetX = 0.0;
        double offsetY = 4.0;

        if (tt.nextToken() != ')') {
            if (tt.currentToken() != CssTokenizer.TT_IDENT) {
                throw new ParseException("CSS Effect: " + func + "(<blur-type>,color,radius,spread,offset-x,offset-y) expected", tt.getStartPosition());
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
                    throw new ParseException("CSS Effect: " + func + "(<gaussian | one-pass-box | three-pass-box | two-pass-box>  expected", tt.getStartPosition());
            }

            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
            if (tt.nextToken() == CssTokenizer.TT_HASH) {
                color = Color.web('#' + tt.currentStringValue());
            } else if (tt.currentToken() == CssTokenizer.TT_IDENT) {
                color = Color.web(tt.currentStringValue());
            } else if (tt.currentToken() == CssTokenizer.TT_FUNCTION) {
                tt.pushBack();
                CssColor colorOrNull =colorConverter.parseColor(tt);
                color = (Color) colorOrNull.getColor();
            } else {
                throw new ParseException("CSS Effect: " + func + "(" + blurType.toString().toLowerCase().replace('_', '-') + ",  <color> expected", tt.getStartPosition());
            }
            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
            if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                throw new ParseException("CSS Effect: radius number expected", tt.getStartPosition());
            }
            radius = tt.currentNumericValue().doubleValue();

            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
            switch (tt.nextToken()) {
                case CssTokenizer.TT_NUMBER:
            spreadOrChocke = tt.currentNumericValue().doubleValue();
                    break;
                case CssTokenizer.TT_PERCENTAGE:
            spreadOrChocke = tt.currentNumericValue().doubleValue()/100.0;
                    break;
                default:
                throw new ParseException("CSS Effect: spread or chocke number expected", tt.getStartPosition());
            }
            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
            if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                throw new ParseException("CSS Effect: offset-x number expected", tt.getStartPosition());
            }
            offsetX = tt.currentNumericValue().doubleValue();
            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
            if (tt.nextToken() != CssTokenizer.TT_NUMBER) {
                throw new ParseException("CSS Effect: offset-y number expected", tt.getStartPosition());
            }
            offsetY = tt.currentNumericValue().doubleValue();
            if (tt.nextToken() != ')') {
                throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
            }
        }

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

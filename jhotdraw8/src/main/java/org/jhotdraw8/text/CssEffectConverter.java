/* @(#)CssEffectConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.draw.key.CssColor;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.IdFactory;

/**
 * CssEffectConverter.
 * <p>
 * Parses the following EBNF:
 * </p>
 * <pre>
 * Effect = "none" | ( Blend | Bloom | BoxBlur | ColorAdjust | DropShadow | GaussianBlur | Glow | InnerShadow | Shadow ) , { Effect };
 * Blend = "blend(" , [
 *                 blendType
 *              ] , ")";
 * Bloom = "bloom(" , [
 *                 threshold
 *              ] , ")";
 * BoxBlur = "box-blur(" , [
 *                 width, Sep, height, Sep, iterations
 *              ] , ")";
 * ColorAdjust = "color-adjust(" , [
 *                 "hue", S, hue, Sep,
 *                 "saturation" , S , saturation, Sep,
 *                 "brightness", S, brightness, Sep,
 *                 "contrast", S, contrast
 *              ] , ")";
 * DropShadow = "drop-shadow(" , [
 *                 blurType , Sep , color , Sep ,
 *                 radius , Sep ,  spread , Sep ,  xOffset , Sep ,  yOffset
 *              ] , ")";
 * GaussianBlur = "gaussian-blur(" , [
 *                 radius
 *              ] , ")";
 * Glow = "glow(" , [
 *                 level
 *              ] , ")";
 * InnerShadow = "inner-shadow(" , [
 *                 blurType , Sep , color , Sep ,
 *                 radius , Sep, choke , Sep ,  xOffset , Sep ,  yOffset
 *               ] , ")";
 * Shadow = "shadow(" , [
 *                 blurType , Sep , color , Sep ,
 *                 radius
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

    private static final String BLEND = "blend";
    private static final String BLOOM = "bloom";
    private static final String BOX_BLUR = "box-blur";
    private static final String COLOR_ADJUST = "color-adjust";
    private static final String DROP_SHADOW = "drop-shadow";
    private static final String GAUSSIAN_BLUR = "gaussian-blur";
    private static final String GLOW = "glow";
    private static final String INNER_SHADOW = "inner-shadow";
    private static final String SHADOW = "shadow";

    private CssEnumConverter<BlurType> blurTypeConverter = new CssEnumConverter<>(BlurType.class,false);
    private CssEnumConverter<BlendMode> blendModeConverter = new CssEnumConverter<>(BlendMode.class,false);
    private CssColorConverter colorConverter = new CssColorConverter(false);
    private CssDoubleConverter nb = new CssDoubleConverter();

    @Override
    public void toString(Appendable out, IdFactory idFactory, Effect value) throws IOException {
        if (value instanceof Blend) {
            Blend fx = (Blend) value;
            out.append(BLEND).append('(');
            out.append(blendModeConverter.toString(fx.getMode()));
            out.append(')');
            //FIXME
            /* if (fx.getInput() != null) {
                out.append(", ");
                toString(out, idFactory, fx.getInput());
            }*/
        } else if (value instanceof Bloom) {
            Bloom fx = (Bloom) value;
            out.append(BLOOM).append('(');
            out.append(nb.toString(fx.getThreshold() * 100));
            out.append("%)");
            if (fx.getInput() != null) {
                out.append(", ");
                toString(out, idFactory, fx.getInput());
            }
        } else if (value instanceof BoxBlur) {
            BoxBlur fx = (BoxBlur) value;
            out.append(BOX_BLUR).append('(');
            out.append(nb.toString(fx.getWidth()));
            out.append(',');
            out.append(nb.toString(fx.getHeight()));
            out.append(',');
            out.append(Integer.toString(fx.getIterations()));
            out.append(")");
            if (fx.getInput() != null) {
                out.append(", ");
                toString(out, idFactory, fx.getInput());
            }
        } else if (value instanceof ColorAdjust) {
            ColorAdjust fx = (ColorAdjust) value;
            out.append(COLOR_ADJUST).append('(');
            boolean needComma = false;
            final double hue = fx.getHue();
            final double saturation = fx.getSaturation();
            final double brightness = fx.getBrightness();
            final double contrast = fx.getContrast();
            boolean all = hue == 0 && saturation == 0 && brightness == 0 && contrast == 0;
            if (hue != 0 || all) {
                out.append("hue ");
                if (hue > 0) {
                    out.append('+');
                }
                out.append(nb.toString(hue * 100));
                out.append("%");
                needComma = true;
            }
            if (saturation != 0 || all) {
                if (needComma) {
                    out.append(", ");
                }
                out.append("saturation ");
                if (saturation > 0) {
                    out.append('+');
                }
                out.append(nb.toString(saturation * 100));
                out.append("%");
                needComma = true;
            }
            if (brightness != 0 || all) {
                if (needComma) {
                    out.append(", ");
                }
                out.append("brightness ");
                if (brightness > 0) {
                    out.append('+');
                }
                out.append(nb.toString(brightness * 100));
                out.append("%");
                needComma = true;
            }
            if (contrast != 0 || all) {
                if (needComma) {
                    out.append(", ");
                }
                out.append("contrast ");
                if (contrast > 0) {
                    out.append('+');
                }
                out.append(nb.toString(contrast * 100));
                out.append('%');
            }
            out.append(")");
            if (fx.getInput() != null) {
                out.append(", ");
                toString(out, idFactory, fx.getInput());
            }
        } else if (value instanceof DropShadow) {
            DropShadow fx = (DropShadow) value;
            out.append(DROP_SHADOW).append('(');
            out.append(blurTypeConverter.toString(fx.getBlurType()));
            out.append(',');
            colorConverter.toString(out, idFactory, new CssColor(fx.getColor()));
            out.append(',');
            nb.toString(out, idFactory, fx.getRadius());
            out.append(',');
            nb.toString(out, idFactory, fx.getSpread() * 100.0);
            out.append("%,");
            nb.toString(out, idFactory, fx.getOffsetX());
            out.append(',');
            nb.toString(out, idFactory, fx.getOffsetY());
            if (fx.getInput() != null) {
                out.append(", ");
                toString(out, idFactory, fx.getInput());
            }
            out.append(")");
        } else if (value instanceof GaussianBlur) {
            GaussianBlur fx = (GaussianBlur) value;
            out.append(GAUSSIAN_BLUR).append('(');
            out.append(nb.toString(fx.getRadius()));
            out.append(")");
            if (fx.getInput() != null) {
                out.append(", ");
                toString(out, idFactory, fx.getInput());
            }
        } else if (value instanceof Glow) {
            Glow fx = (Glow) value;
            out.append(GLOW).append('(');
            out.append(nb.toString(fx.getLevel() * 100));
            out.append("%)");
            if (fx.getInput() != null) {
                out.append(", ");
                toString(out, idFactory, fx.getInput());
            }
        } else if (value instanceof InnerShadow) {
            InnerShadow fx = (InnerShadow) value;
            out.append(INNER_SHADOW).append('(');
            out.append(blurTypeConverter.toString(fx.getBlurType()));
            out.append(',');
            colorConverter.toString(out, idFactory, new CssColor(fx.getColor()));
            out.append(',');
            nb.toString(out, idFactory, fx.getRadius());
            out.append(',');
            nb.toString(out, idFactory, fx.getChoke() * 100.0);
            out.append("%,");
            nb.toString(out, idFactory, fx.getOffsetX());
            out.append(',');
            nb.toString(out, idFactory, fx.getOffsetY());
            out.append(')');
            if (fx.getInput() != null) {
                out.append(", ");
                toString(out, idFactory, fx.getInput());
            }
        } else if (value instanceof Shadow) {
            Shadow fx = (Shadow) value;
            out.append(SHADOW).append('(');
            out.append(blurTypeConverter.toString(fx.getBlurType()));
            out.append(',');
            colorConverter.toString(out, idFactory, new CssColor(fx.getColor()));
            out.append(',');
            nb.toString(out, idFactory, fx.getRadius());
            out.append(')');
            if (fx.getInput() != null) {
                out.append(", ");
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
                case BLEND:
                    current = parseBlend(tt);
                    break;
                case BLOOM:
                    current = parseBloom(tt);
                    break;
                case BOX_BLUR:
                    current = parseBoxBlur(tt);
                    break;
                case COLOR_ADJUST:
                    current = parseColorAdjust(tt);
                    break;
                case DROP_SHADOW:
                    current = parseDropShadow(tt);
                    break;
                case GAUSSIAN_BLUR:
                    current = parseGaussianBlur(tt);
                    break;
                case GLOW:
                    current = parseGlow(tt);
                    break;
                case INNER_SHADOW:
                    current = parseInnerShadow(tt);
                    break;
                case SHADOW:
                    current = parseShadow(tt);
                    break;
                default:
                    throw new ParseException("CSS Effect: \"" + BLEND + ", " + DROP_SHADOW + "(\" or \"" + INNER_SHADOW + "(\"  expected", tt.getStartPosition());
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

            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
        }
        return first;
    }

    private Effect parseBlend(CssTokenizerInterface tt) throws ParseException, IOException {
        BlendMode mode = BlendMode.SRC_OVER;
        if (tt.nextToken() == CssTokenizerInterface.TT_IDENT) {
            tt.pushBack();
            mode = blendModeConverter.parse(tt);
        }
        if (tt.nextToken() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new Blend(mode);
    }

    private Effect parseBloom(CssTokenizerInterface tt) throws ParseException, IOException {
        double threshold = 0.3;
        switch (tt.nextToken()) {
            case CssTokenizer.TT_NUMBER:
                threshold = tt.currentNumericValue().doubleValue();
                break;
            case CssTokenizer.TT_PERCENTAGE:
                threshold = tt.currentNumericValue().doubleValue() / 100;
                break;
            default:
                tt.pushBack();
        }
        if (tt.nextToken() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new Bloom(Geom.clamp(threshold, 0, 1));
    }

    private Effect parseBoxBlur(CssTokenizerInterface tt) throws ParseException, IOException {
        double width = 5;
        double height = 5;
        int iterations = 1;
        switch (tt.nextToken()) {
            case CssTokenizer.TT_NUMBER:
                width = Geom.clamp(tt.currentNumericValue().doubleValue(), 0, 255);
                break;
            default:
                tt.pushBack();
        }
        if (tt.nextToken() != ',') {
            tt.pushBack();
        }
        switch (tt.nextToken()) {
            case CssTokenizer.TT_NUMBER:
                height = Geom.clamp(tt.currentNumericValue().doubleValue(), 0, 255);
                break;
            default:
                tt.pushBack();
        }
        if (tt.nextToken() != ',') {
            tt.pushBack();
        }
        switch (tt.nextToken()) {
            case CssTokenizer.TT_NUMBER:
                iterations = Geom.clamp(tt.currentNumericValue().intValue(), 0, 3);
                break;
            default:
                tt.pushBack();
        }
        if (tt.nextToken() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new BoxBlur(width, height, iterations);
    }

    private Effect parseColorAdjust(CssTokenizerInterface tt) throws ParseException, IOException {
        double hue = 0.0;
        double saturation = 0.0;
        double brightness = 0.0;
        double contrast = 0.0;
        while (tt.nextToken() == CssTokenizer.TT_IDENT) {
            String ident = tt.currentStringValue();
            int identPos = tt.getStartPosition();
            double adjust = 0.0;
            switch (tt.nextToken()) {
                case CssTokenizer.TT_NUMBER:
                    adjust = tt.currentNumericValue().doubleValue();
                    break;
                case CssTokenizer.TT_PERCENTAGE:
                    adjust = tt.currentNumericValue().doubleValue() / 100;
                    break;
                default:
                    tt.pushBack();
            }
            adjust = Geom.clamp(adjust, 0, 1);
            switch (ident) {
                case "hue":
                    hue = adjust;
                    break;
                case "saturation":
                    saturation = adjust;
                    break;
                case "brightness":
                    brightness = adjust;
                    break;
                case "contrast":
                    contrast = adjust;
                    break;
                default:
                    throw new ParseException("CSS illegal identifier: " + ident, identPos);
            }
            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
        }
        if (tt.currentToken() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new ColorAdjust(hue, saturation, brightness, contrast);
    }

    private Effect parseDropShadow(CssTokenizerInterface tt) throws ParseException, IOException {
        return parseDropShadowOrInnerShadow(tt, true);
    }

    private Effect parseGaussianBlur(CssTokenizerInterface tt) throws ParseException, IOException {
        double radius = 5;
        switch (tt.nextToken()) {
            case CssTokenizer.TT_NUMBER:
                radius = Geom.clamp(tt.currentNumericValue().doubleValue(), 0, 63);
                break;
            default:
                tt.pushBack();
        }
        if (tt.nextToken() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new GaussianBlur(radius);
    }

    private Effect parseInnerShadow(CssTokenizerInterface tt) throws ParseException, IOException {
        return parseDropShadowOrInnerShadow(tt, false);
    }

    private Effect parseGlow(CssTokenizerInterface tt) throws ParseException, IOException {
        double level = 0.3;
        switch (tt.nextToken()) {
            case CssTokenizer.TT_NUMBER:
                level = tt.currentNumericValue().doubleValue();
                break;
            case CssTokenizer.TT_PERCENTAGE:
                level = tt.currentNumericValue().doubleValue() / 100;
                break;
            default:
                tt.pushBack();
        }
        if (tt.nextToken() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new Glow(Geom.clamp(level, 0, 1));
    }

    private Effect parseDropShadowOrInnerShadow(CssTokenizerInterface tt, boolean isDropShadow) throws ParseException, IOException {
        String func = isDropShadow ? DROP_SHADOW : INNER_SHADOW;
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
            tt.pushBack();
            blurType = blurTypeConverter.parse(tt);

            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
            if (tt.nextToken() == CssTokenizer.TT_HASH) {
                color = Color.web('#' + tt.currentStringValue());
            } else if (tt.currentToken() == CssTokenizer.TT_IDENT) {
                color = Color.web(tt.currentStringValue());
            } else if (tt.currentToken() == CssTokenizer.TT_FUNCTION) {
                tt.pushBack();
                CssColor colorOrNull = colorConverter.parseColor(tt);
                color = colorOrNull.getColor();
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
                    spreadOrChocke = tt.currentNumericValue().doubleValue() / 100.0;
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
            return new DropShadow(blurType, color, Geom.clamp(radius, 0, 127), spreadOrChocke, offsetX, offsetY);
        } else {
            return new InnerShadow(blurType, color, Geom.clamp(radius, 0, 127), spreadOrChocke, offsetX, offsetY);
        }
    }

    private Effect parseShadow(CssTokenizerInterface tt) throws ParseException, IOException {
        String func = SHADOW;
        BlurType blurType = BlurType.GAUSSIAN;
        Color color = new Color(0, 0, 0, 0.75);
        double radius = 10.0;

        if (tt.nextToken() != ')') {
            if (tt.currentToken() != CssTokenizer.TT_IDENT) {
                throw new ParseException("CSS Effect: " + func + "(<blur-type>,color,radius,spread,offset-x,offset-y) expected", tt.getStartPosition());
            }
            tt.pushBack();
            blurType = blurTypeConverter.parse(tt);

            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
            if (tt.nextToken() == CssTokenizer.TT_HASH) {
                color = Color.web('#' + tt.currentStringValue());
            } else if (tt.currentToken() == CssTokenizer.TT_IDENT) {
                color = Color.web(tt.currentStringValue());
            } else if (tt.currentToken() == CssTokenizer.TT_FUNCTION) {
                tt.pushBack();
                CssColor colorOrNull = colorConverter.parseColor(tt);
                color = colorOrNull.getColor();
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

            if (tt.nextToken() != ')') {
                throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
            }
        }
        return new Shadow(blurType, color, Geom.clamp(radius, 0, 127));
    }

    @Override
    public Effect getDefaultValue() {
        return null;
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨Effect⟩: none｜（⟨Blend⟩｜⟨Bloom⟩｜⟨BoxBlur⟩｜⟨ColorAdjust⟩｜⟨DropShadow⟩｜⟨GaussianBlur⟩｜ ⟨InnerShadow⟩）｛, ⟨Effect⟩｝"
                + "\nFormat of ⟨Blend⟩: blend(⟨BlendMode⟩)"
                + "\nFormat of ⟨Bloom⟩: bloom(⟨luminosity⟩%)"
                + "\nFormat of ⟨BoxBlur⟩: box-blur(⟨width⟩,⟨height⟩,⟨iterations⟩)"
                + "\nFormat of ⟨ColorAdjust⟩: color-adjust(hue ±⟨h⟩%, saturation ±⟨s⟩%, brightness ±⟨b⟩%, contrast ±⟨c⟩%)"
                + "\nFormat of ⟨DropShadow⟩: drop-shadow(⟨BlurType⟩,⟨Color⟩,⟨radius⟩,⟨spread⟩,⟨xoffset⟩,⟨yoffset⟩)"
                + "\nFormat of ⟨GaussianBlur⟩: gaussian-blur(⟨radius⟩)"
                + "\nFormat of ⟨InnerShadow⟩: inner-shadow(⟨BlurType⟩,⟨Color⟩,⟨radius⟩,⟨choke⟩,⟨xoffset⟩,⟨yoffset⟩)"
                + "\nFormat of ⟨Shadow⟩: shadow(⟨BlurType⟩,⟨Color⟩,⟨radius⟩)"
                + "\n" + blendModeConverter.getHelpText()
                + "\n" + blurTypeConverter.getHelpText()
                + "\n" + colorConverter.getHelpText();
    }
}

/* @(#)CssEffectConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

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
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.function.Consumer;

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
 * @version $Id$
 */
public class CssEffectConverter implements CssConverter<Effect> {

    private static final String BLEND = "blend";
    private static final String BLOOM = "bloom";
    private static final String BOX_BLUR = "box-blur";
    private static final String COLOR_ADJUST = "color-adjust";
    private static final String DROP_SHADOW = "drop-shadow";
    private static final String GAUSSIAN_BLUR = "gaussian-blur";
    private static final String GLOW = "glow";
    private static final String INNER_SHADOW = "inner-shadow";
    private static final String SHADOW = "shadow";

    @Nonnull
    private CssEnumConverter<BlurType> blurTypeConverter = new CssEnumConverter<>(BlurType.class, false);
    @Nonnull
    private CssEnumConverter<BlendMode> blendModeConverter = new CssEnumConverter<>(BlendMode.class, false);
    @Nonnull
    private CssColorConverter colorConverter = new CssColorConverter(false);

    @Override
    public <TT extends Effect> void produceTokens(@Nullable TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        if (value instanceof Blend) {
            Blend fx = (Blend) value;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, BLEND));
            blendModeConverter.produceTokens(fx.getMode(), idFactory, out);
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
            //FIXME
            /* if (fx.getInput() != null) {
                out.accept(new CssToken(CssTokenType.,", ");
                toString(out, idFactory, fx.getInput());
            }*/
        } else if (value instanceof Bloom) {
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, BLOOM));
            Bloom fx = (Bloom) value;
            out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, fx.getThreshold() * 100));
            if (fx.getInput() != null) {
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                produceTokens(fx.getInput(), idFactory, out);
            }
        } else if (value instanceof BoxBlur) {
            BoxBlur fx = (BoxBlur) value;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, BOX_BLUR));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getWidth()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getHeight()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getIterations()));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
            if (fx.getInput() != null) {
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                produceTokens(fx.getInput(), idFactory, out);
            }
        } else if (value instanceof ColorAdjust) {
            ColorAdjust fx = (ColorAdjust) value;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, COLOR_ADJUST));
            boolean needComma = false;
            final double hue = fx.getHue();
            final double saturation = fx.getSaturation();
            final double brightness = fx.getBrightness();
            final double contrast = fx.getContrast();
            boolean all = hue == 0 && saturation == 0 && brightness == 0 && contrast == 0;
            if (hue != 0 || all) {
                out.accept(new CssToken(CssTokenType.TT_IDENT, "hue"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                if (hue > 0) {
                    out.accept(new CssToken(CssTokenType.TT_PLUS));
                }
                out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, hue * 100));
                needComma = true;
            }
            if (saturation != 0 || all) {
                if (needComma) {
                    out.accept(new CssToken(CssTokenType.TT_COMMA));
                }
                out.accept(new CssToken(CssTokenType.TT_IDENT, "saturation"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                if (saturation > 0) {
                    out.accept(new CssToken(CssTokenType.TT_PLUS));
                }
                out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, saturation * 100));
                needComma = true;
            }
            if (brightness != 0 || all) {
                if (needComma) {
                    out.accept(new CssToken(CssTokenType.TT_COMMA));
                }
                out.accept(new CssToken(CssTokenType.TT_IDENT, "brightness"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                if (brightness > 0) {
                    out.accept(new CssToken(CssTokenType.TT_PLUS));
                }
                out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, brightness * 100));
                needComma = true;
            }
            if (contrast != 0 || all) {
                if (needComma) {
                    out.accept(new CssToken(CssTokenType.TT_COMMA));
                }
                out.accept(new CssToken(CssTokenType.TT_IDENT, "contrast"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                if (contrast > 0) {
                    out.accept(new CssToken(CssTokenType.TT_PLUS));
                }
                out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, contrast * 100));
            }
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
            if (fx.getInput() != null) {
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                produceTokens(fx.getInput(), idFactory, out);
            }
        } else if (value instanceof DropShadow) {
            DropShadow fx = (DropShadow) value;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, DROP_SHADOW));
            blurTypeConverter.produceTokens(fx.getBlurType(), idFactory, out);
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            colorConverter.produceTokens(new CssColor(fx.getColor()), idFactory, out);
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getRadius()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, fx.getSpread() * 100));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getOffsetX()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getOffsetY()));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
            if (fx.getInput() != null) {
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                produceTokens(fx.getInput(), idFactory, out);
            }
        } else if (value instanceof GaussianBlur) {
            GaussianBlur fx = (GaussianBlur) value;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, GAUSSIAN_BLUR));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getRadius()));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
            if (fx.getInput() != null) {
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                produceTokens(fx.getInput(), idFactory, out);
            }
        } else if (value instanceof Glow) {
            Glow fx = (Glow) value;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, GLOW));
            out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, fx.getLevel() * 100));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
            if (fx.getInput() != null) {
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                produceTokens(fx.getInput(), idFactory, out);
            }
        } else if (value instanceof InnerShadow) {
            InnerShadow fx = (InnerShadow) value;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, INNER_SHADOW));
            blurTypeConverter.produceTokens(fx.getBlurType(), idFactory, out);
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            colorConverter.produceTokens(new CssColor(fx.getColor()), idFactory, out);
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getRadius()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, fx.getChoke() * 100));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getOffsetX()));
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getOffsetY()));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
            if (fx.getInput() != null) {
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                produceTokens(fx.getInput(), idFactory, out);
            }
        } else if (value instanceof Shadow) {
            Shadow fx = (Shadow) value;
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, SHADOW));
            blurTypeConverter.produceTokens(fx.getBlurType(), idFactory, out);
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            colorConverter.produceTokens(new CssColor(fx.getColor()), idFactory, out);
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getRadius()));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
            if (fx.getInput() != null) {
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                produceTokens(fx.getInput(), idFactory, out);
            }
        } else {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        }
    }


    @Nullable
    @Override
    public Effect parse(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.nextIsIdentNone()) {
            return null;
        }
        tt.pushBack();
        return parseEffect(tt);
    }

    @Nullable
    private Effect parseEffect(CssTokenizer tt) throws ParseException, IOException {
        Effect first = null;
        Effect previous = null;
        while (tt.next() == CssTokenType.TT_FUNCTION) {

            Effect current = null;
            switch (tt.currentStringNonnull()) {
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
                } catch (@Nonnull NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    ParseException pe = new ParseException("CSS Effect: can not combine effects", tt.getStartPosition());
                    pe.initCause(ex);
                    throw pe;
                }
                previous = current;
            }

            if (tt.next() != ',') {
                tt.pushBack();
            }
        }
        return first;
    }

    private Effect parseBlend(CssTokenizer tt) throws ParseException, IOException {
        BlendMode mode = BlendMode.SRC_OVER;
        if (tt.next() == CssTokenType.TT_IDENT) {
            tt.pushBack();
            mode = blendModeConverter.parse(tt, null);
        }
        if (tt.next() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new Blend(mode);
    }

    private Effect parseBloom(CssTokenizer tt) throws ParseException, IOException {
        double threshold = 0.3;
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                threshold = tt.currentNumber().doubleValue();
                break;
            case CssTokenType.TT_PERCENTAGE:
                threshold = tt.currentNumber().doubleValue() / 100;
                break;
            default:
                tt.pushBack();
        }
        if (tt.next() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new Bloom(Geom.clamp(threshold, 0, 1));
    }

    private Effect parseBoxBlur(CssTokenizer tt) throws ParseException, IOException {
        double width = 5;
        double height = 5;
        int iterations = 1;
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                width = Geom.clamp(tt.currentNumber().doubleValue(), 0, 255);
                break;
            default:
                tt.pushBack();
        }
        if (tt.next() != ',') {
            tt.pushBack();
        }
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                height = Geom.clamp(tt.currentNumber().doubleValue(), 0, 255);
                break;
            default:
                tt.pushBack();
        }
        if (tt.next() != ',') {
            tt.pushBack();
        }
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                iterations = Geom.clamp(tt.currentNumber().intValue(), 0, 3);
                break;
            default:
                tt.pushBack();
        }
        if (tt.next() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new BoxBlur(width, height, iterations);
    }

    private Effect parseColorAdjust(CssTokenizer tt) throws ParseException, IOException {
        double hue = 0.0;
        double saturation = 0.0;
        double brightness = 0.0;
        double contrast = 0.0;
        while (tt.next() == CssTokenType.TT_IDENT) {
            String ident = tt.currentString();
            int identPos = tt.getStartPosition();
            double adjust = 0.0;
            switch (tt.next()) {
                case CssTokenType.TT_NUMBER:
                    adjust = tt.currentNumber().doubleValue();
                    break;
                case CssTokenType.TT_PERCENTAGE:
                    adjust = tt.currentNumber().doubleValue() / 100;
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
            if (tt.next() != ',') {
                tt.pushBack();
            }
        }
        if (tt.current() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new ColorAdjust(hue, saturation, brightness, contrast);
    }

    @Nonnull
    private Effect parseDropShadow(@Nonnull CssTokenizer tt) throws ParseException, IOException {
        return parseDropShadowOrInnerShadow(tt, true);
    }

    private Effect parseGaussianBlur(CssTokenizer tt) throws ParseException, IOException {
        double radius = 5;
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                radius = Geom.clamp(tt.currentNumber().doubleValue(), 0, 63);
                break;
            default:
                tt.pushBack();
        }
        if (tt.next() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new GaussianBlur(radius);
    }

    @Nonnull
    private Effect parseInnerShadow(@Nonnull CssTokenizer tt) throws ParseException, IOException {
        return parseDropShadowOrInnerShadow(tt, false);
    }

    private Effect parseGlow(CssTokenizer tt) throws ParseException, IOException {
        double level = 0.3;
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                level = tt.currentNumber().doubleValue();
                break;
            case CssTokenType.TT_PERCENTAGE:
                level = tt.currentNumber().doubleValue() / 100;
                break;
            default:
                tt.pushBack();
        }
        if (tt.next() != ')') {
            throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
        }
        return new Glow(Geom.clamp(level, 0, 1));
    }

    @Nonnull
    private Effect parseDropShadowOrInnerShadow(CssTokenizer tt, boolean isDropShadow) throws ParseException, IOException {
        String func = isDropShadow ? DROP_SHADOW : INNER_SHADOW;
        BlurType blurType = BlurType.GAUSSIAN;
        Color color = new Color(0, 0, 0, 0.75);
        double radius = 10.0;
        double spreadOrChocke = 0.0;
        double offsetX = 0.0;
        double offsetY = 4.0;
        Effect input = null;

        if (tt.next() != ')') {
            if (tt.current() != CssTokenType.TT_IDENT) {
                throw new ParseException("CSS Effect: " + func + "(<blur-type>,color,radius,spread,offset-x,offset-y) expected", tt.getStartPosition());
            }
            tt.pushBack();
            blurType = blurTypeConverter.parse(tt, null);

            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() == CssTokenType.TT_HASH) {
                color = Color.web('#' + tt.currentString());
            } else if (tt.current() == CssTokenType.TT_IDENT) {
                color = Color.web(tt.currentString());
            } else if (tt.current() == CssTokenType.TT_FUNCTION) {
                tt.pushBack();
                CssColor colorOrNull = colorConverter.parse(tt, null);
                color = colorOrNull.getColor();
            } else {
                throw new ParseException("CSS Effect: " + func + "(" + blurType.toString().toLowerCase().replace('_', '-') + ",  <color> expected", tt.getStartPosition());
            }
            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() != CssTokenType.TT_NUMBER) {
                throw new ParseException("CSS Effect: radius number expected", tt.getStartPosition());
            }
            radius = tt.currentNumber().doubleValue();

            if (tt.next() != ',') {
                tt.pushBack();
            }
            switch (tt.next()) {
                case CssTokenType.TT_NUMBER:
                    spreadOrChocke = tt.currentNumber().doubleValue();
                    break;
                case CssTokenType.TT_PERCENTAGE:
                    spreadOrChocke = tt.currentNumber().doubleValue() / 100.0;
                    break;
                default:
                    throw new ParseException("CSS Shadow-Effect: spread or chocke number expected", tt.getStartPosition());
            }
            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() != CssTokenType.TT_NUMBER) {
                throw new ParseException("CSS Shadow-Effect: offset-x number expected", tt.getStartPosition());
            }
            offsetX = tt.currentNumber().doubleValue();
            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() != CssTokenType.TT_NUMBER) {
                throw new ParseException("CSS Shadow-Effect: offset-y number expected", tt.getStartPosition());
            }
            offsetY = tt.currentNumber().doubleValue();
            if (tt.next() != ',') {
                tt.pushBack();
            } else {
                input = parseEffect(tt);
            }
        }
        if (tt.current() != ')') {
            throw new ParseException("CSS Shadow-Effect: ')'  expected", tt.getStartPosition());
        }

        final Effect effect;
        if (isDropShadow) {
            DropShadow dropShadow = new DropShadow(blurType, color, Geom.clamp(radius, 0, 127), spreadOrChocke, offsetX, offsetY);
            if (input != null) {
                dropShadow.setInput(input);
            }
            effect = dropShadow;
        } else {
            InnerShadow innerhShadow = new InnerShadow(blurType, color, Geom.clamp(radius, 0, 127), spreadOrChocke, offsetX, offsetY);
            if (input != null) {
                innerhShadow.setInput(input);
            }
            effect = innerhShadow;
        }
        return effect;
    }

    private Effect parseShadow(CssTokenizer tt) throws ParseException, IOException {
        String func = SHADOW;
        BlurType blurType = BlurType.GAUSSIAN;
        Color color = new Color(0, 0, 0, 0.75);
        double radius = 10.0;

        if (tt.next() != ')') {
            if (tt.current() != CssTokenType.TT_IDENT) {
                throw new ParseException("CSS Effect: " + func + "(<blur-type>,color,radius,spread,offset-x,offset-y) expected", tt.getStartPosition());
            }
            tt.pushBack();
            blurType = blurTypeConverter.parse(tt, null);

            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() == CssTokenType.TT_HASH) {
                color = Color.web('#' + tt.currentString());
            } else if (tt.current() == CssTokenType.TT_IDENT) {
                color = Color.web(tt.currentString());
            } else if (tt.current() == CssTokenType.TT_FUNCTION) {
                tt.pushBack();
                CssColor colorOrNull = colorConverter.parse(tt, null);
                color = colorOrNull.getColor();
            } else {
                throw new ParseException("CSS Effect: " + func + "(" + blurType.toString().toLowerCase().replace('_', '-') + ",  <color> expected", tt.getStartPosition());
            }
            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() != CssTokenType.TT_NUMBER) {
                throw new ParseException("CSS Effect: radius number expected", tt.getStartPosition());
            }
            radius = tt.currentNumber().doubleValue();

            if (tt.next() != ')') {
                throw new ParseException("CSS Effect: ')'  expected", tt.getStartPosition());
            }
        }
        return new Shadow(blurType, color, Geom.clamp(radius, 0, 127));
    }


    @Nullable
    @Override
    public Effect getDefaultValue() {
        return null;
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Nonnull
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

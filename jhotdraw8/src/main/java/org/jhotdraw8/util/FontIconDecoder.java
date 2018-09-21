/* @(#)FontIconDecoder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javax.annotation.Nonnull;

/**
 * FontIconDecoder decodes a property value if it starts with the specified
 * prefix.
 * <p>
 * The property value must have the following format:
 * <pre>
 * format = prefix, "U+", codePoint
 * </pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FontIconDecoder implements ResourceDecoder {

    @Nonnull
    private final Pattern keyPattern;
    private final String valuePrefix;
    private final Font font;

    /**
     * Creates a new instance.
     *
     * @param keyRegex the regex used on the property key
     * @param font The font
     */
    public FontIconDecoder(@Nonnull String keyRegex, Font font) {
        this(keyRegex, "fonticon:", font);
    }

    /**
     * Creates a new instance.
     *
     * @param keyRegex the regex used on the property key
     * @param valuePrefix the prefix for the value.
     * @param font The font
     */
    public FontIconDecoder(@Nonnull String keyRegex, String valuePrefix, Font font) {
        this.keyPattern = Pattern.compile(keyRegex);
        this.valuePrefix = valuePrefix;
        this.font = font;
    }

    /**
     * Creates a new instance.
     *
     * @param keyRegex the regex used on the property key
     * @param valuePrefix the prefix for the value.
     * @param fontResourceName The resource name of the font
     * @param fontSize The size of the font
     * @param baseClass The base class for loading the font
     * @throws IOException if the font resource can not be read
     */
    public FontIconDecoder(@Nonnull String keyRegex, String valuePrefix, String fontResourceName, float fontSize, Class<?> baseClass) throws IOException {
        keyPattern = Pattern.compile(keyRegex);
        this.valuePrefix = valuePrefix;
        try (InputStream in = baseClass.getResourceAsStream(fontResourceName)) {
            font = Font.loadFont(in, fontSize);
        }
    }

    @Override
    public boolean canDecodeValue(@Nonnull String key, @Nonnull String propertyValue, @Nonnull Class<?> type) {
        return keyPattern.matcher(key).matches() //
                && propertyValue.startsWith(valuePrefix)
                && (Node.class.isAssignableFrom(type));
    }

    @Nonnull
    @Override
    public <T> T decode(String key, @Nonnull String propertyValue, Class<T> type, Class<?> baseClass) {

        Text txt = new Text();
        txt.setFont(font);
        txt.setText(decodeValue(key, propertyValue));
        @SuppressWarnings("unchecked")
        T t = (T) txt;
        return t;
    }

    private String decodeValue(String key, String propertyValue) {
        String str = propertyValue.substring(valuePrefix.length()).trim();
        if (!str.startsWith("U+")) {
            throw new InternalError("illegal property value \"" + propertyValue + "\" for key " + key);
        }
        int codePoint = Integer.valueOf(str.substring(2).trim(), 16);
        return new String(Character.toChars(codePoint));
    }
}

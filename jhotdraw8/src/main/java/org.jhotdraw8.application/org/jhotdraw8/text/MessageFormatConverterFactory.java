/*
 * @(#)MessageFormatConverterFactory.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Together with this factory {@link PatternConverter} can be used to produce
 * the same output as {@code java.text.MessageFormat}.
 *
 * <pre>
 *   <i>FormatType: one of</i>
 *           number date time choice word
 *
 *   <i>FormatStyle:</i>
 *           short
 *           medium
 *           long
 *           full
 *           integer
 *           currency
 *           percent
 *           <i>SubformatPattern</i>
 * </pre>
 * <p>
 * If the type is an empty String or null, then the {@code DefaultConverter} is
 * used. If the {@code DefaultConverter} is used, this factory can only be used
 * for one way conversion to a String but not from a String!
 * </p>
 *
 * @author Werner Randelshofer
 */
public class MessageFormatConverterFactory implements ConverterFactory {

    /**
     * The locale to be used.
     */
    private final Locale locale;

    public MessageFormatConverterFactory() {
        this(Locale.getDefault());
    }

    public MessageFormatConverterFactory(Locale locale) {
        this.locale = locale;
    }

    @Nonnull
    @Override
    public Converter<?> apply(@Nullable String type, @Nullable String style) {
        if (type == null || type.isEmpty()) {
            return new DefaultConverter();
        }
        switch (type) {
            case "word":
                return new XmlWordConverter();
            case "number":
                if (style == null || style.isEmpty()) {
                    return new ConverterFormatAdapter(NumberFormat.getInstance(locale));
                }
                switch (style) {
                    case "integer":
                        return new ConverterFormatAdapter(NumberFormat.getIntegerInstance(locale));
                    case "currency":
                        return new ConverterFormatAdapter(NumberFormat.getCurrencyInstance(locale));
                    case "percent":
                        return new ConverterFormatAdapter(NumberFormat.getPercentInstance(locale));
                    default:
                        return new ConverterFormatAdapter(new DecimalFormat(style, DecimalFormatSymbols.getInstance(locale)));
                }
            case "date":
                if (style == null || style.isEmpty()) {
                    return new ConverterFormatAdapter(DateFormat.getDateInstance(DateFormat.DEFAULT, locale));
                }
                switch (style) {
                    case "short":
                        return new ConverterFormatAdapter(DateFormat.getDateInstance(DateFormat.SHORT, locale));
                    case "medium":
                        return new ConverterFormatAdapter(DateFormat.getDateInstance(DateFormat.MEDIUM, locale));
                    case "long":
                        return new ConverterFormatAdapter(DateFormat.getDateInstance(DateFormat.LONG, locale));
                    case "full":
                        return new ConverterFormatAdapter(DateFormat.getDateInstance(DateFormat.FULL, locale));
                    default:
                        return new ConverterFormatAdapter(new SimpleDateFormat(style, locale));
                }
            case "time":
                if (style == null || style.isEmpty()) {
                    return new ConverterFormatAdapter(DateFormat.getTimeInstance(DateFormat.DEFAULT, locale));
                }
                switch (style) {
                    case "short":
                        return new ConverterFormatAdapter(DateFormat.getTimeInstance(DateFormat.SHORT, locale));
                    case "medium":
                        return new ConverterFormatAdapter(DateFormat.getTimeInstance(DateFormat.MEDIUM, locale));
                    case "long":
                        return new ConverterFormatAdapter(DateFormat.getTimeInstance(DateFormat.LONG, locale));
                    case "full":
                        return new ConverterFormatAdapter(DateFormat.getTimeInstance(DateFormat.FULL, locale));
                    default:
                        return new ConverterFormatAdapter(new SimpleDateFormat(style, locale));
                }

            default:
                throw new IllegalArgumentException("type=" + type);
        }
    }
}

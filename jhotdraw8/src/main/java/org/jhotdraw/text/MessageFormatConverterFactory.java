/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.text;

import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * With this factory, {@link PatternConverter} can be used to produce the same
 * output as {@code java.text.MessageFormat}.
 *
 * <pre>{@code
 *   <i>FormatType: one of</i>
 *           number date time choice
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
 * }</pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
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

    @Override
    public Converter<?> apply(String type, String style) {
        if (type == null||type.isEmpty()) {
            return new DefaultConverter();
        }
        switch (type) {
            case "number":
                if (style == null||style.isEmpty()) {
                    return new ConverterFormatWrapper(NumberFormat.getInstance(locale));
                }
                switch (style) {
                    case "integer":
                        return new ConverterFormatWrapper(NumberFormat.getIntegerInstance(locale));
                    case "currency":
                        return new ConverterFormatWrapper(NumberFormat.getCurrencyInstance(locale));
                    case "percent":
                        return new ConverterFormatWrapper(NumberFormat.getPercentInstance(locale));
                    default:
                        return new ConverterFormatWrapper(new DecimalFormat(style, DecimalFormatSymbols.getInstance(locale)));
                }
            case "date":
                if (style == null||style.isEmpty()) {
                    return new ConverterFormatWrapper(DateFormat.getDateInstance(DateFormat.DEFAULT, locale));
                }
                switch (style) {
                    case "short":
                        return new ConverterFormatWrapper(DateFormat.getDateInstance(DateFormat.SHORT, locale));
                    case "medium":
                        return new ConverterFormatWrapper(DateFormat.getDateInstance(DateFormat.MEDIUM, locale));
                    case "long":
                        return new ConverterFormatWrapper(DateFormat.getDateInstance(DateFormat.LONG, locale));
                    case "full":
                        return new ConverterFormatWrapper(DateFormat.getDateInstance(DateFormat.FULL, locale));
                    default:
                        return new ConverterFormatWrapper(new SimpleDateFormat(style, locale));
                }
            case "time":
                if (style == null||style.isEmpty()) {
                    return new ConverterFormatWrapper(DateFormat.getTimeInstance(DateFormat.DEFAULT, locale));
                }
                switch (style) {
                    case "short":
                        return new ConverterFormatWrapper(DateFormat.getTimeInstance(DateFormat.SHORT, locale));
                    case "medium":
                        return new ConverterFormatWrapper(DateFormat.getTimeInstance(DateFormat.MEDIUM, locale));
                    case "long":
                        return new ConverterFormatWrapper(DateFormat.getTimeInstance(DateFormat.LONG, locale));
                    case "full":
                        return new ConverterFormatWrapper(DateFormat.getTimeInstance(DateFormat.FULL, locale));
                    default:
                        return new ConverterFormatWrapper(new SimpleDateFormat(style, locale));
                }
            
            default:
                throw new IllegalArgumentException("type=" + type);
        }
    }
}

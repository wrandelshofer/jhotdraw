/* @(#)LocaleUtil.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.util;

import java.util.Locale;

/**
 * LocaleUtil provides a setDefault()/getDefault() wrapper to java.util.Locale
 * in order to overcome the security restriction preventing Applets from using
 * their own locale.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LocaleUtil {
    private static Locale defaultLocale;
    
    /** Creates a new instance. */
    public LocaleUtil() {
    }
    
    public static void setDefault(Locale newValue) {
        defaultLocale = newValue;
    }
    public static Locale getDefault() {
        return (defaultLocale == null) ? Locale.getDefault() : defaultLocale;
    }
}

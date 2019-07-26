/*
 * @(#)Exceptions.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util;

public class Exceptions {
    /**
     * Gets the most specific localized error message from the given throwable.
     *
     * @param t a throwable
     * @return the error message
     */
    public static String getLocalizedMessage(Throwable t) {
        String message = null;
        for (Throwable tt = t; tt != null; tt = tt.getCause()) {
            String msg = tt.getLocalizedMessage();
            if (msg != null) {
                message = msg;
            }
        }

        return message == null || message.isEmpty() ? t.toString() : message;
    }
}

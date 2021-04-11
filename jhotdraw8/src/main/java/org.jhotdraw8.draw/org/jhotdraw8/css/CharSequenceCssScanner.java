/*
 * @(#)CharSequenceCssScanner.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import java.io.IOException;

public class CharSequenceCssScanner extends AbstractCssScanner {
    private final CharSequence seq;

    public CharSequenceCssScanner(CharSequence seq) {
        this.seq = seq;
    }

    @Override
    protected int read() throws IOException {
        return (position < seq.length()) ? seq.charAt((int) position++) : -1;
    }

    @Override
    public void pushBack(int ch) {
        if (ch != -1) {
            position--;
            if (ch == '\n') {
                lineNumber--;
            }
        }
    }
}

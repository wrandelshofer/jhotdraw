/*
 * @(#)AbstractCssScanner.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import java.io.IOException;

public abstract class AbstractCssScanner implements CssScanner {
    /**
     * The current position in the input stream.
     */
    protected long position;
    /**
     * The current line number in the input stream.
     */
    protected long lineNumber;

    /**
     * The current character.
     */
    protected int currentChar;

    /**
     * Whether we need to skip a linefeed on the next read.
     */
    protected boolean skipLF;

    public int currentChar() {
        return currentChar;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    protected abstract int read() throws IOException;

    public int nextChar() throws IOException {
        currentChar = read();
        if (skipLF && currentChar == '\n') {
            skipLF = false;
            currentChar = read();
        }

        switch (currentChar) {
        case -1: // EOF
            break;
        case '\r': // translate "\r", "\r\n" into "\n"
            skipLF = true;
            currentChar = '\n';
            lineNumber++;
            break;
        case '\f': // translate "\f" into "\n"
            currentChar = '\n';
            lineNumber++;
            break;
        case '\n':
            lineNumber++;
            break;
        case '\000':
            currentChar = '\ufffd';
            break;
        default:
            break;
        }

        return currentChar;
    }

    public long getPosition() {
        return position;
    }
}

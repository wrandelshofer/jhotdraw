/*
 * @(#)CssScanner.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import java.io.IOException;

/**
 * The {@code CssScanner} preprocesses an input stream of UTF-16 code points for
 * the {@link StreamCssTokenizer}.
 * <p>
 * The scanner filters out the characters '\r', '\f' and '\000' using the
 * following ISO 14977 EBNF productions:
 * <pre>
 * char          = inline | newline;
 * newline       = ( '\r' , ['\n']
 *                 | '\n' | '\t' | '\f
 *                 );
 * inline        = legalInline | illegalInline ;
 * legalInline   = char - ( '\r' | '\n' | '\t' | '\f' | '\000' ) ;
 * illegalInline = '\000' ;
 * char          = (* the set of unicode UTF-16 characters *) ;
 * </pre>
 * <p>
 * Any {@code illegalInline} production is replaced with U+FFFD REPLACEMENT
 * CHARACTER. Any {@code newline} production is replaced with U+000A LINE FEED
 * CHARACTER.
 * <p>
 * The scanner also keeps track of the current position and line number and
 * supports lookahead of multiple characters.
 *
 * <p>
 * References:
 * <dl>
 * <dt>Module Level 3, Chapter 3.3. Preprocessing the input stream</dt>
 * <dd><a href="http://www.w3.org/TR/2014/CR-css-syntax-3-20140220/">w3.org</a></dd>
 * </dl>
 *
 * @author Werner Randelshofer
 */
public interface CssScanner {
    /**
     * Phase 2: Processes unicode escape sequences first, and then processes
     * newlines.
     *
     * @return the next character. Returns -1 if EOF.
     * @throws IOException from the underlying input stream
     */
    int nextChar() throws IOException;

    /**
     * Returns the current character.
     *
     * @return the current character
     */
    int currentChar();

    /**
     * Pushes the specified character back into the scanner.
     *
     * @param ch The character to be pushed back
     */
    void pushBack(int ch);

    /**
     * Returns the position in the input stream.
     *
     * @return the position
     */
    long getPosition();

    /**
     * Returns the line number in the input stream.
     *
     * @return the line number
     */
    long getLineNumber();
}

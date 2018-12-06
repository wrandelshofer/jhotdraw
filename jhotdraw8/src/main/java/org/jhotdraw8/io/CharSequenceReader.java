/* @(#)CharSequenceReader.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import java.io.IOException;
import java.io.Reader;

/**
 * CharSequenceReader.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CharSequenceReader extends Reader {

    private CharSequence buf;
    private int pos;

    public CharSequenceReader(CharSequence buf) {
        this.buf = buf;
    }

    @Override
    public int read() throws IOException {
        if (buf.length() <= pos) {
            return -1;
        }
        return buf.charAt(pos++);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        len = Math.min(len, buf.length()-pos);
        for (int i=0;i<len;i++) {
            cbuf[i]=buf.charAt(pos++);
        }
        return len;
    }

    @Override
    public void close() throws IOException {
        // empty
    }

}

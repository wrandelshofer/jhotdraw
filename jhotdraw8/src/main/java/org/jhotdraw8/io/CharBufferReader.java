/* @(#)CharBufferReader.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * CharBufferReader.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CharBufferReader extends Reader {

    private final CharBuffer buf;

    public CharBufferReader(CharBuffer buf) {
        this.buf = buf;
    }

    @Override
    public int read() throws IOException {
        if (buf.remaining() <= 0) {
            return -1;
        }
        return buf.get();
    }

    @Override
    public int read(CharBuffer target) throws IOException {
        return buf.read(target);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        len = Math.min(len, buf.remaining());
        buf.get(cbuf, off, len);
        return len;
    }

    @Override
    public void close() throws IOException {
        // empty
    }

}

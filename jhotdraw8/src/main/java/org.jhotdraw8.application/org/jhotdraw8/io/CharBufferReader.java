/*
 * @(#)CharBufferReader.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import org.jhotdraw8.annotation.NonNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * CharBufferReader.
 *
 * @author Werner Randelshofer
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
    public int read(@NonNull CharBuffer target) throws IOException {
        return buf.read(target);
    }

    @Override
    public int read(@NonNull char[] cbuf, int off, int len) throws IOException {
        len = Math.min(len, buf.remaining());
        buf.get(cbuf, off, len);
        return len;
    }

    @Override
    public void close() throws IOException {
        // empty
    }

}

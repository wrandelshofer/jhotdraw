/* @(#)PegDownPrintWriter.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.sysdoc;

import java.io.IOException;
import java.io.PrintWriter;
import org.pegdown.FastEncoder;
import org.pegdown.Printer;

/**
 * PrintWriter adapter for PegDown Printer.
 * <p>
 * XXX get rid of this clutch
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PegDownPrintWriter extends Printer implements AutoCloseable {

    private PrintWriter printWriter;
    private boolean endsWithNewLine = false;

    public PegDownPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    public Printer indent(int delta) {
        indent += delta;
        return this;
    }

    public Printer print(String string) {
        printWriter.print(string);
        endsWithNewLine = !string.isEmpty() && string.charAt(string.length() - 1) == '\n';
        return this;
    }

    public Printer printEncoded(String string) {
        sb.setLength(0);
        FastEncoder.encode(string, sb);
        printWriter.print(sb.toString());
        if (sb.length() > 0) {
            endsWithNewLine = sb.charAt(sb.length() - 1) == '\n';
        }
        sb.setLength(0);
        return this;
    }

    public Printer print(char c) {
        printWriter.write(c);
        endsWithNewLine = c == '\n';
        return this;
    }

    public Printer println() {
        print('\n');
        for (int i = 0; i < indent; i++) {
            print(' ');
        }
        endsWithNewLine = false; // really??
        return this;
    }

    public Printer printchkln() {
        return println();
    }

    public Printer printchkln(boolean printNewLine) {
        return println();
    }

    public boolean endsWithNewLine() {
        return endsWithNewLine;
    }

    public String getString() {
        return "";
    }

    public Printer clear() {
        // empty
        return this;
    }

    @Override
    public void close() throws IOException {
        printWriter.close();
    }
}

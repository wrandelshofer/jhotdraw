/* @(#)PegDownHtmlSerializer.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.sysdoc;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import org.pegdown.LinkRenderer;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

/**
 * PegDownHtmlSerializer.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PegDownHtmlSerializer extends ToHtmlSerializer {

    public PegDownHtmlSerializer(PrintWriter w, LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers, List<ToHtmlSerializerPlugin> plugins) {
        super(linkRenderer, verbatimSerializers, plugins);
        printer = new PegDownPrintWriter(w);
    }

    PegDownHtmlSerializer(PegDownPrintWriter w, LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializerMap, List<ToHtmlSerializerPlugin> plugins) {
        super(linkRenderer, verbatimSerializerMap, plugins);
        printer = w;
    }

}

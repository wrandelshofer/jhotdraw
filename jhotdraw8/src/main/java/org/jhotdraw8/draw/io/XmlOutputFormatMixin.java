/* @(#)XmlOutputFormatMixin.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.util.Collection;
import java.util.function.Function;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.io.UriResolver;
import org.jhotdraw8.xml.XmlUtil;
import org.w3c.dom.Document;

/**
 * XmlOutputFormatMixin.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface XmlOutputFormatMixin extends OutputFormat {

    public void setUriResolver( Function<URI,URI> uriResolver);
 
    default Document toDocument( Drawing drawing) throws IOException {
        return toDocument(drawing, drawing.getChildren());
    }
 
    Document toDocument( Drawing drawing,  Collection<Figure> selection) throws IOException;

    @Override
    default void write( File file,  Drawing drawing) throws IOException {
                setUriResolver(new UriResolver(drawing.get(Drawing.DOCUMENT_HOME), file.getParentFile() == null ? new File(System.getProperty("user.home")).toURI() : file.getParentFile().toURI()));
        Document doc = toDocument(drawing);
        XmlUtil.write(file, doc);
    }

    @Override
    default void write( OutputStream out,  Drawing drawing) throws IOException {
        write(out, drawing, drawing.getChildren());
    }

    default void write( OutputStream out,  Drawing drawing,  Collection<Figure> selection) throws IOException {
        Document doc = toDocument(drawing, selection);
        XmlUtil.write(out, doc);
    }


    default void write( Writer out,  Drawing drawing,  Collection<Figure> selection) throws IOException {
        Document doc = toDocument(drawing, selection);
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            t.transform(source, result);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }

}

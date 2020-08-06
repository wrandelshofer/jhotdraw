package org.jhotdraw8.css;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

abstract class AbstractCssFunctionProcessorTest {

    protected abstract CssFunctionProcessor<Element> createInstance(DocumentSelectorModel model, Map<String, ImmutableList<CssToken>> customProperties);


    protected void doTestProcess(String expression, @Nullable String expected) throws Exception {
        final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        // We do not want that the reader creates a socket connection!
        builder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
        Document doc = builder.newDocument();
        doc.getDocumentElement();
        Element elem = doc.createElement("Car");
        elem.setAttribute("id", "o1");
        elem.setAttribute("doors", "5");
        elem.setAttribute("length", "3475mm");
        elem.setAttribute("width", "1475mm");
        elem.setAttribute("height", "1608mm");
        elem.setAttribute("rearBrakes", "Drum");
        doc.appendChild(elem);

        StreamCssTokenizer tt = new StreamCssTokenizer(expression);
        StringBuilder buf = new StringBuilder();
        Consumer<CssToken> consumer = t -> buf.append(t.fromToken());

        DocumentSelectorModel model = new DocumentSelectorModel();
        Map<String, ImmutableList<CssToken>> customProperties = new LinkedHashMap<>();
        customProperties.put("--blarg", ImmutableLists.of(new CssToken(CssTokenType.TT_STRING, "blarg")));
        customProperties.put("--endless-recursion", ImmutableLists.of(new CssToken(CssTokenType.TT_FUNCTION, "var"),
                new CssToken(CssTokenType.TT_IDENT, "--endless-recursion"),
                new CssToken(CssTokenType.TT_RIGHT_BRACKET)));
        CssFunctionProcessor<Element> instance = createInstance(model, customProperties);

        try {
            instance.process(elem, tt, consumer, 0);
            if (expected == null) {
                fail("must throw ParseException");
            }
            assertEquals(expected, buf.toString());
        } catch (ParseException e) {
            if (expected != null) {
                e.printStackTrace();
                fail("must not throw ParseException " + e);
            }
        }


    }

}
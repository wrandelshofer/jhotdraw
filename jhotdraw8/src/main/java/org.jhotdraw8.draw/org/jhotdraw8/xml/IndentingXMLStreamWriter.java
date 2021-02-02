/*
 * @(#)IndentingXMLStreamWriter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.xml;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.Preconditions;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * IndentingXMLStreamWriter is a {@link XMLStreamWriter} that supports automatic
 * indentation of XML elements and alphabetic sorting of XML attributes.
 * <p>
 * This writer writes an XML 1.0 document with the following syntax rules.
 * <pre>
 * Document
 *  [1]  document       ::=  prolog element Misc*
 *
 * Character Range
 *  [2]  Char           ::=  #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
 *
 * White Space
 *  [3]     S           ::=  (#x20 | #x9 | #xD | #xA)+
 *
 * Names and Tokens
 *  [4]  NameStartChar  ::=  ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
 *  [4a] NameChar       ::=  NameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
 *  [5]  Name           ::=  NameStartChar (NameChar)*
 *  [6]  Names          ::=  Name (#x20 Name)*
 *  [7]  Nmtoken        ::=  (NameChar)+
 *  [8]  Nmtokens       ::=  Nmtoken (#x20 Nmtoken)*
 *
 * Literals
 *  [9]  EntityValue    ::=  '"' ([^%&"] | PEReference | Reference)* '"'
 *                        |  "'" ([^%&'] | PEReference | Reference)* "'"
 * [10]  AttValue       ::=  '"' ([^&lt;&"] | Reference)* '"'
 *                        |  "'" ([^&lt;&'] | Reference)* "'"
 * [11]  SystemLiteral  ::=  ('"' [^"]* '"') | ("'" [^']* "'")
 * [12]  PubidLiteral   ::=  '"' PubidChar* '"' | "'" (PubidChar - "'")* "'"
 * [13]  PubidChar      ::=  #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
 *
 * Character Data
 * [14]  CharData       ::=  [^&lt;&]* - ([^&lt;&]* ']]>' [^&lt;&]*)
 *
 * Comments
 * [15]  Comment        ::=  '&lt;!--' ((Char - '-') | ('-' (Char - '-')))* '-->'
 *
 * Processing Instructions
 * [16]  PI             ::=  '&lt;?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'
 * [17]  PITarget       ::=  Name - (('X' | 'x') ('M' | 'm') ('L' | 'l'))
 *
 * CDATA Sections
 * [18]  CDSect         ::=  CDStart CData CDEnd
 * [19]  CDStart        ::=  '&lt;![CDATA['
 * [20]  CData          ::=  (Char* - (Char* ']]>' Char*))
 * [21]  CDEnd          ::=  ']]>'
 *
 * Prolog
 * [22] prolog          ::=  XMLDecl? Misc* (doctypedecl Misc*)?
 * [23] XMLDecl         ::=  '&lt;?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'
 * [24] VersionInfo     ::=  S 'version' Eq ("'" VersionNum "'" | '"' VersionNum '"')
 * [25] Eq              ::=  S? '=' S?
 * [26] VersionNum      ::=  '1.' [0-9]+
 * [27] Misc            ::=  Comment | PI | S
 *
 * Document Type Definition
 * [28]  doctypedecl    ::=  '&lt;!DOCTYPE' S Name (S ExternalID)? S? ('[' intSubset ']' S?)? '>'
 * [28a] DeclSep        ::=  PEReference | S     [WFC: PE Between Declarations]
 * [28b] intSubset      ::=  (markupdecl | DeclSep)*
 * [29]  markupdecl     ::=  elementdecl | AttlistDecl | EntityDecl | NotationDecl | PI | Comment
 *
 * External Subset
 * [30]  extSubset      ::=  TextDecl? extSubsetDecl
 * [31]  extSubsetDecl  ::=  ( markupdecl | conditionalSect | DeclSep)*
 *
 * Standalone Document Declaration
 * [32]  SDDecl         ::=  S 'standalone' Eq (("'" ('yes' | 'no') "'") | ('"' ('yes' | 'no') '"'))
 *
 * Element
 * [39]  element        ::=  EmptyElemTag
 *                        |  STag content ETag
 *
 * Start-tag
 * [40]  STag           ::=  '&lt;' Name (S Attribute)* S? '>'
 * [41]  Attribute      ::=  Name Eq AttValue
 *
 * End-tag
 * [42]  ETag           ::=  '&lt;/' Name S? '>'
 *
 * Content of Elements
 * [43]  content        ::=  CharData? ((element | Reference | CDSect | PI | Comment) CharData?)*
 *
 * Tags for Empty Elements
 * [44]  EmptyElemTag   ::=  '&lt;' Name (S Attribute)* S? '/>'    [WFC: Unique Att Spec]
 *
 * Element Type Declaration
 * [45]  elementdecl    ::=  '&lt;!ELEMENT' S Name S contentspec S? '>'    [VC: Unique Element Type Declaration]
 * [46]  contentspec    ::=  'EMPTY' | 'ANY' | Mixed | children
 *
 * Element-content Models
 * [47]  children       ::=  (choice | seq) ('?' | '*' | '+')?
 * [48]  cp             ::=  (Name | choice | seq) ('?' | '*' | '+')?
 * [49]  choice         ::=  '(' S? cp ( S? '|' S? cp )+ S? ')'    [VC: Proper Group/PE Nesting]
 * [50]  seq            ::=  '(' S? cp ( S? ',' S? cp )* S? ')'
 *
 * Mixed-content Declaration
 * [51]  Mixed          ::= '(' S? '#PCDATA' (S? '|' S? Name)* S? ')*'
 *                        | '(' S? '#PCDATA' S? ')'
 *
 * Attribute-list Declaration
 * [52]  AttlistDecl    ::=  '&lt;!ATTLIST' S Name AttDef* S? '>'
 * [53]  AttDef         ::=  S Name S AttType S DefaultDecl
 *
 * Attribute Types
 * [54]  AttType        ::=  StringType | TokenizedType | EnumeratedType
 * [55]  StringType     ::=  'CDATA'
 * [56]  TokenizedType  ::=  'ID'
 *                        |  'IDREF'
 *                        |  'IDREFS'
 *                        |  'ENTITY'
 *                        |  'ENTITIES'
 *                        |  'NMTOKEN'
 *                        |  'NMTOKENS'
 *
 *
 * Enumerated Attribute Types
 * [57]  EnumeratedType ::=  NotationType | Enumeration
 * [58]  NotationType   ::=  'NOTATION' S '(' S? Name (S? '|' S? Name)* S? ')'
 *
 * [59]  Enumeration    ::=  '(' S? Nmtoken (S? '|' S? Nmtoken)* S? ')'
 *
 * Attribute Defaults
 * [60] DefaultDecl     ::=  '#REQUIRED' | '#IMPLIED'
 *                        |  (('#FIXED' S)? AttValue)
 *
 * Conditional Section
 * [61]  conditionalSect ::= includeSect | ignoreSect
 * [62]  includeSect    ::=  '&lt;![' S? 'INCLUDE' S? '[' extSubsetDecl ']]>'
 * [63]  ignoreSect     ::=  '&lt;![' S? 'IGNORE' S? '[' ignoreSectContents* ']]>'
 * [64]  ignoreSectContents ::= Ignore ('&lt;![' ignoreSectContents ']]>' Ignore)*
 * [65]  Ignore         ::=  Char* - (Char* ('&lt;![' | ']]>') Char*)
 *
 * Character Reference
 * [66]  CharRef        ::=  '&' '#' [0-9]+ ';'
 *                        |  '&' '#' 'x' [0-9a-fA-F]+ ';'
 *
 * Entity Reference
 * [67]  Reference      ::=  EntityRef | CharRef
 * [68]  EntityRef      ::=  '&' Name ';'
 * [69]  PEReference    ::=  '%' Name ';'
 *
 * Entity Declaration
 * [70]  EntityDecl     ::=  GEDecl | PEDecl
 * [71]  GEDecl         ::=  '&lt;!ENTITY' S Name S EntityDef S? '>'
 * [72]  PEDecl         ::=  '&lt;!ENTITY' S '%' S Name S PEDef S? '>'
 * [73]  EntityDef      ::=  EntityValue | (ExternalID NDataDecl?)
 * [74]  PEDef          ::=  EntityValue | ExternalID
 *
 * External Entity Declaration
 * [75]  ExternalID     ::=  'SYSTEM' S SystemLiteral
 *                        |  'PUBLIC' S PubidLiteral S SystemLiteral
 * [76]  NDataDecl      ::=  S 'NDATA' S Name
 *
 *
 * Text Declaration
 * [77]  TextDecl       ::=  '&lt;?xml' VersionInfo? EncodingDecl S? '?>'
 *
 * Well-Formed External Parsed Entity
 * [78]  extParsedEnt   ::=  TextDecl? content
 *
 * Encoding Declaration
 * [80]  EncodingDecl   ::=  S 'encoding' Eq ('"' EncName '"' | "'" EncName "'" )
 * [81]  EncName        ::=  [A-Za-z] ([A-Za-z0-9._] | '-')*
 *
 * Notation Declarations
 * [82]  NotationDecl   ::=  '&lt;!NOTATION' S Name S (ExternalID | PublicID) S? '>'
 * [83]  PublicID       ::=  'PUBLIC' S PubidLiteral
 * </pre>
 * <p>
 * References:<br>
 * <a href="https://www.w3.org/TR/xml/">Extensible Markup Language (XML) 1.0 (Fifth Edition)</a>
 */
public class IndentingXMLStreamWriter implements XMLStreamWriter {
    public static final String DEFAULT_PREFIX = "";
    public static final String DEFAULT_NAMESPACE = "";
    public static final String START_CHAR_REF = "&#x";
    public static final String END_CHAR_REF = ";";
    public static final String START_ENTITY_REF = "&";
    public static final String END_ENTITY_REF = ";";
    public static final String START_PROCESSING_INSTRUCTION = "<?";
    public static final String END_PROCESSING_INSTRUCTION = "?>";
    private static final String START_COMMENT = "<!--";
    private static final String END_COMMENT = "-->";
    private static final String START_ENCODING = " encoding=\"";
    private static final String END_ENCODING = "\"";
    private static final String START_VERSION = " version=\"";
    private static final String END_VERSION = "\"";
    private static final String STANDALONE = " standalone=\"no\"";
    private static final String START_XML_DECLARATION = "<?xml";
    private static final String END_XML_DECLARATION = "?>";
    private static final String DEFAULT_XML_VERSION = "1.0";
    private static final String CLOSE_START_TAG = ">";
    private static final String OPEN_START_TAG = "<";
    private static final String OPEN_END_TAG = "</";
    private static final String CLOSE_END_TAG = ">";
    private static final String START_CDATA = "<![CDATA[";
    private static final String END_CDATA = "]]>";
    private static final String CLOSE_EMPTY_ELEMENT = "/>";
    private static final String PREFIX_SEPARATOR = ":";
    private static final String SPACE = " ";
    private static final String UTF_8 = "UTF-8";
    private static final String START_ATTRIBUTE_VALUE = "=\"";
    private static final String END_ATTRIBUTE_VALUE = "\"";
    private static final String XMLNS_ATTRIBUTE = "xmlns";
    private static final String XMLNS_NAMESPACE = "https://www.w3.org/TR/REC-xml-names/";
    private static final String INDENTATION = "  ";
    private static final String LINE_BREAK = "\n";
    private final Writer w;
    /**
     * Invariant: this stack always contains at least the root element.
     */
    private final Deque<Element> stack = new ArrayDeque<>();
    private final List<Attribute> attributes = new ArrayList<>();
    private final CharsetEncoder encoder;
    private boolean isStartTagOpen = false;
    private boolean escapeGreaterThan;

    public IndentingXMLStreamWriter(Writer w) {
        this.w = w;
        this.encoder = StandardCharsets.UTF_8.newEncoder();
        stack.push(new Element("", "", "<root>", false));
    }

    public IndentingXMLStreamWriter(OutputStream out) {
        this(out, StandardCharsets.UTF_8);
    }

    public IndentingXMLStreamWriter(OutputStream out, Charset charset) {
        this.w = new BufferedWriter(new OutputStreamWriter(out, charset));
        this.encoder = charset.newEncoder();
        stack.push(new Element("", "", "<root>", false));
    }

    @Override
    public void close() throws XMLStreamException {

    }

    private void closeStartTagOrCloseEmptyElemTag() throws XMLStreamException {
        if (isStartTagOpen) {
            doWriteAttributes();
            Element peeked = stack.peek();
            if (peeked != null) {
                if (peeked.isEmpty()) {
                    write(CLOSE_EMPTY_ELEMENT);
                    stack.pop();
                } else {
                    write(CLOSE_START_TAG);
                }
            }
        }
        isStartTagOpen = false;
    }


    private void doWriteAttributes() throws XMLStreamException {
        attributes.sort(Comparator.comparing(Attribute::getSortName));
        for (Attribute attribute : attributes) {
            doWriteAttribute(attribute);
        }
        attributes.clear();
    }

    private void doWriteAttribute(Attribute attribute) throws XMLStreamException {
        write(" ");
        String prefix = attribute.getPrefix() == null ? getPrefixNonNull(attribute.getNamespace()) : attribute.getPrefix();
        if (!prefix.equals(DEFAULT_PREFIX)) {
            write(prefix);
            write(PREFIX_SEPARATOR);
        }
        write(attribute.getLocalName());
        write(START_ATTRIBUTE_VALUE);
        writeXmlContent(attribute.value, true, false);
        write(END_ATTRIBUTE_VALUE);
    }

    @Override
    public void flush() throws XMLStreamException {
        try {
            w.flush();
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return stack.getLast().namespaceContext;
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        stack.getLast().namespaceContext = context;
    }

    private MyNamespaceContext getOrCreateNamespaceContext() {
        Element element = stack.getFirst();
        MyNamespaceContext ctx;
        if (element.namespaceContext instanceof MyNamespaceContext) {
            ctx = (MyNamespaceContext) element.namespaceContext;
        } else {
            ctx = new MyNamespaceContext();
            element.namespaceContext = ctx;
        }
        return ctx;
    }

    @Override
    @Nullable
    public String getPrefix(@NonNull String uri) throws XMLStreamException {
        Objects.requireNonNull(uri);
        for (Element element : stack) {
            if (element.namespaceContext != null) {
                String prefix = element.namespaceContext.getPrefix(uri);
                if (prefix != null) {
                    return prefix;
                }
            }
        }
        return null;
    }

    @NonNull
    private String getPrefixNonNull(@NonNull String uri) throws XMLStreamException {
        String prefix = getPrefix(uri);
        return prefix == null ? DEFAULT_PREFIX : prefix;
    }

    @Override
    @Nullable
    public Object getProperty(@NonNull String name) throws IllegalArgumentException {
        Objects.requireNonNull(name);
        throw new IllegalArgumentException("unsupported property: " + name);
    }

    public boolean isEscapeGreaterThan() {
        return escapeGreaterThan;
    }

    public void setEscapeGreaterThan(boolean newValue) {

        this.escapeGreaterThan = newValue;
    }

    private void requireStartTagOpened() {
        if (!isStartTagOpen) {
            throw new IllegalStateException("There is currently no open start tag.");
        }
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        getOrCreateNamespaceContext().setNamespace(uri, DEFAULT_PREFIX);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        requireStartTagOpened();
        getOrCreateNamespaceContext().setNamespace(uri, prefix);

    }

    private void write(String str) throws XMLStreamException {
        try {
            w.write(str);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    private void write(char ch) throws XMLStreamException {
        try {
            w.write(ch);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        requireStartTagOpened();
        attributes.add(new Attribute(DEFAULT_PREFIX, DEFAULT_NAMESPACE, localName, value));
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        requireStartTagOpened();
        attributes.add(new Attribute(prefix, namespaceURI, localName, value));
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        requireStartTagOpened();
        attributes.add(new Attribute(null, namespaceURI, localName, value));
    }

    @Override
    public void writeCData(@NonNull String data) throws XMLStreamException {
        Objects.requireNonNull(data);
        if (data.contains(END_CDATA)) {
            throw new XMLStreamException("CData must not contain \"" + END_CDATA + "\", CData: " + data);
        }
        setHasContent(true);
        closeStartTagOrCloseEmptyElemTag();
        write(START_CDATA);
        write(data);
        write(END_CDATA);
    }

    public void setHasContent(boolean hasContent) {
        Element peek = stack.peek();
        if (peek == null) {
            throw new AssertionError("the stack should never be empty!");
        }
        peek.setHasContent(hasContent);
    }

    /**
     * Writes character reference in hex format.
     */
    private void writeCharRef(int codePoint) throws XMLStreamException {
        write(START_CHAR_REF);
        write(Integer.toHexString(codePoint));
        write(END_CHAR_REF);
    }

    @Override
    public void writeCharacters(@NonNull String text) throws XMLStreamException {
        Objects.requireNonNull(text);
        closeStartTagOrCloseEmptyElemTag();
        setHasContent(true);
        writeXmlContent(text, false, false);

    }

    @Override
    public void writeCharacters(@NonNull char[] text, int start, int len) throws XMLStreamException {
        Objects.requireNonNull(text);
        Preconditions.checkFromIndexSize(start, len, text.length);
        closeStartTagOrCloseEmptyElemTag();
        setHasContent(true);
        writeXmlContent(text, start, len, false, false);
    }

    @Override
    public void writeComment(@NonNull String data) throws XMLStreamException {
        Objects.requireNonNull(data);
        closeStartTagOrCloseEmptyElemTag();
        Element element = stack.peek();
        assert element != null : "the stack is never empty!";
        if (!element.isHasContent()) {
            writeLineBreakAndIndentation();
        }
        write(START_COMMENT);
        writeLineBreakAndIndentation();
        writeXmlContent(data, false, true);
        writeLineBreakAndIndentation();
        write(END_COMMENT);
        setHasContent(false);
    }

    @Override
    public void writeDTD(@NonNull String dtd) throws XMLStreamException {
        Objects.requireNonNull(dtd);
        closeStartTagOrCloseEmptyElemTag();
        write(dtd);
    }

    @Override
    public void writeDefaultNamespace(@Nullable String namespaceURI) throws XMLStreamException {
        requireStartTagOpened();
        if (namespaceURI == null || namespaceURI.isEmpty()) {
            setPrefix(DEFAULT_PREFIX, DEFAULT_NAMESPACE);
        } else {
            setPrefix(DEFAULT_PREFIX, namespaceURI);
            attributes.add(new Attribute(DEFAULT_PREFIX, XMLNS_NAMESPACE, XMLNS_ATTRIBUTE, namespaceURI));
        }
    }

    @Override
    public void writeEmptyElement(@NonNull String namespaceURI, @NonNull String localName) throws XMLStreamException {
        writeEmptyElement(getPrefixNonNull(namespaceURI), localName, namespaceURI);

    }

    @Override
    public void writeEmptyElement(@NonNull String prefix, @NonNull String localName, @NonNull String namespaceURI) throws XMLStreamException {
        closeStartTagOrCloseEmptyElemTag();
        Element e = new Element(prefix, namespaceURI, localName, true);
        stack.push(e);
        isStartTagOpen = true;
        setPrefix(prefix, namespaceURI);
        writeLineBreakAndIndentation();
        write(OPEN_START_TAG);
        if (!prefix.equals(DEFAULT_PREFIX)) {
            write(prefix);
            write(PREFIX_SEPARATOR);
        }
        write(localName);
    }

    private void writeLineBreakAndIndentation() throws XMLStreamException {
        write(LINE_BREAK);
        for (int i = stack.size() - 3; i >= 0; i--) {
            write(INDENTATION);
        }
    }

    private void writeEndElementLineBreakAndIndentation() throws XMLStreamException {
        write(LINE_BREAK);
        for (int i = stack.size() - 2; i >= 0; i--) {
            write(INDENTATION);
        }
    }

    @Override
    public void writeEmptyElement(@NonNull String localName) throws XMLStreamException {
        writeEmptyElement(DEFAULT_PREFIX, localName, DEFAULT_NAMESPACE);
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        closeStartTagOrCloseEmptyElemTag();
        while (stack.size() > 1) {
            writeEndElement();
        }
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        if (stack.size() <= 1) {
            throw new XMLStreamException("no such element");
        }

        Element element = stack.pop();
        if (element.isEmpty()) {
            write(CLOSE_EMPTY_ELEMENT);
            element = stack.pop();
        }

        if (isStartTagOpen) {
            doWriteAttributes();
            write(CLOSE_EMPTY_ELEMENT);
            isStartTagOpen = false;
        } else {
            if (!element.isHasContent()) {
                writeEndElementLineBreakAndIndentation();
            }
            write(OPEN_END_TAG);
            String prefix = element.getPrefix();
            if (!prefix.isEmpty()) {
                write(prefix);
                write(PREFIX_SEPARATOR);
            }
            write(element.getLocalName());
            write(CLOSE_END_TAG);
        }
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException {
        Objects.requireNonNull(name);
        closeStartTagOrCloseEmptyElemTag();
        write(START_ENTITY_REF);
        write(name);
        write(END_ENTITY_REF);
    }

    @Override
    public void writeNamespace(@NonNull String prefix, @NonNull String namespaceURI) throws XMLStreamException {
        Objects.requireNonNull(prefix);
        Objects.requireNonNull(namespaceURI);
        requireStartTagOpened();
        attributes.add(new Attribute(prefix.isEmpty() ? "" : XMLNS_ATTRIBUTE,
                XMLNS_NAMESPACE, prefix.isEmpty() ? XMLNS_ATTRIBUTE : prefix, namespaceURI));
    }

    @Override
    public void writeProcessingInstruction(@NonNull String target) throws XMLStreamException {
        Objects.requireNonNull(target);
        closeStartTagOrCloseEmptyElemTag();
        write(START_PROCESSING_INSTRUCTION);
        write(target);
        write(END_PROCESSING_INSTRUCTION);
    }

    @Override
    public void writeProcessingInstruction(@NonNull String target, @NonNull String data) throws XMLStreamException {
        Objects.requireNonNull(target);
        closeStartTagOrCloseEmptyElemTag();
        write(START_PROCESSING_INSTRUCTION);
        write(target);
        write(SPACE);
        write(data);
        write(END_PROCESSING_INSTRUCTION);

    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        writeStartDocument(encoder.charset().name(), DEFAULT_XML_VERSION);
    }

    @Override
    public void writeStartDocument(@NonNull String version) throws XMLStreamException {
        writeStartDocument(encoder.charset().name(), version);
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        write(START_XML_DECLARATION);
        write(START_VERSION);
        write(version);
        write(END_VERSION);
        write(START_ENCODING);
        write(encoding);
        write(END_ENCODING);
        write(STANDALONE);
        write(END_XML_DECLARATION);
    }

    @Override
    public void writeStartElement(@NonNull String localName) throws XMLStreamException {
        writeStartElement(DEFAULT_PREFIX, localName, DEFAULT_NAMESPACE);
    }

    @Override
    public void writeStartElement(@NonNull String namespaceURI, @NonNull String localName) throws XMLStreamException {
        writeStartElement(getPrefixNonNull(namespaceURI), localName, namespaceURI);
    }

    @Override
    public void writeStartElement(@NonNull String prefix, @NonNull String localName, @NonNull String namespaceURI) throws XMLStreamException {
        closeStartTagOrCloseEmptyElemTag();
        Element e = new Element(prefix, namespaceURI, localName, false);
        stack.push(e);
        isStartTagOpen = true;
        setPrefix(prefix, namespaceURI);
        writeLineBreakAndIndentation();
        write(OPEN_START_TAG);
        if (!prefix.equals(DEFAULT_PREFIX)) {
            write(prefix);
            write(PREFIX_SEPARATOR);
        }
        write(localName);
    }

    private void writeXmlContent(char[] data, int start, int len, boolean escapeDoubleQuotes, boolean escapeDoubleDashes) throws XMLStreamException {
        writeXmlContent(new String(data, start, len), escapeDoubleQuotes, escapeDoubleDashes);
    }

    private void writeXmlContent(String content, boolean escapeDoubleQuotesAndNonPrintables, boolean escapeDoubleDashes) throws XMLStreamException {
        for (int index = 0, end = content.length(); index < end; index++) {
            char ch = content.charAt(index);

            if (!encoder.canEncode(ch)) {
                if (index != end - 1 && Character.isSurrogatePair(ch, content.charAt(index + 1))) {
                    writeCharRef(Character.toCodePoint(ch, content.charAt(index + 1)));
                    index++;
                } else {
                    writeCharRef(ch);
                }
                continue;
            }
            if (escapeDoubleQuotesAndNonPrintables &&
                    (Character.isWhitespace(ch)&&ch!=' ')
                    ||Character.isISOControl(ch)
                    ||index != end - 1
                    && Character.isSurrogatePair(ch, content.charAt(index + 1))
                    &&Character.isISOControl(Character.toCodePoint(ch, content.charAt(index + 1)))
            ) {
                writeCharRef(ch);
                continue;
            }

            switch (ch) {
            case '<':
                write("&lt;");
                break;

            case '&':
                write("&amp;");
                break;

            case '-':
                if (escapeDoubleDashes && index < end - 1 && content.charAt(index + 1) == '-') {
                    index++;
                    writeCharRef('-');
                    writeCharRef('-');
                } else {
                    write(ch);
                }
                break;
            case '>':
                write("&gt;");
                break;
            case '"':
                if (escapeDoubleQuotesAndNonPrintables) {
                    write("&quot;");
                } else {
                    write(ch);
                }
                break;
            default:
                write(ch);
                break;
            }
        }
    }

    private static class MyNamespaceContext implements NamespaceContext {
        private final Map<String, List<String>> nsToPrefix = new HashMap<>();
        private final Map<String, String> prefixToNs = new HashMap<>();

        @Override
        public String getNamespaceURI(@NonNull String prefix) {
            return prefixToNs.get(prefix);
        }

        @Override
        public String getPrefix(@NonNull String namespaceURI) {
            Objects.requireNonNull(namespaceURI);
            List<String> prefixes = nsToPrefix.get(namespaceURI);
            return prefixes == null || prefixes.isEmpty() ? null : prefixes.get(0);
        }

        @Override
        public Iterator<String> getPrefixes(@NonNull String namespaceURI) {
            Objects.requireNonNull(namespaceURI);
            List<String> prefixes = nsToPrefix.get(namespaceURI);
            return prefixes == null ? Collections.emptyIterator() : prefixes.iterator();
        }

        public void setNamespace(String uri, String prefix) {
            String oldNs = prefixToNs.put(prefix, uri);
            if (oldNs != null) {
                nsToPrefix.get(oldNs).removeIf(prefix::equals);
            }
            if (DEFAULT_PREFIX.equals(prefix)) {
                nsToPrefix.computeIfAbsent(uri, k -> new ArrayList<>()).add(0, prefix);
            } else {
                nsToPrefix.computeIfAbsent(uri, k -> new ArrayList<>()).add(prefix);
            }
        }
    }

    private static class Element {
        private final String localName;
        private final String namespaceUri;
        private final String prefix;
        private final boolean isEmpty;
        private NamespaceContext namespaceContext = new MyNamespaceContext();
        private boolean hasContent;

        public Element(@NonNull String prefix, @NonNull String namespaceUri, @NonNull String localName, boolean isEmpty) {
            this.prefix = prefix;
            this.namespaceUri = namespaceUri;
            this.localName = localName;
            this.isEmpty = isEmpty;
        }

        public String getLocalName() {
            return localName;
        }

        public String getPrefix() {
            return prefix;
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        public boolean isHasContent() {
            return hasContent;
        }

        public void setHasContent(boolean hasContent) {
            this.hasContent = hasContent;
        }
    }

    private static class Attribute {
        private final String localName;
        private final String value;

        private final String namespace;
        private final String prefix;

        private final String sortName;

        public Attribute(@Nullable String prefix, @NonNull String namespace, @NonNull String localName, @NonNull String value) {
            this.localName = localName;
            this.value = value;
            this.namespace = namespace;
            this.prefix = prefix;
            if (XMLNS_NAMESPACE.equals(namespace)) {
                if (prefix == null || prefix.isEmpty()) {
                    sortName = "  " + localName;// xmlns="..." comes first
                } else {
                    sortName = " " + prefix;// xlmns:...="..." come second
                }
            } else {
                sortName = localName;// then we sort by local name
            }
        }

        public String getLocalName() {
            return localName;
        }

        @NonNull
        public String getNamespace() {
            return namespace;
        }

        @Nullable
        public String getPrefix() {
            return prefix;
        }

        public String getSortName() {
            return sortName;
        }


        public String getValue() {
            return value;
        }

    }
}

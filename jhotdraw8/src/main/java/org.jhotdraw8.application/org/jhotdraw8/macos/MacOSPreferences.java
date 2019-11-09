package org.jhotdraw8.macos;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.StreamSupport;

public class MacOSPreferences {
    /**
     * Path to global preferences.
     */
    public final static File GLOBAL_PREFERENCES = new File(System.getProperty("user.home"), "Library/Preferences/.GlobalPreferences.plist");
    /**
     * Path to finder preferences.
     */
    public final static File FINDER_PREFERENCES = new File(System.getProperty("user.home"), "Library/Preferences/com.apple.finder.plist");
    /**
     * Each entry in this hash map represents a cached preferences file.
     */
    private static HashMap<File, HashMap<String, Object>> cachedFiles;

    /**
     * Creates a new instance.
     */
    public MacOSPreferences() {
    }

    public static String getString(File file, String key) {
        return (String) get(file, key);
    }

    public static String getString(File file, String key, String defaultValue) {
        return (String) get(file, key, defaultValue);
    }

    public static boolean isStringEqualTo(File file, String key, String defaultValue, String compareWithThisValue) {
        return ((String) get(file, key, defaultValue)).equals(compareWithThisValue);
    }

    /**
     * Gets a preferences value
     *
     * @param file the preferences file
     * @param key  the key may contain tabulator separated entries to directly access a value in a sub-dictionary
     * @return the value associated with the key
     */
    public static Object get(File file, String key) {
        ensureCached(file);
        final HashMap<String, Object> map = cachedFiles.get(file);
        final String[] split = key.split("\t");
        if (map != null) {
            final Object plist = map.get("plist");
            if (plist instanceof List) {
                for (Object o : (List) plist) {
                    if (o instanceof Map) {
                        Map m = (Map) o;
                        for (int i = 0, n = split.length; i < n; i++) {
                            String subkey = split[i];
                            Object value;
                            if (m.containsKey(subkey)) {
                                value = m.get(subkey);
                                if (i < n - 1 && (value instanceof Map)) {
                                    m = (Map) value;
                                } else if (i == n - 1) {
                                    return value;
                                }
                            }
                        }

                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns all known keys for the specified preferences file.
     *
     * @return
     */
    public static Set<String> getKeySet(File file) {
        ensureCached(file);
        return cachedFiles.get(file).keySet();
    }

    /**
     * Clears all caches.
     */
    public static void clearAllCaches() {
        cachedFiles.clear();

    }

    /**
     * Clears the cache for the specified preference file.
     */
    public static void clearCache(File f) {
        cachedFiles.remove(f);
    }

    /**
     * Get a value from a Mac OS X preferences file.
     *
     * @param file         The preferences file.
     * @param key          Hierarchical keys are separated by \t characters.
     * @param defaultValue This value is returned when the key does not exist.
     * @return Returns the preferences value.
     */
    public static Object get(File file, String key, Object defaultValue) {
        ensureCached(file);
        return (cachedFiles.get(file).containsKey(key)) ? cachedFiles.get(file).get(key) : defaultValue;
    }

    private static void ensureCached(File file) {
        if (cachedFiles == null) {
            cachedFiles = new HashMap<File, HashMap<String, Object>>();
        }
        if (!cachedFiles.containsKey(file)) {
            HashMap<String, Object> cache = new HashMap<String, Object>();
            cachedFiles.put(file, cache);
            updateCache(file, cache);
        }
    }

    private static boolean isOSX() {
        return true;
    }

    private static void updateCache(File file, HashMap<String, Object> cache) {
        cache.clear();

        if (isOSX()) {
            try {
                Document plist = readPList(file);

                Stack<String> keyPath = new Stack<String>();
                cache.put("plist", readNode(plist.getDocumentElement()));
            } catch (Throwable e) {
                System.err.println("Warning: ch.randelshofer.quaqua.util.OSXPreferences failed to load " + file);
                e.printStackTrace();
            }
        }
    }

    private static Object readNode(Element node) throws IOException {
        String name = node.getTagName();
        Object value;
        switch (name) {
            case "plist":
                value = readPList(node);
                break;
            case "dict":
                value = readDict(node);
                break;
            case "array":
                value = readArray(node);
                break;
            default:
                value = readValue(node);
                break;
        }
        return value;
    }

    private static Iterable<Node> getChildren(final Element elem) {
        return () -> new Iterator<Node>() {
            int index = 0;
            final NodeList children = elem.getChildNodes();

            @Override
            public boolean hasNext() {
                return index < children.getLength();
            }

            @Override
            public Node next() {
                return children.item(index++);
            }
        };
    }

    private static Iterable<Element> getChildElements(final Element elem) {
        return () -> StreamSupport.stream(getChildren(elem).spliterator(), false)
                .filter(e -> e instanceof Element).map(e -> (Element) e).iterator();
    }

    private static List<Object> readPList(Element plistElem) throws IOException {
        List<Object> plist = new ArrayList<>();
        for (Node child : getChildElements(plistElem)) {
            plist.add(readNode((Element) child));
        }
        return plist;
    }

    private static String getContent(Element elem) {
        StringBuilder buf = new StringBuilder();
        for (Node child : getChildren(elem)) {
            if (child instanceof Text) {
                buf.append(((Text) child).getTextContent());
            }
        }
        return buf.toString().trim();
    }

    private static Map<String, Object> readDict(Element dictElem) throws IOException {
        LinkedHashMap<String, Object> dict = new LinkedHashMap<>();
        for (Iterator<Element> iterator = getChildElements(dictElem).iterator(); iterator.hasNext(); ) {
            Element keyElem = iterator.next();
            if (!"key".equals(keyElem.getTagName())) {
                throw new IOException("missing dictionary key at" + dictElem);
            }
            Element valueElem = iterator.next();
            Object elemValue = readNode(valueElem);
            dict.put(getContent(keyElem), elemValue);
        }
        return dict;
    }

    private static List<Object> readArray(Element arrayElem) throws IOException {
        List<Object> array = new ArrayList<>();
        for (Element child : getChildElements(arrayElem)) {
            array.add(readNode(child));
        }
        return array;
    }

    private static Object readValue(Element value) throws IOException {
        Object parsedValue;
        switch (value.getTagName()) {
            case "true":
                parsedValue = true;
                break;
            case "false":
                parsedValue = false;
                break;
            case "data":
                parsedValue = Base64.getDecoder().decode(getContent(value));
                break;
            case "date":
                try {
                    parsedValue = DatatypeFactory.newInstance().newXMLGregorianCalendar(getContent(value));
                } catch (IllegalArgumentException | DatatypeConfigurationException e) {
                    throw new IOException(e);
                }
                break;
            case "real":
                try {
                    parsedValue = Double.valueOf(getContent(value));
                } catch (NumberFormatException e) {
                    throw new IOException(e);
                }
                break;
            case "integer":
                try {
                    parsedValue = Long.valueOf(getContent(value));
                } catch (NumberFormatException e) {
                    throw new IOException(e);
                }
                break;
            default:
                parsedValue = getContent(value);
                break;
        }
        return parsedValue;
    }

    private static Document readXmlPropertyList(File file) throws IOException {
        InputSource inputSource = new InputSource(file.toString());
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
            return builder.parse(inputSource);
        } catch (ParserConfigurationException e) {
            throw new IOException("Cannot create document builder for file: " + file, e);
        } catch (SAXException e) {
            throw new IOException("Illegal file format in file: " + file, e);
        }
    }

    private static Document readBinaryPropertyList(File file) throws IOException {
        return new BinaryPListParser().parse(file);
    }

    /**
     * Reads the specified PList file and returns it as a document.
     * This method can deal with XML encoded and binary encoded PList files.
     */
    private static Document readPList(File file) throws IOException {
        Document doc;
        try {
            doc = readBinaryPropertyList(file);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            try {
                doc = readXmlPropertyList(file);
            } catch (IOException e3) {
                throw e3;
            }
        }
        if (doc == null) {
            throw new IOException("File is neither an XML PList nor a Binary PList. File: " + file);
        }
        return doc;
    }
}

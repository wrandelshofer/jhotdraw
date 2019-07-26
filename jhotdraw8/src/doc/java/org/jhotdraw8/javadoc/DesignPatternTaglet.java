/* @(#)DesignPatternTaglet.java
 * Copyright (c) 2016 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.javadoc;


import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.util.DocTreeScanner;
import jdk.javadoc.doclet.Taglet;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.Set;

/**
 * DesignPatternTaglet processes the {@literal @design.pattern} tag.
 * <p>
 * This tag can be used to document the design pattern of a type. The
 * description of the design pattern only needs to be written once for
 * the instantiating type of the design pattern, the description is copied to
 * all other participating types.
 * <p>
 * This tag can only be used in the Javadoc comment of a type declaration.
 * The tag consists of a header and an optional description.
 * <p>
 * The header specifies the following properties:
 * <dl>
 * <dt>instantiatingType</dt><dd>The Java type which instantiates the
 * design pattern.</dd>
 * <dt>patternName</dt><dd>The name of the design pattern.</dd>
 * <dt>patternRole</dt><dd>The role of this type in the design pattern</dd>
 * </dl>
 * <p>
 * This taglet prints the name of a design pattern, the role of the type in
 * the design pattern, and the description. If no description is supplied,
 * then the description of the {@code instantiatingType} type is copied.
 *
 * <p>
 * EBNF syntax of the tag:
 * <pre>
 * tag = "@design.pattern" , header , [ "." , description ] ;
 *
 * header = instantiatingType, " ", patternName , [ "," , patternRole ] ;
 *
 * instantiatingType = JavaTypeName ;
 * patternName = String ;
 * patternRole = String ;
 * description = (TextTag | SeeTag) , { TextTag | SeeTag };
 * </pre>
 *
 * @author Werner Randelshofer
*/
public class DesignPatternTaglet implements Taglet {
    @Override
    public Set<Location> getAllowedLocations() {
        return Set.of(Location.TYPE);
    }

    @Override
    public boolean isInlineTag() {
        return false;
    }

    @Override
    public String getName() {
        return "design.pattern";
    }

    private class TagsVisitor extends DocTreeScanner<Void, StringBuilder> {

        @Override
        public Void scan(DocTree node, StringBuilder result) {
            switch (node.getKind()) {

                case ATTRIBUTE:
                    break;
                case AUTHOR:
                    break;
                case CODE:
                    break;
                case COMMENT:
                    break;
                case DEPRECATED:
                    break;
                case DOC_COMMENT:
                    break;
                case DOC_ROOT:
                    break;
                case DOC_TYPE:
                    break;
                case END_ELEMENT:
                    break;
                case ENTITY:
                    break;
                case ERRONEOUS:
                    break;
                case EXCEPTION:
                    break;
                case HIDDEN:
                    break;
                case IDENTIFIER:
                    break;
                case INDEX:
                    break;
                case INHERIT_DOC:
                    break;
                case LINK:
                    StringBuilder labelBuilder = new StringBuilder();
                    for (DocTree docTree : ((LinkTree) node).getLabel()) {
                        docTree.accept(this, labelBuilder);
                    }
                    String label = labelBuilder.toString();
                    ReferenceTree reference = ((LinkTree) node).getReference();
                    result.append(label);
                    String href = reference.getSignature().replace('.', '/');
                    if (label.isEmpty()) {
                        label = reference.getSignature();
                        int p = label.lastIndexOf('.');
                        label = label.substring(p + 1);
                    }

                    result.append("<a href=\"");
                           /*
                            if (holder instanceof Type) {
                                Type type = (Type) holder;
                                String qualifiedName = type.qualifiedTypeName();
                                for (int p = qualifiedName.indexOf('.'); p != -1; p = qualifiedName.indexOf('.', p + 1)) {
                                    result.append("../");
                                }
                            }*/
                    result.append(href)//
                            .append(".html\">");
                    result.append(label)//
                            .append("</a>");
                    break;
                case LINK_PLAIN:
                    break;
                case LITERAL:
                    break;
                case PARAM:
                    break;
                case PROVIDES:
                    break;
                case REFERENCE:
                    break;
                case RETURN:
                    break;
                case SEE:
                    break;
                case SERIAL:
                    break;
                case SERIAL_DATA:
                    break;
                case SERIAL_FIELD:
                    break;
                case SINCE:
                    break;
                case START_ELEMENT:
                    break;
                case SUMMARY:
                    break;
                case TEXT:
                    result.append(((TextTree) node).getBody());
                    break;
                case THROWS:
                    break;
                case UNKNOWN_BLOCK_TAG:
                    break;
                case UNKNOWN_INLINE_TAG:
                    break;
                case USES:
                    break;
                case VALUE:
                    break;
                case VERSION:
                    break;
                case OTHER:
                    break;
            }

            return null;
        }
    }

    @Override
    public String toString(List<? extends DocTree> tags, Element element) {
        if (tags.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append("<hr>\n");
        result.append("<div class=\"block\">");
        for (DocTree docTree : tags) {
            result.append(docTree.getKind());
            docTree.accept(new TagsVisitor(), result);
        }

//        for (int i = 0; i < tags.length; i++) {
//            result.append("<p>");
//            for (Tag parsedTag : lookupDescription(parseInlineTags(tags[i]))) {
//                switch (parsedTag.kind()) {
//                    case NAME:
//                        DesignPatternHeaderTag dpt = (DesignPatternHeaderTag) parsedTag;
//                        result.append("<b>Design Pattern:</b> ")//
//                                // .append(dpt.instantiatingType).append(" ")//
//                                .append(dpt.patternName);
//                        if (dpt.patternRole.length() > 0) {
//                            result.append(", <b>Role:</b> ").append(dpt.patternRole);
//                        }
//                        result.append('.');
//                        result.append("<br>");
//                        break;
//                    case "@see":
//                        if (parsedTag instanceof SeeTag) {
//                            SeeTag see = (SeeTag) parsedTag;
//                            String href = see.referencedClassName().replace('.', '/');
//                            String label = see.label();
//                            if (label.isEmpty()) {
//                                label = see.referencedClassName();
//                                int p = label.lastIndexOf('.');
//                                label = label.substring(p + 1);
//                            }
//
//                            result.append("<a href=\"");
//                            Doc holder = tags[0].holder();
//                            if (holder instanceof Type) {
//                                Type type = (Type) holder;
//                                String qualifiedName = type.qualifiedTypeName();
//                                for (int p = qualifiedName.indexOf('.'); p != -1; p = qualifiedName.indexOf('.', p + 1)) {
//                                    result.append("../");
//                                }
//                            }
//                            result.append(href)//
//                                    .append(".html\">");
//                            result.append(label)//
//                                    .append("</a>");
//                        } else {
//                            result.append(parsedTag.text());
//                        }
//                        break;
//                    default:
//                        result.append(parsedTag.text());
//                        break;
//                }
//            }
//
//            String[] textParts = splitText(tags[i]);
//            result.append("<b>Design Pattern:</b> ").append(textParts[0]);
//            if (textParts[1].length() > 0) {
//                result.append(", <b>Role:</b> ").append(textParts[1]);
//            }
//            result.append('.');
//
//            String description;
//            if (textParts[2].length() > 0) {
//                description = textParts[2];
//                descriptions.put(textParts[0], textParts[2]);
//            } else {
//                description = descriptions.get(textParts[0]);
//                if (description == null) {
//                    description = "";
//                }
//            }
//
//            if (description.length() > 0) {
//
//                Doc holder = tags[0].holder();
//                if (holder instanceof Type) {
//                    Type type = (Type) holder;
//                    String qualifiedName = type.qualifiedTypeName();
//                    String href = "";
//                    for (int p = qualifiedName.indexOf('.'); p != -1; p = qualifiedName.indexOf('.', p + 1)) {
//                        href = "../" + href;
//                    }
//                    description = description.replaceAll("href=\"", "href=\"" + href);
//                }
//
//                result.append("<br>").append(description);
//            }
//
//            result.append("</p>");
//        }
        result.append("</div");
        result.append("<hr>\n");
        return result.toString();
    }


/*
    public static final String NAME = "design.pattern";
    private static final String HEADER = "Design Patterns:";

    private HashMap<String, ArrayList<Tag>> descriptions = new HashMap<>();

    *//**
     * Return the name of this custom tag.
     *
     * @return the name
     *//*
    @Override
    public String getName() {
        return NAME;
    }

    *//**
     * Will return false.
     *
     * @return false
     *//*
    @Override
    public boolean inField() {
        return false;
    }

    *//**
     * Will return false.
     *
     * @return false
     *//*
    @Override
    public boolean inConstructor() {
        return false;
    }

    *//**
     * Will return false.
     *
     * @return false
     *//*
    @Override
    public boolean inMethod() {
        return false;
    }

    *//**
     * Will return false.
     *
     * @return false
     *//*
    @Override
    public boolean inOverview() {
        return false;
    }

    *//**
     * Will return false.
     *
     * @return false
     *//*
    @Override
    public boolean inPackage() {
        return false;
    }

    *//**
     * Will return true.
     *
     * @return true
     *//*
    @Override
    public boolean inType() {
        return true;
    }

    *//**
     * Will return false.
     *
     * @return false
     *//*
    @Override
    public boolean isInlineTag() {
        return false;
    }

    *//**
     * Register this Taglet.
     *
     * @param tagletMap the map to register this tag to.
     *//*
    public static void register(Map<String, Taglet> tagletMap) {
        DesignPatternTaglet tag = new DesignPatternTaglet();
        Taglet t = tagletMap.get(tag.getName());
        if (t != null) {
            tagletMap.remove(tag.getName());
        }
        tagletMap.put(tag.getName(), tag);
    }

    *//**
     * Given the <code>Tag</code> representation of this custom tag, return its
     * string representation.
     *
     * @param tag the <code>Tag</code> representation of this custom tag.
     * @return the String representation
     *//*
    @Override
    public String toString(Tag tag) {
        return toString(new Tag[]{tag});
    }
*/
    /*    static class DesignPatternHeaderTag extends CompositeTag {
        *//**
     * The qualified name of the instantiating type.
     *//*
        private String instantiatingType;
        private String patternName;
        private String patternRole;

        public DesignPatternHeaderTag(Doc holder, String instantiatingType, String patternName, String patternRole, SourcePosition position) {
            super(holder, new Tag[0], position);
            this.instantiatingType = instantiatingType;
            this.patternName = patternName;
            this.patternRole = patternRole;
        }

        @Override
        public String kind() {
            return NAME;
        }

        public String unqualifiedInstantiatingType() {
            return toUnqualifiedName(instantiatingType);
        }

        public String getInstantiatingType() {
            return instantiatingType;
        }

        public String getPatternName() {
            return patternName;
        }

        public String getPatternRole() {
            return patternRole;
        }

    }*/

    /**
     * Tries to return a qualified name. Returns the unqualified name,
     * if it can not be looked up.
     *
     * @param tag  A tag which is used for lookup.
     * @param name A name which can be qualified or unqualified.
     * @return the qualified name if lookup was successful, otherwise returns
     * the unqualified nam.
     *//*
    public static String toQualifiedName(Tag tag, String name) {
        if (name.indexOf('.') != -1) {
            return name;
        }

        Doc doc = tag.holder();
        if (doc instanceof ClassDoc) {
            ClassDoc cd = (ClassDoc) doc;
            ClassDoc nameClass = cd.findClass(name);
            if (nameClass != null) {
                return nameClass.qualifiedName();
            }

        }
        return name;
    }

    public static String toUnqualifiedName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);

    }

    private Tag[] lookupDescription(Tag[] tags) {
        if (tags.length > 0 && NAME.equals(tags[0].kind())) {
            DesignPatternHeaderTag dpt = (DesignPatternHeaderTag) tags[0];
            String key = dpt.instantiatingType + " " + dpt.patternName;
            if (tags.length == 1) {
                ArrayList<Tag> descr = descriptions.get(key);
                if (descr != null) {
                    Tag[] newTags = new Tag[descr.size() + 1];
                    newTags[0] = dpt;
                    for (int i = 1; i < newTags.length; i++) {
                        newTags[i] = descr.get(i - 1);
                    }
                    return newTags;
                } else {
                    Doc doc = dpt.holder();
                    if (doc instanceof ClassDoc) {
                        ClassDoc cd = (ClassDoc) doc;
                        ClassDoc maincd = cd.findClass(dpt.instantiatingType);
                        if (maincd != null) {
                            Tag[] lookupedTags = maincd.tags(NAME);
                            DesignPatternHeaderTag lookedUpDpt = null;
                            for (Tag lookup : lookupedTags) {
                                Tag[] inline = parseInlineTags(lookup);
                                if (inline.length > 0 && (inline[0] instanceof DesignPatternHeaderTag)) {
                                    lookedUpDpt = (DesignPatternHeaderTag) inline[0];
                                    if (lookedUpDpt.instantiatingType.equals(dpt.instantiatingType)
                                            && lookedUpDpt.patternName.equals(dpt.patternName)) {
                                        descr = new ArrayList<>();
                                        for (int i = 1; i < inline.length; i++) {
                                            descr.add(inline[i]);
                                        }
                                        break;
                                    }
                                }
                            }
                            if (descr != null) {
                                if (descr.isEmpty()) {
                                    System.err.println(lookedUpDpt.position() + ": warning: DesignPatternTaglet \"@" + NAME + " " + dpt.unqualifiedInstantiatingType() + " " + dpt.patternName + "\" must have a description.");
                                    descriptions.put(key, descr);
                                } else {
                                    descriptions.put(key, descr);
                                    ArrayList<Tag> result = new ArrayList<>();
                                    result.add(dpt);
                                    result.addAll(descr);
                                    return result.toArray(new Tag[result.size()]);
                                }
                            } else {
                                System.err.println(dpt.position() + ": warning: DesignPatternTaglet could not find a \"@" + NAME + " " + dpt.unqualifiedInstantiatingType() + " " + dpt.patternName + "\" tag in class " + dpt.instantiatingType + ".");
                            }
                        } else {
                            System.err.println(dpt.position() + ": warning: DesignPatternTaglet could not find class " + dpt.unqualifiedInstantiatingType() + ".");
                        }
                    }
                }

            } else {
                ArrayList<Tag> descr = new ArrayList<>();
                for (int i = 1; i < tags.length; i++) {
                    descr.add(tags[i]);
                }
                descriptions.put(key, descr);
            }
        }
        return tags;
    }

    *//**
     * Parses the inline tags of a {@literal @design.pattern} Tag.
     * <p>
     * If parsing was successful, then the first tag in the returned array is a
     * DesignPatternHeaderTag. Subsequent contain the description of the
     * design pattern.
     * <p>
     * If parsing was unsuccessful, then the unparsed inline tags are returned.
     *
     * @param tag A design pattern tag
     * @return The parsed inline tags
     */
   /* public static Tag[] parseInlineTags(Tag tag) {
        Tag[] inline = tag.inlineTags();
        if (inline.length == 0 || "@see".equals(inline[0].kind())) {
            return inline;
        }

        String text = inline[0].text();
        int p0 = text.indexOf(' ');
        int p1 = text.indexOf(',', p0 + 1);
        int p2 = text.indexOf('.', p1 + 1);

        String instantiatingType = "";
        String patternName = "";
        String patternRole = "";
        String description = "";

        if (p0 != -1 && p1 != -1 && p2 != -1) {
            instantiatingType = toQualifiedName(tag, text.substring(0, p0).trim());
            patternName = cleanupWhitespace(text.substring(p0 + 1, p1));
            patternRole = cleanupWhitespace(text.substring(p1 + 1, p2));
            description = cleanupWhitespace(text.substring(p2 + 1));
        } else {
            System.err.println(tag.position() + ": warning: DesignPatternTaglet illegal @" + NAME + " tag. Expected \"@" + NAME + " className patternName, roleName. description.\"");
            description = text;
        }

        ArrayList<Tag> parsed = new ArrayList<Tag>(inline.length + 1);
        DesignPatternHeaderTag dpt = new DesignPatternHeaderTag(inline[0].holder(), instantiatingType, patternName, patternRole, inline[0].position());
        parsed.add(dpt);
        if (!description.isEmpty()) {
            parsed.add(new TextTag(inline[0].holder(), description, inline[0].position()));
        }
        for (int i = 1; i < inline.length; i++) {
            parsed.add(inline[i]);
        }

        return parsed.toArray(new Tag[parsed.size()]);
    }

    private static String cleanupWhitespace(String str) {
        return str.replaceAll("\\s+", " ").trim();
    }*/
}

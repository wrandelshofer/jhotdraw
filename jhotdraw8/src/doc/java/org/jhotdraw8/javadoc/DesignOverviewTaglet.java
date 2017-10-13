/* @(#)DesignOverviewTaglet.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * DesignOverviewTaglet processes the {@literal @design.overview} tag.
 * <p>
 * This tag can be used to give an overview over all design patterns used
 * in a library.
 * <p>
 * This tag can only be used in the Javadoc comment of the overview page.
 * <p>
 * The tag is empty.
 * <p>
 * This taglet lists all types that were annotated with the
 * {@literal @design.pattern} tag in a table. The table contains the name 
 * of the design pattern, the first sentence of the pattern description,
 * and a list of all participating types and their roles in the design pattern.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
@SuppressWarnings("deprecation")
public class DesignOverviewTaglet implements Taglet {

    private static final String NAME = "design.overview";
    private static final String HEADER = "Design Patterns";

    private HashMap<String, ArrayList<Tag>> descriptions = new HashMap<>();

    /**
     * Return the name of this custom tag.
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean inField() {
        return false;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean inConstructor() {
        return false;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean inMethod() {
        return false;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean inOverview() {
        return true;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean inPackage() {
        return false;
    }

    /**
     * Will return true.
     *
     * @return true
     */
    @Override
    public boolean inType() {
        return false;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean isInlineTag() {
        return false;
    }

    /**
     * Register this Taglet.
     *
     * @param tagletMap the map to register this tag to.
     */
    public static void register(Map<String, Taglet> tagletMap) {
        DesignOverviewTaglet tag = new DesignOverviewTaglet();
        Taglet t = tagletMap.get(tag.getName());
        if (t != null) {
            tagletMap.remove(tag.getName());
        }
        tagletMap.put(tag.getName(), tag);
    }

    /**
     * Given the <code>Tag</code> representation of this custom tag, return its
     * string representation.
     *
     * @param tag the <code>Tag</code> representation of this custom tag.
     */
    @Override
    public String toString(Tag tag) {
        return toString(new Tag[]{tag});
    }

    /**
     * Given an array of <code>Tag</code>s representing this custom tag, return
     * its string representation.
     *
     * @param tags the array of <code>Tag</code>s representing of this custom
     * tag.
     */
    @Override
    public String toString(Tag[] tags) {
        if (tags.length == 0) {
            return "";
        }

        Tag tag = tags[0];

        if (!(tag.holder() instanceof RootDoc)) {
            System.err.println(tag.position() + ": error: DesignOverviewTaglet is not in root document.");
            return "";
        }

        TreeMap<String, DesignPatternSummary> map = new TreeMap<>();
        RootDoc root = (RootDoc) tag.holder();
        for (ClassDoc classDoc : root.classes()) {
            for (Tag dpTag : classDoc.tags(DesignPatternTaglet.NAME)) {
                Tag[] parsedInlineTags = DesignPatternTaglet.parseInlineTags(dpTag);
                if (parsedInlineTags.length > 0 && (parsedInlineTags[0] instanceof DesignPatternTaglet.DesignPatternHeaderTag)) {
                    DesignPatternTaglet.DesignPatternHeaderTag dph = (DesignPatternTaglet.DesignPatternHeaderTag) parsedInlineTags[0];

                    String key = dph.getPatternName() + " " + dph.getInstantiatingType();
                    DesignPatternSummary summary = map.get(key);
                    if (summary == null) {
                        summary = new DesignPatternSummary();
                        map.put(key, summary);
                        summary.header = dph;
                        summary.description = parsedInlineTags;
                    }
                    if (parsedInlineTags.length > summary.description.length) {
                        summary.description = parsedInlineTags;
                    }
                    summary.participants.add(new ParticipationSummary(classDoc, dph.getPatternRole()));
                }
            }
        }

        StringBuilder result = new StringBuilder();
        result.append("<table class=\"overviewSummary\" "
                + "summary=\"Design Patterns table, listing design patterns, "
                + "and participating types\" "
                + "border=\"0\" cellpadding=\"3\" cellspacing=\"0\">\n"
                + "\n"
                + "<tbody><tr>\n"
                + "<th class=\"colFirst\" scope=\"col\">Design Pattern</th>\n"
                + "<th class=\"colLast\" scope=\"col\">Description and "
                + "Participating Types</th>\n"
                + "</tr>\n"
                + "</tbody><tbody>\n");
        boolean alt = false;
        for (Map.Entry<String, DesignPatternSummary> entry : map.entrySet()) {
            DesignPatternSummary summary = entry.getValue();
            alt = !alt;
            result.append(alt ? "<tr class=\"altColor\">\n" : "<tr class=\"rowColor\">\n");
            result.append("<td class=\"colFirst\">");
            result.append(summary.header.getPatternName());
            result.append("</td>\n");
            result.append("<td class=\"colLast\">");
            result.append("<div class=\"block\">");
            for (Tag descr : summary.description) {
                if (descr instanceof DesignPatternTaglet.DesignPatternHeaderTag) {
                    // do nothing
                } else if (descr instanceof SeeTag) {
                    SeeTag see=(SeeTag)descr;
                    result.append(see.label().isEmpty()?DesignPatternTaglet.toUnqualifiedName(see.referencedClassName()):see.label());
                } else {
                    // stop after first sentence
                    String text = descr.text();
                    int p = (text.replaceAll("\\s", " ")+" ").indexOf(". ");
                    if (p != -1) {
                        text = text.substring(0, p+1);
                    }
                    result.append(text);
                    if (p != -1) {
                        break;
                    }
                }
            }
            result.append("</div>");
            result.append("<div class=\"block\">");
            boolean first = true;
            String previousRole = "";
            Collections.sort(summary.participants);
            for (ParticipationSummary ps : summary.participants) {
                if (first) {
                    first = false;
                } else {
                    result.append(", ");
                }
                if (!ps.role.equals(previousRole)) {
                    result.append(ps.role).append(": ");
                }
                result.append("<a href=\"");
                result.append(ps.classDoc.qualifiedTypeName().replace('.', '/'));
                result.append(".html\">");
                result.append(ps.classDoc.simpleTypeName());
                result.append("</a>");

                previousRole = ps.role;
            }
            result.append("</div>");
            result.append("</td>\n");
            result.append("</tr>\n");
        }
        result.append("</tbody>\n"
                + "<caption><span>Design Patterns</span><span class=\"tabEnd\">&nbsp;</span></caption></table>");

        return result.toString();

    }

    private static class ParticipationSummary implements Comparable<ParticipationSummary> {

        ClassDoc classDoc;
        String role;

        public ParticipationSummary(ClassDoc classDoc, String role) {
            this.classDoc = classDoc;
            this.role = role;
        }

        @Override
        public int compareTo(ParticipationSummary that) {
            int cmp = role.compareTo(that.role);
            if (cmp == 0) {
                cmp = classDoc.simpleTypeName().compareTo(that.classDoc.simpleTypeName());
            }
            return cmp;
        }

    }

    private static class DesignPatternSummary {

        DesignPatternTaglet.DesignPatternHeaderTag header;
        Tag[] description;
        List<ParticipationSummary> participants = new ArrayList<>();

        public DesignPatternSummary() {
        }
    }
}

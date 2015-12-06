/*
 * @(#)Regex.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Regex {

    private final String find;
    private final String replace;
    private transient Pattern pattern;

    public Regex() {
        this.find = ".*";
        this.replace = "$0";
    }

    public Regex(String find, String replace) {
        this.find = find;
        this.replace = replace;
    }

    public String getFind() {
        return find;
    }

    public String getReplace() {
        return replace;
    }

    @Override
    public String toString() {
        return "/" + escape(find) + "/" + escape(find) + "/";
    }

    private String escape(String str) {
        return find.replace("/", "\\/");
    }

    /**
     * Applies the regular expression to the string.
     */
    public String apply(String str) {
        if (pattern == null) {
            pattern = Pattern.compile(find);
        }

        Matcher m = pattern.matcher(str);
        return m.replaceAll(replace);
    }
}

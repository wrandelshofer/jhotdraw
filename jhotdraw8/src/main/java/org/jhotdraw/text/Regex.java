/* @(#)Regex.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.util.Objects;
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
     * @param str the string
     * @return the replaced string
     */
    public String apply(String str) {
        if (pattern == null) {
            pattern = Pattern.compile(find);
        }

        Matcher m = pattern.matcher(str);
        return m.replaceAll(replace);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.find);
        hash = 53 * hash + Objects.hashCode(this.replace);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Regex other = (Regex) obj;
        if (!Objects.equals(this.find, other.find)) {
            return false;
        }
        if (!Objects.equals(this.replace, other.replace)) {
            return false;
        }
        return true;
    }
    
    
}

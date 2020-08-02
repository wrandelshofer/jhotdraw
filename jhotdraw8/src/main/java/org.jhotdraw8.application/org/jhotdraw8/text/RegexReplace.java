/*
 * @(#)RegexReplace.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find - ReplaceAll regular expression.
 *
 * @author Werner Randelshofer
 */
public class RegexReplace {

    @Nullable
    private final String find;
    @Nullable
    private final String replace;
    private transient Pattern pattern;

    public RegexReplace() {
        this(null, null);
    }

    public RegexReplace(@Nullable String find, @Nullable String replace) {
        this.find = find;
        this.replace = replace;
    }

    @Nullable
    public String getFind() {
        return find;
    }

    @Nullable
    public String getReplace() {
        return replace;
    }

    @NonNull
    @Override
    public String toString() {
        return "/" + escape(find) + "/" + escape(replace) + "/";
    }

    @NonNull
    private String escape(@Nullable String str) {
        return str == null ? "" : str.replace("/", "\\/");
    }

    /**
     * Applies the regular expression to the string.
     *
     * @param str the string
     * @return the replaced string
     */
    @Nullable
    public String apply(@Nullable String str) {
        if (str == null) {
            return str;
        }
        if (find == null) {
            return replace == null ? str : replace;
        }
        if (pattern == null) {
            pattern = Pattern.compile(find);
        }

        Matcher m = pattern.matcher(str);
        try {
            return replace == null ? m.replaceAll("$0") : m.replaceAll(replace);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return str;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.find);
        hash = 53 * hash + Objects.hashCode(this.replace);
        return hash;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RegexReplace other = (RegexReplace) obj;
        if (!Objects.equals(this.find, other.find)) {
            return false;
        }
        return Objects.equals(this.replace, other.replace);
    }

}

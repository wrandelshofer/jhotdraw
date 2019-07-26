/*
 * @(#)OSXCollator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import java.text.CollationKey;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;

/**
 * The OSXCollator strives to match the collation rules used by the Mac OS X
 * Finder and of Mac OS X file dialogs.
 * <p>
 * If we wanted to match the OS X collation rules exactly, we would have to
 * implement the rules for all languages supported by Mac OS X and Java. To
 * reduce the amount of work needed for implementing these rules, the
 * OSXCollator changes the collation rules returned by
 * java.text.Collator.getInstance() to do the following:
 * <ul>
 * <li>Space characters are treated as primary collation differences</li>
 * <li>Hyphen characters are treated as primary collation differences</li>
 * <li>Sequence of digits (characters '0' through '9') is treated as a single
 * collation object. The current implementation supports sequences of up to 999
 * characters length.</li>
 * </ul>
 * If java.text.Collator.getInstance() does not return an instance of
 * java.text.RuleBasedCollator, then the returned collator is used, and only
 * sequences of digits are changed to match the collation rules of Mac OS X.
 *
 * @author Werner Randelshofer
 */
public class OSXCollator extends Collator {

    private Collator collator;

    /**
     * Creates a new instance.
     */
    public OSXCollator() {
        this(Locale.getDefault());
    }

    public OSXCollator(Locale locale) {
        collator = Collator.getInstance(locale);

        if (collator instanceof RuleBasedCollator) {
            String rules = ((RuleBasedCollator) collator).getRules();

            // If hyphen is ignored except for tertiary difference, make it
            // a primary difference, and move in front of the first primary 
            // difference found in the rules
            int pos = rules.indexOf(",'-'");
            int primaryRelationPos = rules.indexOf('<');
            if (primaryRelationPos == rules.indexOf("'<'")) {
                primaryRelationPos = rules.indexOf('<', primaryRelationPos + 2);
            }
            if (pos != -1 && pos < primaryRelationPos) {
                rules = rules.substring(0, pos)
                        + rules.substring(pos + 4, primaryRelationPos)
                        + "<'-'"
                        + rules.substring(primaryRelationPos);
            }

            // If space is ignored except for secondary and tertiary 
            // difference, make it a primary difference, and move in front 
            // of the first primary difference found in the rules
            pos = rules.indexOf(";' '");
            primaryRelationPos = rules.indexOf('<');
            if (primaryRelationPos == rules.indexOf("'<'")) {
                primaryRelationPos = rules.indexOf('<', primaryRelationPos + 2);
            }
            if (pos != -1 && pos < primaryRelationPos) {
                rules = rules.substring(0, pos)
                        + rules.substring(pos + 4, primaryRelationPos)
                        + "<' '"
                        + rules.substring(primaryRelationPos);
            }

            try {
                collator = new RuleBasedCollator(rules);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int compare(@Nullable String source, @Nullable String target) {
        if (source == null) {
            return target == null ? 0 : 1;
        }
        if (target == null) {
            return -1;
        }
        return collator.compare(expandNumbers(source), expandNumbers(target));
    }

    @Override
    public CollationKey getCollationKey(String source) {
        return collator.getCollationKey(expandNumbers(source));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof OSXCollator) {
            OSXCollator that = (OSXCollator) o;
            return this.collator.equals(that.collator);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return collator.hashCode();
    }

    /**
     * Prepends each group of digits with the number of digits - 1.
     * The number of digits - 1 is always two digits long.
     * <pre>e.g.
     * "a3b21" becomes "a003b0121".
     * </pre>
     */
    @Nullable
    String expandNumbers(@Nullable String s) {
        if (s == null) {
            return null;
        }

        StringBuilder out = new StringBuilder();
        final int n = s.length();
        int start = -1; // start index of number group
        for (int i = 0; i < n; i++) {
            char ch = s.charAt(i);
            if ('0' <= ch && ch <= '9') {
                if (start == -1) {
                    start = i;
                }
            } else {
                if (start != -1) {
                    appendDigitGroup(out, s, start, i);
                    start = -1;
                }
                out.append(ch);
            }
        }
        if (start != -1) {
            appendDigitGroup(out, s, start, n);
        }

        return out.toString();
    }

    /**
     * Prepends the specified group of digits with the number of digits - 1.
     * The number of digits - 1 is always two digits long.
     *
     * @param out   output
     * @param s     string
     * @param start start index of digit group
     * @param end   end index+ 1 of digit group
     */
    private void appendDigitGroup(StringBuilder out, @Nonnull String s, int start, int end) {
        assert start < end : "start:" + start + " end:" + end;
        int num = Math.min(100, end - start) - 1;
        out.append((char) (num / 10 + '0'));
        out.append((char) (num % 10 + '0'));
        for (int j = start; j < end; j++) {
            out.append(s.charAt(j));
        }
    }
}

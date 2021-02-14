/*
 * @(#)PreferencesFontChooserModelFactory.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.fontchooser;

import javafx.collections.ObservableList;
import org.jhotdraw8.annotation.NonNull;

import java.util.Iterator;
import java.util.prefs.Preferences;

/**
 * PreferencesFontChooserModelFactory.
 *
 * @author Werner Randelshofer
 */
public class PreferencesFontChooserModelFactory extends DefaultFontChooserModelFactory {

    @Override
    public FontChooserModel create() {
        FontChooserModel model = super.create();
        updateModelFromPrefs(model);
        return model;
    }

    private static final char ESCAPE_CHAR = '\\';
    private static final char UNIT_SEPARATOR = '\t';
    private static final char RECORD_SEPARATOR = '\n';
    private static final char UNIT_ESCAPE_CHAR = 't';
    private static final char RECORD_ESCAPE_CHAR = 'n';

    private void escape(@NonNull String string, @NonNull StringBuilder buf) {
        for (char ch : string.toCharArray()) {
            switch (ch) {
            case ESCAPE_CHAR:
                buf.append(ESCAPE_CHAR);
                buf.append(ESCAPE_CHAR);
                break;
            case UNIT_SEPARATOR:
                buf.append(ESCAPE_CHAR);
                    buf.append(UNIT_ESCAPE_CHAR);
                    break;
                case RECORD_SEPARATOR:
                    buf.append(ESCAPE_CHAR);
                    buf.append(RECORD_ESCAPE_CHAR);
                    break;
                default:
                    buf.append(ch);
                    break;
            }
        }
    }

    private @NonNull String resetAndUnescape(@NonNull String string, @NonNull StringBuilder buf) {
        buf.setLength(0);
        unescape(string, buf);
        return buf.toString();
    }

    private void unescape(@NonNull String string, @NonNull StringBuilder buf) {
        char[] chars = string.toCharArray();
        for (int i = 0, n = chars.length; i < n; i++) {
            char ch = chars[i];
            switch (ch) {
                case ESCAPE_CHAR:
                    if (i < n - 1) {
                        char escapechar = chars[++i];
                        switch (escapechar) {
                            case ESCAPE_CHAR:
                                buf.append(ESCAPE_CHAR);
                                break;
                            case UNIT_ESCAPE_CHAR:
                                buf.append(UNIT_SEPARATOR);
                                break;
                            case RECORD_ESCAPE_CHAR:
                                buf.append(RECORD_SEPARATOR);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    buf.append(ch);
                    break;
            }
        }
    }

    public void writeModelToPrefs(@NonNull FontChooserModel model) {
        StringBuilder buf = new StringBuilder();
        for (FontCollection fontCollection : model.getFontCollections()) {
            if (fontCollection.isSmartCollection()) {
                continue;
            }
            if (buf.length() != 0) {
                buf.append(RECORD_SEPARATOR);
            }
            escape(fontCollection.getName(), buf);
            for (FontFamily fontFamily : fontCollection.getFamilies()) {
                buf.append(UNIT_SEPARATOR);
                escape(fontFamily.getName(), buf);
            }
        }
        Preferences prefs = Preferences.userNodeForPackage(PreferencesFontChooserModelFactory.class);
        prefs.put("FontCollections", buf.toString());
    }

    public void updateModelFromPrefs(@NonNull FontChooserModel model) {
        Preferences prefs = Preferences.userNodeForPackage(PreferencesFontChooserModelFactory.class);
        String persisted = prefs.get("FontCollections", null);
        if (persisted == null) {
            return;
        }


        ObservableList<FontCollection> root = model.getFontCollections();
        final ObservableList<FontFamily> families = model.getAllFonts().getFamilies();
        for (final Iterator<FontCollection> iter = model.getFontCollections().iterator(); iter.hasNext(); ) {
            final FontCollection collection = iter.next();
            if (!collection.isSmartCollection()) {
                iter.remove();
            }
        }

        StringBuilder buf = new StringBuilder();
        for (String record : persisted.split("" + RECORD_SEPARATOR)) {
            String[] units = record.split("" + UNIT_SEPARATOR);
            int n = units.length;
            if (n > 0) {
                String collectionName = resetAndUnescape(units[0], buf);
                String[] facenames = new String[n - 1];
                for (int i = 1; i < n; i++) {
                    facenames[i - 1] = resetAndUnescape(units[i], buf);
                }
                root.add(new FontCollection(collectionName, false,
                        collectFamiliesNamed(families, facenames)));
            }
        }
    }

}

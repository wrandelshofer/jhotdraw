/*
 * @(#)EmptyResources.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class EmptyResources implements Resources {
    @Nullable
    private Resources parent;
    private final ResourceBundle emptyResourceBundle;

    {
        try {
            emptyResourceBundle = new PropertyResourceBundle(new StringReader(""));
        } catch (IOException e) {
            throw new RuntimeException("Could not create empty PropertyResourceBundle", e);
        }
    }

    @Override
    public ResourceBundle asResourceBundle() {
        return emptyResourceBundle;
    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public Class<?> getBaseClass() {
        return getClass();
    }

    @Override
    public Object getModule() {
        return null;
    }

    @Override
    public @NonNull String getBaseName() {
        return "empty";
    }


    @Override
    public @NonNull Locale getLocale() {
        return Locale.getDefault();
    }

    @Override
    public @NonNull String getString(String key) {
        throw new MissingResourceException("Can't find resource for bundle "
                + this.getClass().getName()
                + ", key " + key,
                this.getClass().getName(),
                key);
    }

    @Nullable
    @Override
    public Resources getParent() {
        return parent;
    }

    @Override
    public @Nullable Object handleGetObjectRecursively(@NonNull String key) {
        return null;
    }

    @Override
    public @NonNull Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
    }

    @Override
    public void setParent(Resources parent) {
        this.parent = parent;
    }
}

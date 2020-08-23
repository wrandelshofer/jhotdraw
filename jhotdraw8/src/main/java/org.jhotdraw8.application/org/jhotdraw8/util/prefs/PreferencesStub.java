/*
 * @(#)PreferencesUtil.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util.prefs;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * {@code PreferencesStub} can be used as a stub when the system
 * preferences are not available due to security restrictions.
 *
 * @author Werner Randelshofer
 */
public class PreferencesStub
        extends Preferences {

    @NonNull
    private final HashMap<String, Object> map = new HashMap<>();
    private final boolean isUserNode;

    public PreferencesStub(boolean isUserNode) {
        this.isUserNode = isUserNode;
    }

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @NonNull
    @Override
    public String get(String key, String def) {
        return (String) (map.getOrDefault(key, def));
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

    @Override
    public void clear() throws BackingStoreException {
        map.clear();
    }

    @Override
    public void putInt(String key, int value) {
        map.put(key, value);
    }

    @Override
    public int getInt(String key, int def) {
        return (Integer) (map.getOrDefault(key, def));
    }

    @Override
    public void putLong(String key, long value) {
        map.put(key, value);
    }

    @Override
    public long getLong(String key, long def) {
        return (Long) (map.getOrDefault(key, def));
    }

    @Override
    public void putBoolean(String key, boolean value) {
        map.put(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return (Boolean) (map.getOrDefault(key, def));
    }

    @Override
    public void putFloat(String key, float value) {
        map.put(key, value);
    }

    @Override
    public float getFloat(String key, float def) {
        return (Float) (map.getOrDefault(key, def));
    }

    @Override
    public void putDouble(String key, double value) {
        map.put(key, value);
    }

    @Override
    public double getDouble(String key, double def) {
        return (Double) (map.getOrDefault(key, def));
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        map.put(key, value);
    }

    @NonNull
    @Override
    public byte[] getByteArray(String key, byte[] def) {
        return (byte[]) (map.getOrDefault(key, def));
    }

    @NonNull
    @Override
    public String[] keys() throws BackingStoreException {
        return map.keySet().toArray(new String[0]);
    }

    @NonNull
    @Override
    public String[] childrenNames() throws BackingStoreException {
        return new String[0];
    }

    @Nullable
    @Override
    public Preferences parent() {
        return null;
    }

    @Nullable
    @Override
    public Preferences node(String pathName) {
        return null;
    }

    @Override
    public boolean nodeExists(String pathName) throws BackingStoreException {
        return false;
    }

    @Override
    public void removeNode() throws BackingStoreException {
        // empty
    }

    @NonNull
    @Override
    public String name() {
        return "Stub";
    }

    @NonNull
    @Override
    public String absolutePath() {
        return "Stub";
    }

    @Override
    public boolean isUserNode() {
        return isUserNode;
    }

    @NonNull
    @Override
    public String toString() {
        return "Stub";
    }

    @Override
    public void flush() throws BackingStoreException {
        clear();
    }

    @Override
    public void sync() throws BackingStoreException {
        //
    }

    @Override
    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        //
    }

    @Override
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        //
    }

    @Override
    public void addNodeChangeListener(NodeChangeListener ncl) {
        //
    }

    @Override
    public void removeNodeChangeListener(NodeChangeListener ncl) {
        //
    }

    @Override
    public void exportNode(OutputStream os) throws IOException, BackingStoreException {
        //
    }

    @Override
    public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
        //
    }

}

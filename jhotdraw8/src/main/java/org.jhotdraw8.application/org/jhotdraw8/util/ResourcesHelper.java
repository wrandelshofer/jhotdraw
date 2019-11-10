/*
 * @(#)ResourcesHelper.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.logging.Logger;

class ResourcesHelper {
    final static Logger LOG = Logger.getLogger(Resources.class.getName());
    /**
     * The global map of property name modifiers. The key of this map is the
     * name of the property name modifier, the value of this map is a fallback
     * chain.
     */
    @NonNull
    static Map<String, String[]> propertyNameModifiers = Collections.synchronizedMap(new HashMap<>());


    static {
        String osName = System.getProperty("os.name").toLowerCase();
        String os;
        if (osName.startsWith("mac")) {
            os = "mac";
        } else if (osName.startsWith("windows")) {
            os = "win";
        } else {
            os = "other";
        }
        propertyNameModifiers.put("os", new String[]{os, "default"});
    }

    static final Set<String> acceleratorKeys = Collections.synchronizedSet(new HashSet<>(
            Arrays.asList("shift", "control", "ctrl", "meta", "alt", "altGraph")));
    /**
     * List of decoders. The first decoder which can decode a resource value is
     * will be used to convert the resource value to an object.
     */
    @NonNull
    static List<ResourceDecoder> decoders = Collections.synchronizedList(new ArrayList<>());

    /**
     * Generates fallback keys by processing all property name modifiers in the
     * key.
     */
    static void generateFallbackKeys(@NonNull String key, @NonNull ArrayList<String> fallbackKeys) {
        int p1 = key.indexOf("[$");
        if (p1 == -1) {
            fallbackKeys.add(key);
        } else {
            int p2 = key.indexOf(']', p1 + 2);
            if (p2 == -1) {
                return;
            }
            String modifierKey = key.substring(p1 + 2, p2);
            String[] modifierValues = ResourcesHelper.propertyNameModifiers.get(modifierKey);
            if (modifierValues == null) {
                modifierValues = new String[]{"default"};
            }
            for (String mv : modifierValues) {
                generateFallbackKeys(key.substring(0, p1) + mv + key.substring(p2 + 1), fallbackKeys);
            }
        }
    }

    @Nullable
    static Node getIconProperty(@NonNull Resources r, String key, String suffix, @NonNull Class<?> baseClass) {
        try {
            String rsrcName = r.getString(key + suffix);
            if ("".equals(rsrcName) || rsrcName == null) {
                return null;
            }

            for (ResourceDecoder d : ResourcesHelper.decoders) {
                if (d.canDecodeValue(key, rsrcName, Node.class)) {
                    return d.decode(key, rsrcName, Node.class, baseClass);
                }
            }

            URL url = baseClass.getResource(rsrcName);
            if (url == null) {
                ResourcesHelper.LOG.warning("ClasspathResources[" + r.getBaseName() + "].getIconProperty \"" + key + suffix + "\" resource:" + rsrcName + " not found.");
            }
            return (url == null) ? null : new ImageView(url.toString());
        } catch (MissingResourceException e) {
            ResourcesHelper.LOG.warning("ClasspathResources[" + r.getBaseName() + "].getIconProperty \"" + key + suffix + "\" not found.");
            return null;
        }
    }

}

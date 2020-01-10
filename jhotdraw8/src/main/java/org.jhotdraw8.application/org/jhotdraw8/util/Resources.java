/*
 * @(#)Resources.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util;

import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCombination;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.action.Action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

/**
 * This is a convenience wrapper for accessing resources stored in a
 * ResourceBundle.
 * <p>
 * A resources object may reference a parent resources object using the resource
 * key "$parent".
 * <p>
 * <b>Placeholders</b><br>
 * On top of the functionality provided by ResourceBundle, a property value can
 * include text from another property, by specifying the desired property name
 * and format type between <code>"${"</code> and <code>"}"</code>.
 * <p>
 * For example, if there is a {@code "imagedir"} property with the value
 * {@code "/org/jhotdraw8/undo/images"}, then this could be used in an attribute
 * like this: <code>${imagedir}/editUndo.png</code>. This is resolved at
 * run-time as {@code /org/jhotdraw8/undo/images/editUndo.png}.
 * <p>
 * Property names in placeholders can contain modifiers. MLModifier are written
 * between @code "[$"} and {@code "]"}. Each modifier has a fallback chain.
 * <p>
 * For example, if the property name modifier {@code "os"} has the value "win",
 * and its fallback chain is {@code "mac","default"}, then the property name
 * <code>${preferences.text.[$os]}</code> is first evaluted to {@code
 * preferences.text.win}, and - if no property with this name exists - it is
 * evaluated to {@code preferences.text.mac}, and then to
 * {@code preferences.text.default}.
 * <p>
 * The property name modifier "os" is defined by default. It can assume the
 * values "win", "mac" and "other". Its fallback chain is "default".
 * <p>
 * The format type can be optinally specified after a comma. The following
 * format types are supported:
 * <ul>
 * <li>{@code string} This is the default format.</li>
 * <li>{@code accelerator} This format replaces all occurences of the keywords
 * shift, control, ctrl, meta, alt, altGraph by getProperties which start with
 * {@code accelerator.}. For example, shift is replaced by
 * {@code accelerator.shift}.
 * </li>
 * </ul>
 *
 * @author Werner Randelshofer
 */
public interface Resources {
    String PARENT_RESOURCE_KEY = "$parent";

    /**
     * Adds a decoder.
     *
     * @param decoder the resource decoder
     */
    static void addDecoder(ResourceDecoder decoder) {
        ResourcesHelper.decoders.add(decoder);
    }

    /**
     * Puts a property name modifier along with a fallback chain.
     *
     * @param name          The name of the modifier.
     * @param fallbackChain The fallback chain of the modifier.
     */
    static void putPropertyNameModifier(String name, String... fallbackChain) {
        ResourcesHelper.propertyNameModifiers.put(name, fallbackChain);
    }

    /**
     * Removes a property name modifier.
     *
     * @param name The name of the modifier.
     */
    static void removePropertyNameModifier(String name) {
        ResourcesHelper.propertyNameModifiers.remove(name);
    }

    @NonNull
    static Resources getResources(String moduleName, @NonNull String resourceBundle) {
        try {
            Class<?> clazz = Class.forName("org.jhotdraw8.util.ModulepathResources");
            Method method = clazz.getMethod("getResources", String.class, String.class);
            return (Resources) method.invoke(null, moduleName, resourceBundle);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            return ClasspathResources.getResources(resourceBundle);
        }
    }

    ResourceBundle asResourceBundle();

    default void configureAction(@NonNull Action action, String argument) {
        configureAction(action, argument, getBaseClass());
    }

    default void configureAction(@NonNull Action action, String argument, @NonNull Class<?> baseClass) {
        action.set(Action.LABEL, getTextProperty(argument));
        String shortDescription = getToolTipTextProperty(argument);
        if (shortDescription != null && shortDescription.length() > 0) {
            action.set(Action.SHORT_DESCRIPTION, shortDescription);
        }
        action.set(Action.ACCELERATOR_KEY, getAcceleratorProperty(argument));
        action.set(Action.MNEMONIC_KEY, getMnemonicProperty(argument));
        action.set(Action.SMALL_ICON, getSmallIconProperty(argument, baseClass));
        action.set(Action.LARGE_ICON_KEY, getLargeIconProperty(argument, baseClass));
    }

    default void configureButton(@NonNull ButtonBase button, String argument) {
        configureButton(button, argument, getBaseClass());
    }

    default void configureButton(@NonNull ButtonBase button, String argument, @NonNull Class<?> baseClass) {
        button.setText(getTextProperty(argument));
        //button.setACCELERATOR_KEY, getAcceleratorProperty(argument));
        //action.putValue(Action.MNEMONIC_KEY, new Integer(getMnemonicProperty(argument)));
        button.setGraphic(getLargeIconProperty(argument, baseClass));
        button.setTooltip(new Tooltip(getToolTipTextProperty(argument)));
    }

    /**
     * Configures a menu item with a text, an accelerator, a mnemonic and a menu
     * icon.
     *
     * @param menu     the menu
     * @param argument the argument
     */
    default void configureMenu(@NonNull Menu menu, String argument) {
        menu.setText(getTextProperty(argument));
        menu.setText(getTextProperty(argument));
        menu.setAccelerator(getAcceleratorProperty(argument));
        menu.setGraphic(getSmallIconProperty(argument, getBaseClass()));
    }

    /**
     * Configures a menu item with a text, an accelerator, a mnemonic and a menu
     * icon.
     *
     * @param menu     the menu item
     * @param argument the argument
     */
    default void configureMenuItem(@NonNull MenuItem menu, String argument) {
        menu.setText(getTextProperty(argument));
        menu.setText(getTextProperty(argument));
        menu.setAccelerator(getAcceleratorProperty(argument));
        menu.setGraphic(getSmallIconProperty(argument, getBaseClass()));
    }

    default void configureToolBarButton(@NonNull ButtonBase button, String argument) {
        configureToolBarButton(button, argument, getBaseClass());
    }

    default void configureToolBarButton(@NonNull ButtonBase button, String argument, @NonNull Class<?> baseClass) {
        Node icon = getLargeIconProperty(argument, baseClass);
        if (icon != null) {
            button.setGraphic(getLargeIconProperty(argument, baseClass));
            button.setText(null);
        } else {
            button.setGraphic(null);
            button.setText(getTextProperty(argument));
        }
        button.setTooltip(new Tooltip(getToolTipTextProperty(argument)));
    }

    boolean containsKey(String key);

    /**
     * Returns a formatted string using java.util.Formatter().
     *
     * @param key       the key
     * @param arguments the arguments
     * @return formatted String
     */
    @NonNull
    default String format(@NonNull String key, Object... arguments) {
        //return String.format(resource.getLocale(), getString(key), arguments);
        return new Formatter(getLocale()).format(getString(key), arguments).toString();
    }

    /**
     * Gets a KeyStroke for a JavaBeans "accelerator" property from the
     * ResourceBundle.
     * <BR>Convenience method.
     *
     * @param key The key of the property. This method adds ".accelerator" to
     *            the key.
     * @return <code>javax.swing.KeyStroke.getKeyStroke(value)</code>. Returns
     * null if the property is missing.
     */
    @Nullable
    default KeyCombination getAcceleratorProperty(String key) {
        return getKeyCombination(key + ".accelerator");
    }

    Class<?> getBaseClass();

    void setBaseClass(Class<?> baseClass);

    Module getModule();

    @NonNull
    String getBaseName();

    /**
     * Returns a formatted string using javax.text.MessageFormat.
     *
     * @param key       the key
     * @param arguments the arguments
     * @return formatted String
     */
    @NonNull
    default String getFormatted(@NonNull String key, Object... arguments) {
        return MessageFormat.format(getString(key), arguments);
    }

    /**
     * Get an Integer from the ResourceBundle.
     * <br>Convenience method to save casting.
     *
     * @param key The key of the property.
     * @return The value of the property. Returns -1 if the property is missing.
     */
    @NonNull
    default Integer getInteger(@NonNull String key) {
        try {
            return Integer.valueOf(getString(key));
        } catch (MissingResourceException e) {
            ResourcesHelper.LOG.warning("ClasspathResources[" + getBaseName() + "] \"" + key + "\" not found.");
            return -1;
        }
    }

    /**
     * Get a KeyStroke from the ResourceBundle.
     * <BR>Convenience method.
     *
     * @param key The key of the property.
     * @return <code>javax.swing.KeyStroke.getKeyStroke(value)</code>. Returns
     * null if the property is missing.
     */
    @Nullable
    default KeyCombination getKeyCombination(@NonNull String key) {
        KeyCombination ks = null;
        String s = getString(key);
        try {
            ks = (s == null || s.isEmpty()) ? null : KeyCombination.valueOf(translateKeyStrokeToKeyCombination(s));
        } catch (NoSuchElementException | StringIndexOutOfBoundsException e) {
            throw new InternalError(key + "=" + s, e);
        }
        return ks;
    }

    @NonNull Enumeration<String> getKeys();

    /**
     * Get a large image icon from the ResourceBundle for use on a
     * {@code JButton}.
     * <br>Convenience method .
     *
     * @param key       The key of the property. This method appends ".largeIcon" to
     *                  the key.
     * @param baseClass the base class used to retrieve the image resource
     * @return The value of the property. Returns null if the property is
     * missing.
     */
    @Nullable
    default Node getLargeIconProperty(String key, @NonNull Class<?> baseClass) {
        return ResourcesHelper.getIconProperty(this, key, ".largeIcon", baseClass);
    }

    @NonNull Locale getLocale();

    /**
     * Get a Mnemonic from the ResourceBundle.
     * <br>Convenience method.
     *
     * @param key The key of the property.
     * @return The first char of the value of the property. Returns '\0' if the
     * property is missing.
     */
    default char getMnemonic(@NonNull String key) {
        String s = getString(key);
        return (s == null || s.length() == 0) ? '\0' : s.charAt(0);
    }

    /**
     * Gets a char for a JavaBeans "mnemonic" property from the ResourceBundle.
     * <br>Convenience method.
     *
     * @param key The key of the property. This method appends ".mnemonic" to
     *            the key.
     * @return The first char of the value of the property. Returns '\0' if the
     * property is missing.
     */
    @Nullable
    default KeyCombination getMnemonicProperty(String key) {
        String s;
        try {
            s = getString(key + ".mnemonic");
        } catch (MissingResourceException e) {
            ResourcesHelper.LOG.warning("Warning ClasspathResources[" + getBaseName() + "] \"" + key + ".mnemonic\" not found.");
            s = null;
        }
        return (s == null || s.length() == 0) ? null : KeyCombination.valueOf(s);
    }

    /**
     * Get a small image icon from the ResourceBundle for use on a
     * {@code JMenuItem}.
     * <br>Convenience method .
     *
     * @param key       The key of the property. This method appends ".smallIcon" to
     *                  the key.
     * @param baseClass the base class used to retrieve the image resource
     * @return The value of the property. Returns null if the property is
     * missing.
     */
    @Nullable
    default Node getSmallIconProperty(String key, @NonNull Class<?> baseClass) {
        return ResourcesHelper.getIconProperty(this, key, ".smallIcon", baseClass);
    }

    @NonNull String getString(String s);

    /**
     * Get a String for a JavaBeans "text" property from the ResourceBundle.
     * <br>Convenience method.
     *
     * @param key The key of the property. This method appends ".text" to the
     *            key.
     * @return The ToolTip. Returns null if no tooltip is defined.
     */
    @Nullable
    default String getTextProperty(String key) {
        try {
            String value = getString(key + ".text");
            return value;
        } catch (MissingResourceException e) {
            ResourcesHelper.LOG.warning("Warning ClasspathResources[" + getBaseName() + "] \"" + key + ".text\" not found.");
            return null;
        }
    }

    /**
     * Get a String for a JavaBeans "toolTipText" property from the
     * ResourceBundle.
     * <br>Convenience method.
     *
     * @param key The key of the property. This method appends ".toolTipText" to
     *            the key.
     * @return The ToolTip. Returns null if no tooltip is defined.
     */
    @Nullable
    default String getToolTipTextProperty(String key) {
        try {
            String value = getString(key + ".toolTipText");
            return value;
        } catch (MissingResourceException e) {
            ResourcesHelper.LOG.warning("Resources[" + getBaseName() + "] \"" + key + ".toolTipText\" not found.");
            return null;
        }
    }

    ResourceBundle getWrappedBundle();

    @NonNull
    default String substitutePlaceholders(String key, @NonNull String value) throws MissingResourceException {

        // Substitute placeholders in the value
        for (int p1 = value.indexOf("${"); p1 != -1; p1 = value.indexOf("${")) {
            int p2 = value.indexOf('}', p1 + 2);
            if (p2 == -1) {
                break;
            }

            String placeholderKey = value.substring(p1 + 2, p2);
            String placeholderFormat;
            int p3 = placeholderKey.indexOf(',');
            if (p3 != -1) {
                placeholderFormat = placeholderKey.substring(p3 + 1);
                placeholderKey = placeholderKey.substring(0, p3);
            } else {
                placeholderFormat = "string";
            }
            ArrayList<String> fallbackKeys = new ArrayList<>();
            ResourcesHelper.generateFallbackKeys(placeholderKey, fallbackKeys);

            String placeholderValue = null;
            for (String fk : fallbackKeys) {
                try {
                    placeholderValue = getString(fk);
                    break;
                } catch (MissingResourceException e) {
                }
            }
            if (placeholderValue == null) {
                throw new MissingResourceException("Placeholder value for fallback keys \"" + fallbackKeys + "\" in key \"" + key + "\" not found in " + getBaseName(), getBaseName(), key);
            }

            // Do post-processing depending on placeholder format
            if ("accelerator".equals(placeholderFormat)) {
                // Localize the keywords shift, control, ctrl, meta, alt, altGraph
                StringBuilder b = new StringBuilder();
                for (String s : placeholderValue.split(" ")) {
                    if (ResourcesHelper.acceleratorKeys.contains(s)) {
                        b.append(getString("accelerator." + s));
                    } else {
                        b.append(s);
                    }
                }
                placeholderValue = b.toString();
            }

            // Insert placeholder value into value
            value = value.substring(0, p1) + placeholderValue + value.substring(p2 + 1);
        }

        return value;

    }

    /**
     * Translate a String defining a {@code javax.swing.KeyStroke} into a String
     * for {@code javafx.input.KeyCombination}.
     *
     * @param s The KeyStroke String
     * @return The KeyCombination String
     */
    @Nullable
    default String translateKeyStrokeToKeyCombination(@Nullable String s) {
        if (s != null) {
            s = s.replace("ctrl ", "Ctrl+");
            s = s.replace("meta ", "Meta+");
            s = s.replace("alt ", "Alt+");
            s = s.replace("shift ", "Shift+");
        }
        return s;
    }


}

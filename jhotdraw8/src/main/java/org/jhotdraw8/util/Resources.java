/* @(#)Resources.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util;

import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Objects;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Menu;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javax.annotation.Nonnull;
import org.jhotdraw8.app.action.Action;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.annotation.Nullable;

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
 * Property names in placeholders can contain modifiers. Modifiers are written
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
 * @version $Id$
 */
public class Resources extends ResourceBundle implements Serializable {
private final static Logger LOG = Logger.getLogger(Resources.class.getName());

    final static public String PARENT_RESOURCE_KEY = "$parent";

    private static final HashSet<String> acceleratorKeys = new HashSet<>(
            Arrays.asList(new String[]{
                    "shift", "control", "ctrl", "meta", "alt", "altGraph"
            }));
    /**
     * List of decoders. The first decoder which can decode a resource value is
     * will be used to convert the resource value to an object.
     */
    @Nonnull
    private static List<ResourceDecoder> decoders = new ArrayList<>();
    /**
     * The global verbose property.
     * FIXME use logging API instead
     */
    private static boolean isVerbose = false;
    /**
     * The global map of property name modifiers. The key of this map is the
     * name of the property name modifier, the value of this map is a fallback
     * chain.
     */
    @Nonnull
    private static HashMap<String, String[]> propertyNameModifiers = new HashMap<>();
    private static final long serialVersionUID = 1L;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        String os;
        if (osName.startsWith("mac os x")) {
            os = "mac";
        } else if (osName.startsWith("windows")) {
            os = "win";
        } else {
            os = "other";
        }
        propertyNameModifiers.put("os", new String[]{os, "default"});
    }

    /**
     * Adds a decoder.
     *
     * @param decoder the resource decoder
     */
    public static void addDecoder(ResourceDecoder decoder) {
        decoders.add(decoder);
    }

    /**
     * Get the appropriate ResourceBundle subclass.
     *
     * @param baseName the base name
     * @return the resource bundle
     * @see java.util.ResourceBundle
     */
    public static Resources getResources(@Nonnull String baseName)
            throws MissingResourceException {
        return getResources(baseName, LocaleUtil.getDefault());
    }

    /**
     * Get the appropriate ResourceBundle subclass.
     *
     * @param baseName the base name
     * @param locale the locale
     * @return the resource bundle
     * @see java.util.ResourceBundle
     */
    public static Resources getResources(@Nonnull String baseName, @Nonnull Locale locale)
            throws MissingResourceException {
        Resources r;
        r = new Resources(baseName, locale);
        return r;
    }

    public static boolean isVerbose() {
        return isVerbose;
    }

    public static void setVerbose(boolean newValue) {
        isVerbose = newValue;
    }

    /**
     * Puts a property name modifier along with a fallback chain.
     *
     * @param name The name of the modifier.
     * @param fallbackChain The fallback chain of the modifier.
     */
    public static void putPropertyNameModifier(String name, String... fallbackChain) {
        propertyNameModifiers.put(name, fallbackChain);
    }

    /**
     * Removes a decoder.
     *
     * @param decoder the resource decoder
     */
    public static void removeDecoder(ResourceDecoder decoder) {
        decoders.remove(decoder);
    }

    /**
     * Removes a property name modifier.
     *
     * @param name The name of the modifier.
     */
    public static void removePropertyNameModifier(String name) {
        propertyNameModifiers.remove(name);
    }
    /**
     * The base class
     */
    private Class<?> baseClass = getClass();
    /**
     * The base name of the resource bundle.
     */
    @Nonnull
    private final String baseName;
    /**
     * The locale.
     */
    @Nonnull
    private final Locale locale;

    /**
     * The parent resources object.
     */
    @Nullable
    private final Resources parent;
    /**
     * The wrapped resource bundle.
     */
    private transient ResourceBundle resource;

    /**
     * Creates a new Resources object which wraps the provided resource bundle.
     *
     * @param baseName the base name
     * @param locale the locale
     */
    public Resources(@Nonnull String baseName, @Nonnull Locale locale) {
        this.locale = locale;
        this.baseName = baseName;
        this.resource = ResourceBundle.getBundle(baseName, locale);

        Resources potentialParent = null;
        try {
            String parentBaseName = this.resource.getString(PARENT_RESOURCE_KEY);
            if (parentBaseName != null && !Objects.equals(baseName, parentBaseName)) {
                potentialParent = new Resources(parentBaseName, locale);
            }
                // FIXME use logger for this
            if (potentialParent == null) {
                System.err.println("Can't find parent resource bundle. =" + PARENT_RESOURCE_KEY + "=" + parentBaseName);
            } else {
                if (isVerbose) {
                   // System.out.println("Found parent resource bundle. " + PARENT_RESOURCE_KEY + "=" + parentBaseName);
                }
            }
        } catch (MissingResourceException e) {
        }
        this.parent = potentialParent;
    }

    public void configureAction(@Nonnull Action action, String argument) {
        configureAction(action, argument, getBaseClass());
    }

    public void configureAction(@Nonnull Action action, String argument, @Nonnull Class<?> baseClass) {
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

    public void configureButton(@Nonnull ButtonBase button, String argument) {
        configureButton(button, argument, getBaseClass());
    }

    public void configureButton(@Nonnull ButtonBase button, String argument, @Nonnull Class<?> baseClass) {
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
     * @param menu the menu
     * @param argument the argument
     */
    public void configureMenu(@Nonnull Menu menu, String argument) {
        menu.setText(getTextProperty(argument));
        menu.setText(getTextProperty(argument));
        menu.setAccelerator(getAcceleratorProperty(argument));
        menu.setGraphic(getSmallIconProperty(argument, baseClass));
    }

    public void configureToolBarButton(@Nonnull ButtonBase button, String argument) {
        configureToolBarButton(button, argument, getBaseClass());
    }

    public void configureToolBarButton(@Nonnull ButtonBase button, String argument, @Nonnull Class<?> baseClass) {
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

    public boolean containsKey(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (resource.containsKey(key)) {
            return true;
        }
        if (parent != null) {
            return parent.containsKey(key);
        }
        if (isVerbose) {
            LOG.warning("Can't find resource for bundle " + baseName + " key not found: " + key);
        }
        return false;
    }

    /**
     * Returns a formatted string using java.util.Formatter().
     *
     * @param key the key
     * @param arguments the arguments
     * @return formatted String
     */
    @Nonnull
    public String format(@Nonnull String key, Object... arguments) {
        //return String.format(resource.getLocale(), getString(key), arguments);
        return new Formatter(resource.getLocale()).format(getString(key), arguments).toString();
    }

    /**
     * Generates fallback keys by processing all property name modifiers in the
     * key.
     */
    private void generateFallbackKeys(String key, @Nonnull ArrayList<String> fallbackKeys) {
        int p1 = key.indexOf("[$");
        if (p1 == -1) {
            fallbackKeys.add(key);
        } else {
            int p2 = key.indexOf(']', p1 + 2);
            if (p2 == -1) {
                return;
            }
            String modifierKey = key.substring(p1 + 2, p2);
            String[] modifierValues = propertyNameModifiers.get(modifierKey);
            if (modifierValues == null) {
                modifierValues = new String[]{"default"};
            }
            for (String mv : modifierValues) {
                generateFallbackKeys(key.substring(0, p1) + mv + key.substring(p2 + 1), fallbackKeys);
            }
        }
    }

    /**
     * Gets a KeyStroke for a JavaBeans "accelerator" property from the
     * ResourceBundle.
     * <BR>Convenience method.
     *
     * @param key The key of the property. This method adds ".accelerator" to
     * the key.
     * @return <code>javax.swing.KeyStroke.getKeyStroke(value)</code>. Returns
     * null if the property is missing.
     */
    @javax.annotation.Nullable
    public KeyCombination getAcceleratorProperty(String key) {
        return getKeyCombination(key + ".accelerator");
    }

    public Class<?> getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(Class<?> baseClass) {
        this.baseClass = baseClass;
    }

    /**
     * Returns a formatted string using javax.text.MessageFormat.
     *
     * @param key the key
     * @param arguments the arguments
     * @return formatted String
     */
    @Nonnull
    public String getFormatted(@Nonnull String key, Object... arguments) {
        return MessageFormat.format(getString(key), arguments);
    }

    private Node getIconProperty(String key, String suffix, @Nonnull Class<?> baseClass) {
        try {
            String rsrcName = getString(key + suffix);
            if ("".equals(rsrcName) || rsrcName == null) {
                return null;
            }

            for (ResourceDecoder d : decoders) {
                if (d.canDecodeValue(key, rsrcName, Node.class)) {
                    return d.decode(key, rsrcName, Node.class, baseClass);
                }
            }

            URL url = baseClass.getResource(rsrcName);
            if (isVerbose && url == null) {
                System.err.println("Warning Resources[" + baseName + "].getIconProperty \"" + key + suffix + "\" resource:" + rsrcName + " not found.");
            }
            return (url == null) ? null : new ImageView(url.toString());
        } catch (MissingResourceException e) {
            if (isVerbose) {
                System.err.println("Warning Resources[" + baseName + "].getIconProperty \"" + key + suffix + "\" not found.");
                //e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Get an Integer from the ResourceBundle.
     * <br>Convenience method to save casting.
     *
     * @param key The key of the property.
     * @return The value of the property. Returns -1 if the property is missing.
     */
    @Nonnull
    public Integer getInteger(@Nonnull String key) {
        try {
            return Integer.valueOf(getString(key));
        } catch (MissingResourceException e) {
            if (isVerbose) {
                System.err.println("Warning Resources[" + baseName + "] \"" + key + "\" not found.");
                //e.printStackTrace();
            }
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
    @javax.annotation.Nullable
    public KeyCombination getKeyCombination(@Nonnull String key) {
        KeyCombination ks = null;
        String s = getString(key);
        try {
            ks = (s == null || s.isEmpty()) ? (KeyCombination) null : KeyCombination.valueOf(translateKeyStrokeToKeyCombination(s));
        } catch (@Nonnull NoSuchElementException | StringIndexOutOfBoundsException e) {
            throw new InternalError(key + "=" + s, e);
        }
        return ks;
    }

    @Nonnull
    @Override
    public Enumeration<String> getKeys() {
        return resource.getKeys();
    }

    /**
     * Get a large image icon from the ResourceBundle for use on a
     * {@code JButton}.
     * <br>Convenience method .
     *
     * @param key The key of the property. This method appends ".largeIcon" to
     * the key.
     * @param baseClass the base class used to retrieve the image resource
     * @return The value of the property. Returns null if the property is
     * missing.
     */
    @javax.annotation.Nullable
    public Node getLargeIconProperty(String key, @Nonnull Class<?> baseClass) {
        return getIconProperty(key, ".largeIcon", baseClass);
    }

    /**
     * Get a Mnemonic from the ResourceBundle.
     * <br>Convenience method.
     *
     * @param key The key of the property.
     * @return The first char of the value of the property. Returns '\0' if the
     * property is missing.
     */
    public char getMnemonic(@Nonnull String key) {
        String s = getString(key);
        return (s == null || s.length() == 0) ? '\0' : s.charAt(0);
    }

    /**
     * Gets a char for a JavaBeans "mnemonic" property from the ResourceBundle.
     * <br>Convenience method.
     *
     * @param key The key of the property. This method appends ".mnemonic" to
     * the key.
     * @return The first char of the value of the property. Returns '\0' if the
     * property is missing.
     */
    @javax.annotation.Nullable
    public KeyCombination getMnemonicProperty(String key) {
        String s;
        try {
            s = getString(key + ".mnemonic");
        } catch (MissingResourceException e) {
            if (isVerbose) {
                System.err.println("Warning Resources[" + baseName + "] \"" + key + ".mnemonic\" not found.");
                //e.printStackTrace();
            }
            s = null;
        }
        return (s == null || s.length() == 0) ? null : KeyCombination.valueOf(s);
    }

    /**
     * Get a small image icon from the ResourceBundle for use on a
     * {@code JMenuItem}.
     * <br>Convenience method .
     *
     * @param key The key of the property. This method appends ".smallIcon" to
     * the key.
     * @param baseClass the base class used to retrieve the image resource
     * @return The value of the property. Returns null if the property is
     * missing.
     */
    @javax.annotation.Nullable
    public Node getSmallIconProperty(String key, @Nonnull Class<?> baseClass) {
        return getIconProperty(key, ".smallIcon", baseClass);
    }

    /**
     * Get a String for a JavaBeans "text" property from the ResourceBundle.
     * <br>Convenience method.
     *
     * @param key The key of the property. This method appends ".text" to the
     * key.
     * @return The ToolTip. Returns null if no tooltip is defined.
     */
    @javax.annotation.Nullable
    public String getTextProperty(String key) {
        try {
            String value = getString(key + ".text");
            return value;
        } catch (MissingResourceException e) {
            if (isVerbose) {
                System.err.println("Warning Resources[" + baseName + "] \"" + key + ".text\" not found.");
                //e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Get a String for a JavaBeans "toolTipText" property from the
     * ResourceBundle.
     * <br>Convenience method.
     *
     * @param key The key of the property. This method appends ".toolTipText" to
     * the key.
     * @return The ToolTip. Returns null if no tooltip is defined.
     */
    @javax.annotation.Nullable
    public String getToolTipTextProperty(String key) {
        try {
            String value = getString(key + ".toolTipText");
            return value;
        } catch (MissingResourceException e) {
            if (isVerbose) {
                System.err.println("Warning Resources[" + baseName + "] \"" + key + ".toolTipText\" not found.");
                //e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Returns the wrapped resource bundle.
     *
     * @return The wrapped resource bundle.
     */
    public ResourceBundle getWrappedBundle() {
        return resource;
    }

    @javax.annotation.Nullable
    @Override
    protected Object handleGetObject(String key) {
        Object obj = handleGetObjectRecursively(key);
        if (obj == null) {
            obj = "";
            LOG.warning("Can't find resource for bundle " + baseName + ", key " + key);
        }

        if (obj instanceof String) {
            obj = substitutePlaceholders(key, (String) obj);
        }
        return obj;
    }

    @javax.annotation.Nullable
    protected Object handleGetObjectRecursively(@Nonnull String key) {
        Object obj = null;
        try {
            obj = resource.getObject(key);
        } catch (MissingResourceException e) {
            if (parent != null) {
                return parent.handleGetObjectRecursively(key);
            }
        }
        return obj;
    }

    @Nonnull
    private String substitutePlaceholders(String key, String value) throws MissingResourceException {

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
            generateFallbackKeys(placeholderKey, fallbackKeys);

            String placeholderValue = null;
            for (String fk : fallbackKeys) {
                try {
                    placeholderValue = getString(fk);
                    break;
                } catch (MissingResourceException e) {
                }
            }
            if (placeholderValue == null) {
                throw new MissingResourceException("Placeholder value for fallback keys \"" + fallbackKeys + "\" in key \"" + key + "\" not found in " + baseName, baseName, key);
            }

            // Do post-processing depending on placeholder format
            if ("accelerator".equals(placeholderFormat)) {
                // Localize the keywords shift, control, ctrl, meta, alt, altGraph
                StringBuilder b = new StringBuilder();
                for (String s : placeholderValue.split(" ")) {
                    if (acceleratorKeys.contains(s)) {
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

    @Nonnull
    @Override
    public String toString() {
        return "Resources" + "[" + baseName + "]";
    }

    /**
     * Translate a String defining a {@code javax.swing.KeyStroke} into a String
     * for {@code javafx.input.KeyCombination}.
     *
     * @param s The KeyStroke String
     * @return The KeyCombination String
     */
    @javax.annotation.Nullable
    protected String translateKeyStrokeToKeyCombination(@javax.annotation.Nullable String s) {
        if (s != null) {
            s = s.replace("ctrl ", "Ctrl+");
            s = s.replace("meta ", "Meta+");
            s = s.replace("alt ", "Alt+");
            s = s.replace("shift ", "Shift+");
        }
        return s;
    }

}

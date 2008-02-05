/* * @(#)ResourceBundleUtil.java  1.7.1  2006-12-11 * * Copyright (c) 1996-2006 by the original authors of JHotDraw * and all its contributors. * All rights reserved. * * The copyright of this software is owned by the authors and   * contributors of the JHotDraw project ("the copyright holders").   * You may not use, copy or modify this software, except in   * accordance with the license agreement you entered into with   * the copyright holders. For details see accompanying license terms.  */package org.jhotdraw.util;import java.beans.PropertyChangeListener;import java.util.*;import javax.swing.*;import java.text.*;import java.net.*;import java.io.*;/** * This is a convenience wrapper for accessing resources stored in a ResourceBundle. * * @author  Werner Randelshofer, Staldenmattweg 2, CH-6405 Immensee, Switzerland * @version 1.7.1 2006-12-11 Method configureToolBarButton sets the text of the *                           button when no image is provided. * <br>     1.7 2006-05-06 Suffixes changed. Redirection via LocaleUtil added. * <br>     1.6.1 2006-04-12 Method getBundle() added. * <br>     1.6 2006-03-15  Method setBaseClass/getBaseClass added. * <br>     1.5 2006-02-15   Methods getInteger, getLAFBundle, configureButton *                            and getTip() added. * <br>      1.4 2005-01-04   Methods configureAction and configureMenu added. * <br>      1.3 2001-10-10   The default resource name changed from 'name_Metal' *                            to 'name'. * <br>      1.2 2001-07-23   Adaptation to JDK 1.3 in progress. * <br>      1.0 2000-06-10   Created. */public class ResourceBundleUtil {    /** The wrapped resource bundle. */    private ResourceBundle resource;    private Class baseClass = getClass();        /**     * Creates a new ResouceBundleUtil which wraps     * the provided resource bundle.     */    public ResourceBundleUtil(ResourceBundle r) {        resource = r;    }        public ResourceBundle getBundle() {        return resource;    }        /**     * Get a String from the ResourceBundle.     * <br>Convenience method to save casting.     *     * @param key The key of the property.     * @return The value of the property. Returns the key     *          if the property is missing.     */    public String getString(String key) {        try {            return resource.getString(key);        } catch (MissingResourceException e) {            return key;        }    }    /**     * Get an Integer from the ResourceBundle.     * <br>Convenience method to save casting.     *     * @param key The key of the property.     * @return The value of the property. Returns -1     *          if the property is missing.     */    public Integer getInteger(String key) {        try {            return Integer.valueOf(resource.getString(key));        } catch (MissingResourceException e) {            return new Integer(-1);        }    }    /**     * Get an image icon from the ResourceBundle.     * <br>Convenience method .     *     * @param key The key of the property. This method appends ".icon" to the key.     * @return The value of the property. Returns null     *          if the property is missing.     */    public ImageIcon getImageIcon(String key, Class baseClass) {        try {                                    String rsrcName = resource.getString(key+".icon");                        if (rsrcName.equals("")) {                return null;            }            if (! rsrcName.startsWith("/")) {                String imageDir;                try {                    imageDir = resource.getString("$imageDir");                    if (! imageDir.endsWith("/")) {                        imageDir = imageDir+"/";                    }                } catch (MissingResourceException e) {                    imageDir = "";                }                rsrcName = imageDir+rsrcName;            }            URL url = baseClass.getResource(rsrcName);            /*            try {                File f = new File(url.toURI());                if (! f.exists()) {            System.out.println(f+" does not exist!");            }            } catch (Throwable t) {                t.printStackTrace();            }*/            return (url == null) ? null : new ImageIcon(url);        } catch (MissingResourceException e) {            return null;        }    }        /**     * Get a Mnemonic from the ResourceBundle.     * <br>Convenience method.     *     * @param key The key of the property.     * @return The first char of the value of the property.     *          Returns '\0' if the property is missing.     */    public char getMnemonic(String key) {        String s = resource.getString(key);        return (s == null || s.length() == 0) ? '\0' : s.charAt(0);    }    /**     * Get a Mnemonic from the ResourceBundle.     * <br>Convenience method.     *     * @param key The key of the property. This method appends ".mnem" to the key.     * @return The first char of the value of the property.     *          Returns '\0' if the property is missing.     */    public char getMnem(String key) {        String s;        try {            s = resource.getString(key+".mnem");        } catch (MissingResourceException e) {            s = null;        }        return (s == null || s.length() == 0) ? '\0' : s.charAt(0);    }    /**     * Get a Mnemonic from the ResourceBundle.     * <br>Convenience method.     *     * @param key The key of the property. This method appends ".tip" to the key.     * @return The ToolTip. Returns null if no tooltip is defined.     */    public String getTip(String key) {        try {            return resource.getString(key+".tip");        } catch (MissingResourceException e) {            return null;        }            }        /**     * Get a KeyStroke from the ResourceBundle.     * <BR>Convenience method.     *     * @param key The key of the property.     * @return <code>javax.swing.KeyStroke.getKeyStroke(value)</code>.     *          Returns null if the property is missing.     */    public KeyStroke getKeyStroke(String key) {        KeyStroke ks = null;        try {            String s = resource.getString(key);            ks = (s == null) ? (KeyStroke) null : KeyStroke.getKeyStroke(s);        } catch (NoSuchElementException e) {        }        return ks;    }    /**     * Get a KeyStroke from the ResourceBundle.     * <BR>Convenience method.     *     * @param key The key of the property. This method adds ".acc" to the key.     * @return <code>javax.swing.KeyStroke.getKeyStroke(value)</code>.     *          Returns null if the property is missing.     */    public KeyStroke getAcc(String key) {        KeyStroke ks = null;        try {            String s = resource.getString(key+".acc");            ks = (s == null) ? (KeyStroke) null : KeyStroke.getKeyStroke(s);        } catch (MissingResourceException e) {        } catch (NoSuchElementException e) {        }        return ks;    }        public String getFormatted(String key, String argument) {        return MessageFormat.format(resource.getString(key), new Object[] {argument});    }    public String getFormatted(String key, Object... arguments) {        return MessageFormat.format(resource.getString(key), arguments);    }            /**     * Get the appropriate ResourceBundle subclass.     * The baseName is extended by the Swing Look and Feel ID     * and by the Locale code.     *     * The default Look and Feel ID is Metal.     *     * @see java.util.ResourceBundle     */    public static ResourceBundleUtil getLAFBundle(String baseName)    throws MissingResourceException {        return getLAFBundle(baseName, LocaleUtil.getDefault());        /*        ResourceBundleUtil r;        try {            r = new ResourceBundleUtil(                    ResourceBundle.getBundle(baseName + "_" + UIManager.getLookAndFeel().getID()                    )                    );        } catch (MissingResourceException e) {            r = new ResourceBundleUtil(                    ResourceBundle.getBundle(baseName)                    );        }        return r;*/    }        public void setBaseClass(Class baseClass) {        this.baseClass = baseClass;    }    public Class getBaseClass() {     return baseClass;    }        public void configureAction(Action action, String argument) {        configureAction(action, argument, getBaseClass());    }    public void configureAction(Action action, String argument, Class baseClass) {        action.putValue(Action.NAME, getString(argument));        action.putValue(Action.ACCELERATOR_KEY, getAcc(argument));        action.putValue(Action.MNEMONIC_KEY, new Integer(getMnem(argument)));        action.putValue(Action.SMALL_ICON, getImageIcon(argument, baseClass));    }    public void configureButton(AbstractButton button, String argument) {        configureButton(button, argument, getBaseClass());    }    public void configureButton(AbstractButton button, String argument, Class baseClass) {        button.setText(getString(argument));        //button.setACCELERATOR_KEY, getAcc(argument));        //action.putValue(Action.MNEMONIC_KEY, new Integer(getMnem(argument)));        button.setIcon(getImageIcon(argument, baseClass));        button.setToolTipText(getTip(argument));    }    public void configureToolBarButton(AbstractButton button, String argument) {        configureToolBarButton(button, argument, getBaseClass());    }    public void configureToolBarButton(AbstractButton button, String argument, Class baseClass) {        Icon icon = getImageIcon(argument, baseClass);        if (icon != null) {        button.setIcon(getImageIcon(argument, baseClass));        button.setText(null);        } else {            button.setIcon(null);            button.setText(getString(argument));        }        button.setToolTipText(getTip(argument));    }            public void configureMenu(JMenuItem menu, String argument) {        menu.setText(getString(argument));        if (! (menu instanceof JMenu)) {            menu.setAccelerator(getAcc(argument));        }        menu.setMnemonic(getMnem(argument));        menu.setIcon(getImageIcon(argument, baseClass));    }    public JMenuItem createMenuItem(Action a, String baseName) {        JMenuItem mi = new JMenuItem();        mi.setAction(a);         configureMenu(mi, baseName);	return mi;    }    /**     * Get the appropriate ResourceBundle subclass.     * The ID of the current Look and Feel is prepended to the locale attributes.     *     * @see java.util.ResourceBundle     */    public static ResourceBundleUtil getLAFBundle(String baseName, Locale locale)    throws MissingResourceException {        ResourceBundleUtil r;        /*        Locale lafLocale = new Locale(locale.getLanguage(), locale.getCountry(), UIManager.getLookAndFeel().getID());            r = new ResourceBundleUtil(                    ResourceBundle.getBundle(baseName, lafLocale)                    );        */        try {            r = new ResourceBundleUtil(                    ResourceBundle.getBundle(baseName+'_'+UIManager.getLookAndFeel().getID(), locale                    )                    );        } catch (MissingResourceException e) {            r = new ResourceBundleUtil(                    ResourceBundle.getBundle(baseName, locale)                    );        }        return r;    }        public String toString() {        return super.toString()+"["+resource+"]";    }}
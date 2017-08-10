/* @(#)package-info.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 *
 * @author Werner Randelshofer
 * @version $Id$
*/

/**
 * Provides general purpose graphical user interface classes leveraging the
 * javax.swing package.
 * 
 * <hr>
 * <b>Features</b>
 *
 * <p><em>Activity monitoring</em><br>
 * Background activities can be monitored using the {@link org.jhotdraw.gui.ActivityModel} class.
 * A activity model can have an owner. This allows to associate activities to
 * different views of an application.
 * All current activity models can be viewed in the {@link org.jhotdraw.gui.JActivityWindow}.
 * A {@code JActivityIndicator} can be used to indicate that one or more 
 * activity is active. {@code JActivityIndicator} can either indicate all
 * running activities, or only those belonging to a specific owner.
 * </p>
 *
 */
@DefaultAnnotation(NonNull.class)
package org.jhotdraw.gui;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

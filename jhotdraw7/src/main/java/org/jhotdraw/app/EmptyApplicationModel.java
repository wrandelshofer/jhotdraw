/* @(#)EmptyApplicationModel.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */
package org.jhotdraw.app;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.*;
import javax.swing.*;

/**
 * An {@link ApplicationModel} which neither creates {@code Action}s,
 * nor overrides the menu bars, nor creates tool bars.
 * <p>
 * The {@code createActionMap} method of this model returns an empty ActionMap.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class EmptyApplicationModel
        extends AbstractApplicationModel {
    private static final long serialVersionUID = 1L;

    /** Returns an empty ActionMap. */
    @Override
    public ActionMap createActionMap(Application a, @Nullable View v) {
        return new ActionMap();
    }

    /** Returns an empty unmodifiable list. */
    @Override
    public List<JToolBar> createToolBars(Application app, @Nullable View v) {
        return Collections.emptyList();
    }

    @Override
    public MenuBuilder getMenuBuilder() {
        return new EmptyMenuBuilder();
    }



}

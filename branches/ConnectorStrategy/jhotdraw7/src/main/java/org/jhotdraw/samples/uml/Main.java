/*
 * @(#)Main.java
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and
 * contributors of the JHotDraw project ("the copyright holders").
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * the copyright holders. For details see accompanying license terms.
 */

package org.jhotdraw.samples.uml;

import org.jhotdraw.app.*;
/**
 * Main.
 *
 * @version $Id: Main.java,v 1.1 2009/10/18 20:41:48 cfm1 Exp $
 */
public class Main {

    /** Creates a new instance. */
    public static void main(String[] args) {
         Application app;
         String os = System.getProperty("os.name").toLowerCase();
         if (os.startsWith("mac")) {
             app = new OSXApplication();
         } else if (os.startsWith("win")) {
             //  app = new DefaultMDIApplication();
             app = new SDIApplication();
         } else {
             app = new SDIApplication();
         }

        UMLApplicationModel model = new UMLApplicationModel();
        model.setName("JHotDraw UML Sample Application");
        model.setVersion(Main.class.getPackage().getImplementationVersion());
        model.setCopyright("Copyright 2006-2009 (c) by the authors of JHotDraw\n" +
                "This software is licensed under LGPL or Creative Commons 3.0 BY");
        model.setViewClassName("org.jhotdraw.samples.uml.UMLView");
        app.setModel(model);
        app.launch(args);
    }

}

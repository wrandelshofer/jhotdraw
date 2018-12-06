/* @(#)OSXAdapter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 *
 * This class has been derived from class OSXAdapter 2.0 by Apple Inc.
 * http://developer.apple.com/mac/library/samplecode/OSXAdapter/listing3.html
 *
 * Original disclaimer:
 *
 * File: OSXAdapter.java
 *
 * Abstract: Hooks existing preferences/about/quit functionality from an
 * existing Java app into handlers for the Mac OS X application menu.
 * Uses a Proxy object to dynamically implement the
 * com.apple.eawt.ApplicationListener interface and register it with the
 * com.apple.eawt.Application object.  This allows the complete project
 * to be both built and run on any platform without any stubs or
 * placeholders. Useful for developers looking to implement Mac OS X
 * features while supporting multiple platforms with minimal impact.
 *
 * Version: 2.0
 *
 * Disclaimer: IMPORTANT:  This Apple software is supplied to you by
 * Apple Inc. ("Apple") in consideration of your agreement to the
 * following terms, and your use, installation, modification or
 * redistribution of this Apple software constitutes acceptance of these
 * terms.  If you do not agree with these terms, please do not use,
 * install, modify or redistribute this Apple software.
 *
 * In consideration of your agreement to abide by the following terms, and
 * subject to these terms, Apple grants you a personal, non-exclusive
 * license, under Apple's copyrights in this original Apple software (the
 * "Apple Software"), to use, reproduce, modify and redistribute the Apple
 * Software, with or without modifications, in source and/or binary forms;
 * provided that if you redistribute the Apple Software in its entirety and
 * without modifications, you must retain this notice and the following
 * text and disclaimers in all such redistributions of the Apple Software.
 * Neither the name, trademarks, service marks or logos of Apple Inc.
 * may be used to endorse or promote products derived from the Apple
 * Software without specific prior written permission from Apple.  Except
 * as expressly stated in this notice, no other rights or licenses, express
 * or implied, are granted by Apple herein, including but not limited to
 * any patent rights that may be infringed by your derivative works or by
 * other works in which the Apple Software may be incorporated.
 *
 * The Apple Software is provided by Apple on an "AS IS" basis.  APPLE
 * MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND
 * OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 *
 * IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 * MODIFICATION AND/OR DISTRIBUTION OF THE APPLE SOFTWARE, HOWEVER CAUSED
 * AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 * STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * Copyright © Apple, Inc., All Rights Reserved
 */
package org.jhotdraw.app.osx;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * {@code OSXAdapter} uses a Proxy object to dynamically implement the
 * {@code com.apple.eawt.ApplicationListener} interface and register it with the
 * {@code com.apple.eawt.Application object}. This allows the complete project
 * to be both built and run on any platform without any stubs or
 * placeholders. Useful for developers looking to implement Mac OS X
 * features while supporting multiple platforms with minimal impact.
 * <p>
 * This class has been derived from <a
 * href="http://developer.apple.com/mac/library/samplecode/OSXAdapter/listing3.html"
 * >OSXAdapter 2.0 © Apple Inc., All Rights Rserved</a>.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class OSXAdapter {

    /**
     * The action listener will be called when the Quit menu item is selected
     * from the application menu.
     */
    public static void setQuitHandler(ActionListener aboutHandler) {
        if (aboutHandler == null) {
            Desktop.getDesktop().setQuitHandler(null);
        } else {
            Desktop.getDesktop().setQuitHandler((quitEvent,quitResponse) -> {
                quitResponse.cancelQuit();
                aboutHandler.actionPerformed(new ActionEvent(quitEvent.getSource(), ActionEvent.ACTION_PERFORMED, ""));
            });
        }
    }

    /**
     * The action listener will be called when the user selects the About item
     * in the application menu.
     */
    public static void setAboutHandler(ActionListener aboutHandler) {
        if (aboutHandler == null) {
            Desktop.getDesktop().setAboutHandler(null);
        } else {
            Desktop.getDesktop().setAboutHandler(aboutEvent -> {
                aboutHandler.actionPerformed(new ActionEvent(aboutEvent.getSource(), ActionEvent.ACTION_PERFORMED, ""));
            });
        }
    }

    /**
     * Pass this method an {@code ActionListener} equipped to
     * display application options.
     * They will be called when the Preferences menu item is selected from the
     * application menu.
     */
    public static void setPreferencesHandler(ActionListener prefsHandler) {
        if (prefsHandler == null) {
            Desktop.getDesktop().setPreferencesHandler(null);
        } else {
            Desktop.getDesktop().setPreferencesHandler(prefsEvent -> {
                prefsHandler.actionPerformed(new ActionEvent(prefsEvent.getSource(), ActionEvent.ACTION_PERFORMED, ""));
            });
        }
    }

    /**
     * Pass this method an {@code ActionListener} equipped to
     * handle document events from the Finder.
     * Documents are registered with the Finder via the
     * CFBundleDocumentTypes dictionary in the application bundle's Info.plist.
     * <p>
     * The filename is passed as the {@code actionCommand}.
     */
    public static void setOpenFileHandler(ActionListener fileHandler) {
        if (fileHandler == null) {
            Desktop.getDesktop().setOpenFileHandler(null);
        } else {
            Desktop.getDesktop().setOpenFileHandler(openFileEvent -> {
                for (File file:openFileEvent.getFiles()) {
                    fileHandler.actionPerformed(new ActionEvent(openFileEvent.getSource(), ActionEvent.ACTION_PERFORMED, file.getPath()));
                }
            });
        }
    }

    /**
     * Pass this method an {@code ActionListener} equipped to
     * handle document events from the Finder.
     * Documents are registered with the Finder via the
     * CFBundleDocumentTypes dictionary in the application bundle's Info.plist.
     * <p>
     * The filename is passed as the {@code actionCommand}.
     */
    public static void setPrintFileHandler(ActionListener fileHandler) {
        if (fileHandler == null) {
            Desktop.getDesktop().setPrintFileHandler(null);
        } else {
            Desktop.getDesktop().setPrintFileHandler(printEvent -> {
                for (File file:printEvent.getFiles()) {
                    fileHandler.actionPerformed(new ActionEvent(printEvent.getSource(), ActionEvent.ACTION_PERFORMED, file.getPath()));
                }
            });
        }
    }
}

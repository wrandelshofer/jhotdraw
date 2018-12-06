/* @(#)ImageTransferable.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.gui.datatransfer;

import org.jhotdraw.util.Images;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A Transferable with an Image as its transfer class.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ImageTransferable implements Transferable {
    private Image image;
    
    public static final DataFlavor IMAGE_PNG_FLAVOR;
    static {
        try {
            IMAGE_PNG_FLAVOR = new DataFlavor("image/png");
        } catch (Exception e) {
            InternalError error = new InternalError("Unable to crate image/png data flavor");
            error.initCause(e);
            throw error;
        }
    }
    
    /** Creates a new instance. */
    public ImageTransferable(Image image) {
        this.image = image;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.imageFlavor) ||
                flavor.equals(IMAGE_PNG_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        /*if (! isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }*/
        if (flavor.equals(DataFlavor.imageFlavor)) {
        return image;
        } else if (flavor.equals(IMAGE_PNG_FLAVOR)) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    ImageIO.write(Images.toBufferedImage(image), "PNG", buf);
                    return new ByteArrayInputStream(buf.toByteArray());

        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DataFlavor.imageFlavor, IMAGE_PNG_FLAVOR };
    }
    
}

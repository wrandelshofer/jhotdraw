/* @(#)EditorColorIcon.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.net.URL;
/**
 * EditorColorIcon draws a shape with the color of the specified default
 * attribute of the DrawingEditor onto the icon.
 * <p>
 * The behavior for choosing the drawn color matches with
 * {@link EditorColorChooserAction }.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EditorColorIcon extends javax.swing.ImageIcon {
    private static final long serialVersionUID = 1L;
    private DrawingEditor editor;
    private AttributeKey<Color> key;
    private Shape colorShape;
    
    /** Creates a new instance. 
     * @param editor The drawing editor.
     * @param key The key of the default attribute
     * @param imageLocation the icon image
     * @param colorShape The shape to be drawn with the color of the default
     * attribute.
     */
    public EditorColorIcon(
            DrawingEditor editor,
            AttributeKey<Color> key,
            URL imageLocation,
            Shape colorShape) {
        super(imageLocation);
        this.editor = editor;
        this.key = key;
        this.colorShape = colorShape;
    }
    public EditorColorIcon(
            DrawingEditor editor, 
            AttributeKey<Color> key, 
            Image image, 
            Shape colorShape) {
        super(image);
        this.editor = editor;
        this.key = key;
        this.colorShape = colorShape;
    }
    
    @Override
    public void paintIcon(java.awt.Component c, java.awt.Graphics gr, int x, int y) {
        Graphics2D g = (Graphics2D) gr;
        super.paintIcon(c, g, x, y);
        Color color = editor.getDefaultAttribute(key);
        if (color != null) {
            g.setColor(color);
            g.translate(x, y);
            g.fill(colorShape);
            g.translate(-x, -y);
        }
    }    
}

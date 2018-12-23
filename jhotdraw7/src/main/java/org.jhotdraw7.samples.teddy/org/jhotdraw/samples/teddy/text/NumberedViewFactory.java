/* @(#)NumberedViewFactory.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 *
 * Original code (c) Stanislav Lapitsky
 * http://www.developer.com/java/other/article.php/3318421
 */

package org.jhotdraw.samples.teddy.text;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
/**
 * NumberedViewFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NumberedViewFactory implements ViewFactory {
    private boolean isLineNumbersVisible;
    
    public void setLineNumbersVisible(boolean newValue) {
        boolean oldValue = isLineNumbersVisible;
        isLineNumbersVisible = newValue;
    }
    public boolean isLineNumbersVisible() {
        return isLineNumbersVisible;
    }
    
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null)
            if (kind.equals(AbstractDocument.ContentElementName)) {
            return new LabelView(elem);
            } else if (kind.equals(AbstractDocument.
                ParagraphElementName)) {
           // if (isLineNumbersVisible()) {
                return new NumberedParagraphView(elem, this);
           // } else {
               // return new ParagraphView(elem);
            //}
            } else if (kind.equals(AbstractDocument.
                SectionElementName)) {
            return new BoxView(elem, View.Y_AXIS);
            } else if (kind.equals(StyleConstants.
                ComponentElementName)) {
            return new ComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
            return new IconView(elem);
            }
        // default to text display
        return new LabelView(elem);
    }
}

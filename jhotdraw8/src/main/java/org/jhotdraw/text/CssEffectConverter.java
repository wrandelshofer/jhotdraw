/* @(#)CssEffectConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.effect.Effect;
import org.jhotdraw.draw.io.IdFactory;

/**
 * CssEffectConverter.
 * @author Werner Randelshofer
 */
public class CssEffectConverter implements Converter<Effect> {

    @Override
    public void toString(Appendable out, IdFactory idFactory, Effect value) throws IOException {
        out.append("null");
    }

    @Override
    public Effect fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        String text=in.toString();
        in.position(in.limit());
return null;
    }

    @Override
    public Effect getDefaultValue() {
        return null;
    }

}

package org.jhotdraw8.text;

import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.function.Consumer;

public class CssConverterConverterAdapter<T> implements Converter<T> {
    private final CssConverter<T> conv;

    public CssConverterConverterAdapter(CssConverter<T> conv) {
        this.conv = conv;
    }

    @Nullable
    @Override
    public T fromString(@Nullable CharBuffer in, @Nullable IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(in);
        return conv.parse(tt,idFactory);
    }

    @Nullable
    @Override
    public T getDefaultValue() {
        return null;
    }

    @Override
    public void toString(Appendable out, @Nullable IdFactory idFactory, @Nullable T value) throws IOException {
        Consumer<Token> consumer = token -> {
            try {
                out.append(token.fromToken());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
        try {
        conv.produceTokens(value,idFactory,consumer);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    @Nullable
    @Override
    public String getHelpText() {
        return conv.getHelpText();
    }
}

package org.jhotdraw8.css;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class CssPrettyPrinter implements Appendable {
    private enum Syntax {
        STYLESHEET, SELECTOR, DECLARATION_KEY, DECLARATION_VALUE, ROUND_BLOCK, SQUARE_BLOCK, CURLY_BLOCK
    }

    private Deque<Syntax> stack = new ArrayDeque<>();
    int indentation = 0;
    private String indenter = "\t";
    private Appendable w;
    private boolean mustIndent = false;

    public CssPrettyPrinter(Appendable w) {
        this.w = w;
        stack.push(Syntax.STYLESHEET);
    }

    public CssPrettyPrinter append(CharSequence str) {
        try {
            print(str);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    @Override
    public CssPrettyPrinter append(CharSequence csq, int start, int end) {
        append(csq.subSequence(start, end));
        return this;
    }

    @Override
    public CssPrettyPrinter append(char c) {
        return append("" + c);
    }

    public String getIndenter() {
        return indenter;
    }

    public void setIndenter(String indenter) {
        this.indenter = indenter;
    }

    public void print(CharSequence str) throws IOException {
        StreamCssTokenizer tt = new StreamCssTokenizer(str);
        print(tt);
    }

    public void print(CssTokenizer tt) throws IOException {

        while (tt.nextNoSkip() != CssTokenType.TT_EOF) {
            CssToken token = tt.getToken();

            int oldsize = stack.size();
            parseToken(token);
            if (stack.size() > oldsize) {
                // increase has only an effect on the next token
                indentation = oldsize;
            } else {
                // decrease (or no change) affects the current token
                indentation = stack.size();
            }
            //
            switch (token.getType()) {
                case '\n':
                    w.append('\n');
                    mustIndent = true;
                    continue;
                case CssTokenType.TT_S:
                    if (token.getStringValueNonnull().indexOf('\n') == -1) {
                        if (!mustIndent) {
                            w.append(" ");
                        }
                    } else {
                        w.append('\n');
                        mustIndent = true;
                    }
                    continue;
                case CssTokenType.TT_FUNCTION:
                case CssTokenType.TT_LEFT_BRACKET:
                case CssTokenType.TT_LEFT_CURLY_BRACKET:
                case CssTokenType.TT_LEFT_SQUARE_BRACKET:
                case CssTokenType.TT_RIGHT_BRACKET:
                case CssTokenType.TT_RIGHT_CURLY_BRACKET:
                case CssTokenType.TT_RIGHT_SQUARE_BRACKET:
                case CssTokenType.TT_SEMICOLON:
                case CssTokenType.TT_COLON:
                case CssTokenType.TT_AT_KEYWORD:
                case CssTokenType.TT_BAD_COMMENT:
                case CssTokenType.TT_BAD_STRING:
                case CssTokenType.TT_BAD_URI:
                case CssTokenType.TT_CDC:
                case CssTokenType.TT_CDO:
                case CssTokenType.TT_COLUMN:
                case CssTokenType.TT_COMMA:
                case CssTokenType.TT_COMMENT:
                case CssTokenType.TT_DASH_MATCH:
                case CssTokenType.TT_DIMENSION:
                case CssTokenType.TT_EOF:
                case CssTokenType.TT_HASH:
                case CssTokenType.TT_IDENT:
                case CssTokenType.TT_INCLUDE_MATCH:
                case CssTokenType.TT_NUMBER:
                case CssTokenType.TT_PERCENT_DELIM:
                case CssTokenType.TT_PERCENTAGE:
                case CssTokenType.TT_PLUS:
                case CssTokenType.TT_PREFIX_MATCH:
                case CssTokenType.TT_SLASH:
                case CssTokenType.TT_STRING:
                case CssTokenType.TT_SUBSTRING_MATCH:
                case CssTokenType.TT_SUFFIX_MATCH:
                case CssTokenType.TT_UNICODE_RANGE:
                case CssTokenType.TT_URL:
                case CssTokenType.TT_VERTICAL_LINE:
                    break;
            }

            if (mustIndent) {
                indent();
                mustIndent = false;
            }
            w.append(token.fromToken());
        }
    }

    private void indent() throws IOException {
        for (int i = 1; i < indentation; i++) {
            w.append(indenter);
        }
    }

    private void parseToken(CssToken token) {
        switch (token.getType()) {
            case CssTokenType.TT_S:
                break;
            case CssTokenType.TT_FUNCTION:
            case CssTokenType.TT_LEFT_BRACKET:
                stack.push(Syntax.ROUND_BLOCK);
                break;
            case CssTokenType.TT_LEFT_CURLY_BRACKET:
                stack.push(Syntax.CURLY_BLOCK);
                break;
            case CssTokenType.TT_LEFT_SQUARE_BRACKET:
                stack.push(Syntax.SQUARE_BLOCK);
                break;
            case CssTokenType.TT_RIGHT_BRACKET:
                if (stack.peek() == Syntax.ROUND_BLOCK) {
                    stack.pop();
                }
                break;
            case CssTokenType.TT_RIGHT_CURLY_BRACKET:
                if (stack.peek() == Syntax.CURLY_BLOCK) {
                    stack.pop();
                }
                break;
            case CssTokenType.TT_RIGHT_SQUARE_BRACKET:
                if (stack.peek() == Syntax.SQUARE_BLOCK) {
                    stack.pop();
                }
                break;
            case CssTokenType.TT_SEMICOLON:
                if (stack.peek() == Syntax.DECLARATION_VALUE) {
                    stack.pop();
                }
                break;
            case CssTokenType.TT_COLON:
                if (stack.peek() == Syntax.DECLARATION_KEY) {
                    stack.pop();
                    stack.push(Syntax.DECLARATION_VALUE);
                }
                break;
            case CssTokenType.TT_IDENT:
                if (stack.peek() == Syntax.CURLY_BLOCK) {
                    stack.push(Syntax.DECLARATION_KEY);
                }
                break;
            case CssTokenType.TT_AT_KEYWORD:
            case CssTokenType.TT_BAD_COMMENT:
            case CssTokenType.TT_BAD_STRING:
            case CssTokenType.TT_BAD_URI:
            case CssTokenType.TT_CDC:
            case CssTokenType.TT_CDO:
            case CssTokenType.TT_COLUMN:
            case CssTokenType.TT_COMMA:
            case CssTokenType.TT_COMMENT:
            case CssTokenType.TT_DASH_MATCH:
            case CssTokenType.TT_DIMENSION:
            case CssTokenType.TT_EOF:
            case CssTokenType.TT_HASH:
            case CssTokenType.TT_INCLUDE_MATCH:
            case CssTokenType.TT_NUMBER:
            case CssTokenType.TT_PERCENT_DELIM:
            case CssTokenType.TT_PERCENTAGE:
            case CssTokenType.TT_PLUS:
            case CssTokenType.TT_PREFIX_MATCH:
            case CssTokenType.TT_SLASH:
            case CssTokenType.TT_STRING:
            case CssTokenType.TT_SUBSTRING_MATCH:
            case CssTokenType.TT_SUFFIX_MATCH:
            case CssTokenType.TT_UNICODE_RANGE:
            case CssTokenType.TT_URL:
            case CssTokenType.TT_VERTICAL_LINE:
                break;
        }
    }
}

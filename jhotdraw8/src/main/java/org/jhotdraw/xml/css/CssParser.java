/* @(#)CSSLoader.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.xml.css;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jhotdraw.io.StreamPosTokenizer;
import org.jhotdraw.xml.css.ast.AdjacentSiblingCombinator;
import org.jhotdraw.xml.css.ast.AndCombinator;
import org.jhotdraw.xml.css.ast.AttributeValueStartsWithSelector;
import org.jhotdraw.xml.css.ast.AttributeSelector;
import org.jhotdraw.xml.css.ast.AbstractAttributeSelector;
import org.jhotdraw.xml.css.ast.AttributeValueEqualsSelector;
import org.jhotdraw.xml.css.ast.AttributeValueContainsWordSelector;
import org.jhotdraw.xml.css.ast.ChildCombinator;
import org.jhotdraw.xml.css.ast.ClassSelector;
import org.jhotdraw.xml.css.ast.Declaration;
import org.jhotdraw.xml.css.ast.DescendantCombinator;
import org.jhotdraw.xml.css.ast.GeneralSiblingCombinator;
import org.jhotdraw.xml.css.ast.IdSelector;
import org.jhotdraw.xml.css.ast.PseudoClassSelector;
import org.jhotdraw.xml.css.ast.Ruleset;
import org.jhotdraw.xml.css.ast.Selector;
import org.jhotdraw.xml.css.ast.SelectorGroup;
import org.jhotdraw.xml.css.ast.SimpleSelector;
import org.jhotdraw.xml.css.ast.Stylesheet;
import org.jhotdraw.xml.css.ast.TypeSelector;
import org.jhotdraw.xml.css.ast.UniversalSelector;

/**
 * Parsers a Cascading Style Sheet (CSS).
 * <p>
 * The parser processes the following grammar, which is a subset of the W3C CSS2
 * grammar. The following is an EBNF ISO/IEC 14977 grammar:
 * <pre>
 * stylesheet   = { ruleset } ;
 *
 * ruleset      = selector_group , "{" , [ declarations ], "}" ;
 *
 * selector_group = selector , { "," , selector } ;
 * selector     = simple_selector , { [combinator] , selector } ;
 * combinator   = "+" | {@literal ">"} | " " | "," | '~' ;
 * simple_selector = universal_selector | type_selector | id_selector
 *                 | class_selector | pseudoclass_selector | attribute_selector ;
 * universal_selector   = "*" ;
 * type_selector        = IDENT ;
 * id_selector          = "#" , IDENT;
 * class_selector       = "." , IDENT ;
 * pseudoclass_selector = ":" , IDENT ;
 * attribute_selector   = "[" , IDENT
 *                            , [ ( "=" | "~=" | "|=" ) , ( IDENT | STRING ) ],
 *                        "]" ;
 *
 * declarations = [ declaration ] , { ";" , [ declaration ] } ;
 * declaration  = property, ":",  expr ;
 * expr         = term , { term } ;
 * term         = NUMBER | PERCENTAGE | LENGTH | EMS | EXS | ANGLE | TIME
 *              | FREQ | STRING | IDENT | URI | HEXCOLOR ;
 *
 * IDENT = (* an XML identifier *) ;
 * </pre>
 * <p>
 * References:
 * <ul>
 * <li><a href="http://www.w3.org/TR/CSS2/">W3C CSS2</a></li>
 * <li><a href="http://www.w3.org/TR/selectors/">Selectors Level 3</a></li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssParser {

    public Stylesheet parseStylesheet(String css) throws IOException {
        return parseStylesheet(new StringReader(css));
    }

    public Stylesheet parseStylesheet(URL css) throws IOException {
        try (Reader in = new BufferedReader(new InputStreamReader(css.openConnection().getInputStream()))) {
            return parseStylesheet(in);
        }
    }

    public Stylesheet parseStylesheet(Reader css) throws IOException {
        StreamPosTokenizer tt = new StreamPosTokenizer(css);
        setSyntax(tt);
        return parseStylesheet(tt);
    }

    public List<Declaration> parseDeclarations(String css) throws IOException {
        return parseDeclarations(new StringReader(css));
    }

    public List<Declaration> parseDeclarations(Reader css) throws IOException {
        StreamPosTokenizer tt = new StreamPosTokenizer(css);
        setSyntax(tt);
        return parseDeclarations(tt);
    }

    private Stylesheet parseStylesheet(StreamPosTokenizer tt) throws IOException {
        List<Ruleset> rulesets = new ArrayList<>();
        while (tt.nextToken() != StreamPosTokenizer.TT_EOF) {
            tt.pushBack();
            rulesets.add(parseRuleset(tt));
        }
        return new Stylesheet(rulesets);
    }

    private Ruleset parseRuleset(StreamPosTokenizer tt) throws IOException {
        // parseStylesheet selector group
        SelectorGroup selectorGroup = parseSelectorGroup(tt);
        if (tt.nextToken() != '{') {
            throw new IOException("Ruleset: '{' instead of " + value(tt) + " expected in line " + tt.lineno());
        }
        List<Declaration> declarations = parseDeclarations(tt);
        if (tt.nextToken() != '}') {
            throw new IOException("Ruleset: '}' instead of " + value(tt) + " expected in line " + tt.lineno());
        }

        return new Ruleset(selectorGroup, declarations);
    }

    private SelectorGroup parseSelectorGroup(StreamPosTokenizer tt) throws IOException {

        List<Selector> selectors = new ArrayList<>();
        selectors.add(parseSelector(tt));

        while (tt.nextToken() != StreamPosTokenizer.TT_EOF
                && tt.ttype != '{') {
            if (tt.ttype != ',') {
                throw new IOException("SelectorGroup: ',' instead of " + value(tt) + " expected in line " + tt.lineno());
            }

            selectors.add(parseSelector(tt));
        }

        tt.pushBack();
        return new SelectorGroup(selectors);
    }

    private Selector parseSelector(StreamPosTokenizer tt) throws IOException {
        tt.ordinaryChars(' ', ' '); // whitespace is significant in a selector!
        while (isWhitespace(tt.nextToken())) {
        }
        tt.pushBack();
        SimpleSelector simpleSelector = parseSimpleSelector(tt);
        Selector selector = simpleSelector;
        while (tt.nextToken() != StreamPosTokenizer.TT_EOF
                && tt.ttype != '{' && tt.ttype != ',') {

            boolean potentialDescendantCombinator = false;
            if (isWhitespace(tt.ttype)) {
                potentialDescendantCombinator = true;
                while (isWhitespace(tt.nextToken())) {
                }
            }
            if (tt.ttype == StreamPosTokenizer.TT_EOF
                    || tt.ttype == '{' || tt.ttype == ',') {
                break;
            }
            switch (tt.ttype) {
            case '>':
                selector = new ChildCombinator(simpleSelector, parseSelector(tt));
                break;
            case '+':
                selector = new AdjacentSiblingCombinator(simpleSelector, parseSelector(tt));
                break;
            case '~':
                selector = new GeneralSiblingCombinator(simpleSelector, parseSelector(tt));
                break;
            default:
                tt.pushBack();
                if (potentialDescendantCombinator) {
                    selector = new DescendantCombinator(simpleSelector, parseSelector(tt));
                } else {
                    selector = new AndCombinator(simpleSelector, parseSelector(tt));
                }
                break;
            }
        }
        tt.whitespaceChars(0, ' '); // reset whitespace
        tt.pushBack();
        return selector;
    }

    private SimpleSelector parseSimpleSelector(StreamPosTokenizer tt) throws IOException {
        switch (tt.nextToken()) {
        case '*':
            return new UniversalSelector();
        case StreamPosTokenizer.TT_WORD:
            return new TypeSelector(tt.sval);
        case '#':
            if (tt.nextToken() != StreamPosTokenizer.TT_WORD) {
                throw new IOException("SimpleSelector: identifier instead of " + value(tt) + " expected after '#' in line " + tt.lineno());
            }
            return new IdSelector(tt.sval);
        case '.':
            if (tt.nextToken() != StreamPosTokenizer.TT_WORD) {
                throw new IOException("SimpleSelector: identifier instead of " + value(tt) + " expected after '.' in line " + tt.lineno());
            }
            return new ClassSelector(tt.sval);
        case ':':
            if (tt.nextToken() != StreamPosTokenizer.TT_WORD) {
                throw new IOException("SimpleSelector: identifier instead of " + value(tt) + " expected after ':' in line " + tt.lineno());
            }
            return new PseudoClassSelector(tt.sval);
        case '[':
            tt.pushBack();
            return parseAttributeSelector(tt);
        default:
            throw new IOException("SimpleSelector: SimpleSelector instead of " + value(tt) + " expected in line " + tt.lineno());
        }
    }

    private AbstractAttributeSelector parseAttributeSelector(StreamPosTokenizer tt) throws IOException {
        if (tt.nextToken() != '[') {
            throw new IOException("AttributeSelector: '[' instead of " + value(tt) + " expected in line " + tt.lineno());
        }
        if (tt.nextToken() != StreamPosTokenizer.TT_WORD) {
            throw new IOException("AttributeSelector: word instead of " + value(tt) + " expected in line " + tt.lineno());
        }
        String attributeName = tt.sval;
        AbstractAttributeSelector selector;
        switch (tt.nextToken()) {
        case '=':
            if (tt.nextToken() != StreamPosTokenizer.TT_WORD && tt.ttype != '\'' && tt.ttype != '"') {
                throw new IOException("AttributeSelector: word or string instead of " + value(tt) + " expected in line " + tt.lineno());
            }
            selector = new AttributeValueEqualsSelector(attributeName, tt.sval);
            break;
        case '~':
            if (tt.nextToken() != '=') {
                throw new IOException("AttributeSelector: '=' instead of " + value(tt) + " expected in line " + tt.lineno());
            }
            if (tt.nextToken() != StreamPosTokenizer.TT_WORD && tt.ttype != '\'' && tt.ttype != '"') {
                throw new IOException("AttributeSelector: word or string instead of " + value(tt) + " expected in line " + tt.lineno());
            }
            selector = new AttributeValueContainsWordSelector(attributeName, tt.sval);
            break;
        case '|':
            if (tt.nextToken() != '=') {
                throw new IOException("AttributeSelector: '=' instead of " + value(tt) + " expected in line " + tt.lineno());
            }
            if (tt.nextToken() != StreamPosTokenizer.TT_WORD && tt.ttype != '\'' && tt.ttype != '"') {
                throw new IOException("AttributeSelector: word or string instead of " + value(tt) + " expected in line " + tt.lineno());
            }
            selector = new AttributeValueStartsWithSelector(attributeName, tt.sval);
            break;
        case ']':
            selector = new AttributeSelector(attributeName);
            tt.pushBack();
            break;
        default:
            throw new IOException("AttributeSelector: operator '=', '~=' or '|=' instead of " + value(tt) + " expected in line " + tt.lineno());

        }
        if (tt.nextToken() != ']') {
            throw new IOException("AttributeSelector: ']' instead of " + value(tt) + " expected in line " + tt.lineno());
        }
        return selector;
    }

    private List<Declaration> parseDeclarations(StreamPosTokenizer tt) throws IOException {
        List<Declaration> declarations = new ArrayList<>();

        while (tt.nextToken() != StreamPosTokenizer.TT_EOF
                && tt.ttype != '}') {
            if (tt.ttype != ';') {
                if (tt.ttype == StreamPosTokenizer.TT_WORD) {
                    tt.pushBack();
                    declarations.add(parseDeclaration(tt));
                }
            }
        }

        tt.pushBack();
        return declarations;

    }

    private Declaration parseDeclaration(StreamPosTokenizer tt) throws IOException {
        if (tt.nextToken() != StreamPosTokenizer.TT_WORD) {
            throw new IOException("Declaration: property name instead of " + value(tt) + " expected in line " + tt.lineno());
        }
        String property = tt.sval;
        if (tt.nextToken() != ':') {
            throw new IOException("Declaration: ':' name instead of " + value(tt) + " expected in line " + tt.lineno());
        }

        String terms = parseTerms(tt);

        return new Declaration(property, terms);

    }

    private String parseTerms(StreamPosTokenizer tt) throws IOException {
        // FIXME we do not properly parse the terms yet
        StringBuilder terms = new StringBuilder();
        tt.ordinaryChars(0, ' ');

        while (tt.nextToken() != StreamPosTokenizer.TT_EOF && tt.ttype != '}' && tt.ttype != ';') {
            switch (tt.ttype) {
            case StreamPosTokenizer.TT_WORD:
                terms.append(tt.sval);
                break;
            case '\'':
                terms.append('\'');
                terms.append(tt.sval);
                terms.append('\'');
                break;
            case '"':
                terms.append('"');
                terms.append(tt.sval);
                terms.append('"');
                break;
            default:
                if (isWhitespace(tt.ttype)) {
                    //normalize whitespace outside of string
                    if (terms.length() > 0 && terms.charAt(terms.length() - 1) != ' ') {
                        terms.append(' ');
                    }
                } else {
                    terms.append((char) tt.ttype);
                }
                break;
            }
        }
        tt.pushBack();
        setSyntax(tt);
        return terms.toString().trim();
    }

    private boolean isWhitespace(int ttype) {
        return 0 <= ttype && ttype <= ' ';
    }

    private void setSyntax(StreamPosTokenizer tt) {
        tt.resetSyntax();
        tt.wordChars('a', 'z');
        tt.wordChars('A', 'Z');
        tt.wordChars('0', '9');
        tt.wordChars('-', '-'); // vendor specific identifiers may begin with dash
        tt.wordChars('_', '_'); // vendor specific identifiers may begin with underscore
        tt.wordChars(128 + 32, 255);
        tt.whitespaceChars(0, ' ');
        tt.quoteChar('"');
        tt.quoteChar('\'');
        tt.slashStarComments(true);
    }

    private String value(StreamPosTokenizer tt) {
        switch (tt.ttype) {
        case StreamPosTokenizer.TT_EOF:
            return "end of file";
        case StreamPosTokenizer.TT_EOL:
            return "end of line";
        case StreamPosTokenizer.TT_NUMBER:
            return Double.toString(tt.nval);
        case StreamPosTokenizer.TT_WORD:
            return "\"" + tt.sval + "\"";
        default:
            return "'" + (char) tt.ttype + "'";
        }
    }
}

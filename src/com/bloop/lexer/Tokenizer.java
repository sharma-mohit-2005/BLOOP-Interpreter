package com.bloop.lexer;

import com.bloop.token.Token;
import com.bloop.token.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Tokenizer (lexer) reads raw BLOOP source code as a single String
 * and produces a {@code List<Token>}.
 * 
 * It walks through the characters one at a time, recognising patterns
 * (numbers, strings, keywords, operators) and emitting a Token for each.
 * 
 * <h3>Generics</h3>
 * Uses {@code Map<String, TokenType>} for keyword lookup and produces
 * {@code List<Token>} as output — both generic types.
 * 
 * <h3>How Tokenization Works</h3>
 * <ol>
 *   <li>Skip whitespace (but track indentation at line start)</li>
 *   <li>Recognise numbers: sequences of digits and '.'</li>
 *   <li>Recognise strings: text between double quotes</li>
 *   <li>Recognise words: check if they're keywords or identifiers</li>
 *   <li>Recognise operators and punctuation: +, -, *, /, >, <, ==, :</li>
 *   <li>Track newlines for indent detection</li>
 *   <li>Emit EOF at the end</li>
 * </ol>
 */
public class Tokenizer {

    private final String source;
    private int pos;
    private int line;
    private boolean atLineStart;

    /**
     * Keyword map: maps BLOOP keyword strings to their TokenType.
     * 
     * Generics: Map<String, TokenType> — type-safe lookup from
     * string to enum constant.
     */
    private static final Map<String, TokenType> KEYWORDS = Map.of(
        "put",    TokenType.PUT,
        "into",   TokenType.INTO,
        "print",  TokenType.PRINT,
        "if",     TokenType.IF,
        "then",   TokenType.THEN,
        "repeat", TokenType.REPEAT,
        "times",  TokenType.TIMES
    );

    /**
     * Constructs a Tokenizer for the given source code.
     *
     * @param source the raw BLOOP source code
     */
    public Tokenizer(String source) {
        this.source = source;
        this.pos = 0;
        this.line = 1;
        this.atLineStart = true;
    }

    /**
     * Tokenizes the entire source code and returns the token list.
     * The list always ends with an EOF token.
     *
     * @return the complete list of tokens (Generics: List<Token>)
     */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < source.length()) {
            char current = source.charAt(pos);

            // ── Handle newlines ──────────────────────────────
            if (current == '\n') {
                tokens.add(new Token(TokenType.NEWLINE, "\\n", line));
                line++;
                pos++;
                atLineStart = true;
                continue;
            }

            // ── Skip carriage return ─────────────────────────
            if (current == '\r') {
                pos++;
                continue;
            }

            // ── Handle indentation at the start of a line ────
            if (atLineStart && (current == ' ' || current == '\t')) {
                int indentStart = pos;
                while (pos < source.length() &&
                       (source.charAt(pos) == ' ' || source.charAt(pos) == '\t')) {
                    pos++;
                }
                // Only emit INDENT if there's actual content after the spaces
                if (pos < source.length() && source.charAt(pos) != '\n' && source.charAt(pos) != '\r') {
                    tokens.add(new Token(TokenType.INDENT, source.substring(indentStart, pos), line));
                }
                atLineStart = false;
                continue;
            }

            atLineStart = false;

            // ── Skip spaces (not at line start) ──────────────
            if (current == ' ' || current == '\t') {
                pos++;
                continue;
            }

            // ── Numbers ──────────────────────────────────────
            if (Character.isDigit(current)) {
                tokens.add(readNumber());
                continue;
            }

            // ── Strings ──────────────────────────────────────
            if (current == '"') {
                tokens.add(readString());
                continue;
            }

            // ── Words (keywords or identifiers) ──────────────
            if (Character.isLetter(current) || current == '_') {
                tokens.add(readWord());
                continue;
            }

            // ── Operators and punctuation ─────────────────────
            switch (current) {
                case '+':
                    tokens.add(new Token(TokenType.PLUS, "+", line));
                    pos++;
                    break;
                case '-':
                    tokens.add(new Token(TokenType.MINUS, "-", line));
                    pos++;
                    break;
                case '*':
                    tokens.add(new Token(TokenType.STAR, "*", line));
                    pos++;
                    break;
                case '/':
                    tokens.add(new Token(TokenType.SLASH, "/", line));
                    pos++;
                    break;
                case '>':
                    tokens.add(new Token(TokenType.GREATER, ">", line));
                    pos++;
                    break;
                case '<':
                    tokens.add(new Token(TokenType.LESS, "<", line));
                    pos++;
                    break;
                case '=':
                    if (pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
                        tokens.add(new Token(TokenType.EQUALS_EQUALS, "==", line));
                        pos += 2;
                    } else {
                        // Single '=' is not used in BLOOP, skip it
                        pos++;
                    }
                    break;
                case ':':
                    tokens.add(new Token(TokenType.COLON, ":", line));
                    pos++;
                    break;
                default:
                    // Skip unknown characters
                    pos++;
                    break;
            }
        }

        // Always end with EOF
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    /**
     * Reads a number token (integer or decimal).
     * Advances pos past the entire number.
     */
    private Token readNumber() {
        int start = pos;
        while (pos < source.length() && (Character.isDigit(source.charAt(pos)) || source.charAt(pos) == '.')) {
            pos++;
        }
        String numberText = source.substring(start, pos);
        return new Token(TokenType.NUMBER, numberText, line);
    }

    /**
     * Reads a string token (text between double quotes).
     * Advances pos past the closing quote.
     */
    private Token readString() {
        pos++; // skip opening quote
        int start = pos;
        while (pos < source.length() && source.charAt(pos) != '"') {
            pos++;
        }
        String text = source.substring(start, pos);
        if (pos < source.length()) {
            pos++; // skip closing quote
        }
        return new Token(TokenType.STRING, text, line);
    }

    /**
     * Reads a word token — either a BLOOP keyword or an identifier.
     * Uses the KEYWORDS map (generics) for lookup.
     */
    private Token readWord() {
        int start = pos;
        while (pos < source.length() &&
               (Character.isLetterOrDigit(source.charAt(pos)) || source.charAt(pos) == '_')) {
            pos++;
        }
        String word = source.substring(start, pos);

        // Generics: Map<String, TokenType> lookup
        TokenType type = KEYWORDS.getOrDefault(word, TokenType.IDENTIFIER);
        return new Token(type, word, line);
    }
}

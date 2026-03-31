package com.bloop.token;

/**
 * An immutable token representing one piece of source code.
 * 
 * Each token carries:
 *   - its type  (from the TokenType enum)
 *   - the raw text value  (the exact characters from the source)
 *   - the line number  (1-based, for error messages)
 * 
 * All fields are set in the constructor and never changed (immutability).
 * No setters are provided — only getters.
 */
public class Token {

    private final TokenType type;
    private final String value;
    private final int line;

    /**
     * Constructs a new Token.
     *
     * @param type  the kind of token (keyword, number, operator, etc.)
     * @param value the raw text from the source code
     * @param line  the 1-based line number where this token appeared
     */
    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    /** Returns the token type. */
    public TokenType getType() {
        return type;
    }

    /** Returns the raw text value. */
    public String getValue() {
        return value;
    }

    /** Returns the 1-based line number. */
    public int getLine() {
        return line;
    }

    /**
     * String representation for debugging.
     * Example: Token{PRINT, "print", line=3}
     */
    @Override
    public String toString() {
        return "Token{" + type + ", \"" + value + "\", line=" + line + "}";
    }
}

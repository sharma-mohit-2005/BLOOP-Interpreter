package com.bloop.token;

/**
 * Enum listing every kind of token the BLOOP language can produce.
 * 
 * Each token type maps to a distinct syntactic element:
 * - Keywords:   PUT, INTO, PRINT, IF, THEN, REPEAT, TIMES
 * - Literals:   NUMBER, STRING
 * - Names:      IDENTIFIER
 * - Operators:  PLUS, MINUS, STAR, SLASH, GREATER, LESS, EQUALS_EQUALS
 * - Structure:  COLON, NEWLINE, INDENT, EOF
 */
public enum TokenType {
    // ── Literals ──────────────────────────────────────────
    NUMBER,          // e.g. 42, 3.14
    STRING,          // e.g. "hello"
    IDENTIFIER,      // e.g. x, score, result

    // ── BLOOP Keywords ───────────────────────────────────
    PUT,             // put <expr> into <var>
    INTO,            // part of assignment syntax
    PRINT,           // print <expr>
    IF,              // if <cond> then:
    THEN,            // part of conditional syntax
    REPEAT,          // repeat <n> times:
    TIMES,           // part of loop syntax

    // ── Arithmetic Operators ─────────────────────────────
    PLUS,            // +
    MINUS,           // -
    STAR,            // *
    SLASH,           // /

    // ── Comparison Operators ─────────────────────────────
    GREATER,         // >
    LESS,            // <
    EQUALS_EQUALS,   // ==

    // ── Structural Tokens ────────────────────────────────
    COLON,           // :
    NEWLINE,         // end of line
    INDENT,          // leading whitespace (4 spaces or tab)
    EOF              // end of input
}

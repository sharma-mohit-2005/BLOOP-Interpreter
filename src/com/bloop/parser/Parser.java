package com.bloop.parser;

import com.bloop.expression.*;
import com.bloop.instruction.*;
import com.bloop.runtime.OutputHandler;
import com.bloop.token.Token;
import com.bloop.token.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Recursive descent parser for the BLOOP language.
 * 
 * Reads a {@code List<Token>} produced by the Tokenizer and builds
 * a {@code List<Instruction>} — the structured representation of the
 * program that the execution engine can run.
 * 
 * <h3>Generics</h3>
 * Uses {@code List<Token>} as input and produces {@code List<Instruction>}
 * as output — both generic types that enable type-safe collections.
 * 
 * <h3>Dependency Injection</h3>
 * Receives an {@link OutputHandler} that gets injected into every
 * PrintInstruction created during parsing, decoupling output from
 * the parsing logic.
 * 
 * <h3>Recursive Descent — Operator Precedence</h3>
 * Expression parsing uses a three-level call chain:
 * <pre>
 *   parseExpression()  → handles + and -  (lowest precedence)
 *       ↓ calls
 *   parseTerm()        → handles * and /  (higher precedence)
 *       ↓ calls
 *   parsePrimary()     → handles numbers, strings, variables (atoms)
 * </pre>
 * This structure automatically gives * and / higher priority than + and -.
 * 
 * <h3>BLOOP Grammar</h3>
 * <pre>
 *   program     → instruction*
 *   instruction → assignment | printStmt | ifStmt | repeatStmt
 *   assignment  → PUT expression INTO IDENTIFIER NEWLINE
 *   printStmt   → PRINT expression NEWLINE
 *   ifStmt      → IF condition THEN COLON NEWLINE block
 *   repeatStmt  → REPEAT NUMBER TIMES COLON NEWLINE block
 *   block       → (INDENT instruction)+
 *   condition   → expression ( > | < | == ) expression
 *   expression  → term ( ( + | - ) term )*
 *   term        → primary ( ( * | / ) primary )*
 *   primary     → NUMBER | STRING | IDENTIFIER
 * </pre>
 */
public class Parser {

    private final List<Token> tokens;     // Generics: List<Token>
    private final OutputHandler outputHandler;  // DI: injected output handler
    private int current;

    /**
     * Constructs a Parser.
     *
     * @param tokens        the token list from the Tokenizer (Generics: List<Token>)
     * @param outputHandler the output handler to inject into PrintInstructions (DI)
     */
    public Parser(List<Token> tokens, OutputHandler outputHandler) {
        this.tokens = tokens;
        this.outputHandler = outputHandler;
        this.current = 0;
    }

    /**
     * Parses the entire token list into a list of instructions.
     *
     * @return the complete instruction list (Generics: List<Instruction>)
     */
    public List<Instruction> parse() {
        List<Instruction> instructions = new ArrayList<>();

        while (!isAtEnd()) {
            skipNewlines();
            if (isAtEnd()) break;

            // Skip indent tokens at the top level
            if (check(TokenType.INDENT)) {
                advance();
                continue;
            }

            instructions.add(parseInstruction());
        }

        return instructions;
    }

    // ═══════════════════════════════════════════════════════
    //  Instruction Parsing
    // ═══════════════════════════════════════════════════════

    /**
     * Parses one instruction by dispatching on the current token type.
     * 
     * BLOOP instructions start with:
     *   PUT    → assignment (put <expr> into <var>)
     *   PRINT  → print statement (print <expr>)
     *   IF     → conditional (if <cond> then: <body>)
     *   REPEAT → loop (repeat <n> times: <body>)
     */
    private Instruction parseInstruction() {
        Token token = peek();

        switch (token.getType()) {
            case PUT:
                return parseAssignment();
            case PRINT:
                return parsePrint();
            case IF:
                return parseIf();
            case REPEAT:
                return parseRepeat();
            default:
                throw new RuntimeException(
                    "Unexpected token '" + token.getValue() +
                    "' at line " + token.getLine() +
                    ". Expected PUT, PRINT, IF, or REPEAT."
                );
        }
    }

    /**
     * Parses: put <expression> into <identifier>
     * 
     * Example: put x + y * 2 into result
     */
    private Instruction parseAssignment() {
        advance(); // consume PUT

        // Parse the expression (everything before INTO)
        Expression expr = parseExpression();

        // Expect INTO keyword
        expect(TokenType.INTO, "Expected 'into' after expression in 'put' statement");

        // Expect variable name
        Token varToken = expect(TokenType.IDENTIFIER, "Expected variable name after 'into'");
        String variableName = varToken.getValue();

        skipNewlines();

        return new AssignInstruction(variableName, expr);
    }

    /**
     * Parses: print <expression>
     * 
     * Example: print "Hello from BLOOP"
     * 
     * Uses DI: injects outputHandler into PrintInstruction.
     */
    private Instruction parsePrint() {
        advance(); // consume PRINT

        Expression expr = parseExpression();
        skipNewlines();

        // DI: pass the injected outputHandler to PrintInstruction
        return new PrintInstruction(expr, outputHandler);
    }

    /**
     * Parses: if <condition> then: <body>
     * 
     * Example:
     *   if score > 50 then:
     *       print "Pass"
     */
    private Instruction parseIf() {
        advance(); // consume IF

        // Parse the condition (e.g., score > 50)
        Expression condition = parseCondition();

        // Expect THEN
        expect(TokenType.THEN, "Expected 'then' after condition in 'if' statement");

        // Expect colon
        expect(TokenType.COLON, "Expected ':' after 'then'");

        skipNewlines();

        // Parse the indented body block
        List<Instruction> body = parseBlock();

        return new IfInstruction(condition, body);
    }

    /**
     * Parses: repeat <number> times: <body>
     * 
     * Example:
     *   repeat 4 times:
     *       print i
     *       put i + 1 into i
     */
    private Instruction parseRepeat() {
        advance(); // consume REPEAT

        // Expect the count (a number)
        Token countToken = expect(TokenType.NUMBER, "Expected a number after 'repeat'");
        int count = (int) Double.parseDouble(countToken.getValue());

        // Expect TIMES
        expect(TokenType.TIMES, "Expected 'times' after repeat count");

        // Expect colon
        expect(TokenType.COLON, "Expected ':' after 'times'");

        skipNewlines();

        // Parse the indented body block
        List<Instruction> body = parseBlock();

        return new RepeatInstruction(count, body);
    }

    // ═══════════════════════════════════════════════════════
    //  Block Parsing (indented body)
    // ═══════════════════════════════════════════════════════

    /**
     * Parses an indented block of instructions.
     * 
     * A block is a sequence of lines that each start with an INDENT token.
     * The block ends when a line without indentation is encountered,
     * or when we reach EOF.
     *
     * @return the list of instructions in the block (Generics: List<Instruction>)
     */
    private List<Instruction> parseBlock() {
        List<Instruction> body = new ArrayList<>();

        while (!isAtEnd()) {
            skipNewlines();
            if (isAtEnd()) break;

            // If the next token is INDENT, this line belongs to the block
            if (check(TokenType.INDENT)) {
                advance(); // consume INDENT
                body.add(parseInstruction());
            } else {
                // No indent = end of block
                break;
            }
        }

        if (body.isEmpty()) {
            throw new RuntimeException(
                "Empty block at line " + peek().getLine() +
                ". Expected at least one indented instruction."
            );
        }

        return body;
    }

    // ═══════════════════════════════════════════════════════
    //  Expression Parsing (Recursive Descent)
    // ═══════════════════════════════════════════════════════

    /**
     * Parses a condition expression: {@code <expr> <comparator> <expr>}
     * where comparator is >, <, or ==.
     * 
     * This is used in IF statements.
     */
    private Expression parseCondition() {
        Expression left = parseExpression();

        if (check(TokenType.GREATER) || check(TokenType.LESS) || check(TokenType.EQUALS_EQUALS)) {
            Token op = advance();
            Expression right = parseExpression();
            return new BinaryOpNode(left, op.getValue(), right);
        }

        return left;
    }

    /**
     * Parses an expression handling + and - (lowest precedence).
     * 
     * <pre>
     * expression → term ( ( + | - ) term )*
     * </pre>
     * 
     * Calls parseTerm() for the operands, which handles * and / first,
     * giving them higher precedence automatically.
     */
    private Expression parseExpression() {
        Expression left = parseTerm();

        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            Token op = advance();
            Expression right = parseTerm();
            left = new BinaryOpNode(left, op.getValue(), right);
        }

        return left;
    }

    /**
     * Parses a term handling * and / (higher precedence than + and -).
     * 
     * <pre>
     * term → primary ( ( * | / ) primary )*
     * </pre>
     * 
     * Calls parsePrimary() for the operands (individual values).
     */
    private Expression parseTerm() {
        Expression left = parsePrimary();

        while (check(TokenType.STAR) || check(TokenType.SLASH)) {
            Token op = advance();
            Expression right = parsePrimary();
            left = new BinaryOpNode(left, op.getValue(), right);
        }

        return left;
    }

    /**
     * Parses a primary expression — a single atomic value.
     * 
     * <pre>
     * primary → NUMBER | STRING | IDENTIFIER
     * </pre>
     */
    private Expression parsePrimary() {
        Token token = peek();

        switch (token.getType()) {
            case NUMBER:
                advance();
                return new NumberNode(Double.parseDouble(token.getValue()));

            case STRING:
                advance();
                return new StringNode(token.getValue());

            case IDENTIFIER:
                advance();
                return new VariableNode(token.getValue());

            default:
                throw new RuntimeException(
                    "Expected a number, string, or variable name but found '" +
                    token.getValue() + "' (type: " + token.getType() +
                    ") at line " + token.getLine()
                );
        }
    }

    // ═══════════════════════════════════════════════════════
    //  Helper Methods
    // ═══════════════════════════════════════════════════════

    /** Returns the current token without advancing. */
    private Token peek() {
        return tokens.get(current);
    }

    /** Advances to the next token and returns the previous one. */
    private Token advance() {
        Token token = tokens.get(current);
        current++;
        return token;
    }

    /** Checks if the current token has the given type. */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    /** Returns true if we've reached the EOF token. */
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    /** Skips over any NEWLINE tokens. */
    private void skipNewlines() {
        while (!isAtEnd() && check(TokenType.NEWLINE)) {
            advance();
        }
    }

    /**
     * Expects the current token to be of the given type.
     * If it is, advances and returns it.
     * If not, throws a RuntimeException with a helpful error message.
     *
     * @param type    the expected token type
     * @param message error message if the expectation fails
     * @return the consumed token
     */
    private Token expect(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        Token actual = peek();
        throw new RuntimeException(
            message + " — found '" + actual.getValue() +
            "' (type: " + actual.getType() + ") at line " + actual.getLine()
        );
    }
}

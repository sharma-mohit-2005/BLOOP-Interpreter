package com.bloop.expression;

import com.bloop.runtime.Environment;

/**
 * Represents a literal string in the source code, such as "hello".
 * 
 * Like NumberNode, this is a leaf node in the expression tree.
 * It stores a String value and returns it when evaluated.
 * 
 * <h3>Immutability</h3>
 * The value is set in the constructor and never changed (final field).
 */
public class StringNode implements Expression {

    private final String value;

    /**
     * Constructs a StringNode with the given text value.
     *
     * @param value the literal string (without quotes)
     */
    public StringNode(String value) {
        this.value = value;
    }

    /**
     * Returns the stored string value.
     * The Environment is not used since literals are constant.
     *
     * @param env the variable store (unused for literal strings)
     * @return the stored String value
     */
    @Override
    public Object evaluate(Environment env) {
        return value;
    }

    @Override
    public String toString() {
        return "StringNode(\"" + value + "\")";
    }
}

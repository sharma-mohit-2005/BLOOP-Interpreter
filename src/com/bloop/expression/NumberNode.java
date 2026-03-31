package com.bloop.expression;

import com.bloop.runtime.Environment;

/**
 * Represents a literal number in the source code, such as 42 or 3.14.
 * 
 * This is the simplest expression node — it stores a double value
 * and returns it when evaluated, ignoring the Environment entirely
 * since literal numbers don't depend on any variables.
 * 
 * <h3>Immutability</h3>
 * The value is set in the constructor and never changed (final field).
 */
public class NumberNode implements Expression {

    private final double value;

    /**
     * Constructs a NumberNode with the given numeric value.
     *
     * @param value the literal number (e.g. 42.0, 3.14)
     */
    public NumberNode(double value) {
        this.value = value;
    }

    /**
     * Returns the stored numeric value.
     * The Environment is not used since literals are constant.
     *
     * @param env the variable store (unused for literal numbers)
     * @return the stored Double value
     */
    @Override
    public Object evaluate(Environment env) {
        return value;
    }

    @Override
    public String toString() {
        return "NumberNode(" + value + ")";
    }
}

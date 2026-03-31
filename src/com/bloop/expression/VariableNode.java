package com.bloop.expression;

import com.bloop.runtime.Environment;

/**
 * Represents a variable reference such as {@code x} or {@code total}.
 * 
 * When evaluated, it looks up the variable's current value in the
 * Environment. If the variable hasn't been defined yet, the
 * Environment will throw a RuntimeException.
 * 
 * <h3>Immutability</h3>
 * The variable name is set in the constructor and never changed.
 */
public class VariableNode implements Expression {

    private final String name;

    /**
     * Constructs a VariableNode for the named variable.
     *
     * @param name the variable name (e.g. "x", "score")
     */
    public VariableNode(String name) {
        this.name = name;
    }

    /**
     * Looks up this variable's current value in the Environment.
     *
     * @param env the variable store to query
     * @return the variable's current value (Double or String)
     * @throws RuntimeException if the variable has not been defined
     */
    @Override
    public Object evaluate(Environment env) {
        return env.get(name);
    }

    /** Returns the variable name. */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "VariableNode(" + name + ")";
    }
}

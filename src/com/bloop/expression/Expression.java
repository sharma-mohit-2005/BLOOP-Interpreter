package com.bloop.expression;

import com.bloop.runtime.Environment;

/**
 * Represents any expression in the BLOOP language that can be evaluated
 * to produce a value.
 * 
 * <h3>Functional Programming</h3>
 * This is a {@code @FunctionalInterface} — it has exactly one abstract
 * method, which means expressions can be created using lambda syntax:
 * 
 * <pre>{@code
 *   // Lambda that always returns 42
 *   Expression fortyTwo = env -> 42.0;
 *   
 *   // Lambda that looks up a variable
 *   Expression lookup = env -> env.get("x");
 * }</pre>
 * 
 * <h3>Polymorphism</h3>
 * Concrete implementations (NumberNode, StringNode, VariableNode,
 * BinaryOpNode) each implement evaluate() differently, but the rest
 * of the interpreter treats them uniformly through this interface.
 * 
 * @see NumberNode
 * @see StringNode
 * @see VariableNode
 * @see BinaryOpNode
 */
@FunctionalInterface
public interface Expression {

    /**
     * Evaluate this expression using the current variable store.
     *
     * @param env the environment containing variable bindings
     * @return the result — either a {@code Double} (for numbers)
     *         or a {@code String} (for text)
     */
    Object evaluate(Environment env);
}

package com.bloop.instruction;

import com.bloop.expression.Expression;
import com.bloop.runtime.Environment;

/**
 * Handles variable assignment — for example:
 * {@code put 10 + 5 into x}
 * 
 * Stores the variable name and the expression whose evaluated value
 * will be assigned to that variable.
 * 
 * <h3>How it works</h3>
 * 1. Evaluate the expression (e.g. {@code 10 + 5} → {@code 15.0})
 * 2. Store the result in the Environment under the variable name
 */
public class AssignInstruction implements Instruction {

    private final String variableName;
    private final Expression expression;

    /**
     * Constructs an assignment instruction.
     *
     * @param variableName the variable to assign to (e.g. "x")
     * @param expression   the expression whose value will be assigned
     */
    public AssignInstruction(String variableName, Expression expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    /**
     * Evaluates the expression and stores the result in the Environment.
     *
     * @param env the shared variable store
     */
    @Override
    public void execute(Environment env) {
        Object value = expression.evaluate(env);
        env.set(variableName, value);
    }

    @Override
    public String toString() {
        return "AssignInstruction(" + variableName + " = " + expression + ")";
    }
}

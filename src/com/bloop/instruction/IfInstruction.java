package com.bloop.instruction;

import com.bloop.expression.Expression;
import com.bloop.runtime.Environment;

import java.util.List;

/**
 * Handles a conditional block — for example:
 * <pre>{@code
 *   if score > 50 then:
 *       print "Pass"
 * }</pre>
 * 
 * <h3>Generics</h3>
 * The body is stored as {@code List<Instruction>} — a generic list
 * that holds any type of instruction polymorphically.
 * 
 * <h3>How it works</h3>
 * 1. Evaluate the condition expression
 * 2. If the result is Boolean.TRUE, execute every instruction in the body
 * 3. Otherwise, skip the entire body
 */
public class IfInstruction implements Instruction {

    private final Expression condition;
    private final List<Instruction> body;  // Generics: List<Instruction>

    /**
     * Constructs a conditional instruction.
     *
     * @param condition the boolean expression to test
     * @param body      the instructions to execute if condition is true
     */
    public IfInstruction(Expression condition, List<Instruction> body) {
        this.condition = condition;
        this.body = body;
    }

    /**
     * Evaluates the condition. If true, executes each instruction
     * in the body sequentially.
     *
     * @param env the shared variable store
     */
    @Override
    public void execute(Environment env) {
        Object result = condition.evaluate(env);

        if (result instanceof Boolean && (Boolean) result) {
            // Functional programming: using forEach with method reference style
            for (Instruction instruction : body) {
                instruction.execute(env);
            }
        }
    }

    @Override
    public String toString() {
        return "IfInstruction(condition=" + condition + ", body=" + body.size() + " instructions)";
    }
}

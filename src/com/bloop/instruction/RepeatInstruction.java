package com.bloop.instruction;

import com.bloop.runtime.Environment;

import java.util.List;

/**
 * Handles a fixed-count loop — for example:
 * <pre>{@code
 *   repeat 4 times:
 *       print i
 *       put i + 1 into i
 * }</pre>
 * 
 * <h3>Generics</h3>
 * The body is stored as {@code List<Instruction>} — a generic list
 * that holds any type of instruction polymorphically.
 * 
 * <h3>How it works</h3>
 * Executes all body instructions {@code count} times in sequence.
 */
public class RepeatInstruction implements Instruction {

    private final int count;
    private final List<Instruction> body;  // Generics: List<Instruction>

    /**
     * Constructs a repeat/loop instruction.
     *
     * @param count the number of times to repeat
     * @param body  the instructions to execute each iteration
     */
    public RepeatInstruction(int count, List<Instruction> body) {
        this.count = count;
        this.body = body;
    }

    /**
     * Executes all body instructions, repeated {@code count} times.
     *
     * @param env the shared variable store
     */
    @Override
    public void execute(Environment env) {
        for (int i = 0; i < count; i++) {
            for (Instruction instruction : body) {
                instruction.execute(env);
            }
        }
    }

    @Override
    public String toString() {
        return "RepeatInstruction(count=" + count + ", body=" + body.size() + " instructions)";
    }
}

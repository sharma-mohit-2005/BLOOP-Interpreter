package com.bloop.engine;

import com.bloop.instruction.Instruction;
import com.bloop.runtime.Environment;

import java.util.List;

/**
 * The standard BLOOP execution engine — executes instructions sequentially.
 * 
 * <h3>Functional Programming — Stream API</h3>
 * Uses {@code stream().forEach()} with a lambda to execute each instruction,
 * demonstrating the Stream API and lambda expressions.
 * 
 * <h3>Dependency Injection</h3>
 * This is the concrete implementation of {@link ExecutionEngine}.
 * The Interpreter receives it via constructor injection, so a test
 * could provide a mock executor instead.
 */
public class BloopExecutionEngine implements ExecutionEngine {

    /**
     * Executes each instruction in sequence.
     * 
     * Uses functional programming: streams the instruction list and applies
     * a method reference to execute each one.
     *
     * @param instructions the parsed instruction list
     * @param env          the variable store
     */
    @Override
    public void execute(List<Instruction> instructions, Environment env) {
        // Functional programming: Stream API with lambda expression
        instructions.stream().forEach(instruction -> instruction.execute(env));
    }
}

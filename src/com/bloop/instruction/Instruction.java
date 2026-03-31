package com.bloop.instruction;

import com.bloop.runtime.Environment;

/**
 * Represents one complete action the BLOOP language can perform.
 * 
 * <h3>Functional Programming</h3>
 * This is a {@code @FunctionalInterface} with a single abstract method
 * {@code execute(Environment)}. This means instructions can be created
 * using lambda expressions when needed:
 * 
 * <pre>{@code
 *   // Lambda instruction that prints "hello"
 *   Instruction hello = env -> System.out.println("hello");
 * }</pre>
 * 
 * <h3>Polymorphism</h3>
 * Concrete implementations (AssignInstruction, PrintInstruction,
 * IfInstruction, RepeatInstruction) each implement execute() differently,
 * but the interpreter executes them uniformly via this interface.
 * 
 * @see AssignInstruction
 * @see PrintInstruction
 * @see IfInstruction
 * @see RepeatInstruction
 */
@FunctionalInterface
public interface Instruction {

    /**
     * Execute this instruction, reading and writing variables via
     * the Environment.
     *
     * @param env the shared variable store
     */
    void execute(Environment env);
}

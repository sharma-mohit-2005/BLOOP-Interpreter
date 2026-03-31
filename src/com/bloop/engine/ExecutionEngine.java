package com.bloop.engine;

import com.bloop.instruction.Instruction;
import com.bloop.runtime.Environment;

import java.util.List;

/**
 * Interface for executing a list of parsed instructions.
 * 
 * <h3>Dependency Injection</h3>
 * This interface is the DI abstraction point for execution.
 * The Interpreter depends on this interface rather than a concrete class,
 * allowing different execution strategies to be swapped in:
 * - {@link BloopExecutionEngine} — the standard sequential executor
 * - A debug executor that prints each instruction before executing
 * - A test executor that captures results for verification
 * 
 * <h3>Generics</h3>
 * Uses {@code List<Instruction>} — a generic collection of instructions.
 */
public interface ExecutionEngine {

    /**
     * Executes the given list of instructions in the given environment.
     *
     * @param instructions the parsed instruction list (Generics: List<Instruction>)
     * @param env          the variable store for execution
     */
    void execute(List<Instruction> instructions, Environment env);
}

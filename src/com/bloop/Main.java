package com.bloop;

import com.bloop.engine.BloopExecutionEngine;
import com.bloop.engine.ExecutionEngine;
import com.bloop.runtime.OutputHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * CLI entry point for the BLOOP interpreter.
 * 
 * Reads a .bloop source file from the command-line argument,
 * wires up all dependencies, and runs the interpreter.
 * 
 * <h3>Dependency Injection Wiring</h3>
 * This is the "composition root" — the place where all dependencies
 * are created and wired together:
 * - {@link OutputHandler} is set to {@code System.out::println} (method reference)
 * - {@link ExecutionEngine} is set to {@link BloopExecutionEngine}
 * - Both are injected into {@link Interpreter} via its constructor
 * 
 * <h3>Functional Programming</h3>
 * - Uses method reference {@code System.out::println} as the OutputHandler
 * - OutputHandler is a {@code @FunctionalInterface}, so a method reference
 *   that matches the signature {@code void print(String)} works directly
 * 
 * Usage:
 * <pre>{@code
 *   java com.bloop.Main path/to/program.bloop
 * }</pre>
 */
public class Main {

    public static void main(String[] args) {
        // ── Validate command-line arguments ───────────────
        if (args.length < 1) {
            System.err.println("Usage: java com.bloop.Main <source-file.bloop>");
            System.err.println("Example: java com.bloop.Main program1.bloop");
            System.exit(1);
        }

        String filePath = args[0];

        // ── Read source file ─────────────────────────────
        String sourceCode;
        try {
            Path path = Paths.get(filePath);
            sourceCode = Files.readString(path);
        } catch (IOException e) {
            System.err.println("Error reading file '" + filePath + "': " + e.getMessage());
            System.exit(1);
            return; // unreachable, but satisfies the compiler
        }

        // ═══════════════════════════════════════════════════
        //  DEPENDENCY INJECTION WIRING (Composition Root)
        // ═══════════════════════════════════════════════════

        // DI + Functional: System.out::println is a method reference
        // that satisfies the OutputHandler functional interface
        OutputHandler outputHandler = System.out::println;

        // DI: Create the concrete execution engine
        ExecutionEngine engine = new BloopExecutionEngine();

        // DI: Inject both dependencies into the Interpreter
        Interpreter interpreter = new Interpreter(outputHandler, engine);

        // ── Run the program ──────────────────────────────
        try {
            interpreter.run(sourceCode);
        } catch (RuntimeException e) {
            System.err.println("Runtime error: " + e.getMessage());
            System.exit(1);
        }
    }
}

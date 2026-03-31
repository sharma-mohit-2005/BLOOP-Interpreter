package com.bloop;

import com.bloop.engine.ExecutionEngine;
import com.bloop.instruction.Instruction;
import com.bloop.lexer.Tokenizer;
import com.bloop.parser.Parser;
import com.bloop.pipeline.InterpreterPipeline;
import com.bloop.runtime.Environment;
import com.bloop.runtime.OutputHandler;
import com.bloop.token.Token;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The main Interpreter that connects all three steps:
 * Tokenize → Parse → Execute.
 * 
 * <h3>Dependency Injection</h3>
 * All dependencies are injected via the constructor:
 * - {@link OutputHandler} — for program output (enables testing)
 * - {@link ExecutionEngine} — for executing instructions (swappable strategy)
 * 
 * This means the Interpreter itself never creates its dependencies —
 * they're provided from outside. This is the core principle of DI:
 * "Don't call us, we'll call you" (Inversion of Control).
 * 
 * <h3>Functional Programming</h3>
 * Internally creates an {@link InterpreterPipeline} that composes
 * the three stages using {@code Function.andThen()}.
 * 
 * Uses method references and lambdas to wire up the pipeline stages.
 * 
 * <h3>Generics</h3>
 * The pipeline uses generic {@code Function<A, B>} types for type-safe
 * composition of the tokenize, parse, and execute stages.
 */
public class Interpreter {

    private final OutputHandler outputHandler;    // DI: injected output handler
    private final ExecutionEngine executionEngine; // DI: injected execution engine

    /**
     * Constructs an Interpreter with injected dependencies.
     * 
     * <h3>Constructor Injection (DI Pattern)</h3>
     * Both the output handler and execution engine are provided by the
     * caller, not created internally. This makes the Interpreter:
     * - Testable: inject mock handlers to capture output
     * - Flexible: swap execution engines without modifying this class
     * - Decoupled: this class doesn't know about System.out or any
     *   specific engine implementation
     *
     * @param outputHandler   handles program output (DI)
     * @param executionEngine executes parsed instructions (DI)
     */
    public Interpreter(OutputHandler outputHandler, ExecutionEngine executionEngine) {
        this.outputHandler = outputHandler;
        this.executionEngine = executionEngine;
    }

    /**
     * Runs a BLOOP program from source code.
     * 
     * <h3>Pipeline Architecture</h3>
     * 1. Tokenize: source code → List<Token>
     * 2. Parse:    List<Token> → List<Instruction>
     * 3. Execute:  List<Instruction> → program output
     * 
     * <h3>Functional Programming</h3>
     * Each stage is wrapped as a {@code Function<>} and composed
     * using {@code Function.andThen()} via InterpreterPipeline.
     *
     * @param sourceCode the raw BLOOP source code
     */
    public void run(String sourceCode) {
        // Create the environment (shared variable store)
        Environment env = new Environment();

        // ── Stage 1: Tokenizer function ──────────────────────
        // Functional programming: wrapping Tokenizer as a Function<String, List<Token>>
        // Uses lambda expression to create the function
        Function<String, List<Token>> tokenize = source -> {
            Tokenizer tokenizer = new Tokenizer(source);
            return tokenizer.tokenize();
        };

        // ── Stage 2: Parser function ─────────────────────────
        // Functional programming: wrapping Parser as a Function<List<Token>, List<Instruction>>
        // Uses lambda expression; injects outputHandler via DI
        Function<List<Token>, List<Instruction>> parse = tokens -> {
            Parser parser = new Parser(tokens, outputHandler); // DI: pass outputHandler
            return parser.parse();
        };

        // ── Stage 3: Executor consumer ───────────────────────
        // Functional programming: wrapping ExecutionEngine as Consumer<List<Instruction>>
        // Uses lambda expression
        Consumer<List<Instruction>> execute = instructions -> {
            executionEngine.execute(instructions, env);
        };

        // ── Compose and run the pipeline ─────────────────────
        // Functional programming: Function composition with andThen()
        InterpreterPipeline pipeline = new InterpreterPipeline(tokenize, parse, execute);
        pipeline.run(sourceCode);
    }
}

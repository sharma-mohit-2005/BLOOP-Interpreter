package com.bloop.pipeline;

import com.bloop.instruction.Instruction;
import com.bloop.runtime.Environment;
import com.bloop.token.Token;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A functional pipeline that chains tokenization, parsing, and execution
 * using {@code Function.andThen()} composition.
 * 
 * <h3>Functional Programming — Function Composition</h3>
 * This class demonstrates Java's {@code Function<A, B>} interface and
 * the {@code andThen()} method to compose multiple transformation steps
 * into a single pipeline:
 * 
 * <pre>{@code
 *   String → List<Token> → List<Instruction> → Void
 *         tokenize      parse             execute
 * }</pre>
 * 
 * Each stage is a {@code Function<>} that can be composed with
 * {@code andThen()} to create a seamless data pipeline.
 * 
 * <h3>Generics</h3>
 * The entire pipeline uses generic Function types:
 * - {@code Function<String, List<Token>>} for tokenization
 * - {@code Function<List<Token>, List<Instruction>>} for parsing
 * - {@code Consumer<List<Instruction>>} for execution
 * 
 * <h3>Dependency Injection</h3>
 * All three stages are injected via the constructor, making the pipeline
 * fully configurable and testable.
 */
public class InterpreterPipeline {

    // Generics: Function<InputType, OutputType> for each stage
    private final Function<String, List<Token>> tokenizer;
    private final Function<List<Token>, List<Instruction>> parser;
    private final Consumer<List<Instruction>> executor;

    /**
     * Constructs a pipeline with injected stages (DI).
     *
     * @param tokenizer transforms source code into tokens
     * @param parser    transforms tokens into instructions
     * @param executor  executes the instructions
     */
    public InterpreterPipeline(
            Function<String, List<Token>> tokenizer,
            Function<List<Token>, List<Instruction>> parser,
            Consumer<List<Instruction>> executor) {
        this.tokenizer = tokenizer;
        this.parser = parser;
        this.executor = executor;
    }

    /**
     * Runs the full pipeline on the given source code.
     * 
     * <h3>Function Composition with andThen()</h3>
     * The tokenizer and parser are composed using {@code andThen()}:
     * {@code tokenizer.andThen(parser)} creates a single function that
     * first tokenizes, then parses the result.
     * 
     * The composed function's output is then passed to the executor.
     *
     * @param sourceCode the raw BLOOP source code
     */
    public void run(String sourceCode) {
        // Functional programming: Function.andThen() composition
        // Chains tokenize → parse into a single Function
        Function<String, List<Instruction>> tokenizeAndParse = tokenizer.andThen(parser);

        // Apply the composed function, then execute
        List<Instruction> instructions = tokenizeAndParse.apply(sourceCode);
        executor.accept(instructions);
    }
}

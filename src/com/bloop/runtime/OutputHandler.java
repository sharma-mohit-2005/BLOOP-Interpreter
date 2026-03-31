package com.bloop.runtime;

/**
 * A functional interface for handling program output.
 * 
 * <h3>Dependency Injection &amp; Functional Programming</h3>
 * This interface enables dependency injection: instead of instructions
 * calling {@code System.out.println()} directly, they receive an
 * OutputHandler which decouples the output mechanism.
 * 
 * <p>Because this is a {@code @FunctionalInterface}, it can be
 * implemented using lambda expressions or method references:</p>
 * 
 * <pre>{@code
 *   // Production: prints to stdout
 *   OutputHandler handler = System.out::println;
 *   
 *   // Testing: captures output in a list
 *   List<String> output = new ArrayList<>();
 *   OutputHandler testHandler = output::add;
 * }</pre>
 */
@FunctionalInterface
public interface OutputHandler {

    /**
     * Handle one line of program output.
     *
     * @param output the text to output
     */
    void print(String output);
}

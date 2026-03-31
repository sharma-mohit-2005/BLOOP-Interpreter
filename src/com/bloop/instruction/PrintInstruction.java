package com.bloop.instruction;

import com.bloop.expression.Expression;
import com.bloop.runtime.Environment;
import com.bloop.runtime.OutputHandler;

/**
 * Handles printing — for example: {@code print x} or {@code print "hello"}
 * 
 * <h3>Dependency Injection</h3>
 * Instead of calling {@code System.out.println()} directly, this class
 * receives an {@link OutputHandler} via constructor injection. This makes
 * the instruction testable — in tests, you can inject a handler that
 * captures output into a list instead of printing to the console.
 * 
 * <h3>Output Formatting</h3>
 * Numbers that are whole (e.g. 16.0) are printed without the decimal
 * point (as "16"), matching the expected output format from the spec.
 */
public class PrintInstruction implements Instruction {

    private final Expression expression;
    private final OutputHandler outputHandler;

    /**
     * Constructs a print instruction.
     *
     * @param expression    the expression to evaluate and print
     * @param outputHandler the injected output handler (DI)
     */
    public PrintInstruction(Expression expression, OutputHandler outputHandler) {
        this.expression = expression;
        this.outputHandler = outputHandler;
    }

    /**
     * Evaluates the expression and sends the result to the output handler.
     * 
     * Doubles that are whole numbers (e.g., 16.0) are formatted as
     * integers (e.g., "16") to match the expected output.
     *
     * @param env the shared variable store
     */
    @Override
    public void execute(Environment env) {
        Object value = expression.evaluate(env);
        String output = formatOutput(value);
        outputHandler.print(output);
    }

    /**
     * Formats a value for output display.
     * Whole-number doubles are rendered without decimal points.
     *
     * @param value the value to format
     * @return the formatted string
     */
    private String formatOutput(Object value) {
        if (value instanceof Double) {
            double d = (Double) value;
            // Print whole numbers without decimal point: 16.0 → "16"
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return String.valueOf((long) d);
            }
            return String.valueOf(d);
        }
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return "PrintInstruction(" + expression + ")";
    }
}

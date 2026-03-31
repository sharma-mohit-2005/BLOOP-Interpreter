package com.bloop.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * The variable store — a mapping from variable names to their current values.
 * 
 * Uses generics: {@code Map<String, Object>} to store heterogeneous values
 * (numbers as {@code Double}, text as {@code String}).
 * 
 * Every instruction in the program shares a single Environment instance
 * during execution, which allows variables set by one instruction to be
 * read by later instructions.
 * 
 * <h3>Generics Usage</h3>
 * The internal map uses {@code Map<String, Object>} — the String key type
 * ensures only valid variable names are used, while the Object value type
 * allows storing both Double and String values polymorphically.
 */
public class Environment {

    // Generics: Map<String, Object> provides type-safe key lookup
    // while allowing heterogeneous value types (Double, String)
    private final Map<String, Object> variables = new HashMap<>();

    /**
     * Stores or updates the value associated with the given variable name.
     *
     * @param name  the variable name (e.g. "x", "score")
     * @param value the value to store (Double for numbers, String for text)
     */
    public void set(String name, Object value) {
        variables.put(name, value);
    }

    /**
     * Retrieves the current value of a variable.
     *
     * @param name the variable name to look up
     * @return the current value
     * @throws RuntimeException if the variable has not been defined
     */
    public Object get(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable not defined: " + name);
        }
        return variables.get(name);
    }

    /**
     * Checks whether a variable has been defined.
     *
     * @param name the variable name
     * @return true if the variable exists in the store
     */
    public boolean has(String name) {
        return variables.containsKey(name);
    }
}

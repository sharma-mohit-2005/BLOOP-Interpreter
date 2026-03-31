# 🟢 BLOOP Interpreter

A complete interpreter for **BLOOP** (Beginner-Level Object-Oriented Program) — a mini scripting language built entirely in pure Java.

> Built as part of the **Advanced OOP** course at Sitare University, demonstrating **Generics**, **Dependency Injection**, and **Functional Programming** in Java.

## ⚡ Quick Start (Docker)

```bash
docker pull sharmadev2005/bloop-interpreter:latest
docker run -it --rm sharmadev2005/bloop-interpreter:latest
```

You're now inside the BLOOP environment. Create and run programs instantly:

```bash
echo 'put 10 into x
put 3 into y
put x + y * 2 into result
print result' > demo.bloop

bloop demo.bloop
# Output: 16
```

Run built-in sample programs:
```bash
bloop tests/program1.bloop   # Arithmetic → 16
bloop tests/program2.bloop   # Strings → Sitare, Hello from BLOOP
bloop tests/program3.bloop   # Conditional → Pass
bloop tests/program4.bloop   # Loop → 1, 2, 3, 4
```

## 📖 The BLOOP Language

```
put 10 into x                 → Variable assignment
print x                       → Output
if x > 5 then:                → Conditional
    print "big"
repeat 3 times:               → Loop
    print "hello"
```

## 🏗️ Architecture

```
Source Code → Tokenizer → Parser → Executor → Output
               (Lexer)    (AST)    (Engine)
```

The interpreter is a **3-stage pipeline** — each stage transforms data and passes it to the next:

| Stage | Input | Output | Class |
|-------|-------|--------|-------|
| **Tokenize** | Raw source string | `List<Token>` | `Tokenizer.java` |
| **Parse** | Token list | `List<Instruction>` | `Parser.java` |
| **Execute** | Instruction list | Program output | `BloopExecutionEngine.java` |

## 🧬 Advanced OOP Concepts

| Concept | Implementation |
|---------|---------------|
| **Generics** | `Map<String, Object>` environment, `BiFunction<Double, Double, Object>` operators, `Function<String, List<Token>>` pipeline |
| **Dependency Injection** | `Interpreter` receives `OutputHandler` + `ExecutionEngine` via constructor; `PrintInstruction` uses injected output handler |
| **Functional Programming** | `@FunctionalInterface`, lambdas `(a,b) -> a+b`, `System.out::println` method reference, `Function.andThen()` composition, Stream API |

## 📁 Project Structure

```
src/com/bloop/
├── token/          TokenType (enum) + Token (immutable)
├── expression/     Expression interface + NumberNode, StringNode, VariableNode, BinaryOpNode
├── runtime/        Environment (variable store) + OutputHandler (DI interface)
├── instruction/    Instruction interface + Assign, Print, If, Repeat
├── lexer/          Tokenizer (character-by-character lexer)
├── parser/         Parser (recursive descent with operator precedence)
├── engine/         ExecutionEngine interface + BloopExecutionEngine
├── pipeline/       InterpreterPipeline (Function.andThen() composition)
├── Interpreter.java    Main interpreter with DI constructor
└── Main.java           CLI entry point (composition root)
```

## 🛠️ Build from Source

```bash
# Compile
javac -d out -sourcepath src src/com/bloop/Main.java

# Run
java -cp out com.bloop.Main tests/program1.bloop
```

## 🐳 Build Docker Image Locally

```bash
docker build -t bloop-interpreter .
docker run -it --rm bloop-interpreter
```

## 📄 License

This project was built for academic purposes at Sitare University.

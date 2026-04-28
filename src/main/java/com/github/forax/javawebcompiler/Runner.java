package com.github.forax.javawebcompiler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

/**
 * Utility class responsible for executing compiled classes from memory.
 * <p>
 * This class uses a {@link MemoryClassLoader} to dynamically load a class
 * and invoke its {@code main} method via reflection. The standard output
 * is captured and returned along with any compilation errors.
 */
public final class Runner {
  private Runner(){
      throw new AssertionError("no instances");
  }

  /**
   * Result of a program execution.
   *
   * @param output the captured standard output produced by the program
   * @param errors the list of compilation or runtime errors
   */
  record RunResult(String output, List<Compiler.Diagnostic> errors) {}

  /**
   * Loads a compiled class from memory and executes its {@code main} method.
   * <p>
   * If compilation errors are present, the execution is skipped and the errors
   * are returned directly.
   *
   * @param className the fully qualified name of the class to execute
   * @param loader the class loader containing the compiled bytecode
   * @param diagnostics the list of compilation diagnostics
   * @return a {@link RunResult} containing the program output and any errors
   * @throws Exception if an error occurs during class loading or method invocation
   */
  static RunResult runFromMemory(String className, MemoryClassLoader loader, List<Compiler.Diagnostic> diagnostics) throws Exception {
    Objects.requireNonNull(className);
    Objects.requireNonNull(loader);
    Objects.requireNonNull(diagnostics);

    if (!diagnostics.isEmpty()) {
      return new RunResult("", diagnostics);
    }

    var runClass = loader.loadClass(className);
    var method = runClass.getMethod("main", String[].class);
    var out = new ByteArrayOutputStream();
    var old = System.out;
    System.setOut(new PrintStream(out));
    try {
        method.invoke(null, (Object) new String[]{});
    } finally {
        System.setOut(old);
    }

    return new RunResult(out.toString(), List.of());
  }
}
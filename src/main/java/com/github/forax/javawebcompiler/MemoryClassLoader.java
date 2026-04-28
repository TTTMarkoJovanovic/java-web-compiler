// MemoryClassLoader.java
package com.github.forax.javawebcompiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A ClassLoader that loads classes from bytecode stored in memory,
 * instead of reading .class files from the filesystem.
 *
 * <p>Typical usage:
 * <pre>{@code
 * var loader = new MemoryClassLoader();
 * loader.addClass("Main", bytecode);
 * var clazz = loader.loadClass("Main");
 * }</pre>
 */
public final class MemoryClassLoader extends ClassLoader {
  private final HashMap<String, byte[]> classes = new HashMap<>();

  /**
   * add a Class to your MemoryClassLoader
   * @param name the fully qualified name of the class
   * @param bytes the bytecode of the compiled class (.class file) as a byte array
   */
  public void addClass(String name, byte[] bytes) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(bytes);
    classes.put(name, bytes);
  }

  /**
   * Finds and defines a class from the in-memory bytecode.
   *
   * @param name the name of the class to load
   * @return the Class object created from the stored bytecode
   * @throws ClassNotFoundException if no bytecode is found for the given class name
   */
  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    var bytecode = classes.get(name);
    if (bytecode == null) throw new ClassNotFoundException(name);
    return defineClass(name, bytecode, 0, bytecode.length);
  }
}
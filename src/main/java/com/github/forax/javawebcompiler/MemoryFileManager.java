// MemoryFileManager.java
package com.github.forax.javawebcompiler;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * A FileManager that create a copy of the class stored in memory,
 * instead of reading .class files from the filesystem.
 */
public final class MemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
  private final MemoryClassLoader loader;

  public MemoryFileManager(StandardJavaFileManager fileManager, MemoryClassLoader loader) {
    super(fileManager);
    this.loader = loader;
  }

  /**
   * Provides a JavaFileObject where the compiler will write the generated
   * bytecode.
   * The bytecode is captured and stored in the MemoryClassLoader.
   * @param location the location where the class file would normally be written
   * @param className the name of the class
   * @param kind the kind of file (typically CLASS)
   * @param sibling not used
   * @return a JavaFileObject that stores the compiled bytecode in memory
   */
  @Override
  public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling){
    return new SimpleJavaFileObject(URI.create("mem:///" + className), kind) {
      @Override
      public OutputStream openOutputStream() {
        return new ByteArrayOutputStream() {
          @Override
          public void close() throws IOException {
            super.close();
            loader.addClass(className.replace('/', '.'), toByteArray());
          }
        };
      }
    };
  }
}
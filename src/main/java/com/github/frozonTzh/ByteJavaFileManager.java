
package com.github.frozonTzh;
import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ByteJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    private final Map<String, byte[]> classBytes = new HashMap<>();

    protected ByteJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
        return new ByteJavaFileObject(className, kind);
    }

    public Map<String, byte[]> getClassBytes() {
        return classBytes;
    }

    private class ByteJavaFileObject extends SimpleJavaFileObject {
        private final String className;

        protected ByteJavaFileObject(String className, Kind kind) {
            super(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind);
            this.className = className;
        }

        @Override
        public OutputStream openOutputStream() {
            return new ByteArrayOutputStream() {
                @Override
                public void close() {
                    classBytes.put(className, this.toByteArray());
                }
            };
        }
    }
}

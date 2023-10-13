package com.github.frozonTzh;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JavaSourceLoader extends ClassLoader {
    private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    public JavaSourceLoader(ClassLoader parent) {
        super(parent);
    }

    public JavaSourceLoader() {
    }

    private ByteJavaFileManager compile(List<JavaFileObject> compilationUnits) {
        final ByteJavaFileManager fileManager = new ByteJavaFileManager(compiler.getStandardFileManager(null, null, null));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
        boolean compilationSuccess = task.call();
        if (compilationSuccess) {
            return fileManager;
        }
        return null;
    }

    private List<Class<?>> load(ByteJavaFileManager fileManager) {
        return fileManager.getClassBytes().entrySet().stream().map(entry -> defineClass(entry.getKey(), entry.getValue() )).collect(Collectors.toUnmodifiableList());
    }

    public List<Class<?>> loadSingleFile(String filePath, String className) throws IOException {
        File file = new File(filePath);
        String code = new String(Files.readAllBytes(file.toPath()));
        JavaSourceFromString source = new JavaSourceFromString(className, code);
        ByteJavaFileManager fm = compile(List.of(source));
        if (fm != null) {
            return load(fm);
        }
        return List.of();
    }

    public List<Class<?>> loadSrcPkg(String path) throws IOException {
        Path packagePath = Paths.get(path);
        new ArrayList<>();
        try (var n = Files.walk(packagePath)) {
            List<JavaFileObject> javaFileObjects =
                    n.filter(it -> it.toString().endsWith(JavaFileObject.Kind.SOURCE.extension))
                            .map(it -> {
                                Path relPath = packagePath.relativize(it);
                                String classNamePackaged = relPath.toString().replace(JavaFileObject.Kind.SOURCE.extension, "").replace(File.separator, ".");
                                try {
                                    String code = new String(Files.readAllBytes(it));
                                    return new JavaSourceFromString(classNamePackaged, code);
                                } catch (IOException e) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .map(it -> (JavaFileObject) it)
                            .toList();
            ByteJavaFileManager fm = compile(javaFileObjects);
            if (fm != null) {
                return load(fm);
            }
        }
        return List.of();
    }


    private static class JavaSourceFromString extends SimpleJavaFileObject {
        private final String code;

        public JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
    private Class<?> defineClass(String name, byte[] classBytes) {
        return defineClass(name, classBytes, 0, classBytes.length);
    }
}

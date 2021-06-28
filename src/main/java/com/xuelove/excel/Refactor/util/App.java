package com.xuelove.excel.Refactor.util;

import javax.tools.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static Map<String, JavaFileObject> fileObjects = new ConcurrentHashMap<>();
    public String MyJavaCompiler(String code) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        JavaFileManager javaFileManager = new MyJavaFileManager(compiler.getStandardFileManager(collector, null, null));

        List<String> options = new ArrayList<>();
        options.add("-target");
        options.add("1.8");

        Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
        System.out.println(CLASS_PATTERN + "类路径");
        Matcher matcher = CLASS_PATTERN.matcher(code);
        String cls;
        if (matcher.find()) {
            cls = matcher.group(1);
            System.out.println(cls + "这厮代码吗？");
        } else {
            throw new IllegalArgumentException("No such class name in " + code);
        }

        JavaFileObject javaFileObject = new MyJavaFileObject(cls, code);
        JavaCompiler.CompilationTask task = compiler.getTask(null, javaFileManager, collector, options, null, Arrays.asList(javaFileObject));
        Boolean result = task.call();
        return cls;
    }

    public static class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        public MyJavaFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }
        @Override
        public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
            JavaFileObject javaFileObject = fileObjects.get(className);
            if (javaFileObject == null) {
                super.getJavaFileForInput(location, className, kind);
            }
            return javaFileObject;
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String qualifiedClassName, JavaFileObject.Kind kind, FileObject sibling) throws IOException {

            //JavaFileObject javaFileObject = new App.MyJavaFileObject(qualifiedClassName, kind);
            MyJavaFileObject javaFileObject = new MyJavaFileObject(qualifiedClassName, kind);
            fileObjects.put(qualifiedClassName, javaFileObject);
            return javaFileObject;
        }
    }

    public static class MyClassLoader extends ClassLoader {

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            JavaFileObject fileObject = fileObjects.get(name);

            if (fileObject != null) {
                byte[] bytes = ((MyJavaFileObject) fileObject).getCompiledBytes();
                return defineClass(name, bytes, 0, bytes.length);
            }
            try {
                return ClassLoader.getSystemClassLoader().loadClass(name);
            } catch (Exception e) {
                System.out.println("执行这里吗？？？？");
                return super.findClass(name);
            }
        }
    }


}

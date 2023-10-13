package com.github.frozonTzh;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        JavaSourceLoader sourceLoader = new JavaSourceLoader(classLoader);
        try {
            sourceLoader.loadSrcPkg("out-src");
            Class<?> class_ = Class.forName("com.github.frozonTzh.plugin.Test", true, sourceLoader);
            ITest test = (ITest) class_.getConstructor().newInstance();
            System.out.println(test.doTest());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }


    }
}

package com.github.frozonTzh.plugin;

import com.github.frozonTzh.ITest;

public class Test implements ITest {
    @Override
    public String doTest(){
        System.out.println("execute doTest");
        return "doTest";
    }
}

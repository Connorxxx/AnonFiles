package com.connor.anonfiles.test;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionImpl;

public class JavaTest {

    public static void main(String[] args) {

        Test.INSTANCE.test( () -> {
            //channel
            return null;
        });

    }

    public void test() {

    }
}

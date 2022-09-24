package com.connor.anonfiles.test;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionImpl;

public class JavaTest {

    public static void main(String[] args) {

        Test.INSTANCE.test( (fileData) -> {
            //channel
            fileData.getFileID();
            return null;
        });

    }

    public void test() {

    }
}

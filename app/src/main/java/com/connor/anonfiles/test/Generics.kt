package com.connor.anonfiles.test

interface Transformer<in T> {
    fun transform(t: T): String
}

fun main() {
    val trans = object : Transformer<Person> {
        override fun transform(t: Person) =
            "${t.name} ${t.age}"
    }
    handleTransformer(trans)
}

fun handleTransformer(trans: Transformer<Student>) {
    val student = Student("Tom", 19)
    val result = trans.transform(student)
}
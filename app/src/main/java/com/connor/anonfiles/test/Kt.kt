package com.connor.anonfiles.test

open class Person(val name: String, val age: Int)

class Student(name: String, age: Int) : Person(name, age)

class Teacher(name: String, age: Int) : Person(name, age)

class SimpleData<out T>(val data: T?) {

    fun get(): T? = data
}

fun main() {
    val student = Student("Tom", 19)
    val data = SimpleData<Student>(student)
    handleSimpleData(data = data)
    val studentData = data.get()

}

fun handleSimpleData(data: SimpleData<Person>) {
    val personData = data.get()
}
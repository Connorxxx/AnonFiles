package com.connor.anonfiles.test

fun main() {
    val colors = listOf("red", "brown", "grey")
    val animals = listOf("fox", "bear", "wolf")
  //  println(colors zip animals)
    val twoAnimals = listOf("fox", "bear")
    //println(colors.zip(twoAnimals))
    val numberPairs = listOf("one" to 1, "two" to 2, "three" to 3, "four" to 4)
   // println(numberPairs.unzip())
   // val numbers = listOf("one", "two", "three", "four", "five", "six")
    //println(numbers.associateWith { it.length })
    //println(numbers.associateBy { it.first().uppercaseChar() })
    val numberSets = listOf(setOf(1, 2, 3), setOf(4, 5, 6), setOf(1, 2))
    //println(numberSets.flatten())
//    println(numbers)
//    println(numbers.joinToString())
//    println(numbers.joinToString(separator = " | ", prefix = "start: ", postfix = ": end"))
//    println(numbers.joinToString { "Element: ${it.uppercase()}"})
//    val filteredIdx = numbers.filterIndexed { index, s -> (index != 0) && (s.length < 5)  }
//    val filteredNot = numbers.filterNot { it.length <= 3 }
//
//    println(filteredIdx)
//    println(filteredNot)
//    val (match, rest) = numbers.partition { it.length > 3 }
//
//    println(match)
//    println(rest)
//    val plusList = numbers + "five"
//    val minusList = numbers - listOf("three", "four")
//    println(plusList)
//    println(minusList)
//    println(numbers.slice(1..3))
//    println(numbers.slice(0..4 step 2))
//    println(numbers.slice(setOf(3, 5, 0)))
//    println(numbers.take(3))
//    println(numbers.takeLast(3))
//    println(numbers.drop(1))
//    println(numbers.dropLast(5))
//    val numbers = mutableListOf("one", "two", "three", "four")
//    numbers.sort()
//    println(numbers)
//    println(numbers.binarySearch("two"))  // 3
//    println(numbers.binarySearch("z")) // -5
//    println(numbers.binarySearch("two", 0, 2))  // -3
    val numbers = mutableListOf("one", "five", "three")
    numbers[1] =  "two"
    println(numbers)
}
package com.connor.anonfiles.test

class Dependency {
    val libraries = ArrayList<String>()

    fun implementation(lib: String) {
        libraries.add(lib)
    }
}

class Td {
    var content = ""
    fun html() = "<td>$content</td>"
}

class Tr {
    private val children = ArrayList<Td>()

    fun td(block: Td.() -> String) {
        val td = Td()
        td.content = td.block()
        children.add(td)
    }

    fun html(): String {
        val builder = StringBuilder()
        builder.append("<tr>")
        children.forEach {
            builder.append(it.html())
        }
        builder.append("<tr>")
        return builder.toString()
    }
}

class Table {
    private val children = ArrayList<Tr>()

    fun tr(block: Tr.() -> Unit) {
        val tr = Tr()
        tr.block()
        children.add(tr)
    }

    fun html():String {
        val builder = StringBuilder()
        builder.append("<table>")
        children.forEach {
            builder.append(it.html())
        }
        builder.append("</table>")
        return builder.toString()
    }
}

fun main() {
    val libraries = dependencies {
        implementation("com.wu.ying.hao")
        implementation("com.test.wu.com")
    }

    val table = table {
        repeat(2) {
            tr {
                val fruits = listOf("Apple", "Grape", "Orange")
                fruits.forEach {
                    td { it }
                }
            }
        }
    }
    println(table)

    libraries.forEach {
        println(it)
    }
}

fun dependencies(block: Dependency.() -> Unit): List<String> {
    val dependency = Dependency()
    dependency.block()
    return dependency.libraries
}

fun table(block: Table.() -> Unit): String {
    val table = Table()
    table.block()
    return table.html()
}
package net.milosvasic.factory.mail

import net.milosvasic.factory.mail.error.ERROR
import java.lang.StringBuilder
import kotlin.system.exitProcess

fun fail(error: ERROR) {

    System.err.println(error.message)
    exitProcess(error.code)
}

fun fail(error: ERROR, vararg with: Any) {

    val builder = StringBuilder()
    with.forEach {
        builder.append(it.toString()).append("\n")
    }
    System.err.println("${error.message}: $builder")
    exitProcess(error.code)
}
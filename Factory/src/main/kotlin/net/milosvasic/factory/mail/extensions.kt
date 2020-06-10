package net.milosvasic.factory.mail

import net.milosvasic.factory.mail.common.Logger
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.logger.CompositeLogger
import net.milosvasic.logger.FilesystemLogger
import net.milosvasic.logger.SimpleLogger
import kotlin.system.exitProcess

const val tag = BuildInfo.NAME
const val localhost = "127.0.0.1"
val compositeLogger = CompositeLogger()

val log = object : Logger {

    override fun v(message: String) = compositeLogger.v(tag, message)

    override fun d(message: String) = compositeLogger.d(tag, message)

    override fun c(message: String) = compositeLogger.c(tag, message)

    override fun n(message: String) = compositeLogger.n(tag, message)

    override fun i(message: String) = compositeLogger.i(tag, message)

    override fun w(message: String) = compositeLogger.w(tag, message)

    override fun w(exception: Exception) = compositeLogger.w(tag, exception)

    override fun e(message: String) = compositeLogger.e(tag, message)

    override fun e(exception: Exception) = compositeLogger.e(tag, exception)
}

fun fail(error: ERROR) {

    System.err.println(error.message)
    exitProcess(error.code)
}

fun fail(error: ERROR, with: String) {

    log.e("${error.message}: $with")
    exitProcess(error.code)
}

fun fail(error: ERROR, vararg with: Any) {

    val builder = StringBuilder()
    with.forEach {
        builder.append(it.toString()).append("\n")
    }
    fail(error, builder)
}

fun fail(e: Exception) {

    val error = ERROR.FATAL_EXCEPTION
    var message = "Error: $e"
    e.message?.let {
       message = it
    }
    compositeLogger.e(tag, e, FilesystemLogger::class)
    compositeLogger.e(tag, message, SimpleLogger::class)
    exitProcess(error.code)
}

val String.Companion.EMPTY: String
    get() = ""

val String.Companion.LINE_BREAK: String
    get() = "\n"

fun Exception.getMessage(): String {

    var message = String.EMPTY
    this::class.simpleName?.let {
        message = it
    }
    this.message?.let {
        message = it
    }
    return message
}



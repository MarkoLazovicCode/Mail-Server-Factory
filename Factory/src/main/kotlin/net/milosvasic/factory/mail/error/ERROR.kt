package net.milosvasic.factory.mail.error

enum class ERROR(
    val code: Int,
    val message: String
) {

    RUNTIME_ERROR(1, "Runtime error"),
    UNEXPECTED_EVENT_RECEIVED(2, "Unexpected event received"),
    FILE_DOES_NOT_EXIST(3, "File does not exist"),
    FATAL_EXCEPTION(4, "Fatal exception"),
    INITIALIZATION_FAILURE(5, "Initialization failure"),
    TERMINATION_FAILURE(6, "Termination failure"),
    INSTALLATION_FAILURE(7, "Installation failure")
}
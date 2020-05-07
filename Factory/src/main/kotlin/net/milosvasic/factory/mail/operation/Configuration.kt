package net.milosvasic.factory.mail.operation

enum class Configuration(val configuration: String) {

    LOG_COMMAND("LOG_COMMAND"),
    LOG_COMMAND_RESULT("LOG_COMMAND_RESULT"),
    OBTAIN_RESULT("OBTAIN_RESULT");

    companion object {

        val DEFAULT = mapOf<Configuration, Boolean>(
                LOG_COMMAND to true,
                LOG_COMMAND_RESULT to true,
                OBTAIN_RESULT to false
        )

        val ALL_ON = mapOf<Configuration, Boolean>(
                LOG_COMMAND to true,
                LOG_COMMAND_RESULT to true,
                OBTAIN_RESULT to true
        )

        val ALL_OFF = mapOf<Configuration, Boolean>(
                LOG_COMMAND to false,
                LOG_COMMAND_RESULT to false,
                OBTAIN_RESULT to false
        )

        val MUTED = mapOf<Configuration, Boolean>(
                LOG_COMMAND to false,
                LOG_COMMAND_RESULT to false,
                OBTAIN_RESULT to true
        )
    }
}
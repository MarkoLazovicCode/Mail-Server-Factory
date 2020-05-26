package net.milosvasic.factory.mail.component.installer.step.database

import com.google.gson.Gson
import com.google.gson.JsonParseException
import net.milosvasic.factory.mail.common.DataHandler
import net.milosvasic.factory.mail.common.obtain.Obtain
import net.milosvasic.factory.mail.component.database.*
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.execution.flow.implementation.ObtainableFlow
import net.milosvasic.factory.mail.execution.flow.implementation.RegistrationFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.command.CatCommand
import net.milosvasic.factory.mail.terminal.command.EchoCommand
import net.milosvasic.factory.mail.terminal.command.TestCommand
import java.io.File

class DatabaseStep(val path: String) : RemoteOperationInstallationStep<SSH>() {

    companion object {

        const val defaultConfigurationFile = "configuration.json"
    }

    @Throws(IllegalArgumentException::class)
    override fun getFlow(): CommandFlow {

        connection?.let { conn ->

            val manager = DatabaseManager
            var config: DatabaseConfiguration? = null
            val configurationFile = "$path${File.separator}$defaultConfigurationFile"

            val configurationDataHandler = object : DataHandler<OperationResult> {
                override fun onData(data: OperationResult?) {

                    data?.let {
                        val json = it.data
                        val gson = Gson()
                        try {

                            config = gson.fromJson(json, DatabaseConfiguration::class.java)
                            log.i("Database configuration available: $config")
                        } catch (e: JsonParseException) {

                            log.e(e)
                        } catch (e: IllegalArgumentException) {

                            log.e(e)
                        }
                    }
                }
            }

            val databaseRegistrationProvider = object : Obtain<DatabaseRegistration> {
                override fun obtain(): DatabaseRegistration {
                    return getDatabaseRegistration(conn, config)
                }
            }

            val registrationFlow = RegistrationFlow<DatabaseRegistration>()
                    .width(manager)
                    .perform(databaseRegistrationProvider)

            val databaseFlow = ObtainableFlow<String>().width(
                    object : Obtain<InstallationStepFlow> {
                        override fun obtain(): InstallationStepFlow {
                            val db = databaseRegistrationProvider.obtain().database
                            return db.getInstallation()
                        }
                    }
            )

            return CommandFlow()
                    .width(conn)
                    .perform(TestCommand(configurationFile))
                    .perform(CatCommand(configurationFile), configurationDataHandler)
                    .connect(registrationFlow)
                    .connect(databaseFlow)
        }
        throw IllegalArgumentException("No proper connection provided")
    }

    override fun getOperation() = DatabaseInitializationOperation()

    @Throws(IllegalArgumentException::class)
    private fun getDatabaseRegistration(
            connection: Connection,
            configuration: DatabaseConfiguration?

    ): DatabaseRegistration {
        configuration?.let {

            val type = Type.getType(it.type)
            val dbConnection = DatabaseConnection(
                    it.host,
                    it.getPort(),
                    it.user,
                    it.password,
                    connection
            )
            val factory = DatabaseFactory(type, it.name, dbConnection)
            val database = factory.build()

            val callback = object : OperationResultListener {
                override fun onOperationPerformed(result: OperationResult) {
                    when (result.operation) {
                        is DatabaseRegistrationOperation -> {
                            if (!result.success) {
                                log.e("Database registration failed: $database")
                            }
                        }
                    }
                }
            }

            return DatabaseRegistration(database, callback)
        }
        throw IllegalArgumentException("Proper database configuration unavailable")
    }
}
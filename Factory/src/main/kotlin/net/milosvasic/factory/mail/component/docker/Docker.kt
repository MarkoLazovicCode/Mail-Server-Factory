package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.docker.recipe.DockerRecipeRegistrar
import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection
import java.util.concurrent.atomic.AtomicBoolean

class Docker(entryPoint: Connection) : InstallerAbstract(entryPoint) {

    private val initialized = AtomicBoolean()
    private val recipeRegistrar = DockerRecipeRegistrar()

    init {
        recipeRegistrars.add(recipeRegistrar)
    }

    override fun initialization() {

        initialized.set(true)
        free()
        val installerInitializationOperation = DockerInitializationOperation()
        val operationResult = OperationResult(installerInitializationOperation, true)
        notify(operationResult)
    }

    override fun termination() {
        initialized.set(false)
    }

    @Synchronized
    override fun isInitialized() = initialized.get()

    override fun getEnvironmentName() = "Docker"

    override fun getToolkit() = Toolkit(entryPoint)

    override fun getNotifyOperation() = DockerOperation()
}
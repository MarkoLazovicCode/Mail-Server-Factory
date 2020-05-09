package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.test.implementation.SimpleInitializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class FlowConnectTest : BaseTest() {

    @Test
    fun testFlowConnect() {
        initLogging()
        log.i("Flow connect test started")

        var count = 0
        val echo = "Test"
        val iterations = 3
        var commandFlowExecuted = 0
        var initializationFlowExecuted = 0

        fun getEcho() = Commands.echo("$echo:${++count}")

        val commandFlowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (success) {
                    log.d("Command flow finished")
                } else {
                    log.e(message)
                }
                assert(success)
                commandFlowExecuted++
            }
        }

        val initializationFlowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (success) {
                    log.d("Initialization flow finished")
                } else {
                    log.e(message)
                }
                assert(success)
                initializationFlowExecuted++
            }
        }

        val initializers = mutableListOf<Initializer>()
        for (x in 0 until iterations) {
            val initializer = SimpleInitializer("Initializer no. ${x + 1}")
            initializers.add(initializer)
        }

        fun getInitFlow() : InitializationFlow {
            var initFlow = InitializationFlow()
            initializers.forEach {
                initFlow = initFlow.width(it)
            }
            return initFlow
        }
        val initFlow = getInitFlow().onFinish(initializationFlowCallback)

        fun getCommandFlow() : CommandFlow {
            var flow = CommandFlow()
            val terminal = Terminal()
            for (x in 0 until iterations) {
                flow = flow.width(terminal)
                for (y in 0 .. x) {
                    flow = flow.perform(getEcho())
                }
            }
            return flow
        }
        val flow = getCommandFlow().onFinish(commandFlowCallback)

        for (x in 0 until iterations) {
            flow
                    .connect(getInitFlow())
                    .connect(getCommandFlow())
        }
        flow.run()

        while (commandFlowExecuted < iterations || initializationFlowExecuted < iterations) {
            Thread.yield()
        }
        Assertions.assertEquals(iterations, commandFlowExecuted)
        Assertions.assertEquals(iterations, initializationFlowExecuted)

        log.i("Flow connect test completed")
    }
}
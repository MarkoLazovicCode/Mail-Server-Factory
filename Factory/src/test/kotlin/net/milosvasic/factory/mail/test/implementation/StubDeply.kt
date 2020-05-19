package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.component.installer.step.deploy.Deploy
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.security.Permission
import net.milosvasic.factory.mail.security.Permissions
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.TerminalCommand

class StubDeploy(
        what: String,
        private val where: String,
        private val protoStubs: List<String>
) : Deploy(what, where) {

    override fun getScp(remote: Remote): String {
        return Commands.cp(localTar, where)
    }

    override fun getScpCommand() = Commands.cp

    override fun isRemote(operation: TerminalCommand) =
            operation.command.contains(StubSSH.stubCommandMarker)

    override fun getProtoCleanup(): String {
        var command = ""
        protoStubs.forEachIndexed { index, _ ->
            if (index > 0) {
                command += " && "
            }
            command += Commands.rm("$where/proto.stub.txt")
        }
        return command
    }

    override fun getSecurityChanges(remote: Remote): String {

        val permissions = Permissions(Permission.ALL, Permission.NONE, Permission.NONE)
        return Commands.chmod(where, permissions.obtain())
    }
}
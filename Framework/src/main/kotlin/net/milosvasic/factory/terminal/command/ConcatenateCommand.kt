package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class ConcatenateCommand(vararg commands: String) : TerminalCommand(Commands.concatenate(*commands))
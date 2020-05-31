package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.configuration.Variable
import net.milosvasic.factory.mail.localhost
import net.milosvasic.factory.mail.remote.Remote
import java.io.File

object Commands {

    const val rm = "rm"
    const val here = "."
    const val cp = "cp -R"
    const val ssh = "ssh -p"
    const val scp = "scp -P"
    const val uname = "uname"
    const val hostname = "hostname"
    const val tarExtension = ".tar.gz"

    private const val find = "find "
    private const val mkdir = "mkdir -p"
    private const val chmod = "chmod -R"
    private const val chgrp = "chgrp -R"
    private const val chown = "chown -R"
    private const val openssl = "openssl"
    private const val tarCompress = "tar -cjf"
    private const val tarDecompress = "tar -xvf"

    fun echo(what: String) = "echo '$what'"

    fun printf(what: String) = "printf '$what'"

    fun ssh(user: String = "root", command: String, port: Int = 22, host: String = localhost): String {
        return "$ssh $port $user@$host $command"
    }

    fun ping(host: String, timeoutInSeconds: Int = 3): String {
        return "ping $host -t$timeoutInSeconds"
    }

    fun getHostInfo(): String = "hostnamectl"

    fun getApplicationInfo(application: String): String = "which $application"

    fun reboot(rebootIn: Int = 2) = "( sleep $rebootIn ; reboot ) & "

    fun grep(what: String) = "grep \"$what\""

    fun scp(what: String, where: String, remote: Remote): String {

        return "$scp ${remote.port} $what ${remote.account}@${remote.host}:$where"
    }

    fun cp(what: String, where: String): String {

        return "$cp $what $where"
    }

    fun find(what: String, where: String) = "$find$where -name \"$what\""

    fun tar(what: String, where: String): String {

        val destination = where.replace(".tar", "").replace(".gz", "")
        return "$tarCompress $destination$tarExtension -C $what ."
    }

    fun unTar(what: String, where: String) = "$tarDecompress $what -C $where"

    fun rm(what: String) = "$rm $what"

    fun chmod(where: String, mode: String) = "$chmod $mode $where"

    fun chgrp(group: String, directory: String) = "$chgrp $group $directory"

    fun chown(account: String, directory: String) = "$chown $account $directory"

    fun mkdir(path: String) = "$mkdir $path"

    fun concatenate(vararg commands: String): String {
        var result = String.EMPTY
        commands.forEach {
            if (result.isNotEmpty() && !result.isBlank()) {
                result += "; "
            }
            result += it
        }
        return result
    }

    fun test(what: String) = "test -e $what"

    fun setHostName(hostname: String) = "hostnamectl set-hostname $hostname"

    fun cat(what: String) = "cat $what"

    fun generatePrivateKey(path: String, name: String): String {

        val keyName = getPrivateKyName(name)
        return "$openssl genrsa -out $path${File.separator}$keyName"
    }

    fun generateRequestKey(path: String, keyName: String, reqName: String): String {

        val cmd = "$openssl req -new -key"
        val requestKey = getRequestKeyName(reqName)
        val city = "{{SERVER.CERTIFICATION.CITY}}"
        val country = "{{SERVER.CERTIFICATION.COUNTRY}}"
        val province = "{{SERVER.CERTIFICATION.PROVINCE}}"
        val department = "{{SERVER.CERTIFICATION.DEPARTMENT}}"
        val organisation = "{{SERVER.CERTIFICATION.ORGANISATION}}"
        var params = "/C=$country/ST=$province/L=$city/O=$organisation/OU=$department/CN=$organisation"
        params = Variable.parse(params).replace(" ", "\\ ")
        return "$cmd $path${File.separator}$keyName -out $path${File.separator}$requestKey -subj $params"
    }

    fun getPrivateKyName(name: String): String {
        var fullName = name
        val extension = ".key"
        val prefix = "private."
        if (!fullName.endsWith(extension)) {
            fullName += extension
        }
        if (!fullName.startsWith(prefix)) {
            fullName = prefix + fullName
        }
        return fullName
    }

    private fun getRequestKeyName(reqName: String): String {
        var fullName = reqName
        val extension = ".req"
        val prefix = "request."
        if (!fullName.endsWith(extension)) {
            fullName += extension
        }
        if (!fullName.startsWith(prefix)) {
            fullName = prefix + fullName
        }
        return fullName
    }
}
package net.milosvasic.factory.mail.security

data class Permission(var value: Int) {

    companion object {

        val ALL = Permission(7)
        val NONE = Permission(0)
    }
}
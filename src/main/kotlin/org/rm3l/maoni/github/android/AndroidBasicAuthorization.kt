package org.rm3l.maoni.github.android

import khttp.structures.authorization.Authorization

import android.util.Base64

data class AndroidBasicAuthorization(val user: String, val password: String) : Authorization {
    override val header: Pair<String, String>
        get() {
            val b64 = Base64.encode("${this.user}:${this.password}".toByteArray(), Base64.DEFAULT)
                    .toString(Charsets.UTF_8)
            return "Authorization" to "Basic $b64"
        }
}

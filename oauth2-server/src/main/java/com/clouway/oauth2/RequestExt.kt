package com.clouway.oauth2

import com.clouway.friendlyserve.Request

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
fun Request.buildIssuer(): String {
    // Try RFC7239 Forwarded header first (e.g. "for=1.2.3.4; proto=https; host=example.com")
    val forwarded = header("Forwarded") ?: header("forwarded")
    var scheme: String? = null
    var host: String? = null

    if (!forwarded.isNullOrEmpty()) {
        val first = forwarded.split(",").first().trim()
        first
            .split(";")
            .map { it.trim() }
            .forEach { pair ->
                val idx = pair.indexOf('=')
                if (idx > 0) {
                    val name = pair.substring(0, idx).trim().lowercase()
                    var value = pair.substring(idx + 1).trim()
                    if (value.startsWith("\"") && value.endsWith("\"") && value.length >= 2) {
                        value = value.substring(1, value.length - 1)
                    }
                    when (name) {
                        "proto" -> scheme = value
                        "host" -> host = value
                    }
                }
            }
    }

    // X-Forwarded-* fallbacks
    if (scheme.isNullOrEmpty()) {
        val xProto = header("X-Forwarded-Proto") ?: header("x-forwarded-proto")
        if (!xProto.isNullOrEmpty()) {
            scheme = xProto.split(",").first().trim()
        } else {
            val hdrScheme = header("scheme")
            if (!hdrScheme.isNullOrEmpty()) {
                scheme = hdrScheme.trim()
            }
        }
    }

    if (host.isNullOrEmpty()) {
        val xHost = header("X-Forwarded-Host") ?: header("x-forwarded-host")
        if (!xHost.isNullOrEmpty()) {
            host = xHost.split(",").first().trim()
        } else {
            val hostHeader = header("Host") ?: header("host")
            if (!hostHeader.isNullOrEmpty()) {
                host = hostHeader.trim()
            }
        }
    }

    // If host has no port but X-Forwarded-Port is present, append it when appropriate
    val hostSnapshot = host
    if (!hostSnapshot.isNullOrEmpty() && !hostSnapshot.contains(":")) {
        val xPort = header("X-Forwarded-Port") ?: header("x-forwarded-port")
        val port = xPort?.split(",")?.firstOrNull()?.trim()
        if (!port.isNullOrEmpty()) {
            val portInt = port.toIntOrNull()
            if (portInt != null) {
                val usePort =
                    when (scheme) {
                        "http" -> portInt != 80
                        "https" -> portInt != 443
                        else -> true
                    }
                if (usePort) {
                    host = "$hostSnapshot:$portInt"
                }
            } else {
                host = "$hostSnapshot:$port"
            }
        }
    }

    val finalScheme = if (!scheme.isNullOrEmpty()) scheme else "https"
    val finalHost = if (!host.isNullOrEmpty()) host else (header("Host") ?: "localhost")

    return "$finalScheme://$finalHost"
}

fun Request.paramOf(vararg names: String): String? {
    for (name in names) {
        val value = param(name)
        if (!value.isNullOrEmpty()) {
            return value
        }
    }
    return null
}

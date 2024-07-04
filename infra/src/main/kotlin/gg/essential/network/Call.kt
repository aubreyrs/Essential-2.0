/*
 * Copyright (c) 2024 ModCore Inc. All rights reserved.
 *
 * This code is part of ModCore Inc.'s Essential Mod repository and is protected
 * under copyright registration # TX0009138511. For the full license, see:
 * https://github.com/EssentialGG/Essential/blob/main/LICENSE
 *
 * You may not use, copy, reproduce, modify, sell, license, distribute,
 * commercialize, or otherwise exploit, or create derivative works based
 * upon, this file or any other in this repository, all of which is reserved by Essential.
 */
package gg.essential.network

import gg.essential.connectionmanager.common.packet.Packet
import kotlinx.coroutines.suspendCancellableCoroutine
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Call(
    val connection: CMConnection,
    val packet: Packet,
) {
    private var timeout: Duration = 10.seconds
    private var ignoreUnexpectedPackets: Boolean = false

    fun inResponseTo(original: Packet) = apply {
        packet.setUniqueId(original.packetUniqueId)
    }

    fun timeout(duration: Duration = timeout) = apply {
        this.timeout = duration
    }

    fun fireAndForget() {
        @Suppress("DEPRECATION")
        connection.send(packet, null, TimeUnit.MILLISECONDS, timeout.inWholeMilliseconds)
    }

    suspend inline fun <reified T : Packet> await(): T? {
        return awaitOneOf(T::class.java) as T?
    }

    suspend fun awaitOneOf(vararg classes: Class<out Packet>): Packet? {
        return suspendCancellableCoroutine { cont ->
            @Suppress("DEPRECATION")
            connection.send(packet, { maybeResponse ->
                val response = maybeResponse.orElse(null)
                when {
                    response == null -> cont.resume(null)
                    classes.any { it.isInstance(response) } -> cont.resume(response)
                    else -> {
                        if (ignoreUnexpectedPackets) {
                            LOGGER.error("Got unexpected reply $response for $packet")
                            cont.resume(null)
                        } else {
                            cont.resumeWithException(UnexpectedResponseException(response))
                        }
                    }
                }
            }, TimeUnit.MILLISECONDS, timeout.inWholeMilliseconds)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Call::class.java)
    }
}

class UnexpectedResponseException(val response: Packet) : Exception()
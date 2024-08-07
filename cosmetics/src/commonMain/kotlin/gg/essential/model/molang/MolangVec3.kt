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
package gg.essential.model.molang

import dev.folomeev.kotgl.matrix.vectors.Vec3
import dev.folomeev.kotgl.matrix.vectors.vec3
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Serializable(with = MolangVec3Serializer::class)
data class MolangVec3(val x: MolangExpression, val y: MolangExpression, val z: MolangExpression) {
    fun eval(context: MolangContext): Vec3 =
        vec3(x.eval(context), y.eval(context), z.eval(context))

    companion object {
        val ZERO = MolangVec3(MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO)
        val UNIT_X = MolangVec3(MolangExpression.ONE, MolangExpression.ZERO, MolangExpression.ZERO)
        val UNIT_Y = MolangVec3(MolangExpression.ZERO, MolangExpression.ONE, MolangExpression.ZERO)
        val UNIT_Z = MolangVec3(MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ONE)
    }
}

// FIXME needs to be publicly accessible, otherwise kotlin throws this at runtime:
//   java.lang.IllegalAccessError: tried to access class gg.essential.model.molang.MolangVec3Serializer from class
//   gg.essential.model.file.ParticleEffectComponents$ParticleMotionParametric$$serializer
internal object MolangVec3Serializer : KSerializer<MolangVec3> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor
    override fun deserialize(decoder: Decoder): MolangVec3 = parse((decoder as JsonDecoder).decodeJsonElement())
    override fun serialize(encoder: Encoder, value: MolangVec3) = throw UnsupportedOperationException()

    private fun parse(json: JsonElement): MolangVec3 = when (json) {
        is JsonArray -> {
            val first = (json[0] as JsonPrimitive).parseMolangExpression()
            val second = (json.getOrNull(1) as JsonPrimitive?)?.parseMolangExpression() ?: first
            val third = (json.getOrNull(2) as JsonPrimitive?)?.parseMolangExpression() ?: second
            MolangVec3(first, second, third)
        }
        is JsonPrimitive -> when (json.content) {
            "x" -> MolangVec3.UNIT_X
            "y" -> MolangVec3.UNIT_Y
            "z" -> MolangVec3.UNIT_Z
            else -> json.parseMolangExpression().let { MolangVec3(it, it, it) }
        }
        else -> throw SerializationException("Expected array or primitive, got $json")
    }
}

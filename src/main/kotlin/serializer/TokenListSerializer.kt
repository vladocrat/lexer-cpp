package serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import model.Token

object TokenListSerializer : KSerializer<List<Token>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Token", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<Token> = TODO("Not yet implemented")

    override fun serialize(encoder: Encoder, value: List<Token>) {
        encoder.encodeString(value = value.joinToString(separator = " ") { it.text })
    }
}
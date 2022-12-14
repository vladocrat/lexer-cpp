package serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import model.Token

object TokenSerializer : KSerializer<Token> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Token", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Token = TODO("Not yet implemented")

    override fun serialize(encoder: Encoder, value: Token) {
        encoder.encodeString(value = value.text)
    }
}
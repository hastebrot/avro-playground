package avro

import avro.java.Ingredient
import avro.java.Pizza
import org.apache.avro.Schema
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.specific.SpecificDatumWriter
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

fun main() {
    val pizza = Pizza.newBuilder().run {
        name = "margherita"
        ingredients = listOf(
            Ingredient("tomato", 2.0, 3.0),
            Ingredient("cheese", 4.0, 10.0)
        )
        vegetarian = true
        kcals = 500
        build()
    }
    println(pizza)

    val bytes = pizza.toByteBuffer().array()

    println(bytes.asList())
    println(String(bytes, StandardCharsets.UTF_8))
    println(encodeToEscapedString(bytes))

    val dataPayload = unpackWirePayload(bytes)
    val reader = SpecificDatumReader(Pizza::class.java)
    val decoder = DecoderFactory.get().binaryDecoder(dataPayload, null)
    println(reader.read(null, decoder))

//    println(Foo.getDecoder().decode(dataWithoutWireFormat, null))
//    println(Foo.fromByteBuffer(ByteBuffer.wrap(dataWithoutWireFormat)))

    val schema = Schema.Parser().parse(Pizza.getClassSchema().toString(true))
    println(schema)
}

// https://docs.confluent.io/platform/6.2.0/schema-registry/serdes-develop/index.html#wire-format
fun unpackWirePayload(bytes: ByteArray) = bytes
    .run { sliceArray(WIRE_FORMAT_HEADER_LENGTH..lastIndex) }

const val WIRE_FORMAT_HEADER_LENGTH = 5

// https://stackoverflow.com/questions/7487917/convert-byte-array-to-escaped-string
fun encodeToEscapedString(bytes: ByteArray): String {
    // 0x00..0x1f = non-printing characters
    // 0x20 = SPACE
    // 0x21..0x7e = printing characters
    // 0x7f = DELETE
    val string = StringBuilder()
    val intSize = 0xff
    val byteRange = 0x20..0x7e
    for (byte in bytes) {
        val intByte = byte.toInt()
        when (byte) {
            in byteRange -> string.append(intByte.toChar())
            else -> string.append(formatByteU((intByte and intSize).toByte()))
        }
    }
    return string.toString()
}

private fun formatByteX(byte: Byte) = String.format("\\0x%02x", byte)
private fun formatByteU(byte: Byte) = String.format("\\u00%02x", byte)

val data1 = byteArrayOf()
val data2 = byteArrayOf()

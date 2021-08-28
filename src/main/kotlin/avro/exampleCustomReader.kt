package avro

import avro.java.Pizza
import org.apache.avro.Schema
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.ParsingDecoder
import org.apache.avro.io.ResolvingDecoder
import org.apache.avro.io.parsing.SkipParser
import org.apache.avro.specific.SpecificDatumReader
import java.nio.charset.StandardCharsets

class CustomDatumReader<T>(c: Class<T>) : SpecificDatumReader<T>(c) {
    override fun readRecord(old: Any?, expected: Schema, input: ResolvingDecoder): Any {
        val parser = input._parser
        println("read record: ${expected.name}, ${parser.depth()}")

        return super.readRecord(old, expected, input)
    }

    override fun readWithoutConversion(old: Any?, expected: Schema, input: ResolvingDecoder): Any {
        val parser = input._parser
        println("read: ${expected.name}, ${parser.depth()}")

        val result = super.readWithoutConversion(old, expected, input)
        println(result)
        return result
    }
}

val ParsingDecoder._parser: SkipParser
    get() = ParsingDecoder::class.java
        .getDeclaredField("parser")
        .apply { isAccessible = true }
        .get(this) as SkipParser

fun main() {
    val string1 = String(data2, StandardCharsets.UTF_8)
    val string2 = encodeToEscapedString(data2)
    println(string2)

    val dataPayload = unpackWirePayload(data2)
    val reader = CustomDatumReader(Pizza::class.java)
    val decoder = DecoderFactory.get().binaryDecoder(dataPayload, null)
    println(reader.read(null, decoder))
}


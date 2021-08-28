package avro

import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.apache.avro.compiler.schema.SchemaVisitor
import org.apache.avro.compiler.schema.SchemaVisitorAction
import org.apache.avro.compiler.schema.Schemas
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.BinaryDecoder
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.apache.logging.log4j.LogManager
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

private fun <T> T.println(prefix: String? = null) = apply {
    println(if (prefix == null) this else "$prefix: $this")
}

private val logger = LogManager.getLogger({}.javaClass.canonicalName)

fun main() {
    logger.info("tokenizer example")

    val schema = SchemaBuilder.record("record").run {
        fields().run {
            requiredInt("int")
            requiredLong("long")
            requiredFloat("float")
            requiredDouble("double")
            requiredBoolean("boolean")
            requiredString("string")
            requiredBytes("bytes")
            endRecord()
        }
    }

    val record = GenericData.Record(schema).apply {
        put("int", 123)
        put("long", 123L)
        put("float", 1.23F)
        put("double", 1.23)
        put("boolean", true)
        put("string", "abc")
        put("bytes", byteArrayOf(1, 2, 3).run { ByteBuffer.wrap(this) })
    }

    val writer = GenericDatumWriter<GenericRecord>(schema)
    val bytes = ByteArrayOutputStream().use { outputStream ->
        val encoder = EncoderFactory.get().binaryEncoder(outputStream, null)
        writer.write(record, encoder)
        encoder.flush()
        outputStream.toByteArray()
    }

    bytes.asList().println("bytes")
    encodeToEscapedString(bytes).println("encoded bytes")

    val schemaTypes = collectSchemaTypes(schema).println("schema types")
    val tokens = tokenize(bytes, schemaTypes).println("tokens")
}

typealias Token = Pair<Int, Any?>

fun tokenize(bytes: ByteArray, schemaTypes: List<Schema.Type>): List<Token> {
    val decoder = DecoderFactory.get().binaryDecoder(bytes, null)
//    decoder.skipFixed(5)
    val tokens = mutableListOf<Token>()
    schemaTypes.forEach { schemaType ->
        val pos = decoder._pos
        val data: Any? = when (schemaType) {
            // complex types.
            Schema.Type.RECORD -> null
            Schema.Type.ENUM -> null
            Schema.Type.ARRAY -> null
            Schema.Type.MAP -> null
            Schema.Type.UNION -> null
            Schema.Type.FIXED -> null

            // primitive types.
            Schema.Type.STRING -> decoder.readString()
            Schema.Type.BYTES -> decoder.readBytes(null)
            Schema.Type.INT -> decoder.readInt()
            Schema.Type.LONG -> decoder.readLong()
            Schema.Type.FLOAT -> decoder.readFloat()
            Schema.Type.DOUBLE -> decoder.readDouble()
            Schema.Type.BOOLEAN -> decoder.readBoolean()
            Schema.Type.NULL -> decoder.readNull()
        }
        tokens.add(pos to data)
    }
    return tokens
}

val BinaryDecoder._pos: Int
    get() = BinaryDecoder::class.java
        .getDeclaredField("pos")
        .apply { isAccessible = true }
        .get(this) as Int

fun collectSchemaTypes(schema: Schema): List<Schema.Type> {
    val schemaTypes = mutableListOf<Schema.Type>()
    Schemas.visit(schema, object : SchemaVisitor<Schema> {
        override fun get() = schema

        override fun visitTerminal(terminal: Schema): SchemaVisitorAction {
            schemaTypes.add(terminal.type)
            return SchemaVisitorAction.CONTINUE
        }

        override fun visitNonTerminal(nonTerminal: Schema): SchemaVisitorAction {
            schemaTypes.add(nonTerminal.type)
            return SchemaVisitorAction.CONTINUE
        }

        override fun afterVisitNonTerminal(nonTerminal: Schema): SchemaVisitorAction {
            return SchemaVisitorAction.CONTINUE
        }
    })
    return schemaTypes
}

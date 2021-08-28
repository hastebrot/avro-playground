package avro

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.AvroName
import com.github.avrokotlin.avro4k.schema.overrideNamespace
import kotlinx.serialization.Serializable
import org.apache.avro.SchemaValidatorBuilder

@Serializable
@AvroName("Foo")
data class Writer(
    val bar: String
)

@Serializable
@AvroName("Foo")
data class Reader(
    val baz: Int,
    val bar: String
)

fun main() {
    val writerSchema = Avro.default.schema(Writer.serializer()).overrideNamespace("avro")
    val readerSchema = Avro.default.schema(Reader.serializer()).overrideNamespace("avro")

//    val validator = SchemaValidatorBuilder().canReadStrategy().validateAll()
//    validator.validate(readerSchema, listOf(writerSchema))

    val validator = SchemaValidatorBuilder().canBeReadStrategy().validateAll()
    validator.validate(readerSchema, listOf(writerSchema))
}

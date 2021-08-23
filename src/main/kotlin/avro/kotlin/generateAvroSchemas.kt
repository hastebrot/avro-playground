package avro.kotlin

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.schema.overrideNamespace
import java.io.File

fun main() {
    val schema = Avro.default
        .schema(Pizza.serializer())
        .overrideNamespace("avro.java")

    File("build/pizza.avsc").absoluteFile.let {
        println("Writing avro schema to '${it.path}'.")
        it.parentFile.mkdirs()
        it.writeText(schema.toString(true))
    }
}

package avro

import avro.java.Pizza
import org.apache.avro.Schema
import org.apache.avro.io.parsing.Parser
import org.apache.avro.io.parsing.SkipParser
import org.apache.avro.io.parsing.ValidatingGrammarGenerator
import java.nio.charset.StandardCharsets

fun main() {
    val string1 = String(data2, StandardCharsets.UTF_8)
    val string2 = encodeToEscapedString(data2)
    println(string2)

    val binaryData = unpackWirePayload(data2)


    val schema = Pizza.getClassSchema()
    val rootSymbol = ValidatingGrammarGenerator().generate(schema)
    println(rootSymbol)

    val symbolHandler = Parser.ActionHandler { input, top -> top }
    val skipHandler = object : SkipParser.SkipHandler {
        override fun skipAction(): Unit = TODO("Not yet implemented")
        override fun skipTopSymbol(): Unit = TODO("Not yet implemented")
    }
    val parser = SkipParser(rootSymbol, symbolHandler, skipHandler)

    parser.pushSymbol(rootSymbol)
}

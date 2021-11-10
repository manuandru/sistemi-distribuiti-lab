package sd.lab.ws.token

import io.javalin.plugin.openapi.annotations.ContentType
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation
import io.javalin.plugin.openapi.dsl.document
import it.unibo.ds.ws.Credentials
import it.unibo.ds.ws.Token

val postToken: OpenApiDocumentation =
    document()
        .body<Credentials>(contentType = ContentType.JSON)
        .result<Token>(status = "200", contentType = ContentType.JSON)
        .result<String>(status = "400") { it.description = "Bad request" }
        .result<String>(status = "401") { it.description = "Unauthorized" }
package it.unibo.ds.ws

import io.javalin.plugin.openapi.annotations.ContentType
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation
import io.javalin.plugin.openapi.dsl.document
import it.unibo.ds.ws.tokens.TokenApi
import it.unibo.ds.ws.users.UserApi

object Doc {
    object Tokens {
        @JvmField
        val postToken: OpenApiDocumentation =
            document()
                .operation {
                    it.description = "Generates a token given some credentials"
                    it.operationId = TokenApi::createToken.name
                    it.addTagsItem("tokens")
                }
                .body<Credentials>(contentType = ContentType.JSON) {
                    it.description = "The user's credentials"
                }
                .result<Token>(status = "200", contentType = ContentType.JSON) {
                    it.description = "The user's credentials are valid, and here's the authorization token"
                }
                .result<String>(status = "400") {
                    it.description = "Bad request: some field is missing or invalid in the provided credentials"
                }
                .result<String>(status = "401") {
                    it.description = "Unauthorized: the provided credentials are well formed, but they correspond to no user"
                }
    }

    object Users {
        @JvmField
        val getAllUserNames: OpenApiDocumentation =
            document()
                .operation {
                    it.operationId = UserApi::getAllNames.name
                    it.description = "Retrieves all users' names"
                    it.addTagsItem("users")
                }
                .queryParam<Int>(name = "skip") {
                    it.description = "How many names should be skipped"
                    it.addExtension("default", "0")
                }
                .queryParam<Int>(name = "limit") {
                    it.description = "How many names should be returned"
                    it.addExtension("default", "10")
                }
                .queryParam<String>(name = "filter") {
                    it.description = "Only returns names containing the provided string"
                    it.addExtension("default", "<empty string>")
                }
                .jsonArray<String>(status = "200") {
                    it.description = "An array containing the selected user names"
                }
                .result<String>(status = "400") {
                    it.description = "Bad request: some field is missing or invalid in the provided params"
                }

        @JvmField
        val postNewUser: OpenApiDocumentation =
            document()
                .operation {
                    it.operationId = UserApi::registerUser.name
                    it.description = "Registers a novel user out of the provided user data"
                    it.addTagsItem("users")
                }
                .body<User>(contentType = ContentType.JSON) {
                    it.description = "The user's data"
                }
                .json<String>(status = "200") {
                    it.description = "The username of the newly created user"
                }
                .result<String>(status = "400") {
                    it.description = "Bad request: some field is missing or invalid in the provided user data"
                }
                .result<String>(status = "409") {
                    it.description = "Conflict: some identifier (username or email address) of the provided user data has already been taken"
                }

        @JvmField
        val getUser: OpenApiDocumentation =
            document()
                .operation {
                    it.operationId = UserApi::getUser.name
                    it.description = "Gets the data of a user, given some identifier of theirs (either a username or an email address)"
                    it.addTagsItem("users")
                }
                .pathParam<String>(name = "userId") {
                    it. description = "Some identifier (either a username or an email address) of the user whose data is being requested"
                }
                .json<User>(status = "200") {
                    it.description = "The provided identifier corresponds to a user, whose data is thus returned"
                }
                .result<String>(status = "400") {
                    it.description = "Bad request: some field is missing or invalid in the provided user id"
                }
                .result<String>(status = "404") {
                    it.description = "Not found: the provided identifier corresponds to no known user"
                }

        @JvmField
        val deleteUser: OpenApiDocumentation =
            document()
                .operation {
                    it.operationId = UserApi::removeUser.name
                    it.description = "Deletes a user, given some identifier of theirs (either a username or an email address)"
                    it.addTagsItem("users")
                }
                .pathParam<String>(name = "userId") {
                    it. description = "Some identifier (either a username or an email address) of the user whose data is being requested"
                }
                .result<String>(status = "201") {
                    it.description = "The provided identifier corresponds to a user, which is thus removed. Nothing is returned"
                }
                .result<String>(status = "400") {
                    it.description = "Bad request: some field is missing or invalid in the provided user id"
                }
                .result<String>(status = "404") {
                    it.description = "Not found: the provided identifier corresponds to no known user"
                }

        @JvmField
        val putUser: OpenApiDocumentation =
            document()
                .operation {
                    it.operationId = UserApi::editUser.name
                    it.description = "Edits a user, given their identifier and some new user data"
                    it.addTagsItem("users")
                }
                .pathParam<String>(name = "userId") {
                    it. description = "Some identifier (either a username or an email address) of the user whose data is being requested"
                }
                .body<User>(contentType = ContentType.JSON) {
                    it.description = "The user's credentials"
                }
                .json<String>(status = "200") {
                    it.description = "The provided identifier corresponds to a user, which is thus updated, and the new username is returned"
                }
                .result<String>(status = "400") {
                    it.description = "Bad request: some field is missing or invalid in the provided user id or data"
                }
                .result<String>(status = "404") {
                    it.description = "Not found: the provided identifier corresponds to no known user"
                }
                .result<String>(status = "409") {
                    it.description = "Conflict: some identifier in the new user data has already been taken"
                }

    }
}
package it.unibo.ds.ws.users;

import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.openapi.*;
import it.unibo.ds.ws.AuthService;
import it.unibo.ds.ws.Controller;
import it.unibo.ds.ws.User;
import it.unibo.ds.ws.users.impl.UserControllerImpl;

public interface UserController extends Controller {

    @OpenApi(
            operationId = "UserApi::getAllNames",
            path = AuthService.BASE_URL + "/users",
            methods = {HttpMethod.GET},
            tags = {"users"},
            description = "Retrieves all users' names",
            queryParams = {
                    @OpenApiParam(
                            name = "skip",
                            type = Integer.class,
                            description = "How many names should be skipped (default: 0)",
                            required = false
                    ),
                    @OpenApiParam(
                            name = "limit",
                            type = Integer.class,
                            description = "How many names should be returned (default: 10)",
                            required = false
                    ),
                    @OpenApiParam(
                            name = "filter",
                            type = String.class,
                            description = "Only returns names containing the provided string (default: \"\")",
                            required = false
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "An array containing the selected user names",
                            content = {
                                    @OpenApiContent(
                                            from = User[].class,
                                            mimeType = ContentType.JSON
                                    )
                            }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Bad request: some field is missing or invalid in the provided params"
                    )
            }
    )
    void getAllUserNames(Context context) throws HttpResponseException;

    @OpenApi(
            operationId = "UserApi::registerUser",
            path = AuthService.BASE_URL + "/users",
            methods = {HttpMethod.POST},
            tags = {"users"},
            description = "Registers a novel user out of the provided user data",
            requestBody = @OpenApiRequestBody(
                    description = "The user's data",
                    required = true,
                    content = {
                            @OpenApiContent(
                                    from = User.class,
                                    mimeType = ContentType.JSON
                            )
                    }
            ),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "The username of the newly created user",
                            content = {
                                    @OpenApiContent(
                                            from = User[].class,
                                            mimeType = ContentType.JSON
                                    )
                            }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Bad request: some field is missing or invalid in the provided user data"
                    ),
                    @OpenApiResponse(
                            status = "409",
                            description = "Conflict: some identifier (username or email address) of the provided user data has already been taken"
                    )
            }
    )
    void postNewUser(Context context) throws HttpResponseException;

    @OpenApi(
            operationId = "UserApi::getUser",
            path = AuthService.BASE_URL + "/users/{userId}",
            methods = {HttpMethod.GET},
            tags = {"users"},
            description = "Gets the data of a user, given some identifier of theirs (either a username or an email address)",
            pathParams = {
                    @OpenApiParam(
                            name = "userId",
                            type = String.class,
                            description = "Some identifier (either a username or an email address) of the user whose data is being requested",
                            required = true
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "The provided identifier corresponds to a user, whose data is thus returned",
                            content = {
                                    @OpenApiContent(
                                            from = User.class,
                                            mimeType = ContentType.JSON
                                    )
                            }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Bad request: some field is missing or invalid in the provided user identifier"
                    ),
                    @OpenApiResponse(
                            status = "404",
                            description = "Not found: the provided identifier corresponds to no known user"
                    )
            }
    )
    void getUser(Context context) throws HttpResponseException;

    @OpenApi(
            operationId = "UserApi::removeUser",
            path = AuthService.BASE_URL + "/users/{userId}",
            methods = {HttpMethod.DELETE},
            tags = {"users"},
            description = "Deletes a user, given some identifier of theirs (either a username or an email address)",
            pathParams = {
                    @OpenApiParam(
                            name = "userId",
                            type = String.class,
                            description = "Some identifier (either a username or an email address) of the user whose data is being requested",
                            required = true
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "201",
                            description = "The provided identifier corresponds to a user, which is thus removed. Nothing is returned"
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Bad request: some field is missing or invalid in the provided user identifier"
                    ),
                    @OpenApiResponse(
                            status = "404",
                            description = "Not found: the provided identifier corresponds to no known user"
                    )
            }
    )
    void deleteUser(Context context) throws HttpResponseException;

    @OpenApi(
            operationId = "UserApi::editUser",
            path = AuthService.BASE_URL + "/users/{userId}",
            methods = {HttpMethod.PUT},
            tags = {"users"},
            description = "Edits a user, given their identifier and some new user data",
            pathParams = {
                    @OpenApiParam(
                            name = "userId",
                            type = String.class,
                            description = "Some identifier (either a username or an email address) of the user whose data is being requested",
                            required = true
                    )
            },
            requestBody = @OpenApiRequestBody(
                    description = "The updated user's data",
                    required = true,
                    content = {
                            @OpenApiContent(
                                    from = User.class,
                                    mimeType = ContentType.JSON
                            )
                    }
            ),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "The provided identifier corresponds to a user, which is thus updated, and the new username is returned",
                            content = {
                                    @OpenApiContent(
                                            from = String.class,
                                            mimeType = ContentType.JSON
                                    )
                            }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Bad request: some field is missing or invalid in the provided user identifier or data"
                    ),
                    @OpenApiResponse(
                            status = "404",
                            description = "Not found: the provided identifier corresponds to no known user"
                    ),
                    @OpenApiResponse(
                            status = "404",
                            description = "Conflict: some identifier in the new user data has already been taken"
                    )
            }
    )
    void putUser(Context context) throws HttpResponseException;

    static UserController of(String root) {
        return new UserControllerImpl(root);
    }
}

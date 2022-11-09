package it.unibo.ds.ws.tokens;

import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.openapi.*;
import it.unibo.ds.ws.AuthService;
import it.unibo.ds.ws.Controller;
import it.unibo.ds.ws.Credentials;
import it.unibo.ds.ws.Token;
import it.unibo.ds.ws.tokens.impl.TokenControllerImpl;

public interface TokenController extends Controller {

    @OpenApi(
            path = AuthService.BASE_URL + "/tokens",
            methods = {HttpMethod.POST},
            tags = {"tokens"},
            description = "Generates a token given some credentials",
            requestBody = @OpenApiRequestBody(
                    description = "The user's credentials",
                    required = true,
                    content = {
                            @OpenApiContent(
                                    from = Credentials.class,
                                    mimeType = ContentType.JSON
                            )
                    }
            ),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "The user's credentials are valid, and here's the authorization token",
                            content = {
                                    @OpenApiContent(
                                            from = Token.class,
                                            mimeType = ContentType.JSON
                                    )
                            }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Bad request: some field is missing or invalid in the provided credentials"
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Unauthorized: the provided credentials are well formed, but they correspond to no user"
                    )
            },
            operationId = "TokenApi::createToken"
    )
    void postToken(Context context) throws HttpResponseException;

    static TokenController of(String root) {
        return new TokenControllerImpl(root);
    }
}

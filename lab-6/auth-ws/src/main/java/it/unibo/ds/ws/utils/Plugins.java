package it.unibo.ds.ws.utils;

import io.javalin.openapi.OpenApiInfo;
import io.javalin.openapi.plugin.OpenApiConfiguration;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.plugin.bundled.RouteOverviewPlugin;

public class Plugins {
    public static OpenApiPlugin openApiPlugin(String apiVersion, String path) {
        OpenApiConfiguration configuration = new OpenApiConfiguration();
        OpenApiInfo info = configuration.getInfo();
        info.setTitle("Auth Service");
        info.setVersion(apiVersion);
        info.setDescription("A simple WS managing users and their authorization in totally INSECURE way");
        configuration.setDocumentationPath(path);
        return new OpenApiPlugin(configuration);
    }

    public static SwaggerPlugin swaggerPlugin(String docPath, String uiSubPath) {
        SwaggerConfiguration configuration = new SwaggerConfiguration();
        configuration.setUiPath(docPath + uiSubPath);
        configuration.setDocumentationPath(docPath);
        return new SwaggerPlugin(configuration);
    }

    public static RouteOverviewPlugin routeOverviewPlugin(String path) {
        return new RouteOverviewPlugin(path);
    }
}
